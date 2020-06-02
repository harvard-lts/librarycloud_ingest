package edu.harvard.libcomm.pipeline.enrich;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.pipeline.IProcessor;

public class CollectionsProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(CollectionsProcessor.class);
	
	public void processMessage(LibCommMessage libCommMessage) throws Exception {	
	
		String data;
		String recids;

		libCommMessage.setCommand("enrich-collections");
		try {
			log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath() + "," + libCommMessage.getHistory().getEvent().get(0).getMessageid());
		} catch (Exception e) {
			log.error("Unable to log message info");
		}
		if ((Config.getInstance().COLLECTIONS_URL == null) ||  Config.getInstance().COLLECTIONS_URL.isEmpty()) {
			return;
		}

		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids-comma-separated.xsl",null);
			//System.out.println("RECIDS: " + recids);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		String collApiUrl = Config.getInstance().COLLECTIONS_URL + "/collections/items/" + recids + ".xml";
		//URI uri = new URI(collApiUrl);
		String collectionsXml;
		try {
			Date start = new Date();
			//collectionsXml = IOUtils.toString(uri.toURL().openStream(), "UTF-8");
			collectionsXml = getXml(collApiUrl);
			Date end = new Date();
			log.trace("CollectionsProcessor query time: " + (end.getTime() - start.getTime()));
			log.trace("CollectionsProcessor query : " +  collApiUrl);
		} catch (FileNotFoundException e) {
			// If none of the items are in a collection, we'll get a 404
			return;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		log.trace("CollectionsProcessor result:" + collectionsXml);
		
		try {
			data = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/addcollections.xsl",collectionsXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		libCommMessage.getPayload().setData(data);        
	}

	private String getXml(String url) throws IOException {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("X-LibraryCloud-API-Key",Config.getInstance().COLL_API_KEY);
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			return response.toString();
		} else {
			System.out.println("GET request failed");
			return null;
		}

	}
}
