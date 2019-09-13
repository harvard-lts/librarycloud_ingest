package edu.harvard.libcomm.pipeline.solr;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class DrsMdsSolrProcessor implements Processor {

    protected Logger log = Logger.getLogger(DrsMdsSolrProcessor.class);
    private Integer commitWithinTime = -1;
    Collection<SolrInputDocument> docs = null;

    public void process(Exchange exchange) throws Exception {
        docs = new ArrayList<SolrInputDocument>();
        String drsMetadataJson = exchange.getIn().getBody(String.class);
        //log.info(drsMetadataJson);
        parseJson(drsMetadataJson);
        populateIndex();
        exchange.getIn().setBody("DONE");
    }

    private void parseJson (String json) {

        JSONArray jsonArray = new JSONArray(json);
        JSONArray filteredArray = null;
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONArray urnArr = null;
            try {
                urnArr = jsonObject.getJSONArray("deliveryUrns");
                for(int j=0;j<urnArr.length();j++) {
                    JSONObject urnObj = urnArr.getJSONObject(j);
                    String deliveryType = urnObj.getString("deliveryType");
                    String url = urnObj.getString("url");
                    buildSolrDoc(jsonObject, url, deliveryType);
                }
            }
            catch (JSONException e) {
                log.debug("no urns for this object");
            }
        }
    }

    private void buildSolrDoc (JSONObject jsonObj, String url, String deliveryType) {

        SolrInputDocument doc = new SolrInputDocument();
        String urn = StringUtils.substringAfter(url,"harvard.edu/");
        String drsId = jsonObj.get("drsId").toString();
        String accessFlag = jsonObj.getString("accessFlag");
        JSONObject cmObj = jsonObj.getJSONObject("contentModel");
        String cmCode = cmObj.getString("code");
        String alias = cmObj.getString("alias");
        JSONObject ownerObj = jsonObj.getJSONObject("owner");
        String ownerCode = ownerObj.getString("code");
        String ownerCodeDisplayName = ownerObj.getString("displayName");
        JSONObject lmDateObj = jsonObj.getJSONObject("lastModifiedDate");
        String lastModifiedDate = lmDateObj.get("$date").toString();
        JSONObject iDateObj = jsonObj.getJSONObject("insertionDate");
        String insertionDate = iDateObj.get("$date").toString();
        String ownerSuppliedName = jsonObj.getString("ownerSuppliedName");
        String suppliedFilename = null; //jsonObj.getString("suppliedFilename");
        String metsLabel = null;
        doc.addField("id",urn);
        doc.addField("urn",urn);
        doc.addField("fileDeliveryURL",url);
        doc.addField("uriType",deliveryType);
        //doc.addField("drsId",drsId);
        doc.addField("accessFlag",accessFlag);
        //doc.addField("contentModelCode",cmCode);
        doc.addField("contentModel",alias);
        doc.addField("ownerCode",ownerCode);
        doc.addField("ownerCodeDisplayName",ownerCodeDisplayName);
        doc.addField("lastModifiedDate",lastModifiedDate);
        //doc.addField("insertionDate",insertionDate);
        //doc.addField("ownerSuppliedName",ownerSuppliedName);
        //doc.addField("suppliedFilename",suppliedFilename);
        try {
            suppliedFilename = jsonObj.getString("suppliedFilename");
            //doc.addField("suppliedFilename",suppliedFilename);
        }
        catch (JSONException e) {
            log.debug("No suppliedFilename for this object");
        }
        try {
            metsLabel = jsonObj.getString("metsLabel");
            doc.addField("metsLabel",metsLabel);
        }
        catch (JSONException e) {
            log.debug("No mets label for this object");
        }

        docs.add(doc);
        //log.info(drsId + accessFlag + cmCode + alias + ownerCode + ownerCodeDisplayName + lastModifiedDate + insertionDate + url + deliveryType);

    }

    private void populateIndex() throws Exception {

        HttpSolrClient server = null;
        Date start = new Date();
        server = SolrDrsExtensionsServer.getSolrConnection();
        UpdateRequest update = new UpdateRequest();
        update.add(docs);
        if (commitWithinTime > 0) {
            update.setCommitWithin(commitWithinTime);
            update.process(server);
        } else {
            update.process(server);
            server.commit();
        }

        Date end = new Date();
        log.debug("Solr insert query time: " + (end.getTime() - start.getTime()));
    }

}
