package edu.harvard.libcomm.pipeline;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class EADReader {

	private InputStream is;
	private NodeList nodes;
	private DOMSource domSource;	

    public EADReader(InputStream is) {
		this.is = is;	        
    }

    public NodeList getNodes() throws ParserConfigurationException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
    	Document doc = getDocument(is);
    	this.domSource = new DOMSource(doc);
    	NodeList nodes = getNodeList(doc);
    	return nodes;
    }

    public DOMSource getDOMSource() throws ParserConfigurationException, SAXException, IOException {
    	if (this.domSource == null) {
	    	Document doc = getDocument(is);
    		this.domSource = new DOMSource(doc);
    	}
		return this.domSource;
    }

	private Document getDocument (InputStream is) throws ParserConfigurationException, SAXException, IOException {
	   	Document doc = null;
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.parse(is);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw e;
		} catch (SAXException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return doc;
	}
	
	private NodeList getNodeList (Document doc) throws XPathExpressionException {

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			Object result = null;
			XPathExpression componentId = null;
			try {
				componentId = xpath.compile("//c/@id");
				result = componentId.evaluate(doc, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
				throw e;
			}

			NodeList nodes = (NodeList) result;
			return nodes;
	}

}
