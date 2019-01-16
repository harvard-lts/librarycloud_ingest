package edu.harvard.libcomm.pipeline.mods;

import edu.harvard.libcomm.pipeline.Config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.net.URI;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

import edu.harvard.libcomm.message.LibCommMessage;
import edu.harvard.libcomm.message.LibCommMessage.Payload;
import edu.harvard.libcomm.pipeline.MessageUtils;

public class MODSComponentIterator implements Iterator<String> {
	protected Logger log = Logger.getLogger(MODSComponentIterator.class);

	protected MODSReader modsReader;
	protected NodeList nodes;
    protected NodeList modsNodes;
	protected DOMSource domSource;
	protected Transformer transformer;
	protected int position = 0;
    private ArrayList<String> modsXml;

    public MODSComponentIterator(MODSReader reader) throws Exception {
        this.modsReader = reader;
        nodes = reader.getNodes();
        transformer = buildTransformer("src/main/resources/mods2modscomponent.xsl");
        modsXml = nextMods();
    }

    @Override
    public boolean hasNext() {
    	return ((modsXml != null) && (position < modsXml.size()));
    }

    @Override
    public String next() {
        String componentizedMods = "";
        for (String str : modsXml) {
            componentizedMods += str;
            position++;
            //System.out.println("position: " + position);
        }
        return componentizedMods;
    }

    private String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToIS Transformer Exception");
        }
        return sw.toString();
    }

    private ArrayList<String> nextMods () throws Exception {
        ArrayList<String> modsComponents = new ArrayList<String>();
//System.out.println("nodes.getLength(): " + nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            String xmlStr = nodeToString(nodes.item(i));
            xmlStr = xmlStr.replace("xmlns=\"\"","");
//System.out.println("xmlStr: " + xmlStr);
            InputStream modsIS =  new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
            Document modsDoc = modsReader.getDocument(modsIS);
            NodeList urns = modsReader.getNodeList(modsDoc,"//mods:url[@access='raw object']/text()"); // doesnt work: and contains(.,'urn-3')
            if (urns.getLength() == 0)
                modsComponents.add(xmlStr);
            else {
                int pos = 0;
                ArrayList<String> urnArr = new ArrayList<String>();
                while ((urns != null) && (pos < urns.getLength())) {
                    boolean inDRS = false;
                    String nodeName = urns.item(pos).getNodeName();
                    String nodeValue = urns.item(pos).getNodeValue();
                    //System.out.println("nodeValue: " + nodeValue);
                    if (nodeValue.contains("urn-3") && !nodeValue.contains("HUL.FIG") && !nodeValue.contains("ebookbatch") && !nodeValue.contains("ejournals") && !nodeValue.contains("gisdata")) {
                        // 20190116 - we can't check indrs until extensions are up-to-date;
                        /*
                        String nodeValueChopped = nodeValue.substring(nodeValue.indexOf("urn-3"), nodeValue.length()).split("\\?")[0];
                        //some urls have prepended text (shouldn't be cataloged this way, but account for it anyway ...
                        //nodeValueChopped = nodeValue.substring(nodeValue.indexOf("http"), nodeValue.length()).split("\\?")[0];
                        JSONTokener tokener = null;
                        try {
                            //URI uri = new URI(Config.getInstance().DRSEXTENSIONS_URL + "?urns=" + nodeValueChopped);
                            URI uri = new URI(Config.getInstance().SOLR_EXTENSIONS_URL + "/select?q=urn_keyword:%22" + nodeValueChopped + "%22");
                            try {
                                tokener = new JSONTokener(uri.toURL().openStream());
                                JSONObject json = new JSONObject(tokener);
                                int numFound = json.getJSONObject("response").getInt("numFound");
                                inDRS = numFound == 0 ? false:true;
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            //System.out.println(uri.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        */
                        // 20190116 - we can't check indrs until extensions are up-to-date;
                        // rely on contains urn-3, not FIG ... above, for now
                        //if (inDRS) {
                            urnArr.add(nodeValue);
                        //}
                    }
                    pos++;
                }
                if (urnArr.size() == 0) {
                    //System.out.println("0 URNS");
                    modsComponents.add(xmlStr);
                }
                else {
                    NodeList isCollection = modsReader.getNodeList(modsDoc,"//mods:typeOfResource[@collection='yes']");
                    if (isCollection.getLength() == 0 && urnArr.size() == 1) {
                        //System.out.println("0 COLL, 1 URN");
                        modsComponents.add(xmlStr);
                    }
                    else {
                        if (isCollection.getLength() == 1 || urnArr.size() > 1) {
                            //System.out.println("1 COLL, or > 1 URN");
                            domSource = new DOMSource(modsDoc);
                            modsComponents.add(transformMODS(""));
                        }
                        for (String str : urnArr) {
                            try {
                                domSource = new DOMSource(modsDoc);
                                modsComponents.add(transformMODS(str));
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new NoSuchElementException();
                            }
                            //break;
                        }
                    }

                }
            }

        }
        //System.out.println(modsComponents);
    	return modsComponents;

	}

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected Transformer buildTransformer(String xslFilePath) throws Exception {
		final InputStream xsl = new FileInputStream(xslFilePath);
		final TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
        StreamSource styleSource = new StreamSource(xsl);
        return tFactory.newTransformer(styleSource);    	
    }

	protected String transformMODS (String xslParam) throws Exception {		
       	this.transformer.setParameter("url", xslParam);
		StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(this.domSource, result);
        return writer.toString();
	}
    
}