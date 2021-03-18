package edu.harvard.libcomm.pipeline.solr;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

public class SolrProcessor implements IProcessor {
	protected Logger log = Logger.getLogger(SolrProcessor.class); 	

	private Integer commitWithinTime = -1;
	private String drsObjectId = null;

	@Override
	public void processMessage(LibCommMessage libCommMessage) throws Exception {
		libCommMessage.setCommand("publish-to-solr");

		try {
			log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath() + "," + libCommMessage.getHistory().getEvent().get(0).getMessageid());
		} catch (Exception e) {
			log.error("Unable to log message info");
		}

		try {
			String solrXml = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/mods2solr.xsl",null);
			//log.info("solrXml: " + solrXml);
			populateIndex(solrXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		libCommMessage.setCommand("done");
		// 2021-02-12 pass the drsObjectId for dropping message on iiif bib metadata update mq queue
		libCommMessage.getPayload().setData("{\"drsObjectId\":\"" + drsObjectId + "\"}");
		log.info(libCommMessage.getCommand() + "," + libCommMessage.getPayload().getSource() + "," + libCommMessage.getPayload().getFilepath()); // + "," + libCommMessage.getHistory().getEvent().get(0).getMessageid());

	}
	
	private void populateIndex(String solrXml) throws Exception {

	    HttpSolrClient client = null;
		Date start = new Date();
	    client = SolrClient.getSolrConnection();
		UpdateRequest update = new UpdateRequest();
		update.add(getSolrInputDocumentList(solrXml));
		if (commitWithinTime > 0) {
			update.setCommitWithin(commitWithinTime);
		    update.process(client);
		} else {
		    update.process(client);
    	    client.commit();
		}
		Date end = new Date();
		log.info("Inserting DRS Metadata into \"librarycloud\"  solr collection");
		log.debug("Solr insert query time: " + (end.getTime() - start.getTime()));
	}

	private List<SolrInputDocument> getSolrInputDocumentList(String solrXml) throws Exception {

	    ArrayList<SolrInputDocument> solrDocList = new ArrayList<SolrInputDocument>();

	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    ByteArrayInputStream bais = new ByteArrayInputStream(solrXml.getBytes("utf-8"));
	    Document doc = dBuilder.parse(new InputSource(bais));

	    NodeList docList = doc.getElementsByTagName("doc");

	    for (int docIdx = 0; docIdx < docList.getLength(); docIdx++) {

	        Node docNode = docList.item(docIdx);

	        if (docNode.getNodeType() == Node.ELEMENT_NODE) {

	            SolrInputDocument solrInputDoc = new SolrInputDocument();

	            Element docElement = (Element) docNode;

	            NodeList fieldsList = docElement.getChildNodes();

	            for (int fieldIdx = 0; fieldIdx < fieldsList.getLength(); fieldIdx++) {

	                Node fieldNode = fieldsList.item(fieldIdx);

	                if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {

	                    Element fieldElement = (Element) fieldNode;

	                    String fieldName = fieldElement.getAttribute("name");
	                    String fieldValue = fieldElement.getTextContent();
	                    // 2021-02-13 grab drsObjectId to put in message
	                    if (fieldName.equals("drsObjectId"))
	                    	drsObjectId = fieldValue;
						//log.info(fieldName + ":" + fieldValue);
	                    solrInputDoc.addField(fieldName, fieldValue);
	                }

	            }

	            solrDocList.add(solrInputDoc);
	        }
	    }

	    return solrDocList;

	}

	public void setCommitWithinTime(Integer commitWithinTime) {
		this.commitWithinTime = commitWithinTime;
	}

	public Integer getCommitWithinTime() {
		return this.commitWithinTime;
	}

}
