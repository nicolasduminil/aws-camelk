package fr.simplex_software.quarkus.camel.integrations.s3;

import org.apache.camel.builder.*;
import org.eclipse.microprofile.config.inject.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

import javax.enterprise.context.*;
import java.util.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class S3ToSqsRoute extends RouteBuilder
{
  private static final String RANDOM = new Random().ints('a', 'z')
    .limit(5)
    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
    .toString();
  //@Inject
  S3Client s3client;
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  public String s3BucketName;

  public S3ToSqsRoute() throws InterruptedException
  {
    s3client = S3Client.builder().region(Region.EU_WEST_3).credentialsProvider(ProfileCredentialsProvider.create()).build();
    Optional<Bucket> optionalBucket = s3client.listBuckets().buckets().stream().filter(b -> b.name().startsWith("mys3")).findFirst();
    if (optionalBucket.isPresent())
      s3BucketName = optionalBucket.get().name();
    else
    {
      s3BucketName = "mys3" + RANDOM;
      s3client.createBucket(CreateBucketRequest.builder().bucket(s3BucketName).build());
      s3client.waiter().waitUntilBucketExists(HeadBucketRequest.builder().bucket(s3BucketName).build());
    }
    System.out.println ("### S3ToSqsRoute(): Bucket name " + s3BucketName);
  }

  @Override
  public void configure()
  {
    from(aws2S3(s3BucketName).useDefaultCredentialsProvider(true))
      .split().tokenizeXML("moneyTransfer").streaming()
      .to(aws2Sqs(queueName).autoCreateQueue(true).useDefaultCredentialsProvider(true));
  }
}
