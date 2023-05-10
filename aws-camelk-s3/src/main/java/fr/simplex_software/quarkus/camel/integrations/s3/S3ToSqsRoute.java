package fr.simplex_software.quarkus.camel.integrations.s3;

import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.context.*;
import javax.enterprise.inject.*;
import javax.inject.*;

import java.util.*;
import java.util.stream.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class S3ToSqsRoute extends RouteBuilder
{
  private static final String RANDOM = new Random().ints('a', 'z')
    .limit(5)
    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
    .toString();
  @ConfigProperty(name="illegal-state-exception-msg")
  String illegalStateExceptionMsg;
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  public String s3BucketName;

  public S3ToSqsRoute ()
  {
    AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().build();
    //
    // The following code doesn't work and raises:
    // com.amazonaws.services.s3.model.AmazonS3Exception: Your previous request to create the named bucket succeeded and you already own it
    // Not sure why. Have registered a ticket with re:post but no solution or explanation
    //
    /*this.s3BucketName = amazonS3.listBuckets().stream()
      .filter(b -> b.getName().startsWith("mys3")).findFirst().orElse(amazonS3.createBucket("mys3" + RANDOM)).getName();*/
    //
    // Have replaced with the following code which is equivalent but
    // as opposed to the one above, works as expected
    //
    Optional<Bucket> optionalBucket = amazonS3.listBuckets().stream().filter(b -> b.getName().startsWith("mys3")).findFirst();
    if (optionalBucket.isPresent())
      this.s3BucketName = optionalBucket.get().getName();
    else
      this.s3BucketName = amazonS3.createBucket("mys3" + RANDOM).getName();
  }

  @Override
  public void configure()
  {
    from(aws2S3(s3BucketName).useDefaultCredentialsProvider(true))
      .split().tokenizeXML("moneyTransfer").streaming()
      .to(aws2Sqs(queueName).autoCreateQueue(true).useDefaultCredentialsProvider(true));
  }
}
