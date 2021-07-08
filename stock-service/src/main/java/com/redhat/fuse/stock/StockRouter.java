package com.redhat.fuse.stock;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.impl.saga.InMemorySagaService;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockRouter extends RouteBuilder {

	@Autowired
	StockService stockService;

	@Override
	public void configure() throws Exception {

        this.getContext().addService(new InMemorySagaService());

        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "STOCK REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true");



		onException(IllegalStateException.class)
		.handled(true)
		//  .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
		//  .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
		//  .setBody(constant("Credit not reserved"))
		.to("direct:errorHandlingEndpointCreditRouter");
		

		from("direct:errorHandlingEndpointCreditRouter")
			.routeId("errorHandlingEndpointCreditRouterRoute")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(567))  // non standard code
			.setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
			//.setBody(constant("Credit not reserved!."))
			.transform().simple("Returning message: ${exception.message}")
			.log("## ERROR HANDLED CreditRouter ##");		


		rest("/stock/reservations").description("Manage Items Stock reservations")
		.produces("application/json")
		.delete("/{reserveId}").description("Delete stock reservation")
			.to("direct:returnStock")
		.post("/{itemId}/{quantity}").outType(StockReserve.class).description("Reserve stock quantity for the item")
			.param().name("itemId").description("Item identification").type(RestParamType.path).endParam()
			.param().name("quantity").description("Item quantity").type(RestParamType.path).endParam()
			.route().routeId("reserveStockRestRoute")
			.log("Called 'reserveStockRestRoute'")
			.to("direct:reserveStock");


		from("direct:reserveStock")
		  .routeId("reserveStockRoute")
		  .log("Called 'reserveStockRoute'")
		  .bean(stockService, "generateReserveId")
		  // This option is also valid >>> .setHeader("reserveId",simple("${body}"))
		  .setHeader("reserveId",body())
		  .to("direct:creditReservation");


		from("direct:creditReservation")
		.routeId("creditReservationRoute")
			.saga()
			// This route endpoint can be called outside of the Saga, by setting the propagation mode to SUPPORTS.
			.propagation(SagaPropagation.SUPPORTS)    	  //If a saga already exists, then join it.

			/* 'option' allows you to save properties of the current exchange in order
			 * to reuse them in a compensation or completion callback route.
			 */
			.option("reserveId", body()) 
			.compensation("direct:returnStock")
			
			.bean(stockService, "reserveStock")
/*
			.doTry()
			   .bean(stockService, "reserveStock")
			.doCatch(IllegalStateException.class)
			   .log("######## CATCH ####")
			   .throwException(new RuntimeException("exception... :p "))
			.endDoTry()
*/
			.log("The quantity ${header.quantity} has been reserved. Body content is >> ${body}");
 

		from("direct:returnStock")
		.routeId("returnStockRoute")
		//.to("log:DEBUG?showBody=true&showHeaders=true")
		  .transform(header("reserveId")) // retrieve the reserveId option from headers and sets it as OUT message $body
		  .bean(stockService, "returnStock")
		  .log("Stock for reserveId ${body} compensated");		  
	}

}
