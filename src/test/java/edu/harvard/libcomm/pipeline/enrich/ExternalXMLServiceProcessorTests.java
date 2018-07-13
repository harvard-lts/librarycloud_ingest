package edu.harvard.libcomm.pipeline.enrich;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.net.URLConnection;

import java.util.Date;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.*;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.bind.JAXBException;

import org.w3c.dom.Document;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.Config;
import edu.harvard.libcomm.pipeline.IProcessor;
import edu.harvard.libcomm.pipeline.MessageUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Disabled;

import static org.mockito.Mockito.*;

import edu.harvard.libcomm.test.TestHelpers;
import edu.harvard.libcomm.test.TestMessageUtils;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExternalXMLServiceProcessorTests {

    class MyExternalXMLServiceProcessor extends ExternalXMLServiceProcessor {

        public String getUrns(LibCommMessage libCommMessage) throws Exception {
            return super.getUrns(libCommMessage);
        }


        public String getRecordIds(LibCommMessage libCommMessage) throws Exception {
            return super.getRecordIds(libCommMessage);
        }
    }


    @Test
    void testGetRecordIds() throws Exception {

        MyExternalXMLServiceProcessor p = new MyExternalXMLServiceProcessor();

        String cloudbody = TestHelpers.readFile("001763319.cloudbody.xml");
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(cloudbody, "UTF-8"));
        // System.out.println(lcm.getPayload().getData());

        String ids = p.getRecordIds(lcm);

         assertEquals("001763319", ids);
    }
}
