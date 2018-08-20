package edu.harvard.libcomm.pipeline.solr;

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
class SolrProcessorTests {

    private Document solrDoc;
    private XPath xPath;

    @BeforeAll
    void buildSolrDoc() throws Exception {
        LibCommMessage lcm = new LibCommMessage();
        LibCommMessage.Payload pl = new LibCommMessage.Payload();

        InputStream is = new FileInputStream(this.getClass().getResource("/modsxml_sample_1.xml").getFile());
        String xml = IOUtils.toString(is);

        pl.setFormat("mods");
        pl.setData(xml);
        lcm.setPayload(pl);

        String result = MessageUtils.transformPayloadData(lcm, "src/main/resources/mods2solr.xsl", null);
        InputStream solrIS = IOUtils.toInputStream(result, "UTF-8");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false);
        builderFactory.setNamespaceAware(false);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        solrDoc = builder.parse(solrIS);
        xPath = XPathFactory.newInstance().newXPath();
    }


    @Test
    void buildSolrLanguageFields() throws Exception {

        String languageCode = (String) xPath.compile("//field[@name='languageCode']").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("eng", languageCode);

        String language = (String) xPath.compile("//field[@name='language']").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("English", language);
    }

    @Test
    void buildSolrDateRangeFields() throws Exception {
        String date1 = (String) xPath.compile("(//field[@name='dateRange'])[1]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1967 TO 1967]", date1);

        String date2 = (String) xPath.compile("(//field[@name='dateRange'])[2]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1962 TO 1962]", date2);

        String date3 = (String) xPath.compile("(//field[@name='dateRange'])[3]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1932 TO 1933]", date3);

        String date6 = (String) xPath.compile("(//field[@name='dateRange'])[6]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1960 TO 1967]", date6);

        String date7 = (String) xPath.compile("(//field[@name='dateRange'])[7]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1943 TO 1944]", date7);

        String date8 = (String) xPath.compile("(//field[@name='dateRange'])[8]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1942 TO 1943]", date8);

        String date10 = (String) xPath.compile("(//field[@name='dateRange'])[10]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[741 TO 1981]", date10);

        String date11 = (String) xPath.compile("(//field[@name='dateRange'])[11]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1750 TO 1759]", date11);

        String date12 = (String) xPath.compile("(//field[@name='dateRange'])[12]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[2005 TO 2005]", date12);

        String date13 = (String) xPath.compile("(//field[@name='dateRange'])[13]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1800 TO 1999]", date13);

        String date14 = (String) xPath.compile("(//field[@name='dateRange'])[14]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1965 TO 1966]", date14);

        String date15 = (String) xPath.compile("(//field[@name='dateRange'])[15]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1871 TO 1872]", date15);

        String date20 = (String) xPath.compile("(//doc[field[@name='title'] = 'brokenDate']//field[@name='dateRange'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1848 TO 1849]", date20);

        String date1800_1910 = (String) xPath.compile("(//doc[field[@name='title'] = 'brokenDate2']//field[@name='dateRange'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1800 TO 1910]", date1800_1910);

        String date21 = (String) xPath.compile("(//doc[field[@name='title'] = 'dateFail']//field[@name='dateRange'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("[1920 TO 1944]", date21);

    }

    @Test
    void buildSolrDigitalFormatFields() throws Exception {
        String df1 = (String) xPath.compile("//field[@name='digitalFormat'][1]").evaluate(solrDoc, XPathConstants.STRING);
        String df2 = (String) xPath.compile("//field[@name='digitalFormat'][2]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Audio", df1);
        assertEquals("Images", df2);
    }

    @Test
    void buildSolrOriginDate() throws Exception {
        String originDate1 = (String) xPath.compile("//doc[field[@name='title'] = 'originDateTest']//field[@name='originDate'][1]").evaluate(solrDoc, XPathConstants.STRING);
        String originDate2 = (String) xPath.compile("//doc[field[@name='title'] = 'originDateTest']//field[@name='originDate'][2]").evaluate(solrDoc, XPathConstants.STRING);
        String originDate3 = (String) xPath.compile("//doc[field[@name='title'] = 'originDateTest']//field[@name='originDate'][3]").evaluate(solrDoc, XPathConstants.STRING);
        String originDate4 = (String) xPath.compile("//doc[field[@name='title'] = 'originDateTest']//field[@name='originDate'][4]").evaluate(solrDoc, XPathConstants.STRING);
        String originDate5 = (String) xPath.compile("//doc[field[@name='title'] = 'originDateTest']//field[@name='originDate'][5]").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("2001", originDate1);
        assertEquals("2002", originDate2);
        assertEquals("2003", originDate3);
        assertEquals("2004", originDate4);
        assertEquals("2005", originDate5);

    }


    @Test
    void buildUrlsAccess() throws Exception {
        String preview = (String) xPath.compile("//doc[field[@name='title'] = 'urlAccessTest']//field[@name='url.access.preview'][1]").evaluate(solrDoc, XPathConstants.STRING);
        String rawObject = (String) xPath.compile("//doc[field[@name='title'] = 'urlAccessTest']//field[@name='url.access.raw_object'][1]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("true", preview);
        assertEquals("true", rawObject);
    }

    @Test
    void buildSubjectHierarchicalGeographic() throws Exception {
        String country = (String) xPath.compile("//doc[field[@name='title'] = 'subjectHierarchicalGeographicTest']//field[@name='subject.hierarchicalGeographic.country'][1]").evaluate(solrDoc, XPathConstants.STRING);
        String city = (String) xPath.compile("//doc[field[@name='title'] = 'subjectHierarchicalGeographicTest']//field[@name='subject.hierarchicalGeographic.city'][1]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("Germany", country);
        assertEquals("Cologne", city);
    }

    @Test
    void buildRelatedItem() throws Exception {
        String relatedItem = (String) xPath.compile("//doc[field[@name='title'] = 'relatedItemTest']//field[@name='relatedItem'][1]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("Musique ; 1", relatedItem);
    }

    @Test
    void buildFileDeliveryURL() throws Exception {
        String fileDeliveryURL = (String) xPath.compile("//doc[1]//field[@name='fileDeliveryURL'][1]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("http://nrs.harvard.edu/urn-3:FHCL:1549105", fileDeliveryURL);
    }

    @Test
    void availableTo() throws Exception {
        String availableTo = (String) xPath.compile("//doc[1]//field[@name='availableTo'][1]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("Everyone", availableTo);
    }

    @Test
    void nullLastModifiedDate() throws Exception {
        String lastModifiedDate1 = (String) xPath.compile("//doc[1]//field[@name='_lastModifiedDate'][1]").evaluate(solrDoc, XPathConstants.STRING);
        String lastModifiedDate2 = (String) xPath.compile("//doc[2]//field[@name='_lastModifiedDate'][1]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("2016-04-05T18:31:02.611Z", lastModifiedDate1);
        assertEquals("", lastModifiedDate2);
    }

    @Test // LTSCLOUD-749
    void repositoryFieldAndFacet() throws Exception {
        String r1 = (String) xPath.compile("//doc[1]//field[@name='repository'][1]").evaluate(solrDoc, XPathConstants.STRING);
        String r2 = (String) xPath.compile("//doc[1]//field[@name='repository'][2]").evaluate(solrDoc, XPathConstants.STRING);

        assertEquals("Botany Gray Herbarium", r1);
        assertEquals("Widener", r2);
    }

    @Test //LTSCLOUD-750
    void matchModsNodesRegardlessOfHierarchy() throws Exception {
        String data;

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='name'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Bory, Jean Louis", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='dateCreated'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("2002", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='dateCaptured'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("2003", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='genre'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Catalogs", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='physicalLocation'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Freer Gallery of Art", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='resourceType'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("still image", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='repository'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Widener", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='role'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("creator", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='shelfLocator'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Mus 645.5.717", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='subject.topic'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Theology", data.trim());


        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='originPlace'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Germany", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='publisher'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("PUBLISHER", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='originDate'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("2001", data.trim());

        data = (String) xPath.compile("(//doc[field[@name='title'] = 'deepData']//field[@name='url.access.preview'])").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("true", data.trim());

    }

}
