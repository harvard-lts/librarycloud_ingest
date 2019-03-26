package edu.harvard.libcomm.pipeline.ted;

/**
 * Created by mjv162 on 9/7/2018.
 */
import java.io.InputStream;
import java.util.Iterator;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.ISplitter;
import edu.harvard.libcomm.pipeline.ted.TedIterator;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

public class TedSplitter implements ISplitter {
    protected Logger log = Logger.getLogger(edu.harvard.libcomm.pipeline.ted.TedSplitter.class);
    private int chunkSize;

    public TedSplitter(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Iterator getIterator(InputStream is) {
        TedReader reader = new TedReader(is);
        return new TedIterator(reader, this.chunkSize);
    }

}
