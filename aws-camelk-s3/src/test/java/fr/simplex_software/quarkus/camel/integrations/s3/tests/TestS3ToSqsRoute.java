package fr.simplex_software.quarkus.camel.integrations.s3.tests;

import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.Message;
import fr.simplex_software.quarkus.camel.integrations.s3.*;
import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.apache.camel.component.aws2.s3.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import javax.inject.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestS3ToSqsRoute
{
  @Inject
  ProducerTemplate producer;
  @ConfigProperty(name = "sqs-queue-name")
  String queueName;
  private final AmazonSQS amazonSqsClient = AmazonSQSClientBuilder.defaultClient();
  @Inject
  CamelContext camelContext;
  @Inject
  S3ToSqsRoute s3ToSqsRoute;
  private static final String BODY = "<moneyTransfer/>";
  private static final String ENDPOINT_URI = "aws2-s3://%s?useDefaultCredentialsProvider=true";

  @BeforeAll
  public void beforeAll() throws Exception
  {
    camelContext.addRoutes(s3ToSqsRoute);
    camelContext.start();
  }

  @AfterAll
  public void afterAll()
  {
    camelContext.stop();
  }

  @Test
  public void testSendMessageToSQS() throws Exception
  {
    producer.sendBodyAndHeader(String.format(ENDPOINT_URI, s3ToSqsRoute.s3BucketName), BODY, AWS2S3Constants.KEY, "test.xml");
    Thread.sleep(2000);
    String queueUrl = amazonSqsClient.getQueueUrl(queueName).getQueueUrl();
    assertThat(queueUrl).isNotNull();
    List<Message> messages = amazonSqsClient.receiveMessage(queueUrl).getMessages();
    assertThat(messages).isNotNull();
    assertThat(messages.size()).isGreaterThan(0);
    messages.forEach(msg ->
    {
      assertThat(msg).isNotNull();
      assertThat(msg.getBody()).isEqualTo(BODY);
      amazonSqsClient.deleteMessage(queueUrl, msg.getReceiptHandle());
    });
    messages = amazonSqsClient.receiveMessage(queueUrl).getMessages();
    assertThat(messages).hasSize(0);
    amazonSqsClient.deleteQueue(queueUrl);
  }
}
