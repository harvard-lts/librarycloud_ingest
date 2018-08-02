package edu.harvard.libcomm.pipeline;

import java.io.*;
import org.apache.commons.io.IOUtils;

import org.apache.camel.Exchange;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;

import edu.harvard.libcomm.test.TestHelpers;
import edu.harvard.libcomm.test.TestMessageUtils;
import edu.harvard.libcomm.message.LibCommMessage;

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
}
