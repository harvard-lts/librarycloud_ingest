package edu.harvard.libcomm.pipeline.enrich;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

public class PublishProcessor implements IProcessor {

	@Override
	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		String data = null;
		libCommMessage.setCommand("PUBLISH");
		String modsCount = null;
		try {
      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
      df.setTimeZone(tz);
      String processingDate = df.format(new Date());

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
