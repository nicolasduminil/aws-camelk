package fr.simplex_software.quarkus.camel.integration.sqs;

import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.eclipse.microprofile.config.inject.*;

import javax.enterprise.context.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class SqsToJaxRsRoute extends RouteBuilder
{
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  @ConfigProperty(name="rest-uri")
  String uri;

  @Override
  public void configure() throws Exception
  {
    from(aws2Sqs(queueName).useDefaultCredentialsProvider(true))
      .log(LoggingLevel.INFO, "*** Sending: ${body}")
      /*.setHeader(Exchange.HTTP_METHOD, constant("POST"))
      .to(http(uri))*/;
  }
}
