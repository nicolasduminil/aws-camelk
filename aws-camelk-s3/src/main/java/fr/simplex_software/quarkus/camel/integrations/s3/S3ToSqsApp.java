package fr.simplex_software.quarkus.camel.integrations.s3;

import org.apache.camel.main.*;

public class S3ToSqsApp
{
  public static void main (String... args) throws Exception
  {
    new Main().run(args);
  }
}
