package edu.harvard.libcomm.pipeline.delete;

import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;

import org.apache.log4j.Logger;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.LibCommProcessor;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

/* Given a message that contains a raw DELETE MarcXML record (not wrapped in a LibCommMessage),
   extract the record ID from a header ("deleteID") on the exchange. Use that ID to create
   a delete message */

public class DeleteFileAlma extends LibCommProcessor implements Processor {
	protected Logger log = Logger.getLogger(DeleteFileAlma.class);

	public synchronized void process(Exchange exchange) throws Exception {

		JAXBContext context = initContext();

		/* Get the contents and header of the message */
		Message message = exchange.getIn();
		String recordId = message.getHeader("deleteID", String.class);
		InputStream messageIS = MessageUtils.readMessageBody(message);

		/* Create the delete message */
		LibCommMessage lcmessage = new LibCommMessage();
		Payload payload = new Payload();
		payload.setFormat("DELETE_ID");
		payload.setSource("DELETE");
		payload.setData(recordId);
		lcmessage.setCommand("DELETE");
    	lcmessage.setPayload(payload);

		String messageString = marshalMessage(context, lcmessage);
		log.trace("MESSAGE BODY OUT: " + messageString);
	    message.setBody(messageString);
	    exchange.setOut(message);
	}

}
