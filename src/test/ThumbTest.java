/**
 * Created by mjv162 on 3/1/2018.
 */

package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.json.*;

public class ThumbTest {
    public ThumbTest() {
    }

    public static void main(String[] args) {

        String urls = "http://nrs.harvard.edu/urn-3:fhcl.loeb:957096,http://nrs.harvard.edu/urn-3:HUL.ARCH:26515075?n=13,http://nrs.harvard.edu/urn-3:fhcl.loeb:957096  ,http://nrs.harvard.edu/urn-3:HUL.FIG:006755588";
        for (String s : urls.split(",")) {
            //if test="contains(.,'urn-3') and not(contains(.,'HUL.FIG')) and not(contains(.,'ebookbatch')) and not(contains(.,'ejournals')
            if (s.contains("urn-3") && !s.contains("HUL.FIG") && !s.contains("ebookbatch") && !s.contains("ejournals"))
            {
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

                //System.out.println("LOC: " + location);

                if ((responseCode / 100) == 3) {
                    if (location.contains("//pds")) {
                        String thumb = getIIIFThumb(StringUtils.substringAfterLast(location, "/"));
                        System.out.println(location + " : " + thumb);
                    }
                }
            }
        }

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


