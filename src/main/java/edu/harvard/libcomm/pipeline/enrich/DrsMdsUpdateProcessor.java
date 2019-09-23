package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.pipeline.DrsMdsProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DrsMdsUpdateProcessor implements Processor {

    protected Logger log = Logger.getLogger(DrsMdsProcessor.class);
    public void process(Exchange exchange) throws Exception {
        String urlQuery = "(";
        String urlJson = exchange.getIn().getBody(String.class);
        //log.info("urlJson: " + urlJson);
        JSONArray jsonArray = new JSONArray(urlJson);

        for(int i=0;i<jsonArray.length();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                if (i > 0)
                    urlQuery += " OR ";
                String url = jsonObject.getString("url");
                String urn = StringUtils.substringAfter(url,"harvard.edu/");
                urlQuery += "\"" + urn + "\"";
                //log.info("url: " + urlQuery);
            }
            catch (JSONException e) {
                log.debug("no urns for this object");
            }
        }
        urlQuery += ")";
        log.info("urlQuery: " + urlQuery);
    }
}
