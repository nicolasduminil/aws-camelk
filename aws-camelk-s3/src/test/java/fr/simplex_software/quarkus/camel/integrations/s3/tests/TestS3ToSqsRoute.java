package fr.simplex_software.quarkus.camel.integrations.s3.tests;

import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.Message;
import fr.simplex_software.quarkus.camel.integrations.s3.*;
import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import javax.inject.*;
import java.util.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;
import static org.assertj.core.api.Assertions.*;

@QuarkusTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestS3ToSqsRoute /*extends CamelTestSupport*/
{
  /*@Produce*/
  @Inject
  private FluentProducerTemplate producer;
  @Inject
  S3ToSqsRoute s3ToSqsRoute;
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  private final AmazonSQS amazonSqsClient = AmazonSQSClientBuilder.defaultClient();

  @Test
  //@Order(10)
  public void testsendMessageToSQS() throws Exception
  {
    EndpointProducerBuilder ep = aws2S3(s3ToSqsRoute.s3BucketName).useDefaultCredentialsProvider(true);
    System.out.println ("### URI: " + s3ToSqsRoute.s3BucketName);
    producer.withBody("<test/>").to(ep);
    Thread.sleep(2000);
    String queueUrl = amazonSqsClient.getQueueUrl(queueName).getQueueUrl();
    assertThat(queueUrl).isNotNull();
    List<Message> messages = amazonSqsClient.receiveMessage(queueUrl).getMessages();
    assertThat(messages).isNotNull();
    assertThat(messages.size()).isGreaterThan(0);
    messages.forEach(msg -> {
      assertThat(msg).isNotNull();
      assertThat(msg).isEqualTo("<test/>");
      amazonSqsClient.deleteMessage(queueUrl, msg.getMessageId());
    });
    messages = amazonSqsClient.receiveMessage(queueUrl).getMessages();
    assertThat(messages).hasSize(0);
    amazonSqsClient.deleteQueue(queueUrl);
  }
}
