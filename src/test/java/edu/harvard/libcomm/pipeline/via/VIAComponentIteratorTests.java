package edu.harvard.libcomm.pipeline.via;

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
class VIAComponentIteratorTests {

    private Document mods;

    @BeforeAll
    void setup() throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource("/sample-via-1.xml").getFile());

        VIAReader r = new VIAReader(is);
        Iterator i = new VIAComponentIterator(r);
        String lcmString = (String) i.next();
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(lcmString, "UTF-8"));
        mods = TestHelpers.extractXmlDoc(lcm);
    }


    @Test
    void useResourceLanguageCode() throws Exception {
        String languageCode = TestHelpers.getXPath("//mods:languageTerm[@type='code']", mods);
        String languageText = TestHelpers.getXPath("//mods:languageTerm[@type='text']", mods);
        assertEquals("zxx", languageCode);
        assertEquals("No linguistic content", languageText);
    }


    @Test // LTSCLOUD-710
    void buildModsRoleTerm() throws Exception {
        String roleTerm1 = TestHelpers.getXPath("//mods:name[mods:namePart[1] = 'Bramante, Donato']/mods:role[1]/mods:roleTerm", mods);
        String roleTerm2 = TestHelpers.getXPath("//mods:name[mods:namePart[1] = 'Bramante, Donato']/mods:role[2]/mods:roleTerm", mods);
        assertEquals("creator", roleTerm1);
        assertEquals("architect", roleTerm2);
    }

    @Test // HUAM281333
    void HUAM281333() throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource("/HUAM281333").getFile());

        VIAReader r = new VIAReader(is);
        Iterator i = new VIAComponentIterator(r);
        String lcmString = (String) i.next();
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(lcmString, "UTF-8"));
        System.out.println(lcm.getPayload().getData());
        Document HUAM281333 = TestHelpers.extractXmlDoc(lcm);
    }

}
