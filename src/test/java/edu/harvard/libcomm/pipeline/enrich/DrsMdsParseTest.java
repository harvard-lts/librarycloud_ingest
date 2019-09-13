package edu.harvard.libcomm.pipeline.enrich;

import org.apache.commons.io.IOUtils;
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
    //private String drsIdToMatch = "400338692";
    private JSONObject matchingJsonObj;
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

    @Test
    void parseMetadata() throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource("/400338692_mds.json").getFile());
        JSONArray jsonArray = new JSONArray(IOUtils.toString(is,"UTF-8"));
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
                    //assertEquals(url,"https://nrs-dev.lib.harvard.edu/urn-3:HUL.OIS:1244359");
                    addSolrDoc(jsonObject, url, deliveryType);
                }
            }
            catch (JSONException e) {
                log.debug("no urns for this object");
            }
        }

    }

    private void addSolrDoc (JSONObject jsonObj, String url, String deliveryType) {
        String drsId = jsonObj.get("drsId").toString();
        String accessFlag = jsonObj.get("accessFlag").toString();
        JSONObject cmObj = jsonObj.getJSONObject("contentModel");
        String cmCode = cmObj.getString("code");
        String alias = cmObj.getString("alias");
        JSONObject ownerObj = jsonObj.getJSONObject("owner");
        String ownerCode = ownerObj.getString("code");
        String ownerCodeDisplayName = ownerObj.getString("displayName");
        JSONObject lmdObj = jsonObj.getJSONObject("lastModifiedDate");
        String lastModifiedDate = lmdObj.get("$date").toString();
        String insertionDate = jsonObj.get("insertionDate").toString();
        //log.info("FIELDS: " + drsId + accessFlag + cmCode + alias + ownerCode + ownerCodeDisplayName + lastModifiedDate + insertionDate + url + deliveryType);
        //assertEquals("P", accessFlag);
        log.debug(lastModifiedDate + insertionDate);
    }

}
