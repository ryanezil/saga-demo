package com.redhat.fuse.demo;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service("purchaseService")
public class PurchaseServiceImpl implements PurchaseService {
	
    static final Logger LOG = LoggerFactory.getLogger(PurchaseServiceImpl.class);

	@Override
	public void generateError(@Header("quantity") Integer quantity) {
		
		if(quantity == 27) {
			
			LOG.info("Throwing exception for testing purposes...");
			throw new IllegalStateException("Forced exception when quantity equals 27 for testing purposes");
		}
		
		
	}



/* BEAN BINDING
 * Useful documentation link: https://camel.apache.org/manual/latest/bean-binding.html
 */    
    // the return type must be void when there is only a single parameter of the type org.apache.camel.Exchange
    // public void doSomething(Exchange exchange) {
    //     // process the exchange
    //     exchange.getIn().setHeader("my-header", new Random(System.currentTimeMillis()).nextInt(50));
    //     exchange.getIn().setBody("Bye World");
    // }

}