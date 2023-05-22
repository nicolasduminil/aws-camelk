package fr.simplex_software.quarkus.camel.integration.sqs;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;
import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.apache.camel.model.dataformat.*;
import org.apache.camel.spi.*;
import org.eclipse.microprofile.config.inject.*;

import javax.annotation.*;
import javax.enterprise.context.*;
import javax.enterprise.inject.*;
import javax.inject.*;

import java.util.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class SqsToJaxRsRoute extends RouteBuilder
{
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  @ConfigProperty(name="rest-uri")
  String uri;
  private JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(true);

  @PostConstruct
  public void postConstruct()
  {
    jaxbDataFormat.setContextPath(MoneyTransfer.class.getPackageName());
  }

  @Override
  public void configure() throws Exception
  {
    from(aws2Sqs(queueName).useDefaultCredentialsProvider(true))
      .log(LoggingLevel.INFO, "*** Sending: ${body}")
      .unmarshal(jaxbDataFormat)
      .log (LoggingLevel.INFO, "*** Have unmarshalled: ${body}")
      /*.setHeader(Exchange.HTTP_METHOD, constant("POST"))
      .to(http(uri))*/;
  }
}
