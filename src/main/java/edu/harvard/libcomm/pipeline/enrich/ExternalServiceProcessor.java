package edu.harvard.libcomm.pipeline.enrich;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

/**
 * Base class for processors that call an external service that takes a list of 
 * record IDs, and then uses the results to transform the records using and XSL
 * stylesheet.
 */
public class ExternalServiceProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(ExternalServiceProcessor.class); 	
	
	public void processMessage(LibCommMessage libCommMessage) throws Exception {	
		throw new Exception("Not implemented - must override process() function");
	}

	protected String getRecordIds(LibCommMessage libCommMessage) throws Exception {
		String recids = "0";

		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids.xsl",null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return recids;
	}

	protected String getUrns(LibCommMessage libCommMessage) throws Exception {
		String urns = "";

		try {
			urns = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/urns.xsl",null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return urns;
	}

	protected void process(LibCommMessage libCommMessage, URI uri, String wrapperToken, String transformXSL) throws Exception {
		
		String data = null;
		String xml = "";
		if (uri == null) {
			xml = "";
		}
		else {
			JSONTokener tokener;
			try {
				//tokener = new JSONTokener(uri.toURL().openStream());
				tokener = new JSONTokener(IOUtils.toString(uri.toURL().openStream(), "UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}

			JSONObject json = new JSONObject(tokener);
			xml = XML.toString(json);
		}
		xml = "<" + wrapperToken + ">" + xml + "</" + wrapperToken + ">";
		//log.info("xml: " + xml);
		log.trace("External Service result:" + xml);

		try {
			data = MessageUtils.transformPayloadData(libCommMessage,transformXSL,xml);
			//log.info("MODS data: " + data);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		//System.out.println("DATA: " + data);
		libCommMessage.getPayload().setData(data);		
	}

}
