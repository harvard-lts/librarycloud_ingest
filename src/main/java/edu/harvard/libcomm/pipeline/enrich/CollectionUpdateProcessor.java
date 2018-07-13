package edu.harvard.libcomm.pipeline.enrich;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.pipeline.solr.SolrServer;

public class CollectionUpdateProcessor implements IProcessor {
    protected Logger log = Logger.getLogger(CollectionsProcessor.class);

    public void processMessage(LibCommMessage libCommMessage) throws Exception {
        String itemId;
        String data;
        String collectionData;

        collectionData = libCommMessage.getPayload().getData();

        itemId = MessageUtils.transformPayloadData(libCommMessage, "src/main/resources/item_id.xsl", null);
        if (itemId == null || itemId.length() == 0){
            libCommMessage.getPayload().setData("");
            log.error("Could not find item id in message");
            return;
        }

        data = getSolrModsRecord(itemId);
        if (data == null){
            libCommMessage.getPayload().setData("");
            log.error("Could not find item id in solr");
            return;
        }

        LibCommMessage temporaryMessage = new LibCommMessage();
        Payload temporaryPayload = new Payload();
        temporaryPayload.setData(data);
        temporaryMessage.setPayload(temporaryPayload);
        data = MessageUtils.transformPayloadData(temporaryMessage, "src/main/resources/addcollections.xsl", collectionData);
        libCommMessage.getPayload().setData(data);
    }

    private String replaceSolrSpecialCharacters(String s) {
        String specials = "+-&|!(){}[]^\"~*?:";
        for (int i = 0; i < specials.length(); i++) {
            s = s.replace(specials.substring(i,i+1),"\\" + specials.substring(i,i+1));
        }
        return s;
    }

    private String getSolrModsRecord(String itemId)
    {
        String modsRecord = "";

        SolrDocumentList docs;
        SolrDocument doc = null;
        HttpSolrClient server = null;
        try {
            server = SolrServer.getSolrConnection();

            SolrQuery query = new SolrQuery("recordIdentifier:" + replaceSolrSpecialCharacters(itemId));
            QueryResponse response = server.query(query);
            docs = response.getResults();
            if (docs.size() == 0)
                log.debug("Item " + itemId + " not found");
            else {
                doc = docs.get(0);

            }
        }
        catch (SolrServerException se) {
            se.printStackTrace();
            log.error(se.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return (doc == null) ? null : doc.getFieldValue("originalMods").toString();
    }
}
