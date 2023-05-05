package fr.simplex_software.quarkus.camel.integrations.s3;

import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import io.quarkus.runtime.*;
import io.quarkus.runtime.annotations.*;
import org.apache.camel.main.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.inject.*;
import javax.inject.*;
import java.util.*;

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
