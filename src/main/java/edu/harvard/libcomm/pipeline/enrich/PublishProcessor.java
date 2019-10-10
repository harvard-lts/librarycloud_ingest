package edu.harvard.libcomm.pipeline.enrich;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;
import org.apache.log4j.Logger;

public class PublishProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(PublishProcessor.class);
	@Override
	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		String data = null;
		libCommMessage.setCommand("publish");
		try {
			log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath() + "," + libCommMessage.getHistory().getEvent().get(0).getMessageid());
		} catch (Exception e) {
			log.error("Unable to log message info");
		}
		String modsCount = null;
		try {
		  TimeZone tz = TimeZone.getTimeZone("UTC");
		  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		  df.setTimeZone(tz);
		  String processingDate = df.format(new Date());
		  //log.info("processingDate: " + processingDate);
		  data = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/publish-mods.xsl", "<processingDate>"+processingDate+"</processingDate>");
		  LibCommMessage tempMessage = new LibCommMessage();
		  Payload tempPayload = new Payload();
		  tempPayload.setData(data);
		  tempMessage.setPayload(tempPayload);
		  modsCount = MessageUtils.transformPayloadData(tempMessage, "src/main/resources/recids-count.xsl", null);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		if(modsCount == null || modsCount.equals("") || modsCount.equals("1")) {
			//empty data set
			data = "";
		}

		libCommMessage.getPayload().setData(data);
	}


}
