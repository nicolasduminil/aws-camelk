package fr.simplex_software.quarkus.camel.integrations.jaxrs;

import fr.simplex_software.quarkus.camel.integrations.api.*;
import fr.simplex_software.quarkus.camel.integrations.jaxb.*;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.faulttolerance.exceptions.*;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import org.eclipse.microprofile.openapi.annotations.responses.*;

import javax.enterprise.context.*;
import javax.inject.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

import static javax.ws.rs.core.MediaType.*;

@ApplicationScoped
@Path("xfer")
public class MoneyTransferResource
{
  @Inject
  MoneyTransferFacade moneyTransferFacade;

  @GET
  @Path("live")
  @Produces(TEXT_PLAIN)
  public Response testIfLive()
  {
    return Response.ok("Live").build();
  }

  @GET
  @Path("ready")
  @Produces(TEXT_PLAIN)
  public Response testIfReady()
  {
    return Response.ok("Ready").build();
  }

  @GET
  @Produces(APPLICATION_JSON)
  @Operation(description = "Get the money transfer orders list")
  @APIResponse(responseCode = "404", description = "No money transfer orders found",
    content = @Content(mediaType = APPLICATION_JSON))
  @APIResponseSchema(value = MoneyTransfer.class, responseDescription = "Money transfer orders list", responseCode = "200")
  @Metered(name = "Get money transfer orders", unit = MetricUnits.MINUTES, description = "Metric to monitor the frequency of the getMoneyTransferOrders endpoint invocations", absolute = true)
  @Timeout(250)
  public Response getMoneyTransferOrders()
  {
    GenericEntity<List<MoneyTransfer>> listGenericEntity = new GenericEntity<>(moneyTransferFacade.getMoneyTransferOrders()) {};
    return Response.ok().entity(listGenericEntity).build();
  }

  @GET
  @Path("{ref}")
  @Produces(APPLICATION_JSON)
  @Operation(description = "Get the money transfer order identified by reference")
  @APIResponse(responseCode = "404", description = "No such a money transfer order found",
    content = @Content(mediaType = APPLICATION_JSON))
  @APIResponseSchema(value = MoneyTransfer.class, responseDescription = "Money transfer order found", responseCode = "200")
  @Metered(name = "Get money transfer order", unit = MetricUnits.MINUTES, description = "Metric to monitor the frequency of the getMoneyTransferOrder endpoint invocations", absolute = true)
  @Fallback(fallbackMethod = "fallbackOfGetMoneyTransferOrder")
  public Response getMoneyTransferOrder(@PathParam("ref") String reference)
  {
    return Response.ok().entity(moneyTransferFacade.getMoneyTransferOrder(reference).orElseThrow()).build();
  }

  @POST
  @Consumes(APPLICATION_JSON)
  @Operation(description = "Create a new money transfer order")
  @APIResponse(responseCode = "500", description = "An internal server error has occurred",
    content = @Content(mediaType = APPLICATION_XML))
  @APIResponseSchema(value = MoneyTransfer.class, responseDescription = "The new money transfer order has been created", responseCode = "201")
  @Metered(name = "Create money transfer order", unit = MetricUnits.MINUTES, description = "Metric to monitor the frequency of the createMoneyTransferOrder endpoint invocations", absolute = true)
  @Timeout(250)
  @Retry(retryOn = TimeoutException.class, maxRetries = 2)
  public Response createMoneyTransferOrder(MoneyTransfer moneyTransfer, @Context UriInfo uriInfo)
  {
    String ref = moneyTransferFacade.createMoneyTransferOrder(moneyTransfer);
    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
    uriBuilder.path(ref);
    return Response.created(uriBuilder.build()).build();
  }

  @PUT
  @Path("{ref}")
  @Consumes(APPLICATION_JSON)
  @Operation(description = "Update a money transfer order")
  @APIResponse(responseCode = "404", description = "The money transfer order does not exist",
    content = @Content(mediaType = APPLICATION_JSON))
  @APIResponseSchema(value = MoneyTransfer.class, responseDescription = "The Money transfer orderhas been updated", responseCode = "200")
  @Metered(name = "Update a money transfer order", unit = MetricUnits.MINUTES, description = "Metric to monitor the frequency of the updateMoneyTransferOrder endpoint invocations", absolute = true)
  @CircuitBreaker(successThreshold = 5, requestVolumeThreshold = 4, failureRatio = 0.75, delay = 1000)
  public Response updateMoneyTransferOrder(@PathParam("ref") String ref, MoneyTransfer moneyTransfer)
  {
    moneyTransferFacade.updateMoneyTransferOrder(ref, moneyTransfer);
    return Response.accepted().build();
  }

  @DELETE
  @Path("{ref}")
  @Operation(description = "Delete a money transfer order")
  @APIResponse(responseCode = "404", description = "The money transfer order does not exist",
    content = @Content(mediaType = APPLICATION_JSON))
  @APIResponseSchema(value = MoneyTransfer.class, responseDescription = "The money transfer order has been deleted", responseCode = "200")
  @Metered(name = "Delete a money transfer order", unit = MetricUnits.MINUTES, description = "Metric to monitor the frequency of the deleteMoneyTransferOrders endpoint invocations", absolute = true)
  public Response deleteMoneyTransferOrder(@PathParam("ref") String reference)
  {
    moneyTransferFacade.deleteMoneyTransferOrder(reference);
    return Response.ok().build();
  }

  public Response fallbackOfGetMoneyTransferOrder(String reference)
  {
    return Response.noContent().build();
  }
}
