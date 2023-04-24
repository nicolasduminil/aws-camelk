package fr.simplex_software.quarkus.camel.integrations.file;

import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.apache.camel.component.aws2.s3.*;

import javax.enterprise.context.*;
import javax.inject.*;
import java.io.*;
import java.util.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class FileToS3Route extends RouteBuilder
{
  private static final String RANDOM = new Random().ints('a', 'z')
    .limit(5)
    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
    .toString();
  private static final String AWS_S3_URI_TEMPLATE = "aws2-s3://myBucket%s?autoCreateBucket=true&useDefaultCredentialsProvider=true&prefix=hello.txt";
  private static final String AWS_S3_URI = String.format(AWS_S3_URI_TEMPLATE, RANDOM);
  @Inject
  private CamelContext context;

  public void configure() throws Exception
  {
    context.addComponent("aws2-s3", new AWS2S3Component(context));
    onException(IOException.class)
      .handled(true)
      .log("IOException occurred due: ${exception.message}");
      /*.transform().simple("Error ${exception.message}")
      .to("mock:error");*/
    from("file://tmp/input?delete=true&idempotent=true&bridgeErrorHandler=true")
      .setHeader(AWS2S3Constants.KEY, simple("key"))
      .to(aws2S3("mybucket" + RANDOM).autoCreateBucket(true).useDefaultCredentialsProvider(true));
  }
}
