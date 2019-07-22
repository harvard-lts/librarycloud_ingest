package edu.harvard.libcomm.pipeline.marc;

import org.apache.log4j.Logger;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

public class ModsProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(ModsProcessor.class);

	private String stylesheet = "src/main/resources/MARC21slim2MODS3-6.xsl";

	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		//System.out.println("NORMMARCFILEPATH: " + libCommMessage.getPayload().getFilepath());
		//log.info("NORMALIZEMARC");
		String modsCollection = null;
		libCommMessage.setCommand("ENRICH");
		try {
			modsCollection = MessageUtils.transformPayloadData(libCommMessage, this.getStylesheet(), null);
		} catch (Exception e) {
			log.error("Could not transform record from MARC to MODS");
			throw e;
		}

		log.trace("ModProcessor Result:" + modsCollection);
        libCommMessage.getPayload().setData(modsCollection);
	}

	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	public String getStylesheet() {
		return this.stylesheet;
	}

}
