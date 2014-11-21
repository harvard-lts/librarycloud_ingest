package edu.harvard.libcomm.pipeline;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;


public class EADRawSplitter implements ISplitter {
	protected Logger log = Logger.getLogger(EADRawSplitter.class); 	

	public Iterator getIterator(InputStream is) throws Exception {
		EADReader reader = new EADReader(is);
	    return new EADRawComponentIterator(reader);
	}


}