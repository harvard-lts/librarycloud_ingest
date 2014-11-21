package edu.harvard.libcomm.pipeline;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;

public class EADComponentIterator implements Iterator<String> {
	protected Logger log = Logger.getLogger(EADComponentIterator.class);

	protected EADReader eadReader;
	protected NodeList nodes;
	protected DOMSource domSource;
	protected Transformer transformer;
	protected int position = 0;

    public EADComponentIterator(EADReader reader) throws Exception {
        this.eadReader = reader;
        nodes = reader.getNodes();
        domSource = reader.getDOMSource();
        transformer = buildTransformer("src/main/resources/eadcomponent2mods.xsl");        	
    }

    @Override
    public boolean hasNext() {
    	return ((nodes != null) && (position < nodes.getLength()));
    }

    @Override
    public String next() {
    	log.trace("Processing node " + position + " of " + nodes.getLength());
    	while ((nodes != null) && (position < nodes.getLength())) {
	        String nodeName = nodes.item(position).getNodeName();
	        String nodeValue = nodes.item(position).getNodeValue();
	        position++;
	        if (nodeName.equals("id")) {
	        	String eadComponentMods = null;
				try {
					eadComponentMods = transformOASIS(nodeValue);
				} catch (Exception e) {
					e.printStackTrace();
					throw new NoSuchElementException();
				}
				LibCommMessage lcmessage = new LibCommMessage();
				Payload payload = new Payload();
				payload.setFormat("MODS");
				payload.setSource("OASIS");
				payload.setData(eadComponentMods);
				lcmessage.setCommand("ENRICH");
	        	lcmessage.setPayload(payload);
	        	try {
	        		return MessageUtils.marshalMessage(lcmessage);
				} catch (JAXBException e) {
					e.printStackTrace();
					return null;
				}		        		
	        }
    	}
    	throw new NoSuchElementException();
	}

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected Transformer buildTransformer(String xslFilePath) throws Exception {
		final InputStream xsl = new FileInputStream(xslFilePath);
		final TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
        StreamSource styleSource = new StreamSource(xsl);
        return tFactory.newTransformer(styleSource);    	
    }

	protected String transformOASIS (String xslParam) throws Exception {		
       	this.transformer.setParameter("componentid", xslParam);
		StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(this.domSource, result);
        return writer.toString();
	}
    
}
