package edu.harvard.libcomm.pipeline.ted;

import com.mongodb.client.MongoCursor;
import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.pipeline.ted.TedReader;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import javax.xml.bind.JAXBException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by mjv162 on 9/7/2018.
 */
public class TedIterator implements Iterator<String> {
    protected Logger log = Logger.getLogger(edu.harvard.libcomm.pipeline.ted.TedIterator.class);
    protected TedReader tedReader;
    protected int chunkSize;

    protected MongoCursor<Document> mongoCursor;
    //protected String source = "";

    public TedIterator(TedReader tedReader, int chunkSize) {
        this.tedReader = tedReader;
        this.chunkSize = chunkSize;
        this.mongoCursor = tedReader.getMongoCursor();
    }

    @Override
    public boolean hasNext() {
        return mongoCursor.hasNext();
    }

    @Override
    public String next() {
        log.trace("");
        StringBuilder sb = new StringBuilder();
        String xml = "";
        String data = null;
        int counter = 0;
        try {
            while (mongoCursor.hasNext() && counter < chunkSize) {
                xml = getXML(mongoCursor.next().toJson());
                xml = xml.replace("xmlns:xlink", "XMLNSXLINK");
                String collElem = tedReader.getCollectionName() + "Record";
                String wrappedXml = "<" + collElem + ">" + xml + "</" + collElem + ">";
                sb.append(wrappedXml);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } /*finally {
            mongoCursor.close();
        }*/
        String transformXSL = "src/main/resources/" + tedReader.getXslName(); //"src/main/resources/mcz2mods.xsl";
        try {
            String wrapperToken = "tedCollection";
            xml = "<" + wrapperToken + " xmlns:xlink=\"http://www.w3.org/TR/xlink\"" + ">" + sb + "</" + wrapperToken + ">";
            //System.out.println("MCZXML: " + xml);
            try {
                LibCommMessage lcMess = new LibCommMessage();
                Payload payload = new LibCommMessage.Payload();
                payload.setData(xml);
                lcMess.setPayload(payload);
                data = MessageUtils.transformPayloadData(lcMess, transformXSL, xml);
                //System.out.println("TEDMODS: " + data);
            } catch (Exception e) {
                e.printStackTrace();
                //throw e;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("DATA: " + data);
        LibCommMessage libCommMessage = new LibCommMessage();
        Payload payload = new Payload();
        payload.setData(data);
        libCommMessage.setPayload(payload);
        try {
            return MessageUtils.marshalMessage(libCommMessage);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private static String getXML(String json) {
        JSONTokener tokener;
        //tokener = new JSONTokener(uri.toURL().openStream());
        tokener = new JSONTokener(json);
        JSONObject jsonObj = new JSONObject(tokener);
        String xml = XML.toString(jsonObj);
        return xml;
    }
}
