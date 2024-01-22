package edu.harvard.libcomm.pipeline;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.AggregationStrategy;
import org.apache.log4j.Logger;

import gov.loc.marc.CollectionType; /* Required? */
import gov.loc.mods.v3.ModsCollection;

import edu.harvard.libcomm.message.LibCommMessage;

public class MODSAggregatorStrategy implements AggregationStrategy {
 
	protected Logger log = Logger.getLogger(MODSAggregatorStrategy.class); 
	protected LibCommMessage libCommMessage = null;
    
    /**
     * Combine two LibComMessages into a new LibComMesasge with the payload data concatenated
     * @param  oldExchange message 1
     * @param  newExchange message 2
     * @return             updated message
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
 
        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);

        try {
	        JAXBContext context = initContext();
	        LibCommMessage oldMessage = unmarshalMessage(context, new ByteArrayInputStream((oldBody).getBytes("UTF-8")));
	        LibCommMessage newMessage = unmarshalMessage(context, new ByteArrayInputStream((newBody).getBytes("UTF-8")));
	        String oldData = oldMessage.getPayload().getData();
	        String newData = newMessage.getPayload().getData();
	        oldMessage.getPayload().setData(oldData + newData);
	        String outboundMessage = marshalMessage(context,oldMessage);
	        oldExchange.getIn().setBody(outboundMessage);	        

	        /* Set the approx. size of the message in a header so the information can
	           be used to keep message size below AWS 256k limit */
	        oldExchange.getIn().setHeader("messageLength", outboundMessage.length()); 

	    } catch (Exception e) {
	    	log.error(e);
	    	e.printStackTrace();
	    }
        return oldExchange;
    }

    /**
     * When done combining messages, wrap the payload (a list of MODS XML objects) in a MODSCollection
     * @param exchange 
     */
    @Override
    public void onCompletion(Exchange exchange) {
        try {
	        JAXBContext context = initContext();
	        String body = exchange.getIn().getBody(String.class);
	        LibCommMessage message = unmarshalMessage(context, new ByteArrayInputStream((body).getBytes("UTF-8")));
	        String data = message.getPayload().getData();
	        data = "<modsCollection xmlns=\"http://www.loc.gov/mods/v3\">" + data + "</modsCollection>";
	        message.getPayload().setData(data);
			message.getPayload().setFormat("MODS");
			String uid = UUID.randomUUID().toString();
			LibCommMessage.History hist = new LibCommMessage.History();
			LibCommMessage.History.Event event = new LibCommMessage.History.Event();
			event.setMessageid(uid);
			hist.getEvent().add(event);
			message.setHistory(hist);

	        String outboundMessage = marshalMessage(context, message);
	        exchange.getIn().setBody(outboundMessage);
	    } catch (Exception e) {
	    	log.error(e);
	    	e.printStackTrace();
	    }
    }

    private JAXBContext initContext() throws JAXBException {
		return JAXBContext.newInstance(LibCommMessage.class,ModsCollection.class,CollectionType.class);
    }	

	private LibCommMessage unmarshalMessage (JAXBContext context, InputStream is) throws JAXBException {
 		Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
		libCommMessage = (LibCommMessage) jaxbUnmarshaller.unmarshal(is);
		return libCommMessage;		
	}

	private String marshalMessage (JAXBContext context, LibCommMessage libCommMessage) throws JAXBException {
		StringWriter sw = new StringWriter();
		Marshaller jaxbMarshaller = context.createMarshaller();
		jaxbMarshaller.marshal(libCommMessage, sw);
		return sw.toString();
	}


}
