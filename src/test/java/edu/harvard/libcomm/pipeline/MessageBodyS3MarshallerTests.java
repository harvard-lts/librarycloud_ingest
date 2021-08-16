package edu.harvard.libcomm.pipeline;

import com.amazonaws.services.s3.AmazonS3;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageBodyS3MarshallerTests {

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private Exchange e;

    @InjectMocks
    private MessageBodyS3Marshaller marshaller;


    @BeforeAll
    public void setup() {
        marshaller = new MessageBodyS3Marshaller(5000000, "foo");
    }
/* rewrite these to use local s3 equivalent
    @Test
    public void basicUnmarshallTest() throws Exception {
        String message = TestHelpers.readFile("s3marshall-test.xml");
        InputStream is = IOUtils.toInputStream(message);
        InputStream body = IOUtils.toInputStream("<foo>bar</foo>");
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(body);
        when(s3Client.getObject("harvard.librarycloud.upload.foo.aleph", "001437267.mrc")).thenReturn(s3Object);

        Object message2 = marshaller.unmarshal(e, is);

        LibCommMessage lcm = MessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(message2.toString()));
        String data = lcm.getPayload().getData();
        assertEquals("<foo>bar</foo>", data);
    }

    @Test
    public void worksWithHttp() throws Exception {
        String message = TestHelpers.readFile("s3marshall-test.xml").replace("https", "http");
        InputStream is = IOUtils.toInputStream(message);
        InputStream body = IOUtils.toInputStream("<foo>bar</foo>");
        S3Object s3Object = new S3Object();
        s3Object.setObjectContent(body);
        when(s3Client.getObject("harvard.librarycloud.upload.foo.aleph", "001437267.mrc")).thenReturn(s3Object);

        Object message2 = marshaller.unmarshal(e, is);

        LibCommMessage lcm = MessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(message2.toString()));
        String data = lcm.getPayload().getData();
        assertEquals("<foo>bar</foo>", data);
    }

 */
}
