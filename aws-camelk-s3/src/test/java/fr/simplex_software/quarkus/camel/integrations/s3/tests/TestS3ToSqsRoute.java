package fr.simplex_software.quarkus.camel.integrations.s3.tests;

import com.amazonaws.services.sqs.*;
import com.amazonaws.services.sqs.model.Message;
import fr.simplex_software.quarkus.camel.integrations.s3.*;
import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.apache.camel.main.*;
import org.eclipse.microprofile.config.inject.*;
import org.junit.jupiter.api.*;

import javax.inject.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestS3ToSqsRoute
{
  @Inject
  ProducerTemplate producerTemplate;
  @Inject
  S3ToSqsRoute s3ToSqsRoute;
  @ConfigProperty(name="sqs-queue-name")
  String queueName;

  private static final String RANDOM = new Random().ints('a', 'z')
    .limit(5)
    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
    .toString();
  private final AmazonSQS amazonSqsClient = AmazonSQSClientBuilder.defaultClient();

  @Test
  //@Order(10)
  public void testsendMessageToSQS() throws Exception
  {
    Main main = new Main();
    main.configure().addRoutesBuilder(s3ToSqsRoute);
    main.run();
    Thread.sleep(2000);
    producerTemplate.sendBody("aws2-s3://" + s3ToSqsRoute.getS3BucketName() + "?keyName=test&autoCreateBucket=true", "<test/>");
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
