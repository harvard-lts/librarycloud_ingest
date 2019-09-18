package edu.harvard.libcomm.pipeline.enrich;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.*;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

/* Add Holdings data (physicalLocation, shelfLocator, url) to MODS records, retrieved from lilCloud API */
public class PDSThumbsProcessor extends ExternalServiceProcessor implements IProcessor {

    protected Logger log = Logger.getLogger(PDSThumbsProcessor.class);

    public void processMessage(LibCommMessage libCommMessage) throws Exception {
        libCommMessage.setCommand("enrich-pds-thumbs");
        log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath()); // + "," + libCommMessage.getHistory().getEvent().get(0).getMessageid());
        String results = "";
        String data = libCommMessage.getPayload().getData();
        String urls = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/pds_urns.xsl",null);
        if (urls.length() > 0) {
            urls = urls.replace(",*$", "");
            //String urls = "http://nrs.harvard.edu/urn-3:fhcl.loeb:957096,http://nrs.harvard.edu/urn-3:HUL.ARCH:26515075?n=13,http://nrs.harvard.edu/urn-3:fhcl.loeb:957096  ,http://nrs.harvard.edu/urn-3:HUL.FIG:006755588";
            StringBuilder sb = new StringBuilder();
            for (String s : urls.split(",")) {
                if (s.contains("urn-3") && !s.contains("HUL.FIG") && !s.contains("ebookbatch") && !s.contains("ejournals") && !s.contains("gisdata")) {
                    HttpURLConnection con = null;
                    int responseCode = 0;
                    try {
                        con = (HttpURLConnection) (new URL(s).openConnection());
                        con.setInstanceFollowRedirects(false);
                        con.connect();
                        responseCode = con.getResponseCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String location = con.getHeaderField("Location"); // testing + "?n=3";
                    if ((responseCode / 100) == 3) {
                        if (location.contains("//pds")) {
                            try {
                                String thumb = getIIIFThumb(StringUtils.substringAfterLast(location, "/"));
                                //System.out.println(location + " : " + thumb);
                                sb.append("<image><url>" + s + "</url>" + "<thumb>" + thumb + "</thumb></image>");
                            } catch (Exception e)  {
                                log.error("No thumb found for: " + s);
                            }
                        }
                    }
                }
            }
            results = "<results>" + sb.toString() + "</results>";
            //System.out.println("results: " + results);
            data = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/addpdsurns.xsl",results);
        }
            libCommMessage.getPayload().setData(data);
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