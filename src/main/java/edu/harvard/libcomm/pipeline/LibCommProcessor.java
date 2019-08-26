package edu.harvard.libcomm.pipeline;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.log4j.Logger;

import edu.harvard.libcomm.message.LibCommMessage;
import gov.loc.marc.CollectionType;
import gov.loc.mods.v3.ModsCollection;

public class LibCommProcessor implements Processor {

	protected Logger log = Logger.getLogger(LibCommProcessor.class); 
	protected LibCommMessage libCommMessage = null;
	private   IProcessor processor;

	/**
	 * Invoked by Camel to process the message 
	 * @param  exchange
	 * @throws Exception
	 */
	public synchronized void process(Exchange exchange) throws Exception {	
		
		JAXBContext context = initContext();

		if (null == processor) {
			log.fatal("No processor defined for message");
			throw new Exception("No processor defined for message");
		}

		Message message = exchange.getIn();
		InputStream messageIS = MessageUtils.readMessageBody(message);			
		libCommMessage = unmarshalMessage(context, messageIS);
		try {
			processor.processMessage(libCommMessage);			
		} catch (Exception e) {
			log.info("Error processing message. Route:" + exchange.getFromRouteId() + "; Exception:" + e.getMessage());
			log.error("Error processing message. Route:" + exchange.getFromRouteId() + "; Id:" + exchange.getExchangeId(), e);
			throw e;
		}
		
		String messageString = marshalMessage(context, libCommMessage);
		log.trace("MESSAGE BODY OUT: " + messageString);
	    message.setBody(messageString);
	    exchange.setOut(message);
	}

	/**
	 * Get the JAXBContext to be used for marshalling and unmarshalling
	 * @return populated context
	 */
    protected JAXBContext initContext() throws JAXBException {
		return JAXBContext.newInstance(LibCommMessage.class,ModsCollection.class,CollectionType.class);
    }	
	
	/**
	 * Unmarshall message from XML to object
	 * @param  context       JAXBContext required for marshalling/unmarshalling
	 * @param  is            input stream with XML to unmarshall
	 * @return               populated LibCommMessage
	 * @throws JAXBException 
	 */
	protected LibCommMessage unmarshalMessage (JAXBContext context, InputStream is) throws JAXBException {
 		Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
		libCommMessage = (LibCommMessage) jaxbUnmarshaller.unmarshal(is);
		return libCommMessage;		
	}

	/**
	 * Marshall message from an object to XML
	 * @param  context       JAXBContext required for marshalling/unmarshalling
	 * @param  libCommMessage LibCommMessage to marshall
	 * @return                XML representation of the object
	 * @throws JAXBException  
	 */
	protected String marshalMessage (JAXBContext context, LibCommMessage libCommMessage) throws JAXBException {
		StringWriter sw = new StringWriter();
		Marshaller jaxbMarshaller = context.createMarshaller();
		// Uncomment this to get pretty-printed output.
		// jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(libCommMessage, sw);
		return sw.toString();
	}

	public void setProcessor(IProcessor p) {
		this.processor = p;
	}		

	public IProcessor getProcessor() {
		return this.processor;
	}
}
