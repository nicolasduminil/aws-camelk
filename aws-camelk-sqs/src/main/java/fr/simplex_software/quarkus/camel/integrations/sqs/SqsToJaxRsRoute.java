package fr.simplex_software.quarkus.camel.integrations.sqs;

import fr.simplex_software.quarkus.camel.integrations.jaxb.*;
import org.apache.camel.*;
import org.apache.camel.builder.*;
import org.apache.camel.model.dataformat.*;
import org.eclipse.microprofile.config.inject.*;
import software.amazon.awssdk.services.sqs.*;

import javax.annotation.*;
import javax.enterprise.context.*;
import javax.inject.*;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.*;

@ApplicationScoped
public class SqsToJaxRsRoute extends RouteBuilder
{
  @ConfigProperty(name="sqs-queue-name")
  String queueName;
  @ConfigProperty(name="rest-uri")
  String uri;
  private JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(true);
  @Inject
  SqsClient sqsClient;

  @PostConstruct
  public void postConstruct()
  {
    jaxbDataFormat.setContextPath("fr.simplex_software.quarkus.camel.integrations.jaxb");
  }

  @Override
  public void configure() throws Exception
  {
    from(aws2Sqs(queueName).useDefaultCredentialsProvider(true))
      .unmarshal(jaxbDataFormat)
      .marshal().json(JsonLibrary.Jsonb)
      .setHeader(Exchange.HTTP_METHOD, constant("POST"))
      .to(http(uri));
  }
}
