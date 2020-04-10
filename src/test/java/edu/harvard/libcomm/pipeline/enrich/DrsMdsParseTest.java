package edu.harvard.libcomm.pipeline.enrich;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DrsMdsParseTest {

    protected Logger log = Logger.getLogger(DrsMdsParseTest.class);
    private String url;
    private String deliveryType;
    private String urn;
    private String objectId;
    private String drsFileId;
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
    private String harvardMetadataType;       // object level only
    private String harvardMetadataId;         // object level only
    private String harvardMetadataLabel;      // object level only
    private String viewText;                  // object level only
    private String maxImageDeliveryDimension; // file level only
    private String mimeType;                  // file level only
    private String suppliedFilename;          // file level only

    @Test
    void pdsTest () throws Exception {
        String filename = "/pds_mds_test.json";
        parseMetadata(filename);
    }

    @Test
    void idsTest () throws Exception {
        String filename = "/ids_mds_test.json";
        parseMetadata(filename);
    }

    @Test
    void sdsTest () throws Exception {
        String filename = "/sds_mds_test.json";
        parseMetadata(filename);
    }

    @Test
    void fdsTest () throws Exception {
        String filename = "/fds_mds_test.json";
        parseMetadata(filename);
    }

    @Test
    void pdsListTest () throws Exception {
        String filename = "/pdslist_mds_test.json";
        parseMetadata(filename);
    }

    @Test
    void sdsVideoTest () throws Exception {
        String filename = "/sdsvideo_mds_test.json";
        parseMetadata(filename);
    }

    void parseMetadata(String filename) throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource(filename).getFile());
        JSONArray jsonArray = new JSONArray(IOUtils.toString(is,"UTF-8"));
        for(int i=0;i<jsonArray.length();i++) {
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
                JSONArray harvardMetadataLinks = null;
                try {
                    harvardMetadataLinks = jsonObject.getJSONArray("harvardMetadataLinks");
                    for(int j=0;j<harvardMetadataLinks.length();j++) {
                        JSONObject mdlObj = harvardMetadataLinks.getJSONObject(j);
                        harvardMetadataId = mdlObj.getString("metadataIdentifier");
                        harvardMetadataType = mdlObj.getString("metadataType");
                        try {
                            harvardMetadataLabel = mdlObj.getString("displayLabel");
                        } catch (JSONException e) {
                            log.debug("No harvardMetadataLabel for this HarvardMetadataLink");
                        }
                        log.info("MetadataLinks: " + "|" +  harvardMetadataId +  "|" + harvardMetadataType + "|" + harvardMetadataLabel);
                    }

                } catch (JSONException e) {
                    log.debug("No harvardMetadataLinks for this object");
                }


            }
            JSONArray urnArr = null;
            try {
                urnArr = jsonObject.getJSONArray("deliveryUrns");
                for(int k=0;k<urnArr.length();k++) {
                    JSONObject urnObj = urnArr.getJSONObject(k);
                    deliveryType = urnObj.getString("deliveryType");
                    url = urnObj.getString("url");
                    urn = StringUtils.substringAfter(url,"harvard.edu/");
                    contentType = jsonObject.getString("contentType");
                    accessFlag = jsonObject.get("accessFlag").toString();
                    JSONObject cmObj = jsonObject.getJSONObject("contentModel");
                    cmCode = cmObj.getString("code");
                    alias = cmObj.getString("alias");
                    JSONObject ownerObj = jsonObject.getJSONObject("owner");
                    ownerCode = ownerObj.getString("code");
                    ownerCodeDisplayName = ownerObj.getString("displayName");
                    JSONObject lmdObj = jsonObject.getJSONObject("lastModifiedDate");
                    lastModifiedDate = lmdObj.get("$date").toString();
                    JSONObject idObj = jsonObject.getJSONObject("insertionDate");
                    insertionDate = idObj.get("$date").toString();
                    if (contentType.equals("file")) {
                        drsFileId = jsonObject.get("drsId").toString();
                        suppliedFilename = jsonObject.getString("suppliedFilename");
                        try {
                            maxImageDeliveryDimension = jsonObject.getString("maxImageDeliveryDimension");
                        }
                        catch (JSONException e) {
                            log.debug("No maxImageDeliveryDimension for this object");
                        }
                        try {
                            mimeType = jsonObject.getString("mimeType");
                        }
                        catch (JSONException e) {
                            log.debug("No mimeType for this object");
                        }
                    }
                    //printFields();
                }
            }
            catch (JSONException e) {
                log.debug("no urns for this object");
            }
        }

    }

    private void printFields () {
        log.info(url);
        log.info(deliveryType);
        log.info(urn);
        log.info(drsFileId);
        log.info(drsObjectId);
        log.info(accessFlag);
        log.info(lastModifiedDate);
        log.info(insertionDate);
        log.info(ownerSuppliedName);
        log.info(cmCode);
        log.info(alias);
        log.info(ownerCode);
        log.info(ownerCodeDisplayName);
        log.info(metsLabel);
        log.info(harvardMetadataType);
        log.info(harvardMetadataId);
        log.info(harvardMetadataLabel);
        log.info(viewText);
        log.info(maxImageDeliveryDimension);
        log.info(mimeType);
        log.info(suppliedFilename);
    }

}
