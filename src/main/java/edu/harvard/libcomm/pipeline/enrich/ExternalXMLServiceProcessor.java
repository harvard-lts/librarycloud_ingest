package edu.harvard.libcomm.pipeline.enrich;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;


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
public class ExternalXMLServiceProcessor implements IProcessor {
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
		String urns = "0";

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
		try {
			xml = readUrl(uri.toURL().openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
		xml = xml.replace("&lt;record","<record");
		xml = xml.replace("record&gt;","record>");
		xml = xml.replace("&lt;leader","<leader");
		xml = xml.replace("leader&gt;","leader>");
		xml = xml.replace("&lt;controlfield","<controlfield");
		xml = xml.replace("controlfield&gt;","controlfield>");
		xml = xml.replace("&lt;datafield","<datafield");
		xml = xml.replace("datafield&gt;","datafield>");
		xml = xml.replace("&lt;subfield","<subfield");
		xml = xml.replace("subfield&gt;","subfield>");
		xml = xml.replace("&lt;/","</");
		xml = xml.replace("\"&gt;","\">");
		xml = xml.replace("/&gt;","/>");

		xml = "<" + wrapperToken + ">" + xml + "</" + wrapperToken + ">";
		//System.out.println("EXT XML: " + xml);
		log.trace("External Service result:" + xml);

		try {
			data = MessageUtils.transformPayloadData(libCommMessage,transformXSL,xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		//System.out.println("DATA: " + data);
		libCommMessage.getPayload().setData(data);
	}

	private String readUrl(InputStream is) {
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
