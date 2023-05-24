package fr.simplex_software.quarkus.camel.integrations.s3;

import io.quarkus.runtime.*;
import io.quarkus.runtime.annotations.*;
import org.apache.camel.main.*;
import org.eclipse.microprofile.config.inject.*;

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
}
