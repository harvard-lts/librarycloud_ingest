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
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.message.LibCommMessage.History;
import edu.harvard.libcomm.message.LibCommMessage.History.Event;

public class MARCXMLRawAggregatorStrategy implements AggregationStrategy {

	protected Logger log = Logger.getLogger(MARCXMLRawAggregatorStrategy.class);
	protected LibCommMessage libCommMessage = null;
    private String source;

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
        //log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath());
        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        String body = oldBody + newBody;
        oldExchange.getIn().setBody(body);

        /* Set the approx. size of the message in a header so the information can
           be used to keep message size below AWS 256k limit */
        oldExchange.getIn().setHeader("messageLength", body.length());

        return oldExchange;
    }

    /**
     * When done combining messages, wrap the payload (a list of MODS XML objects) in a MODSCollection
     * @param exchange
     */
    @Override
    public void onCompletion(Exchange exchange) {
        String body = exchange.getIn().getBody(String.class);
        body = "<collection xmlns=\"http://www.loc.gov/MARC21/slim\">" + body + "</collection>";

        LibCommMessage lcmessage = new LibCommMessage();
        Payload payload = new Payload();
        payload.setFormat("MARCXML");
        payload.setSource(getSource());
        payload.setData(body);
        //lcmessage.setCommand("normalize-marcxml");
        lcmessage.setPayload(payload);
        String uid = UUID.randomUUID().toString();
        History hist = new History();
        Event event = new Event();
        event.setMessageid(uid);
        hist.getEvent().add(event);
        lcmessage.setHistory(hist);

        try {
            exchange.getIn().setBody(MessageUtils.marshalMessage(lcmessage));
        } catch (JAXBException e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
