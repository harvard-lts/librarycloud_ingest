package edu.harvard.libcomm.pipeline.ted;

import com.amazonaws.util.IOUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.MessageUtils;
import org.bson.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import java.io.InputStream;

/**
 * Created by mjv162 on 9/7/2018.
 */
public class TedReader {
    private InputStream is;
    private String collectionName;
    private String xslName;

    public TedReader(InputStream is) {
        this.is = is;
        try {
            String commandStr = IOUtils.toString(is);
            System.out.println("commandStr: " + commandStr);
            this.collectionName = commandStr.split(",")[0];
            this.xslName = commandStr.split(",")[1];
            //System.out.println("CN|XSL: " + this.collectionName + "|" + this.xslName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoCursor<Document> getMongoCursor() {
        //System.out.println("FP: " + libCommMessage.getPayload().getFilepath());
        //System.out.println("SRC: " + libCommMessage.getPayload().getSource());
        String mongoHost = Config.getInstance().MONGO_HOST;
        String mongoPort = Config.getInstance().MONGO_PORT;
        String mongoUsername = Config.getInstance().MONGO_USERNAME;
        String mongoPassword = Config.getInstance().MONGO_PASSWORD;
        String mongoUrl = "mongodb://" + mongoUsername + ":" + mongoPassword + "@" + mongoHost + ":" + mongoPort + "/ted";
        MongoClientURI mongoUri = new MongoClientURI(mongoUrl);
        MongoClient mongoClient = new MongoClient(mongoUri);
        MongoDatabase database = mongoClient.getDatabase("ted");
        MongoCollection<Document> coll = database.getCollection(collectionName);
        FindIterable<Document> fi = coll.find();
        MongoCursor<Document> cursor = fi.iterator();
        return cursor;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public String getXslName() {
        return xslName;
    }


}
