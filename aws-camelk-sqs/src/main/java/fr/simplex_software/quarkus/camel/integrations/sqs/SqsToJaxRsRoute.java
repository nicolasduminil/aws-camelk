package fr.simplex_software.quarkus.camel.integrations.sqs;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;
import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.apache.camel.model.dataformat.*;
import org.eclipse.microprofile.config.inject.*;

import javax.annotation.*;
import javax.enterprise.context.*;

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
    jaxbDataFormat.setPartClass(MoneyTransfer.class.getName());
  }

  @Override
  public void configure() throws Exception
  {
    from(aws2Sqs(queueName).useDefaultCredentialsProvider(true))
      .log(LoggingLevel.INFO, "*** Sending: ${body}")
      .bean(UnmarshalXmlFragment.class)
      //.unmarshal(jaxbDataFormat)
      .log (LoggingLevel.INFO, "*** Have unmarshalled from XML to Object: ${body.reference}, ${body.amount}")
      .marshal().json(JsonLibrary.Jsonb)
      .log (LoggingLevel.INFO, "*** Have marshalled from Object to JSON: ${body}")
      .setHeader(Exchange.HTTP_METHOD, constant("POST"))
      .to(http(uri));
  }
}
