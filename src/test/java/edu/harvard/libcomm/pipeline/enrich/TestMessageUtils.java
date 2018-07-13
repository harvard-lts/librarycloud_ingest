package edu.harvard.libcomm.test;

import java.io.*;
import javax.xml.bind.JAXBException;
import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.pipeline.MessageUtils;

public class TestMessageUtils extends MessageUtils {
    public static LibCommMessage unmarshalLibCommMessage(InputStream is) throws JAXBException {
        return MessageUtils.unmarshalLibCommMessage(is);
    }
}
