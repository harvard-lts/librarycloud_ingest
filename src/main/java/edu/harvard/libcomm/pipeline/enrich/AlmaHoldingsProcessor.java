package edu.harvard.libcomm.pipeline.enrich;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

/* Retrieve the MODS record associated with a holdings record, and update holdings data for it */
public class AlmaHoldingsProcessor extends ExternalXMLServiceProcessor implements IProcessor {

	protected Logger log = Logger.getLogger(AlmaHoldingsProcessor.class);

	public void processMessage(LibCommMessage libCommMessage) throws Exception {

		URI uri = new URI(Config.getInstance().SOLR_HOLDINGS_URL + "/select?q=bibId:(" + getRecordIds(libCommMessage) + ")&rows=250&wt=xml");
		process(libCommMessage, uri, "holdings", "src/main/resources/addalmaholdings.xsl");
	}


/*
	public void processMessage(LibCommMessage libCommMessage) throws Exception {

		String data = libCommMessage.getPayload().getData();

		URI uri = new URI(Config.getInstance().SOLR_URL + "/select?q=recordIdentifier:(" + getHoldingsRecordIds(libCommMessage) + ")&rows=250&wt=xml");
		String tempResult = getOriginalMODsRecords(uri);

       	// System.out.println("\n===\nMODS Record from Item API Solr\n===\n" + tempResult + "\n===\n");
       	// System.out.println("\n===\nHoldings Record from Pipeline\n===\n" + data + "\n===\n");

		// TODO: Remove the existing MODS location fields that are going to be replaced

		System.out.println("\n\n===TRANSFORMING==\n\n");
       	String transformed = transform(tempResult, "src/main/resources/addalmaholdings.xsl", data );
		// process(libCommMessage, uri, "holdings", "src/main/resources/addholdings.xsl");
		System.out.println("\n\n===DONE TRANSFORMING==\n\n");

       	// System.out.println("\n===\nTransformed MODS record\n===\n" + transformed + "\n===\n");
       	// String transformedAgain = transform(transformed, "src/main/resources/addalmaholdings.xsl", data );
       	// System.out.println("\n===\nTransformed again MODS record\n===\n" + transformedAgain + "\n===\n");

		// TODO: Change this message into Bib record message that can be tossed onto the bib queue

	}

	private String getHoldingsRecordIds(LibCommMessage libCommMessage) throws Exception {
		String recids = "0";

		try {
			recids = MessageUtils.transformPayloadData(libCommMessage,"src/main/resources/recids-MARC.xsl",null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return recids;
	}

	private String transform(String data, String xslPath, String param) throws Exception {

		StringReader dataReader = new StringReader(data);
		StringWriter writer = new StringWriter();
		final InputStream xsl = new FileInputStream(xslPath);
		final TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);

        StreamSource styleSource = new StreamSource(xsl);
        Transformer transformer = tFactory.newTransformer(styleSource);

        if (param != null) {
    		transformer.setParameter("param1", new StreamSource(new StringReader(param)));
        }

        StreamSource xmlSource = new StreamSource(dataReader);
        StreamResult result = new StreamResult(writer);
        transformer.transform(xmlSource, result);

        String finalResult = writer.toString();

		finalResult = finalResult.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","");
		finalResult = finalResult.replace("&lt;","<");
		finalResult = finalResult.replace("&gt;",">");

		return finalResult;
	}

	private String getOriginalMODsRecords(URI solrQuery) throws Exception {
		String data = null;

		try {
			data = readUrl(solrQuery.toURL().openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return transform(data, "src/main/resources/solroriginalmods.xsl", null);

	}
*/

}
