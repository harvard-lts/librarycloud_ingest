/**
 * Created by mjv162 on 8/28/2018.
 */

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import java.io.IOException;

public class TedMongoTest {

    public TedMongoTest(){

    }

    public static void main (String args[]) {
        MongoClientURI mongoUri = new MongoClientURI("mongodb://localhost:27017/ted");
        MongoClient mongoClient = new MongoClient(mongoUri);
        MongoDatabase database = mongoClient.getDatabase("ted");
        MongoCollection<Document> coll = database.getCollection("mcz");
        try {
            getAllDocuments(coll);
        }
        catch (Exception e) {
            e.printStackTrace();

        }

    }

    private static void getAllDocuments(MongoCollection<Document> col) throws Exception {
        FindIterable<Document> fi = col.find();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while (cursor.hasNext()) {
                String xml = getXML(cursor.next().toJson());
                System.out.println(xml);
            }
        }
        catch (Exception e) {
        } finally {
            cursor.close();
        }
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
