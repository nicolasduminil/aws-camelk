package fr.simplex_software.quarkus.camel.integrations.file.tests;

import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
public class TestFileToS3Route
{
  @EndpointInject("file://tmp/input?delete=true&idempotent=true")
  ProducerTemplate producerTemplate;

  @Test
  public void testStoreFileToS3() throws InterruptedException
  {
    producerTemplate.sendBodyAndHeader("Test", Exchange.FILE_NAME, "test.txt");
    Thread.sleep(2000);
    AmazonS3 s3client = AmazonS3ClientBuilder.standard().build();
    List<Bucket> buckets = s3client.listBuckets();
    assertThat(buckets).isNotNull();
    assertThat(buckets.size()).isGreaterThan(0);
    assertThat(buckets.stream().anyMatch(b -> b.getName().startsWith("mys3"))).isTrue();
  }
}
