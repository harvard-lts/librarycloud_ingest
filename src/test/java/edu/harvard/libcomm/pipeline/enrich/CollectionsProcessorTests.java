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
import edu.harvard.libcomm.test.HttpUrlStreamHandler;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CollectionsProcessorTests {

    private static HttpUrlStreamHandler httpUrlStreamHandler;

    @BeforeAll
    public static void setupURLStreamHandlerFactory() {
        httpUrlStreamHandler = TestHelpers.getHttpUrlStreamHandler();
    }


    @BeforeEach
    public void reset() {
        httpUrlStreamHandler.resetConnections();
    }

    @Test
    void addCollectionsData() throws Exception {

        CollectionsProcessor p = new CollectionsProcessor();

        LibCommMessage lcm = new LibCommMessage();
        LibCommMessage.Payload pl = new LibCommMessage.Payload();

        String xml = TestHelpers.readFile("001490591");

        pl.setFormat("mods");
        pl.setData(xml);
        lcm.setPayload(pl);

        String collectionsResponse = TestHelpers.readFile("collections_items_001490591.xml");

        String href = Config.getInstance().COLLECTIONS_URL + "/collections/items/001490591.xml";
        System.out.println("TEST: " +href);

        URLConnection urlConnection = mock(URLConnection.class);
        httpUrlStreamHandler.addConnection(new URL(href), urlConnection);

        InputStream stream = new ByteArrayInputStream(collectionsResponse.getBytes(StandardCharsets.UTF_8));
        when(urlConnection.getInputStream()).thenReturn(stream);

        try {
            p.processMessage(lcm);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = lcm.getPayload().getData();
        System.out.println(result);

        // byte[] xmlBytes = xml.getBytes();
        // Path p1 = Paths.get("./tmp/holdings_input.xml");
        // Files.write(p1, xmlBytes);

        // byte[] resultBytes = result.getBytes();
        // Path p2 = Paths.get("./tmp/holdings_output.xml");
        // Files.write(p2, resultBytes);

        InputStream modsIS = IOUtils.toInputStream(result, "UTF-8");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false);
        builderFactory.setNamespaceAware(false);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document mods = builder.parse(modsIS);
        XPath xPath = XPathFactory.newInstance().newXPath();


        String setName = TestHelpers.getXPath("//sets:setName", mods);
        assertEquals("scores", setName);

        String systemIdentifier = TestHelpers.getXPath("//sets:systemId", mods);
        assertEquals("44001", systemIdentifier);

        String setSpec = TestHelpers.getXPath("//sets:setSpec", mods);
        assertEquals("scores", setSpec);

        String baseUrl = TestHelpers.getXPath("//sets:baseUrl", mods);
        assertEquals("http://dcp.lib.harvard.edu/spotlight/digital-scores-and-libretti", baseUrl);


        Number setTagsCount = TestHelpers.getNodeCount("//sets:set[1]/*", mods);
        assertEquals(4.0, setTagsCount);


    }
}
