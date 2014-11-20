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
			payload.setData("");
			libCommMessage.setPayload(payload);
			return;
		}
		//remove duplicates
		String duplicateRecordIdString = "<recordIdList><recordId>" + StringUtils.join(duplicateRecordIds,"</recordId><recordId>") + "</recordId></recordIdList>"; 
		String scrubbedMessage = MessageUtils.transformPayloadData(libCommMessage, "src/main/resources/Remove-MODS-Records.xsl", duplicateRecordIdString);
        
        payload.setData(scrubbedMessage);
        libCommMessage.setPayload(payload);
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
