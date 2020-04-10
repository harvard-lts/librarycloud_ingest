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
        assertEquals("https://id.lib.harvard.edu/alma/990000059770203941/catalog", url);

        p.setStylesheet("src/main/resources/MARC21slim2MODS3-6.xsl");
        LibCommMessage lcmAlma = TestHelpers.buildLibCommMessage("marc", "marcxml_sample_1.xml");
        p.processMessage(lcmAlma);
        Document modsAlma = TestHelpers.extractXmlDoc(lcmAlma);
        String urlAlma = TestHelpers.getXPath("//mods:mods[1]//mods:relatedItem[@otherType='HOLLIS record']/mods:location/mods:url", modsAlma);
        assertEquals("https://id.lib.harvard.edu/alma/990000059770203941/catalog", urlAlma);
    }

    @Test //LTSCLOUD-756 rec: 009955294
    void namePartPositionTest() throws Exception {
        p = new ModsProcessor();
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("marc", "009955294");
        p.processMessage(lcm);
        Document mods = TestHelpers.extractXmlDoc(lcm);

        String namePart2 = TestHelpers.getXPath("//mods:subject[2]/mods:name/mods:namePart[2]", mods);
        assertEquals("Viscount", namePart2);

    }
}
