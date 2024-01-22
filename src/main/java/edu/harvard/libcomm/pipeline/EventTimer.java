package edu.harvard.libcomm.pipeline;

import java.util.Date;
import java.util.EventObject;

import org.apache.camel.Exchange;
import org.apache.camel.support.EventNotifierSupport;
import org.apache.camel.impl.event.ExchangeCompletedEvent;
import org.apache.camel.impl.event.ExchangeSentEvent;
import org.apache.log4j.Logger;

public class EventTimer extends EventNotifierSupport {

	protected Logger log = Logger.getLogger(EventTimer.class); 
	 
    public void notify(EventObject event) throws Exception {
 
		  if (event instanceof ExchangeSentEvent) {
		      ExchangeSentEvent sent = (ExchangeSentEvent) event;
		      // log.info("Took " + sent.getTimeTaken() + " millis to send to external system : " + sent.getEndpoint());
		  }

		  if (event instanceof ExchangeCompletedEvent) {;
		      ExchangeCompletedEvent exchangeCompletedEvent = (ExchangeCompletedEvent) event;
		      Exchange exchange = exchangeCompletedEvent.getExchange();
		      String routeId = exchange.getFromRouteId();
		      Date created = ((ExchangeCompletedEvent) event).getExchange().getProperty(Exchange.CREATED_TIMESTAMP, Date.class);
		      // calculate elapsed time
		      Date now = new Date();
		      long elapsed = now.getTime() - created.getTime();
		      log.info("Took " + elapsed + " millis for the exchange on the route : " + routeId);
		  }
    }
 
    public boolean isEnabled(EventObject event) {
        // we only want the sent events
        return (event instanceof ExchangeSentEvent || event instanceof ExchangeCompletedEvent) ;
    }
 
    protected void doStart() throws Exception {
		setIgnoreCamelContextEvents(true);
		setIgnoreServiceEvents(true);
		setIgnoreRouteEvents(true);
		setIgnoreExchangeCreatedEvent(true);
		setIgnoreExchangeCompletedEvent(false);
		setIgnoreExchangeFailedEvents(true);
		setIgnoreExchangeRedeliveryEvents(true);
		setIgnoreExchangeSentEvents(false);
    }
 
    protected void doStop() throws Exception {
        // noop
    }
 
}

