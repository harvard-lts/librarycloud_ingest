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

public class EADRawComponentIterator extends EADComponentIterator {
	protected Logger log = Logger.getLogger(EADRawComponentIterator.class);

    public EADRawComponentIterator(EADReader reader) throws Exception {
    	super(reader);
    }

    @Override
    public String next() {
    	log.trace("Processing node " + position + " of " + nodes.getLength());
    	String eadComponentMods = "";
    	while ((nodes != null) && (position < nodes.getLength())) {
	        String nodeName = nodes.item(position).getNodeName();
	        String nodeValue = nodes.item(position).getNodeValue();
	        position++;
	        if (nodeName.equals("id")) {
				try {
					eadComponentMods = transformOASIS(nodeValue);
				} catch (Exception e) {
					e.printStackTrace();
					throw new NoSuchElementException();
				}
	        }
	        return eadComponentMods;
    	}
		throw new NoSuchElementException();
	}
    
}
