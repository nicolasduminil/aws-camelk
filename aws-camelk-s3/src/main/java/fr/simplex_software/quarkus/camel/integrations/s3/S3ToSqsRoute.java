package fr.simplex_software.quarkus.camel.integrations.s3;

import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.context.*;
import javax.inject.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class S3ToSqsRoute extends RouteBuilder
{
  @ConfigProperty(name="illegal-state-exception-msg")
  String illegalStateExceptionMsg;
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  String s3BucketName;

  @Inject
  public S3ToSqsRoute (@Named("s3BucketName") String s3BucketName)
  {
    this.s3BucketName = s3BucketName;
  }

  @Override
  public void configure()
  {
    from(aws2S3(s3BucketName).useDefaultCredentialsProvider(true))
      .split().tokenizeXML("moneyTransfer").streaming()
      .to(aws2Sqs(queueName).autoCreateQueue(true).useDefaultCredentialsProvider(true));
  }

  public String getS3BucketName()
  {
    return s3BucketName;
  }
}
