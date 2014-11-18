package edu.harvard.libcomm.pipeline;

import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;

public class FilterUnchangedProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(FilterUnchangedProcessor.class); 

	public void processMessage(LibCommMessage libCommMessage) throws Exception {	
		String recids = "0";
		libCommMessage.setCommand("ENRICH");
		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids-MODS-checksum.xsl",null);	
		} catch (Exception e) {
			log.info(e);
			log.error("Could not transform record from MARC to MODS");
			throw e;
		}	
		log.info("RecIds Result:" + recids);
		Map<String, Integer> recordMap = SplitIdAndChecksum(recids);
		
        Payload payload = new Payload();
        payload.setSource("mods");
        payload.setFormat("mods");
        payload.setData(recids);
//        libCommMessage.setPayload(payload);
	}
	
	private Map<String, Integer> SplitIdAndChecksum(String body)
	{
		Map<String, Integer> result = new HashMap<String, Integer>();
		String[] tuples = body.split("\\|");
		for(String tuple : tuples)
		{
			String[] row = tuple.split("\\,");
			result.put(row[0], Integer.parseInt(row[1]));
		}
		
		return result;
	}

	
}
