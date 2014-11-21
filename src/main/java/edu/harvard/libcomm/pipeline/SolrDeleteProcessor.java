package edu.harvard.libcomm.pipeline;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;

public class SolrDeleteProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(CollectionsProcessor.class);
	private Integer commitWithinTime = -1;

	public void processMessage(LibCommMessage libCommMessage) throws Exception {

		String recordId = libCommMessage.getPayload().getData();
		deleteFromSolr(recordId);

	}

	private void deleteFromSolr(String id) throws Exception{
		HttpSolrServer server = null;
		Date start = new Date();
		server = SolrServer.getSolrConnection();
		UpdateRequest update = new UpdateRequest();
		update.deleteById(id);
		if (commitWithinTime > 0) {
			update.setCommitWithin(commitWithinTime);
			update.process(server);
		} else {
			update.process(server);
			server.commit();
		}
		Date end = new Date();
		log.debug("Solr delete time: " + (end.getTime() - start.getTime()));
	}

	public void setCommitWithinTime(Integer commitWithinTime) {
		this.commitWithinTime = commitWithinTime;
	}

	public Integer getCommitWithinTime() {
		return this.commitWithinTime;
	}


}
