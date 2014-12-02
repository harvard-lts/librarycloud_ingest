package edu.harvard.libcomm.pipeline;

import java.io.InputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DeleteFileSplitter implements ISplitter {
	protected Logger log = Logger.getLogger(EADSplitter.class); 	

	public Iterator getIterator(InputStream is) throws Exception {
		DeleteFileIterator marcDeleteFileIterator = new DeleteFileIterator();
	    return marcDeleteFileIterator.getDeleteIds(is);
	}


}
