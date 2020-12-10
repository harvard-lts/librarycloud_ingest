package edu.harvard.libcomm.pipeline.enrich;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

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
public class AddMarcLocationProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(AddMarcLocationProcessor.class); 	
	private String marcBaseUrl;

	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		try {
			log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath() + "," + libCommMessage.getHistory().getEvent().get(0).getMessageid());
		} catch (Exception e) {
			log.error("Unable to log message info");
		}

		String data = null;
		try {
			data = MessageUtils.transformPayloadData(libCommMessage,
				"src/main/resources/addmarcurl.xsl", 
			//	"<marcpath>" + marcBaseUrl + "</marcpath>");
				"<marcpath>harvard.librarycloud.marc</marcpath>";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		libCommMessage.getPayload().setData(data);		
	}

	public void setMarcBaseUrl(String s) {
		this.marcBaseUrl = s;
	}

	public String getMarcBaseUrl() {
		return this.marcBaseUrl;
	}
}
