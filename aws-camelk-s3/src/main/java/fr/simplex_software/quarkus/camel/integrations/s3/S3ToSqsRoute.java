package fr.simplex_software.quarkus.camel.integrations.s3;

import com.amazonaws.services.s3.*;
import org.apache.camel.builder.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.context.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class S3ToSqsRoute extends RouteBuilder
{
  @ConfigProperty(name="illegal-state-exception-msg")
  String illegalStateExceptionMsg;
  @ConfigProperty(name="sqs-queue-name")
  String queueName;

  @Override
  public void configure()
  {
    from(aws2S3(getS3BucketName()).useDefaultCredentialsProvider(true))
      .split().tokenizeXML("moneyTransfer").streaming()
      .to(aws2Sqs(queueName).autoCreateQueue(true).useDefaultCredentialsProvider(true));
  }

  private String getS3BucketName()
  {
    return AmazonS3ClientBuilder.standard().build().listBuckets().stream()
      .filter(b -> b.getName().startsWith("mys3")).findFirst()
      .orElseThrow(() -> new IllegalStateException(illegalStateExceptionMsg)).getName();
  }
}
