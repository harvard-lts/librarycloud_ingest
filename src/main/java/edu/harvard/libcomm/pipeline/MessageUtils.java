
package edu.harvard.libcomm.pipeline;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.harvard.libcomm.message.LibCommMessage;
import gov.loc.marc.CollectionType;
import gov.loc.mods.v3.ModsCollection;

public class MessageUtils {

	private static LibCommMessage libCommMessage = null;
	private static String messageString = null;

    static JAXBContext context = initContext();

    private synchronized static JAXBContext initContext() {
    	JAXBContext context = null;
    	try {
    		context = JAXBContext.newInstance(LibCommMessage.class,ModsCollection.class,CollectionType.class);
    	} catch (JAXBException je) {
    		System.out.println(je);
    	}
    	return context;
    }	


    protected synchronized static JAXBSource getJAXBSource(Object o) throws JAXBException {
    	JAXBSource source = null;
    	try {
    		source = new JAXBSource(context, o);
    	}
    	catch (JAXBException je) {
    		je.printStackTrace();
    		throw je;
    	}
    	return source;
    }
    
	protected synchronized static LibCommMessage unmarshalLibCommMessage(Reader r) throws JAXBException {
		
	 	try {			 
			//unmarshal: xml2java
	 		Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			libCommMessage = (LibCommMessage) jaxbUnmarshaller.unmarshal(r);
		  } catch (JAXBException e) {
			e.printStackTrace();
			throw e;
		  }
		return libCommMessage;
		
	}
	

	protected synchronized static LibCommMessage unmarshalLibCommMessage(InputStream is) throws JAXBException {
		
	 	try {
			//unmarshal: xml2java
	 		Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			libCommMessage = (LibCommMessage) jaxbUnmarshaller.unmarshal(is);
		  } catch (JAXBException e) {
			e.printStackTrace();
    		throw e;
		  }
		return libCommMessage;		
	}
	
	protected synchronized static ModsCollection unmarshalMods(Reader r) throws JAXBException {
		ModsCollection modsCollection = null;
	 	try {
			 
			//unmarshal: xml2java
	 		Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
			modsCollection = (ModsCollection) jaxbUnmarshaller.unmarshal(r);
 
		  } catch (JAXBException e) {
			e.printStackTrace();
    		throw e;
		  }
		return modsCollection;
		
	}

	protected synchronized static CollectionType unmarshalMarc(Reader r) throws JAXBException {
		CollectionType collectionType = null;
	 	try {
			 
			//unmarshal: xml2java
	 		Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
	 		collectionType = (CollectionType) jaxbUnmarshaller.unmarshal(r);
 
		  } catch (JAXBException e) {
			e.printStackTrace();
			throw e;
		  }
		return collectionType;
		
	}
	
	protected synchronized static String marshalMessage(LibCommMessage libCommMessage) throws JAXBException {
		StringWriter sw = null;
	 	try {
			 
			//marshal:java2xml
			//messageOS = new OutputStream();
			sw = new StringWriter();
			Marshaller jaxbMarshaller = context.createMarshaller();
			// output pretty printed
			//jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(libCommMessage, sw);
		
		  } catch (JAXBException e) {
			e.printStackTrace();
			throw e;
		  }
		return sw.toString();
	}

	static synchronized protected String transformPayloadData (LibCommMessage libCommMessage, String xslFilePath, String xslParam) throws Exception {
		String data = libCommMessage.getPayload().getData();
		// System.out.println("DATA: " + data);
		StringReader dataReader = new StringReader(data);
		
		StringWriter writer = new StringWriter();
		final InputStream xsl = new FileInputStream(xslFilePath);

        //final TransformerFactory tFactory = TransformerFactory.newInstance();
		final TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
				
        StreamSource styleSource = new StreamSource(xsl);
        Transformer transformer = tFactory.newTransformer(styleSource);
        if (xslParam == null)
        	System.out.println();
        else
        	transformer.setParameter("param1", new StreamSource(new StringReader(xslParam)));

        StreamSource xmlSource = new StreamSource(dataReader);
        StreamResult result = new StreamResult(writer);

        transformer.transform(xmlSource, result);
        //System.out.println(writer.toString());
        return writer.toString();
	}
	
}
