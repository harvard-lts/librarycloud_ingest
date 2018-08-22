package edu.harvard.libcomm.pipeline.via;

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

public class VIARawComponentIterator extends VIAComponentIterator {
	protected Logger log = Logger.getLogger(VIARawComponentIterator.class);

    public VIARawComponentIterator(VIAReader reader) throws Exception {
        super(reader);
    }

  //   @Override
  //   public String next() {
  //   	log.trace("Processing node " + position + " of " + nodes.getLength());
  //       String viaComponentMods = "";
  //   	while ((nodes != null) && (position < nodes.getLength())) {
	//         String nodeName = nodes.item(position).getNodeName();
	//         String nodeValue = nodes.item(position).getNodeValue();
	//         position++;
	// 		try {
	// 			viaComponentMods += transformVIA(nodeValue);
	// 		} catch (Exception e) {
	// 			e.printStackTrace();
	// 			throw new NoSuchElementException();
	// 		}
  //           break;
  //   	}
  //       return viaComponentMods;
	// }
    
}
