package edu.harvard.libcomm.pipeline.solr;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.IProcessor;

public class SolrDeleteProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(SolrDeleteProcessor.class);
	private Integer commitWithinTime = -1;

	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		String recordId = libCommMessage.getPayload().getData();
		recordId = recordId.replace("*","");
		recordId = recordId.replace(" ","");
		String source = libCommMessage.getPayload().getSource();
		System.out.println("deleting: " + source + ":" + recordId);
		if (recordId.isEmpty())
			log.debug("The record id is empty");
		else if (source.equals("VIA")) {
			if (recordId.startsWith("olvwork"))
				recordId = recordId.replace("olvwork","W");
			if (recordId.startsWith("olvgroup"))
				recordId = recordId.replace("olvgroup","G");
			if (recordId.startsWith("olvsite"))
				recordId = recordId.replace("olvsite","S");
			deleteFromSolrByQuery(recordId + "_*");
		}
		else if (source.equals("OASIS"))
			deleteFromSolrByQuery(recordId + "c*");
		else
			deleteFromSolr(recordId);

	}

	private void deleteFromSolr(String id) throws Exception{
    HttpSolrClient client = SolrClient.getSolrConnection();
		UpdateRequest update = new UpdateRequest();
		update.deleteById(id);
		if (commitWithinTime > 0) {
			update.setCommitWithin(commitWithinTime);
			update.process(client);
		} else {
			update.process(client);
			client.commit();
		}
	}

	private void deleteFromSolrByQuery(String idQry) throws Exception {
		idQry = idQry.replace(" ","");
		HttpSolrClient client = SolrClient.getSolrConnection();
		UpdateRequest update = new UpdateRequest();
		update.deleteByQuery("recordIdentifier:" + idQry);
		if (commitWithinTime > 0) {
			update.setCommitWithin(commitWithinTime);
			update.process(client);
		} else {
			update.process(client);
			client.commit();
		}
	}

	public void setCommitWithinTime(Integer commitWithinTime) {
		this.commitWithinTime = commitWithinTime;
	}

	public Integer getCommitWithinTime() {
		return this.commitWithinTime;
	}


}
