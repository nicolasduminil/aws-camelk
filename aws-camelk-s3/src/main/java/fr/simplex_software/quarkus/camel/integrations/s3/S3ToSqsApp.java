package fr.simplex_software.quarkus.camel.integrations.s3;

import com.amazonaws.services.s3.*;
import io.quarkus.runtime.*;
import io.quarkus.runtime.annotations.*;
import org.apache.camel.main.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.inject.*;
import javax.inject.*;

@QuarkusMain
public class S3ToSqsApp implements QuarkusApplication
{
  private Main main;
  @ConfigProperty(name = "illegal-state-exception-msg")
  String illegalStateExceptionMsg;
  @Inject
  S3ToSqsRoute s3ToSqsRoute;


  @Override
  public int run(String... args) throws Exception
  {
    main = new Main();
    main.configure().addRoutesBuilder(s3ToSqsRoute);
    main.run();
    return 0;
  }

  @Produces
  @Named("s3BucketName")
  public String getS3BucketName()
  {
    return AmazonS3ClientBuilder.standard().build().listBuckets().stream()
      .filter(b -> b.getName().startsWith("mys3")).findFirst()
      .orElseThrow(() -> new IllegalStateException(illegalStateExceptionMsg)).getName();
  }
}
