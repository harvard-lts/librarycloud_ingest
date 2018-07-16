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

    }

    @Test
    void buildSolrRepositoryFields() throws Exception {
        String languageCode = (String) xPath.compile("//field[@name='repository']").evaluate(solrDoc, XPathConstants.STRING);
        assertEquals("Music Repository", languageCode);
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
}
