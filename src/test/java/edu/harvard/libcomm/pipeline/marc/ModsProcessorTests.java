package edu.harvard.libcomm.pipeline.marc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import org.xml.sax.InputSource;

import org.apache.commons.io.IOUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import edu.harvard.libcomm.message.*;
import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.test.TestHelpers;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModsProcessorTests {

    private Document mods;
    private ModsProcessor p;
    private LibCommMessage lcm;

    @BeforeAll
    void buildModsDoc() throws Exception {
        p = new ModsProcessor();
        lcm = TestHelpers.buildLibCommMessage("marc", "marcxml_sample_1.xml");
        p.processMessage(lcm);
        mods = TestHelpers.extractXmlDoc(lcm);
        System.out.println(lcm.getPayload().getData());
    }


    @Test
    void buildModsLanguageFields() throws Exception {
        String languageCode1 = TestHelpers.getXPath("//mods:mods[1]//mods:languageTerm[@type = 'code']", mods);
        String languageCode2 = TestHelpers.getXPath("//mods:mods[2]//mods:languageTerm[@type = 'code']", mods);

        assertEquals("eng", languageCode1);
        assertEquals("und", languageCode2);
    }

    @Test //LTSCLOUD-752
    void idDotLibURL() throws Exception {
        String url = TestHelpers.getXPath("//mods:mods[1]//mods:relatedItem[@otherType='HOLLIS record']/mods:location/mods:url", mods);
        assertEquals("http://id.lib.harvard.edu/aleph/000005977/catalog", url);

        p.setStylesheet("src/main/resources/MARC21slim2MODS3-6-alma.xsl");
        LibCommMessage lcmAlma = TestHelpers.buildLibCommMessage("marc", "marcxml_sample_1.xml");
        p.processMessage(lcmAlma);
        Document modsAlma = TestHelpers.extractXmlDoc(lcmAlma);
        String urlAlma = TestHelpers.getXPath("//mods:mods[1]//mods:relatedItem[@otherType='HOLLIS record']/mods:location/mods:url", modsAlma);
        assertEquals("http://id.lib.harvard.edu/alma/000005977/catalog", urlAlma);
    }
}
