package fr.simplex_software.quarkus.camel.integrations.file;

import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.apache.camel.component.aws2.s3.*;
import org.apache.camel.component.file.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.context.*;
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
  @ConfigProperty(name="inBox")
  String inBox;
  @ConfigProperty(name="s3Name")
  String s3Name;
  @ConfigProperty(name="exMsg")
  String exMsg;
  @ConfigProperty(name="validation-failure-msg")
  String failureMsg;

  public void configure() throws Exception
  {
    onException(IOException.class)
      .handled(true)
      .log(LoggingLevel.ERROR, exMsg + " ${exception.message}");
    fromF("file://%s?include=.*.xml&delete=true&idempotent=true&bridgeErrorHandler=true", inBox)
      .doTry()
        .to("validator:xsd/money-transfers.xsd")
        .setHeader(AWS2S3Constants.KEY, header(FileConstants.FILE_NAME))
        .to(aws2S3(s3Name + RANDOM).autoCreateBucket(true).useDefaultCredentialsProvider(true))
      .doCatch(ValidationException.class)
        .log(LoggingLevel.ERROR, failureMsg + " ${exception.message}")
      .doFinally()
      .end();
  }
}
