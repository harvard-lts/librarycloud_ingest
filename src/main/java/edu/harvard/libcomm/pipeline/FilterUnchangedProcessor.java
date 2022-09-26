package edu.harvard.libcomm.pipeline;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.dao.IngestDAO;

/**
 * Take a message with a set of MODS records, extract the individual MODS records, and compare
 * each one against a checksum saved in a database, keyed by item ID. If the MODS record 
 * matches the checksum (i.e. is unchanged) remove it from the message.
 */
public class FilterUnchangedProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(FilterUnchangedProcessor.class);
	
	@Autowired
	private IngestDAO ingestDao;
	

	public void processMessage(LibCommMessage libCommMessage) throws Exception {	
		String recids = "0";
		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids-MODS-checksum.xsl",null);	
		} catch (Exception e) {
			log.info(e);
			log.error("Could not extract record ids MODS message");
			throw e;
		}	

		Map<String, Integer> recordMap = splitIdAndChecksum(recids);

        Payload payload = new Payload();

		Set<String> duplicateRecordIds = ingestDao.checkAndSaveItemChecksum(recordMap);
		log.info(recordMap.size() + " records, " + duplicateRecordIds.size() + " duplicates");
		if(duplicateRecordIds == null ||  duplicateRecordIds.size() == 0){
	        return;
		} else if (duplicateRecordIds.size() == recordMap.size()){
			libCommMessage.getPayload().setData("");
			return;
		}
		//remove duplicates
		String duplicateRecordIdString = "<recordIdList><recordId>" + StringUtils.join(duplicateRecordIds,"</recordId><recordId>") + "</recordId></recordIdList>"; 
		String scrubbedMessage = MessageUtils.transformPayloadData(libCommMessage, "src/main/resources/Remove-MODS-Records.xsl", duplicateRecordIdString);
        
        libCommMessage.getPayload().setData(scrubbedMessage);
	}
	
	private Map<String, Integer> splitIdAndChecksum(String body)
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
