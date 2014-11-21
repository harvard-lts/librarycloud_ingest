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

public class LCCProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(LCCProcessor.class); 	
	
	public void processMessage(LibCommMessage libCommMessage) throws Exception {	
	
		String data = null;
		String recids = "0";
		
		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids.xsl",null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		URI uri = new URI(Config.getInstance().ITEM_URL + "?filter=collection:hollis_catalog&filter=id_inst:(" + recids + ")&fields=loc_call_num_subject,id_inst&limit=250");
		JSONTokener tokener;
		try {
			Date start = new Date();
			tokener = new JSONTokener(uri.toURL().openStream());
			Date end = new Date();
			log.trace("LCCProcesser query time: " + (end.getTime() - start.getTime()));
			log.trace("LCCProcesser query : " +  uri.toURL());
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		
		JSONObject lccJson = new JSONObject(tokener);
		String lccXml = XML.toString(lccJson);
		lccXml = "<lcc>" + lccXml + "</lcc>";
		log.trace("LCCProcessor result:" + lccXml);
		//System.out.println("lccXml: " + lccXml);
		
		try {
			data = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/addlcc.xsl",lccXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		Payload payload = new Payload();
		payload.setData(data);
        libCommMessage.setPayload(payload);
        
	}

}