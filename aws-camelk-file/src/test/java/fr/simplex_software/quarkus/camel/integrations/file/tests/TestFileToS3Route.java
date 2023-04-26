package fr.simplex_software.quarkus.camel.integrations.file.tests;

import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestFileToS3Route
{
  @EndpointInject("file://tmp/input?delete=true&idempotent=true")
  ProducerTemplate producerTemplate;
  private static AmazonS3 amazonS3Client;
  private static List<Bucket> buckets;

  @BeforeAll
  public static void beforeAll()
  {
    amazonS3Client = AmazonS3ClientBuilder.standard().build();
    buckets = amazonS3Client.listBuckets();
  }

  @AfterAll
  public static void afterAll()
  {
    amazonS3Client = null;
    buckets = null;
  }

  @Test
  @Order(10)
  public void testStoreFileToS3() throws InterruptedException
  {
    producerTemplate.sendBodyAndHeader("Test", Exchange.FILE_NAME, "test.txt");
    Thread.sleep(2000);
    assertThat(buckets).isNotNull();
    assertThat(buckets.size()).isGreaterThan(0);
    assertThat(buckets.stream().anyMatch(b -> b.getName().startsWith("mys3"))).isTrue();
  }

  @Test
  public void testRemoveS3Bucket()
  {
    String bucketName = buckets.stream().filter(b -> b.getName().startsWith("mys3")).findFirst().orElseThrow().getName();
    assertThat(bucketName).isNotNull();
    amazonS3Client.listObjects(bucketName).getObjectSummaries().forEach(os -> amazonS3Client.deleteObject(bucketName, os.getKey()));
    amazonS3Client.deleteBucket(bucketName);
  }
}
