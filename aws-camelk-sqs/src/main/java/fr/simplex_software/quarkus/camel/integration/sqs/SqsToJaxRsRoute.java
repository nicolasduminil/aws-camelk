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
  private Map<String, String> namespaceRef = Map.of("http://www.simplex-software.fr/money-transfer", "mon");

  /*@PostConstruct
  public void postConstruct()
  {
    jaxbDataFormat.setContextPath(MoneyTransfer.class.getPackageName());
    if (getCamelContext() == null)
      System.out.println ("### Camel context is null");
    Registry registry = getCamelContext().getRegistry();
    if (registry == null)
      System.out.println ("### Registry is null");
    if (namespaceRef == null)
      System.out.println ("### namespaceRef is null");
  }*/

  @Override
  public void configure() throws Exception
  {
    this.getCamelContext().getRegistry().bind("namespaceRef", namespaceRef);
    jaxbDataFormat.setNamespacePrefixRef("namespaceRef");
    from(aws2Sqs(queueName).useDefaultCredentialsProvider(true))
      .log(LoggingLevel.INFO, "*** Sending: ${body}")
      .unmarshal(jaxbDataFormat)
      .log (LoggingLevel.INFO, "*** Have unmarshalled: ${body}")
      /*.setHeader(Exchange.HTTP_METHOD, constant("POST"))
      .to(http(uri))*/;
  }
}
