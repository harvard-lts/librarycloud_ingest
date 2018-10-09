package edu.harvard.libcomm.pipeline.ted;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.pipeline.Config;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

/* Given a message that contains a raw DELETE MarcXML record (not wrapped in a LibCommMessage),
   extract the record ID from a header ("deleteID") on the exchange. Use that ID to create
   a delete message */

public class TedToMods implements IProcessor {
    protected Logger log = Logger.getLogger(edu.harvard.libcomm.pipeline.ted.TedToMods.class);

    public void processMessage(LibCommMessage libCommMessage) throws Exception {

        String mongoHost = Config.getInstance().MONGO_HOST;
        String mongoPort = Config.getInstance().MONGO_PORT;
        String mongoUsername = Config.getInstance().MONGO_USERNAME;
        String mongoPassword = Config.getInstance().MONGO_PASSWORD;
        String mongoUrl = "mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoHost + ":" + mongoPort + "/ted";
        MongoClientURI mongoUri = new MongoClientURI(mongoUrl);
        MongoClient mongoClient = new MongoClient(mongoUri);
        MongoDatabase database = mongoClient.getDatabase("ted");
        MongoCollection<Document> coll = database.getCollection("mcz");

        String data = null;
        String xml = "";
        String transformXSL = "src/main/resources/mcz2mods.xsl";
        try {
            String wrapperToken = "mczRecord";
            xml = "<" + wrapperToken + " xmlns:xlink=\"http://www.w3.org/TR/xlink\"" + ">" + getAllDocuments(coll) + "</" + wrapperToken + ">";
            //System.out.println(xml);
            try {
                LibCommMessage lcMess = new LibCommMessage();
                Payload payload = new Payload();
                payload.setData(xml);
                lcMess.setPayload(payload);
                data = MessageUtils.transformPayloadData(lcMess, transformXSL, xml);
                System.out.println("TEDMODS: " + data);
            } catch (Exception e) {
                e.printStackTrace();
                //throw e;
            }
            //System.out.println("DATA: " + data);
            //LibCommMessage libCommMessage = new LibCommMessage();
            Payload payload = new Payload();
            payload.setData(data);
            libCommMessage.setPayload(payload);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private String getAllDocuments(MongoCollection<Document> col) throws Exception {
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        StringBuilder sb = new StringBuilder();
        String xml = "";
        try {
            while (cursor.hasNext()) {
                xml = getXML(cursor.next().toJson());
                xml = xml.replace("xmlns:xlink", "XMLNSXLINK");
                sb.append(xml);
                //System.out.println(xml);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return xml; //sb.toString();
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
