package edu.harvard.libcomm.pipeline.solr;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.IProcessor;

public class SolrHoldingsDeleteProcessor implements IProcessor {
    protected Logger log = Logger.getLogger(SolrDeleteProcessor.class);
    private Integer commitWithinTime = -1;

    public void processMessage(LibCommMessage libCommMessage) throws Exception {
        String recordId = libCommMessage.getPayload().getData();
        System.out.println("recordId: " + recordId);
        deleteFromSolr(recordId);
    }

    private void deleteFromSolr(String id) throws Exception{
        HttpSolrClient client = SolrHoldingsClient.getSolrConnection();
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

    public void setCommitWithinTime(Integer commitWithinTime) {
        this.commitWithinTime = commitWithinTime;
    }

    public Integer getCommitWithinTime() {
        return this.commitWithinTime;
    }


}
