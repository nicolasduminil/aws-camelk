package fr.simplex_software.quarkus.camel.integrations.file;

import org.apache.camel.main.*;

public class FileToS3App
{
  public static void main (String... args) throws Exception
  {
    new Main().run(args);
  }
}
