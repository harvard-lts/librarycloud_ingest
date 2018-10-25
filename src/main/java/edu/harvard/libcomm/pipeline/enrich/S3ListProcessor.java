package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.pipeline.solr.SolrProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by mjv162 on 10/24/2018.
 */
public class S3ListProcessor implements IProcessor {
    protected Logger log = Logger.getLogger(SolrProcessor.class);

    private Integer commitWithinTime = -1;

    @Override
    /*
    public void processMessage(LibCommMessage libCommMessage) throws Exception {
        try {
            String s3Ids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/almaholdings2s3list",null);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
*/
    public void process(Exchange exchange) throws Exception {

        Message message = exchange.getIn();
        InputStream messageIS = MessageUtils.readMessageBody(message);
        LibCommMessage libCommMessage = MessageUtils.unmarshalLibCommMessage(messageIS);
        String s3Ids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/almaholdings2s3list",null);
        LibCommMessage message = new LibCommMessage();
        message.setCommand("NORMALIZE");
        LibCommMessage.Payload payload = new LibCommMessage.Payload();
        payload.setSource("ALMA");
        payload.setFormat("text");
        payload.setData(s3Ids);
        message.setPayload(payload);
        //String marshalledMessage = MessageUtils.marshalMessage(message);
        exchange.getIn().setBody(message);
    }


    public S3ListProcessor() {
    }
}
