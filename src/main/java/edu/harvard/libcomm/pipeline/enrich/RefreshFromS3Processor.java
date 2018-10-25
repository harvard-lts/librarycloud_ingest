package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.MessageUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * Created by mjv162 on 10/23/2018.
 */
public class RefreshFromS3Processor implements Processor {

    public void process(Exchange exchange) throws Exception {

        String almaId = exchange.getIn().getBody(String.class);
        String url = Config.getInstance().MARC_S3_URL + "/" + almaId;
        //System.out.println("url: " + url);
        URI uri = new URI(url);
        String marcxml = IOUtils.toString(uri.toURL().openStream(), "UTF-8");
        //System.out.println("MARCXML: " + marcxml);

        LibCommMessage message = new LibCommMessage();
        message.setCommand("NORMALIZE");
        LibCommMessage.Payload payload = new LibCommMessage.Payload();
        payload.setSource("ALMA");
        payload.setFormat("mods");
        payload.setData(marcxml);
        message.setPayload(payload);
        String marshalledMessage = MessageUtils.marshalMessage(message);
        exchange.getIn().setBody(marshalledMessage);
    }

    public RefreshFromS3Processor() {
    }
}
