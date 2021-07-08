package com.redhat.fuse.order;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.SagaPropagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderRouter extends RouteBuilder {

	@Autowired
	OrderManagerService orderManagerService;

	@Override
	public void configure() throws Exception {

//        this.getContext().addService(new InMemorySagaService());

        restConfiguration()
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "ORDERS REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "camel/")
                .apiProperty("api.path", "/")
                .apiProperty("host", "")
                .apiContextRouteId("doc-api")
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true");

		// onException(Exception.class)
		// .handled(false)
		// .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
		// .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
		// .setBody(constant("Order Not Created"))
		// .to("direct:errorHandlingEndpointOrderRouter");

		// from("direct:errorHandlingEndpointOrderRouter")
		// 	.routeId("errorHandlingEndpointOrderRouterRoute")
		// 	.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
		// 	.setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
		// 	.setBody(constant("Order Not Created!."))
		// 	.log("## ERROR MANAGED OrderRouter ##");


		rest("/orders").description("Orders endpoint")
		.post("/{itemId}/{quantity}").outType(Order.class).description("New order for item")
			.route().routeId("newOrderRestRoute")
			.log("Called 'newOrderRestRoute'")
			.to("direct:newOrder");


		from("direct:newOrder")
		  .routeId("newOrderRoute")
		  .setHeader("orderId", method(orderManagerService, "getNewOrderId"))
		  .saga()
		  //.propagation(SagaPropagation.MANDATORY)		  // A saga must be already present. The existing saga is joined.
		  .propagation(SagaPropagation.SUPPORTS)		  //If a saga already exists, then join it.	  
		  .compensation("direct:cancelOrder")
		  .completion("direct:completeOrder")
		  .bean(orderManagerService, "newOrder")
		  .option("orderId", simple("${header.orderId}"))	// Notice that '.option' is always evaluated before '.bean' executions		  
		  .log("Order ${body.orderId} created");

		
		from("direct:cancelOrder")
		  .routeId("cancelOrderRoute")
		  .transform().header("orderid")	// Retrieve the orderId
		  .bean(orderManagerService, "cancelOrder")
		  .log("Order ${body} cancelled");

		
		from("direct:completeOrder")
//.to("log:DEBUG?showBody=true&showHeaders=true")		
		  .routeId("completeOrderRoute")
		  .transform().header("orderId")	// Retrieve the orderId
		  .bean(orderManagerService, "notifyOrder")
		  //.to("jms:notifyOrder")
		  .log("Order ${body} notified to customer");

	}

}
