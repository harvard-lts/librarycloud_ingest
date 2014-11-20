package edu.harvard.libcomm.pipeline;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;

public class HoldingsProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(HoldingsProcessor.class); 	
	
	public void processMessage(LibCommMessage libCommMessage) throws Exception {	
	
		String data = null;
		String recids = "0";

		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids.xsl",null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		URI uri = new URI(Config.getInstance().HOLDINGS_URL + "?filter=MarcLKRB:(" + recids + ")&fields=MarcLKRB,Marc852B,Marc856U,DisplayCallNumber&limit=250");
		JSONTokener tokener;
		try {
			Date start = new Date();
			tokener = new JSONTokener(uri.toURL().openStream());
			Date end = new Date();
			log.trace("HoldingProcesser query time: " + (end.getTime() - start.getTime()));
			log.trace("HoldingProcesser query : " +  uri.toURL());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		JSONObject holdingsJson = new JSONObject(tokener);
		String holdingsXml = XML.toString(holdingsJson);
		holdingsXml = "<holdings>" + holdingsXml + "</holdings>";
		log.trace("HoldingsProcessor result:" + holdingsXml);
		
		try {
			data = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/addholdings.xsl",holdingsXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		Payload payload = new Payload();
		payload.setData(data);
        libCommMessage.setPayload(payload);
        
	}
}
