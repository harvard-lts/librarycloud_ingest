package edu.harvard.libcomm.pipeline.via;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.test.TestHelpers;
import edu.harvard.libcomm.test.TestMessageUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.w3c.dom.Document;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VIAComponentIteratorTests {

    private Document mods;

    private Iterator getIterator(String filename) throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource(filename).getFile());
        VIAReader r = new VIAReader(is);
        Iterator i = new VIAComponentIterator(r);
        return i;
    }

    private Document transform(String lcmString) throws Exception {
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(lcmString, "UTF-8"));
        //System.out.println("LCM :"+lcm.getPayload().getData());
        Document mods = TestHelpers.extractXmlDoc(lcm);
        return mods;
    }

    private Document transformFirst(String filename) throws Exception {
        Iterator i = getIterator(filename);
        String lcmString = (String) i.next();
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(lcmString, "UTF-8"));
        //System.out.println("LCM :"+lcm.getPayload().getData());
        Document mods = TestHelpers.extractXmlDoc(lcm);
        return mods;
    }

    private int transformAndCountMods(String filename) throws Exception {
        InputStream is = new FileInputStream(this.getClass().getResource(filename).getFile());
        VIAReader r = new VIAReader(is);
        Iterator i = new VIAComponentIterator(r);
        int count = 0;
        while(i.hasNext()) {
            String foo = (String) i.next();
            count = count+1;
        }
        return count;
    }

    @BeforeAll
    void setup() throws Exception {
        mods = transformFirst("/sample-via-1.xml");
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


    // LTSCLOUD-720
    // If a <work> has neither a <surrogate> nor a child <image>,
    // create one MODS record.
    @Test
    void LTCLOUD720Test1() throws Exception {
        int modsCount = transformAndCountMods("/sample-via-2.xml");
        assertEquals(1, modsCount);
    }

    // LTSCLOUD-720
    // If a <work> has either a single <surrogate> or a single child
    // <image>, create one MODS record.
    @Test
    void LTCLOUD720Test2() throws Exception {
        int modsCount = transformAndCountMods("/sample-via-3.xml");
        assertEquals(1, modsCount);
    }

    // If a <work> has >1 (<surrogate> and/or child <image>), create
    // a MODS record for every <surrogate> and every child <image>.
    @Test
    void LTCLOUD720Test3() throws Exception {
        int modsCount = transformAndCountMods("/sample-via-4.xml");
        assertEquals(3, modsCount);
    }

    // Treat group/surrogate|image like work/surrogate|image
    // Treat group/subwork/surrogate|image like work/surrogate|image
    @Test
    void LTCLOUD720Test4() throws Exception {
        int modsCount = transformAndCountMods("/sample-via-5.xml");
        assertEquals(3, modsCount);
    }

    @Test
    void W166800Test() throws Exception {
        Document mods1 = transformFirst("/W166800.xml");
        String recId = TestHelpers.getXPath("/mods:mods/mods:recordInfo/mods:recordIdentifier", mods1);
        assertEquals("W166800_URN-3:FHCL.HOUGH:37205626", recId);
    }

    @Test //olvwork165410.xml
    void olvwork165410Test() throws Exception {
        Document mods1 = transformFirst("/olvwork165410.xml");
        String recId = TestHelpers.getXPath("/mods:mods/mods:recordInfo/mods:recordIdentifier", mods1);
        assertEquals("W165410_4095631", recId);
    }

    @Test
    void olvwork539422Test() throws Exception {
        Iterator i = getIterator("/olvwork539422.xml");
        Document mods1 = transform(i.next().toString());
        String constituent1Title = TestHelpers.getXPath("/mods:mods/mods:relatedItem[@type='constituent']/mods:titleInfo/mods:title", mods1);
        assertEquals("Total", constituent1Title);

        Number imageUrls = TestHelpers.getNodeCount("/mods:mods/mods:relatedItem[@type='constituent']/mods:location[mods:url[@displayLabel='Full Image']]", mods1);
        assertEquals(1.0, imageUrls);

        String recId = TestHelpers.getXPath("/mods:mods/mods:recordInfo/mods:recordIdentifier", mods1);
        assertEquals("W539422_URN-3:VIT.BB:4876783", recId);
    }
}
