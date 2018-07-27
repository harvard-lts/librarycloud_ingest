package edu.harvard.libcomm.pipeline;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.dao.IngestDAO;

/**
 * Take a message with a set of Alma MARC records and remove those that should be suppressed
 */
public class FilterSuppressedProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(FilterSuppressedProcessor.class);

	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		String data = null;
		String modsCount = null;
		try {
			data = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/remove-marc-suppressed.xsl",null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		libCommMessage.getPayload().setData(data);
	}

}
