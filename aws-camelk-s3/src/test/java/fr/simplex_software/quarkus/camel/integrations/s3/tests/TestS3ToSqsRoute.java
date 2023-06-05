package fr.simplex_software.quarkus.camel.integrations.s3.tests;

import fr.simplex_software.quarkus.camel.integrations.s3.*;
import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.apache.camel.component.aws2.s3.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.*;

import javax.inject.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestS3ToSqsRoute
{
  @Inject
  SqsClient sqsclient;
  @Inject
  S3Client s3client;
  @Inject
  ProducerTemplate producer;
  @ConfigProperty(name = "sqs-queue-name")
  String queueName;
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
  public void testSendMessageToSQS()
  {
    String fmt = String.format(ENDPOINT_URI, s3ToSqsRoute.s3BucketName);
    System.out.println ("### Producing to " + fmt);
    producer.sendBodyAndHeader(fmt, BODY, AWS2S3Constants.KEY, "test.xml");
    String queueUrl = sqsclient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
    assertThat(queueUrl).isNotNull();
    ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder().queueUrl(queueUrl).maxNumberOfMessages(5).build();
    List<Message> messages = sqsclient.receiveMessage(receiveMessageRequest).messages();
    assertThat(messages).isNotNull();
    assertThat(messages.size()).isGreaterThan(0);
    messages.forEach(msg ->
    {
      assertThat(msg).isNotNull();
      assertThat(msg.body()).isEqualTo(BODY);
      sqsclient.deleteMessage(DeleteMessageRequest.builder().queueUrl(queueUrl).build());
    });
    messages = sqsclient.receiveMessage(receiveMessageRequest).messages();
    assertThat(messages).hasSize(0);
    sqsclient.deleteQueue(DeleteQueueRequest.builder().queueUrl(queueUrl).build());
  }
}
