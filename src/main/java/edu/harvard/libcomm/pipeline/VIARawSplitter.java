package edu.harvard.libcomm.pipeline;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;


public class VIARawSplitter implements ISplitter {
	protected Logger log = Logger.getLogger(VIARawSplitter.class); 	

	public Iterator getIterator(InputStream is) throws Exception {
		
		VIAReader reader = new VIAReader(is);
	    return new VIARawComponentIterator(reader);
	}


}