package edu.harvard.libcomm.pipeline.ead;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.*;
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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Disabled;

import java.util.Iterator;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import edu.harvard.libcomm.pipeline.MessageUtils;
import edu.harvard.libcomm.test.TestHelpers;
import edu.harvard.libcomm.test.TestMessageUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BTAComponentIteratorTests {

    private Document mods;
    private Iterator i;

    @BeforeAll
    void setup() throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource("/gut5000.xml").getFile());

        EADReader r = new EADReader(is);
        i = new EADComponentIterator(r);
        String skipFirst = (String) i.next();
        String skipSecond = (String) i.next();
        String lcmString = (String) i.next();
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(lcmString, "UTF-8"));
        mods = TestHelpers.extractXmlDoc(lcm);
    }


    @Test //LTSIIIF-1301, using gut5000c00014 - top level tests
    void testTopLevelGut5000c00014() throws Exception {
        String title = TestHelpers.getXPath("mods:mods/mods:titleInfo/mods:title", mods);
        String dateCreatedStart = TestHelpers.getXPath("mods:mods/mods:originInfo/mods:dateCreated[@point='start']", mods);
        String dateCreatedEnd = TestHelpers.getXPath("mods:mods/mods:originInfo/mods:dateCreated[@point='end']", mods);
        String dateCreatedKey = TestHelpers.getXPath("mods:mods/mods:originInfo/mods:dateCreated[@keyDate='yes']", mods);
        String physDescNote = TestHelpers.getXPath("mods:mods/mods:physicalDescription/mods:note[@type='organization']", mods);
        String identifier = TestHelpers.getXPath("mods:mods/mods:identifier[@type='unit id']", mods);
        String recordIdentifier = TestHelpers.getXPath("mods:mods/mods:recordInfo/mods:recordIdentifier", mods);
        String namePartOwner = TestHelpers.getXPath("mods:mods/mods:name[mods:role/mods:roleTerm='Owner']/mods:namePart", mods);
        String namePartAuthor = TestHelpers.getXPath("mods:mods/mods:name[mods:role/mods:roleTerm='Author']/mods:namePart", mods);
        String namePartCorrespondent = TestHelpers.getXPath("mods:mods/mods:name[mods:role/mods:roleTerm='Correspondent']/mods:namePart", mods);
        String topic = TestHelpers.getXPath("mods:mods/mods:subject/mods:topic[1]", mods);
        String genre = TestHelpers.getXPath("mods:mods/mods:genre", mods);
        String SubjGeographic = TestHelpers.getXPath("mods:mods/mods:subject/mods:geographic[1]", mods);
        String subjectNamePartPersonal = TestHelpers.getXPath("mods:mods/mods:subject/mods:name[@type='personal']/mods:namePart", mods);
        String notePhysicalDescription = TestHelpers.getXPath("mods:mods/mods:note[@type='source characteristics'][@displayLabel='Physical Description of Original']", mods);
        String languageCode = TestHelpers.getXPath("mods:mods/mods:language/mods:languageTerm[@type='code']", mods);
        String languageText = TestHelpers.getXPath("mods:mods/mods:language/mods:languageTerm[@type='text']", mods);
        String relatedItemTitle = TestHelpers.getXPath("mods:mods/mods:relatedItem[@displayLabel='Source Institution Digitization']/mods:titleInfo/mods:title", mods);
        String relatedItemLocation = TestHelpers.getXPath("mods:mods/mods:relatedItem[@displayLabel='Source Institution Digitization']/mods:location/mods:url", mods);
        String noteBiographicalHistorical = TestHelpers.getXPath("mods:mods/mods:note[@type='biographical/historical']", mods);
        String noteBibliographicHistory = TestHelpers.getXPath("mods:mods/mods:note[@type='bibliographic history'][@displayLabel='Issuing Body Note']", mods);
        String rawObjectUrl = TestHelpers.getXPath("mods:mods/mods:location/mods:url[@access='raw object']", mods);
        assertEquals("No.2", title);
        assertEquals("1919-08", dateCreatedStart);
        assertEquals("1919-08", dateCreatedEnd);
        assertEquals("August 1919", dateCreatedKey);
        assertEquals("item", physDescNote);
        assertEquals("bta_56402872_US_1919", identifier);
        assertEquals("gut5000c00014", recordIdentifier);
        assertEquals("University of Oregon. Libraries", namePartOwner);
        assertEquals("Washington, George, 1732-1799.", namePartAuthor);
        assertEquals("Washington, Booker T., 1856-1915", namePartCorrespondent);
        assertEquals("Meetings", topic);
        assertEquals("Minutes.", genre);
        assertEquals("North Carolina--Social life and customs", SubjGeographic);
        assertEquals("Aalto, Alvar, 1898-1976", subjectNamePartPersonal);
        assertEquals("This issue is bound together v.1-5 (1930-1934) in the rare books of the North Carolina Collection at University of North Carolina Chapel Hill.", notePhysicalDescription);
        assertEquals("eng", languageCode);
        assertEquals("English", languageText);
        assertEquals("Digital copies of this issue appear in North Carolina Library Internet Archive", relatedItemTitle);
        assertEquals("https://archive.org/details/northcarolinatea1519unse", relatedItemLocation);
        assertEquals("1st paragraph of issue-level biog/hist note", noteBiographicalHistorical);
        assertEquals("1st paragraph of issue-level biog/hist note with Issuing Body Note label", noteBibliographicHistory);
        assertEquals("https://nrs.harvard.edu/URN-3:GSE.LIBR:101457812", rawObjectUrl);
    }

    @Test //LTSIIIF-1301, using gut5000c00012 - first host level tests
    void testFirstHostLevelGut5000c00012() throws Exception {
        
        String title = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:titleInfo/mods:title", mods);
        String physDescNote = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:physicalDescription/mods:note[@type='organization']", mods);
        String recordIdentifier = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:recordInfo/mods:recordIdentifier", mods);
        String namePartCreator = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:name[mods:role/mods:roleTerm='cre']/mods:namePart", mods);
        String topic = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:subject/mods:topic", mods);
        String genre = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:genre", mods);
        String place = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:originInfo/mods:place/mods:placeTerm", mods);
        String noteBiographicalHistorical = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:note[@type='biographical/historical']", mods);
        String noteBibliographicHistory = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:note[@type='bibliographic history'][@displayLabel='Issuing Body Note']", mods);
        String publisher= TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:originInfo/mods:publisher", mods);
        assertEquals("National Note-book Series", title);
        assertEquals("subseries", physDescNote);
        assertEquals("gut5000c00012", recordIdentifier);
        assertEquals("National Association of Teachers in Colored Schools (U.S.)", namePartCreator);
        assertEquals("National Association of Teachers in Colored Schools (U.S.)", publisher);
        assertEquals("African American teachers", topic);
        assertEquals("Periodicals.", genre);
        assertEquals("Augusta, Georgia", place);
        assertEquals("1st paragraph of serial (aka sub-series) biog/hist note", noteBiographicalHistorical);
        assertEquals("\"Official publication of the National Association of Teachers in Colored Schools.\"", noteBibliographicHistory);
    }

    @Test //LTSIIIF-1301, using gut5000c00001 - second host level tests
    void testSecondHostLevelGut5000c00001() throws Exception {
        
        String title = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:relatedItem[@type='host']/mods:titleInfo/mods:title", mods);
        String physDescNote = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:relatedItem[@type='host']/mods:physicalDescription/mods:note[@type='organization']", mods);
        String recordIdentifier = TestHelpers.getXPath("mods:mods/mods:relatedItem[@type='host']/mods:relatedItem[@type='host']/mods:recordInfo/mods:recordIdentifier", mods);
        assertEquals("National Publications", title);
        assertEquals("series", physDescNote);
        assertEquals("gut5000c00001", recordIdentifier);
    }

    @Test //LTSIIIF-1301, using gut5000 - collection level tests
    void testCollectionLevelGut5000() throws Exception {
        String title = TestHelpers.getXPath("mods:mods//mods:relatedItem[@type='host'][@displayLabel='collection']/mods:titleInfo/mods:title", mods);
        String dateCreatedStart = TestHelpers.getXPath("mods:mods//mods:relatedItem[@type='host'][@displayLabel='collection']/mods:originInfo/mods:dateCreated[@point='start']", mods);
        String dateCreatedEnd = TestHelpers.getXPath("mods:mods//mods:relatedItem[@type='host'][@displayLabel='collection']/mods:originInfo/mods:dateCreated[@point='end']", mods);
        String dateCreatedKey = TestHelpers.getXPath("mods:mods//mods:relatedItem[@type='host'][@displayLabel='collection']/mods:originInfo/mods:dateCreated[@keyDate='yes']", mods);
        String identifier = TestHelpers.getXPath("mods:mods//mods:relatedItem[@type='host'][@displayLabel='collection']/mods:identifier[@type='unit id']", mods);
        String recordIdentifier = TestHelpers.getXPath("mods:mods//mods:relatedItem[@type='host'][@displayLabel='collection']/mods:recordInfo/mods:recordIdentifier", mods);
        assertEquals("Black Teacher Archive", title);
        assertEquals("1907", dateCreatedStart);
        assertEquals("1973", dateCreatedEnd);
        assertEquals("1907-1973", dateCreatedKey);
        assertEquals("BTA", identifier);
        assertEquals("gut5000", recordIdentifier);
    }

 }
