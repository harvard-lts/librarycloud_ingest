package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.test.HttpUrlStreamHandler;
import edu.harvard.libcomm.test.TestHelpers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AddMarcLocationProcessorTests {

    private static HttpUrlStreamHandler httpUrlStreamHandler;
    private static AddMarcLocationProcessor p;


    @BeforeAll
    public static void setup() {
        p = new AddMarcLocationProcessor();
    }


    @Test
    void addMarclocation() throws Exception {
        LibCommMessage lcm = TestHelpers.buildLibCommMessage("mods", "empty-mods.xml");
        p.setMarcBaseUrl("http://foo.com/bar");
        p.processMessage(lcm);
        Document doc = TestHelpers.extractXmlDoc(lcm);

        String originalDocument = TestHelpers.getXPath("//mods:mods[1]/mods:extension/librarycloud:originalDocument", doc);
        assertEquals("http://foo.com/bar", originalDocument);

    }

}
