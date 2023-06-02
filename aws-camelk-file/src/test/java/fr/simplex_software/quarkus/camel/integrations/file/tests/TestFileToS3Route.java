package fr.simplex_software.quarkus.camel.integrations.file.tests;

import io.quarkus.test.junit.*;
import org.apache.camel.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

import javax.inject.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestFileToS3Route
{
  @Inject
  S3Client s3client;
  @EndpointInject("file://tmp/input?delete=true&idempotent=true")
  ProducerTemplate producerTemplate;
  private static List<Bucket> buckets;

  @BeforeAll
  public void beforeAll()
  {
    buckets = s3client.listBuckets(ListBucketsRequest.builder().build()).buckets();
  }

  @AfterAll
  public void afterAll()
  {
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
    assertThat(buckets.stream().anyMatch(b -> b.name().startsWith("mys3"))).isTrue();
  }

  @Test
  public void testRemoveS3Bucket()
  {
    String bucketName = buckets.stream().filter(b -> b.name().startsWith("mys3")).findFirst().orElseThrow().name();
    assertThat(bucketName).isNotNull();
    List<S3Object> s3ObjectList = s3client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build()).contents();
    s3ObjectList.forEach(s3o -> s3client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(s3o.key()).build()));
    s3client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
  }
}
