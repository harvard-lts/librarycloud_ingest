package edu.harvard.libcomm.pipeline.solr;

import edu.harvard.libcomm.pipeline.DrsMetadataItem;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DrsMdsSolrProcessor implements Processor {

    protected Logger log = Logger.getLogger(DrsMdsSolrProcessor.class);
    private Integer commitWithinTime = -1;
    Collection<SolrInputDocument> docs = null;
    List<DrsMetadataItem> itemList = null;
/*
    private String url;
    private String deliveryType;
    private String urn;
    private String objectId;
    private String drsFileId; // file level only
    private String drsObjectId;
    private String accessFlag;
    private String lastModifiedDate;
    private String insertionDate;
    private String ownerSuppliedName;
    private String cmCode;
    private String alias;
    private String ownerCode;
    private String ownerCodeDisplayName;
    private String metsLabel;                 // object level only
    //private String harvardMetadataType;       // object level only
    //private String harvardMetadataId;         // object level only
    //private String harvardMetadataLabel;      // object level only
    private ArrayList<String> harvardMetadataLinks;    //
    private String viewText;                  // object level only
    private String maxImageDeliveryDimension; // file level only
    private String mimeType;                  // file level only
    private String suppliedFilename;          // file level only

 */

    public void process(Exchange exchange) throws Exception {
        docs = new ArrayList<SolrInputDocument>();
        itemList = new ArrayList<DrsMetadataItem>();
        String drsMetadataJson = exchange.getIn().getBody(String.class);
        //log.info(drsMetadataJson);
        parseJson(drsMetadataJson);
        populateIndex();
        exchange.getIn().setBody("DONE");
    }


    void parseJson(String json) throws Exception {
        JSONArray jsonArray = new JSONArray(json);
        for(int i=0;i<jsonArray.length();i++) {
            String drsObjectId = null;
            String ownerSuppliedName = null;
            String metsLabel = null;
            String viewText = null;
            ArrayList<String> harvardMetadataLinks = new ArrayList<String>();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            //set all the object fields
            String contentType = jsonObject.getString("contentType");
            if (contentType.equals("object")) {
                drsObjectId = jsonObject.get("objectId").toString();
                ownerSuppliedName = jsonObject.getString("ownerSuppliedName");
                try {
                    metsLabel = jsonObject.getString("metsLabel");
                }
                catch (JSONException e) {
                    log.debug("No mets label for this object");
                }
                try {
                    viewText = jsonObject.getString("viewText");
                }
                catch (JSONException e) {
                    log.debug("No viewText for this object");
                }
                log.info("Object Fields: " + drsObjectId + "|" + ownerSuppliedName + "|" + metsLabel + "|" + viewText);
                JSONArray harvardMetadataArr = null;
                try {
                    harvardMetadataLinks = new ArrayList<String>();
                    harvardMetadataArr = jsonObject.getJSONArray("harvardMetadataLinks");
                    for(int j=0;j<harvardMetadataArr.length();j++) {
                        JSONObject mdlObj = harvardMetadataArr.getJSONObject(j);
                        String harvardMetadataId = mdlObj.getString("metadataIdentifier");
                        String harvardMetadataType = mdlObj.getString("metadataType");
                        String delimitedHarvMeta = harvardMetadataId + "~" + harvardMetadataType;
                        try {
                            String harvardMetadataLabel = mdlObj.getString("displayLabel");
                            delimitedHarvMeta += "~" + harvardMetadataLabel;
                        } catch (JSONException e) {
                            log.debug("No harvardMetadataLabel for this HarvardMetadataLink");
                        }
                        harvardMetadataLinks.add(delimitedHarvMeta);
                        //log.info("MetadataLinks: " + "|" +  harvardMetadataId +  "|" + harvardMetadataType + "|" + harvardMetadataLabel);
                    }

                } catch (JSONException e) {
                    log.debug("No harvardMetadataLinks for this object");
                }


            }
            JSONArray urnArr = null;
            try {
                urnArr = jsonObject.getJSONArray("deliveryUrns");
                for(int k=0;k<urnArr.length();k++) {
                    DrsMetadataItem item = new DrsMetadataItem();
                    item.setDrsObjectId(drsObjectId);
                    item.setOwnerSuppliedName(ownerSuppliedName);
                    item.setMetsLabel(metsLabel);
                    item.setViewText(viewText);
                    item.setHarvardMetadataLinks(harvardMetadataLinks);
                    JSONObject urnObj = urnArr.getJSONObject(k);
                    item.setId(jsonObject.get("drsId").toString());
                    item.setDeliveryType(urnObj.getString("deliveryType"));
                    String url = urnObj.getString("url");
                    item.setUrl(url);
                    String urn = StringUtils.substringAfter(url,"harvard.edu/");
                    item.setUrn(urn);
                    item.setAccessFlag(jsonObject.get("accessFlag").toString());
                    JSONObject cmObj = jsonObject.getJSONObject("contentModel");
                    item.setCmCode(cmObj.getString("code"));
                    item.setAlias(cmObj.getString("alias"));
                    JSONObject ownerObj = jsonObject.getJSONObject("owner");
                    item.setOwnerCode(ownerObj.getString("code"));
                    item.setOwnerCodeDisplayName(ownerObj.getString("displayName"));
                    JSONObject lmdObj = jsonObject.getJSONObject("lastModifiedDate");
                    item.setLastModifiedDate(lmdObj.get("$date").toString());
                    JSONObject idObj = jsonObject.getJSONObject("insertionDate");
                    item.setInsertionDate(idObj.get("$date").toString());
                    contentType = jsonObject.getString("contentType");
                    if (contentType.equals("file")) {

                        item.setDrsFileId(jsonObject.get("drsId").toString());
                        item.setSuppliedFilename(jsonObject.getString("suppliedFilename"));
                        try {
                            item.setMaxImageDeliveryDimension(jsonObject.getString("maxImageDeliveryDimension"));
                        }
                        catch (JSONException e) {
                            log.debug("No maxImageDeliveryDimension for this object");
                        }
                        try {
                            item.setMimeType(jsonObject.getString("mimeType"));
                        }
                        catch (JSONException e) {
                            log.debug("No mimeType for this object");
                        }
                    }
                    itemList.add(item);
                    //printFields();
                }
            }
            catch (JSONException e) {
                log.debug("no urns for this object");
            }
        }

    }
/*
    private void buildSolrDoc (JSONObject jsonObj, String url, String deliveryType) {
        log.info("Parsing and mapping drs metadata to solr fields");
        SolrInputDocument doc = new SolrInputDocument();
        //required
        doc.addField("id",urn);
        doc.addField("urn",urn);
        doc.addField("fileDeliveryURL",url);
        doc.addField("uriType",deliveryType);
        doc.addField("drsObjectId",drsObjectId);
        doc.addField("accessFlag",accessFlag);
        doc.addField("contentModelCode",cmCode);
        doc.addField("contentModel",alias);
        doc.addField("ownerCode",ownerCode);
        doc.addField("ownerCodeDisplayName",ownerCodeDisplayName);
        doc.addField("lastModifiedDate",lastModifiedDate);
        doc.addField("insertionDate",insertionDate);
        doc.addField("ownerSuppliedName",ownerSuppliedName);
        // optional or file
        if (metsLabel != null)
            doc.addField("metsLabel",metsLabel);
        if (viewText != null)
            doc.addField("viewText",viewText);
        if (drsFileId != null)
            doc.addField("drsFileId",drsFileId);
        if (maxImageDeliveryDimension != null)
            doc.addField("maxImageDeliveryDimension",maxImageDeliveryDimension);
        if (mimeType != null)
            doc.addField("mimeType",mimeType);
        if (suppliedFilename != null)
            doc.addField("suppliedFilename",suppliedFilename);
        if (harvardMetadataLinks.size() > 0)
            doc.addField("harvardMetadataLink",harvardMetadataLinks;
        }

        docs.add(doc);
        //log.info("Here are the parsed fields: " + drsId + ", " + accessFlag + ", " + cmCode + ", " + alias + ", " + ownerCode + ", " + ownerCodeDisplayName + ", " + lastModifiedDate + ", " + insertionDate + ", " + url + ", " + deliveryType);

    }
    */


    private void populateIndex() throws Exception {
        log.info("Inserting into solr");
        HttpSolrClient client = null;
        Date start = new Date();
        client = SolrDrsExtensionsClient.getSolrConnection();
        client.addBeans(itemList);
        client.commit();
        /*
        UpdateRequest update = new UpdateRequest();
        update.add(docs);
        if (commitWithinTime > 0) {
            update.setCommitWithin(commitWithinTime);
            update.process(client);
        } else {
            update.process(clinet);
            client.commit();
        }

         */

        Date end = new Date();
        log.debug("Solr insert query time: " + (end.getTime() - start.getTime()));
    }

}
