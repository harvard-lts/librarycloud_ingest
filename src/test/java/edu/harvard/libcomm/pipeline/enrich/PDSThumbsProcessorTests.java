package edu.harvard.libcomm.pipeline.enrich;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.*;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;

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

import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.test.TestHelpers;

import edu.harvard.libcomm.test.HttpUrlStreamHandler;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PDSThumbsProcessorTests {

    private static HttpUrlStreamHandler httpUrlStreamHandler;
    private static HttpURLConnection httpUrlConnection;
    private PDSThumbsProcessor p = new PDSThumbsProcessor();

    @BeforeAll
    public static void setupURLStreamHandlerFactory() {
        httpUrlStreamHandler = TestHelpers.getHttpUrlStreamHandler();
    }

    @BeforeAll
    public static void setupMessageAndUrns() {

    }

    @BeforeEach
    public void reset() {
        httpUrlStreamHandler.resetConnections();
        httpUrlConnection = mock(HttpURLConnection.class);
    }

    @Test
    void onlyIgnoreEmptyPreviewTagsWhenGettingPDSUrns() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "pds-thumbs-processor-tests-sample-1.xml");

        String urls = MessageUtils.transformPayloadData(lcm,"src/main/resources/pds_urns.xsl",null).replace(",*$", "");

        assertEquals("http://nrs.harvard.edu/urn-3:fhcl.loeb:11111,http://nrs.harvard.edu/urn-3:fhcl.loeb:22222", urls);
    }


    @Test
    void dontObliterateExistingThumbnails() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "001763319.PDSThumbsProcessor.xml");

        String urls = MessageUtils.transformPayloadData(lcm,"src/main/resources/pds_urns.xsl",null).replace(",*$", "");
        for (String url : urls.split(",")) {
            TestHelpers.mockResponse(url, 400);
        }
        p.processMessage(lcm);

        Document doc = TestHelpers.extractXmlDoc(lcm);

        String thumb1Url = TestHelpers.getXPath("//mods:mods[2]//mods:url[@access='preview']", doc);

        assertEquals("http://ids.lib.harvard.edu/ids/view/45562415?width=150&height=150&usethumb=y", thumb1Url);
    }

    @Test
    void handleResponse400() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "pds-thumbs-processor-tests-sample-1.xml");

        String urls = MessageUtils.transformPayloadData(lcm,"src/main/resources/pds_urns.xsl",null).replace(",*$", "");
        for (String s : urls.split(",")) {
            httpUrlStreamHandler.addConnection(new URL(s), httpUrlConnection);
            when(httpUrlConnection.getResponseCode()).thenReturn(400);
        }
        p.processMessage(lcm);

        assertEquals(true, true);
    }

    @Test
    void gettingIIIFThumbs() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "pds-thumbs-processor-tests-sample-1.xml");

        String urls = MessageUtils.transformPayloadData(lcm,"src/main/resources/pds_urns.xsl",null).replace(",*$", "");
        for (String url : urls.split(",")) {
            String id = StringUtils.substringAfterLast(url, "/");

            TestHelpers.mockRedirect(url, "http://pds.lib.harvard.edu/pds/view/"+id);
            TestHelpers.mockResponse("https://iiif.lib.harvard.edu/manifests/drs:"+id, 200, "iiif_response.json");
        }

        p.processMessage(lcm);

        Document doc = TestHelpers.extractXmlDoc(lcm);

        String thumb1Url = TestHelpers.getXPath("//mods:mods[1]//mods:url[@access='preview']", doc);

        assertEquals("https://ids.lib.harvard.edu/ids/iiif/8316207/full/,150/0/native.jpg", thumb1Url);
    }
}
