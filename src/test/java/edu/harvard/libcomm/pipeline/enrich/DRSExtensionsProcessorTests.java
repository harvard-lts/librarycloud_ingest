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
import edu.harvard.libcomm.test.TestMessageUtils;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DRSExtensionsProcessorTests {

    private static HttpUrlStreamHandler httpUrlStreamHandler;
    private static DRSExtensionsProcessor p;


    @BeforeAll
    public static void setup() {
        httpUrlStreamHandler = TestHelpers.getHttpUrlStreamHandler();
        p = new DRSExtensionsProcessor();

    }


    @BeforeEach
    public void reset() {
        httpUrlStreamHandler.resetConnections();
    }


    @Test
    void addDRSExtensionsData() throws Exception {

        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "enrich-05-modscollection.xml");

        String urns = MessageUtils.transformPayloadData(lcm,"src/main/resources/urns.xsl",null).replace(" ", "+");
        String url = Config.getInstance().SOLR_EXTENSIONS_URL + "/select?q=urn_keyword:("+urns+")&rows=250";

        TestHelpers.mockResponse(url, 200, "solr_extensions_response_001490591.json");

        p.processMessage(lcm);
        Document doc = TestHelpers.extractXmlDoc(lcm);

        String thumb1Url = TestHelpers.getXPath("//mods:mods[1]//mods:url[@access='preview']", doc);

        assertEquals("http://ids.lib.harvard.edu/ids/view/8316207?width=150&height=150&usethumb=y", thumb1Url);

        //Test case with an empty <url access="preview" /> tag
        String thumb2Url = TestHelpers.getXPath("//mods:mods[2]//mods:url[@access='preview']", doc);

        assertEquals("http://ids.lib.harvard.edu/ids/view/8316207?width=150&height=150&usethumb=y", thumb2Url);

        // Test case with an existing non-empty <url access="preview"> tag
        String thumb3Url = TestHelpers.getXPath("//mods:mods[3]//mods:url[@access='preview']", doc);

        assertEquals("http://dontreplaceme.com/55555", thumb3Url);

        String thumb4Url = TestHelpers.getXPath("//mods:mods[4]//mods:url[@access='preview']", doc);

        assertEquals("http://ids.lib.harvard.edu/ids/view/421568540?width=150&height=150&usethumb=y", thumb4Url);

        // Test case 2 <url> elements
        String thumb5Url = TestHelpers.getXPath("//mods:mods[5]//mods:url[@access='preview']", doc);

        assertEquals("http://ids.lib.harvard.edu/ids/view/421568540?width=150&height=150&usethumb=y", thumb5Url);
    }

    @Test
    void test001763319() throws Exception {

        LibCommMessage lcm = TestHelpers.unmarshalLibCommMessage("001763319.enrich-05.cloudbody.xml");

        String urns = MessageUtils.transformPayloadData(lcm,"src/main/resources/urns.xsl",null).replace(" ", "+");

        String url = Config.getInstance().SOLR_EXTENSIONS_URL + "/select?q=urn_keyword:("+urns+")&rows=250";

        TestHelpers.mockResponse(url, 200, "001763319.drsextensions.json");
        String input = lcm.getPayload().getData();

        p.processMessage(lcm);

        String result = lcm.getPayload().getData();

        Document doc = TestHelpers.extractXmlDoc(lcm);

        String thumb1Url = TestHelpers.getXPath("//mods:mods[2]//mods:url[@access='preview']", doc);

        assertEquals("http://ids.lib.harvard.edu/ids/view/45562415?width=150&height=150&usethumb=y", thumb1Url);
    }


    @Test
    void test009444707() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "009444707");
        String urns = MessageUtils.transformPayloadData(lcm,"src/main/resources/urns.xsl",null).replace(" ", "+");

        TestHelpers.mockResponse(Config.getInstance().SOLR_EXTENSIONS_URL + "/select?q=urn_keyword:(%22urn-3:FHCL:2092181%22+OR+%22urn-3:HUL.gisdata:009444707%22)&rows=250", 200, "009444707_solr_response.json");

        p.processMessage(lcm);

        Document doc = TestHelpers.extractXmlDoc(lcm);
        String thumb1Url = TestHelpers.getXPath("//mods:mods//mods:url[@access='preview']", doc);

        assertEquals("http://nrs.harvard.edu/urn-3:FHCL:2092181?width=150&height=150&usethumb=y", thumb1Url);
    }

}
