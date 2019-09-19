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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DrsMdsSolrProcessor implements Processor {

    protected Logger log = Logger.getLogger(DrsMdsSolrProcessor.class);
    private Integer commitWithinTime = -1;
    Collection<SolrInputDocument> docs = null;
    List<DrsMetadataItem> itemList = null;

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
        String drsObjectId = null;
        String ownerSuppliedName = null;
        String metsLabel = null;
        String viewText = null;
        JSONArray jsonArray = new JSONArray(json);
        for(int i=0;i<jsonArray.length();i++) {
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
                //log.info("Object Fields: " + drsObjectId + "|" + ownerSuppliedName + "|" + metsLabel + "|" + viewText);
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
                    String deliveryType = urnObj.getString("deliveryType");
                    item.setDeliveryType(deliveryType);
                    if (deliveryType.equals("PDS")) {
                        String thumb = getIIIFThumb(drsObjectId);
                        // below just for testing with real object in iiif
                        //String thumb = getIIIFThumb("457753039");
                        item.setThumbnailURL(thumb);
                    }
                    String url = urnObj.getString("url");
                    item.setUrl(url);
                    String urn = StringUtils.substringAfter(url,"harvard.edu/");
                    item.setId(urn);
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

    private static String getIIIFThumb(String objectId) {
        String thumb = "";
        String iiifBaseUrl = "https://iiif.lib.harvard.edu/manifests/drs:";
        try {
            String id = objectId.split("\\?")[0];
            int seq = 1;
            if (objectId.contains("?n="))
                seq = Integer.parseInt(StringUtils.substringAfter(objectId,"?n="));
            URL iiifUrl = new URL(iiifBaseUrl + id); //(objectId.contains("?n=") ? objectId.replace("?n=","$") + "i" : objectId));
            InputStream is = iiifUrl.openConnection().getInputStream();
            try {
                JSONObject iiifObj = new JSONObject(readUrl(is));
                JSONArray seqArr = iiifObj.getJSONArray("sequences");
                JSONArray canvArr = seqArr.getJSONObject(0).getJSONArray("canvases");
                thumb = canvArr.getJSONObject(seq -1).getJSONObject("thumbnail").getString("@id");
            } catch (JSONException jse) {
                jse.printStackTrace();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return thumb;
    }

    private static String readUrl(InputStream is) {
        StringBuilder content = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("BAD REQ: " + e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("READURL: " + content.toString());
        return content.toString();
    }

}
