package edu.harvard.libcomm.test;

import java.io.*;
import java.util.Iterator;
import javax.xml.XMLConstants;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandlerFactory;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;
import edu.harvard.libcomm.test.HttpUrlStreamHandler;
import static org.mockito.Mockito.*;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class TestHelpers {
    private static InputStream is;
    private static boolean setupDone = false;
    private static HttpUrlStreamHandler httpUrlStreamHandler;

    private static XPath xPath = XPathFactory.newInstance().newXPath();
    private static boolean namespaces = false;

    private static void setNamespaces() {
        xPath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if (prefix == null) throw new NullPointerException("Null prefix");
                    else if ("mods".equals(prefix)) return "http://www.loc.gov/mods/v3";
                    else if ("librarycloud".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/librarycloud";
                    else if ("sets".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/libraryCloud";
                    else if ("drs".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/HarvardDRS";
                    else if ("hvd".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/HarvardRepositories";
                    else if ("avail".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/availableTo";
                    else if ("procdate".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/processingDate";
                    else if ("digitalFormats".equals(prefix)) return "http://hul.harvard.edu/ois/xml/ns/digitalFormats";
                    return XMLConstants.NULL_NS_URI;
                }

                // This method isn't necessary for XPath processing.
                public String getPrefix(String uri) {
                    throw new UnsupportedOperationException();
                }

                // This method isn't necessary for XPath processing either.
                public Iterator getPrefixes(String uri) {
                    throw new UnsupportedOperationException();
                }
            });

        namespaces = true;
    }

    public static LibCommMessage buildLibCommMessage(String format, String filePath) throws IOException {
        LibCommMessage lcm = new LibCommMessage();
        LibCommMessage.Payload pl = new LibCommMessage.Payload();

        String xml = readFile(filePath);
        pl.setFormat(format);
        pl.setData(xml);
        lcm.setPayload(pl);

        return lcm;
    }

    public static String readFile(String resourceName) throws IOException {
        is = new FileInputStream(TestHelpers.class.getResource("/"+resourceName).getFile());
        return IOUtils.toString(is);
    }

    public static Document extractXmlDoc(LibCommMessage lcm) throws Exception{
        String s = lcm.getPayload().getData();
                //System.out.println("S: " + s);
        InputStream docIS = IOUtils.toInputStream(s, "UTF-8");

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false);
        builderFactory.setNamespaceAware(false);
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(docIS);
        return doc;
    }

    public static String getXPath(String xPathPath, Document doc) throws Exception {
        if (!namespaces)
            setNamespaces();
        String result = (String) xPath.compile(xPathPath).evaluate(doc, XPathConstants.STRING);
        return result;
    }

    public static Number getNodeCount(String xPathPath, Document doc) throws Exception {
        if (!namespaces)
            setNamespaces();
        Number result = (Number) xPath.compile("count("+xPathPath+")").evaluate(doc, XPathConstants.NUMBER);
        return result;
    }

    public static LibCommMessage unmarshalLibCommMessage(String filePath) throws Exception {
        String cloudbody = TestHelpers.readFile(filePath);
        LibCommMessage lcm = TestMessageUtils.unmarshalLibCommMessage(IOUtils.toInputStream(cloudbody, "UTF-8"));
        return lcm;
    }

    public static void mockResponse(String url, int code) throws Exception {
        mockResponse(url, code, null);
    }

    public static void mockResponse(String url, int code, String responseBodyFile) throws Exception {
        HttpURLConnection urlConnection = mock(HttpURLConnection.class);
        httpUrlStreamHandler.addConnection(new URL(url), urlConnection);

        if (code == 400) {
            when(urlConnection.getResponseCode()).thenReturn(400);
            when(urlConnection.getInputStream()).thenThrow(IOException.class);
        } else if (code == 200) {
             when(urlConnection.getResponseCode()).thenReturn(200);
            String mockResponse = TestHelpers.readFile(responseBodyFile);
            InputStream stream = new ByteArrayInputStream(mockResponse.getBytes(StandardCharsets.UTF_8));
            when(urlConnection.getInputStream()).thenReturn(stream);
        }
    }

    public static void mockRedirect(String urlFrom, String urlTo) throws Exception {
        HttpURLConnection urlConnection = mock(HttpURLConnection.class);
        httpUrlStreamHandler.addConnection(new URL(urlFrom), urlConnection);
        when(urlConnection.getResponseCode()).thenReturn(303);
        when(urlConnection.getHeaderField("Location")).thenReturn(urlTo);
    }

    public static HttpUrlStreamHandler getHttpUrlStreamHandler() {
        if(!setupDone) {
            // Allows for mocking URL connections
            URLStreamHandlerFactory urlStreamHandlerFactory = mock(URLStreamHandlerFactory.class);
            URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
            httpUrlStreamHandler = new HttpUrlStreamHandler();
            when(urlStreamHandlerFactory.createURLStreamHandler("http")).thenReturn(httpUrlStreamHandler);
            when(urlStreamHandlerFactory.createURLStreamHandler("https")).thenReturn(httpUrlStreamHandler);
            setupDone = true;
        }
        return httpUrlStreamHandler;
    }
}
