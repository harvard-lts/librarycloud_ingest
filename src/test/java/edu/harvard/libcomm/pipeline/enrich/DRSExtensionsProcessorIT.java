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

import edu.harvard.libcomm.test.TestHelpers;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DRSExtensionsProcessorIT {

    private static DRSExtensionsProcessor p;

    @BeforeAll
    public static void setup() {
        p = new DRSExtensionsProcessor();
    }

    @Disabled
    @Test
    void test004903630() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "004903630");
        p.processMessage(lcm);

        Document doc = TestHelpers.extractXmlDoc(lcm);

        String thumb1Url = TestHelpers.getXPath("//mods:mods//mods:url[@access='preview']", doc);

        assertEquals("http://nrs.harvard.edu/urn-3:FHCL:562268?width=150&height=150&usethumb=y", thumb1Url);
    }

    @Test
    void test009180601() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "009180601");
        p.processMessage(lcm);

        Document doc = TestHelpers.extractXmlDoc(lcm);

        String thumb1Url = TestHelpers.getXPath("//mods:mods//mods:url[@access='preview']", doc);

        assertEquals("http://nrs.harvard.edu/urn-3:FHCL:562268?width=150&height=150&usethumb=y", thumb1Url);
    }
}
