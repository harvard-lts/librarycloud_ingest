package edu.harvard.libcomm.pipeline;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;


/**
 * This version of the Splitter does not assume that there's an existing LibComMessage
 * command with a reference to a file to be processed - it operates on raw data that
 * isn't yet structured into a message
 */
public class RawSplitter extends Splitter {
	protected Logger log = Logger.getLogger(RawSplitter.class); 	

	@Override
	protected InputStream getResourceInputStream(String body) throws FileNotFoundException, IOException, JAXBException  {
		return new ByteArrayInputStream(body.getBytes("UTF-8"));
	}

}