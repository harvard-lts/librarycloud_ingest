package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.pipeline.solr.SolrClient;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class DrsMdsUpdateProcessor implements Processor {

    protected Logger log = Logger.getLogger(DrsMdsProcessor.class);
    public void process(Exchange exchange) throws Exception {
        String urnQuery = "(";
        String urnJson = exchange.getIn().getBody(String.class);
        //log.info("urlJson: " + urlJson);
        JSONArray jsonArray = new JSONArray(urnJson);
        //log.info("jsonArray: " + jsonArray.toString());
        for(int i=0;i<jsonArray.length();i++) {
            //JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                if (i > 0)
                    urnQuery += " OR ";
                //String url = jsonObject.getString("url");
                String urn = jsonArray.getString(i);
                urnQuery += "\"" + urn + "\"";
                //log.info("url: " + urlQuery);
            }
            catch (JSONException e) {
                log.debug("no urns for this object");
            }
        }
        urnQuery = "urn:" + urnQuery + ")";
        //log.info("urlQuery: " + urnQuery);
        String modsRecords = getSolrModsRecords(urnQuery);
        String modsCollection = "<modsCollection xmlns=\"http://www.loc.gov/mods/v3\">" + modsRecords + "</modsCollection>";
        //log.info("modsCollection: " + modsCollection);
        LibCommMessage message = new LibCommMessage();
        Payload payload = new Payload();
        payload.setData(modsCollection);
        message.setPayload(payload);
        exchange.getIn().setBody(MessageUtils.marshalMessage(message));
    }

    private String getSolrModsRecords(String urnQuery)
    {
        StringBuffer modsRecords = new StringBuffer();

        SolrDocumentList docs;
        SolrDocument doc = null;
        HttpSolrClient client = null;
        try {
            client = SolrClient.getSolrConnection();

            SolrQuery query = new SolrQuery(urnQuery);
            QueryResponse response = client.query(query);
            docs = response.getResults();
            if (docs.size() == 0)
                log.debug("No items found for query: " + urnQuery);
            else {
                for (int i=0;i<docs.size();i++) {
                    doc = docs.get(i);
                    modsRecords.append(doc.getFieldValue("originalMods").toString());
                    //log.info("modsRecords: " + modsRecords.toString());
                }

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

        return modsRecords.toString();
    }
}
