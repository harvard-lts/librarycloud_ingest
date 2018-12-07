package edu.harvard.libcomm.pipeline.via;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.lang.String;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.ArrayList;

public class VIAReader {

    private InputStream is;
    private NodeList nodes;
    private DOMSource domSource;

    public VIAReader(InputStream is) {
        this.is = is;
    }

    class FlexNodeList implements NodeList {
        private List<NodeList> lists = new ArrayList<NodeList>();

        @Override
        public int getLength() {
            int len = 0;
            for (NodeList list : lists) {
                len += list.getLength();
            }
            return len;
        }

        @Override
        public Node item(int index) {
            for (NodeList list : lists) {
                if (list.getLength() > index) {
                    return list.item(index);
                } else {
                    index -= list.getLength();
                }
            }
            return null;
        }

        public void add(NodeList list) {
            lists.add(list);
        }
    }

    public NodeList getNodes() throws ParserConfigurationException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
      Document doc = getDocument(is);
      this.domSource = new DOMSource(doc);
      NodeList nodes = getNodeList(doc);
      return nodes;
    }

    public DOMSource getDOMSource() throws ParserConfigurationException, SAXException, IOException {
      if (this.domSource == null) {
        Document doc = getDocument(is);
        this.domSource = new DOMSource(doc);
      }
    return this.domSource;
    }

  private Document getDocument (InputStream is) throws ParserConfigurationException, SAXException, IOException {
      Document doc = null;
    DocumentBuilder builder;
    try {
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      doc = builder.parse(is);

    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      throw e;
    } catch (SAXException e) {
      e.printStackTrace();
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
      throw e;
    }
    return doc;
  }

  private NodeList getNodeList (Document doc) throws XPathExpressionException {

      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();

      xpath.setNamespaceContext(new NamespaceContext() {
          public String getNamespaceURI(String prefix) {
              return prefix.equals("xlink") ? "http://www.w3.org/TR/xlink"  : null;
          }

          public Iterator<?> getPrefixes(String val) {
              return null;
          }

          public String getPrefix(String uri) {
              return null;
          }
      });

      FlexNodeList nodes = new FlexNodeList();

      String[] via2ModsPaths = new String[] {
          "//work/image",
          "//work/surrogate[not(image)]",
          "//work/surrogate/image",
          "//work[not(image) and not(surrogate)]",
          "//group/surrogate[not(image)]",
          "//group/surrogate/image",
          "//group/image",
          "//group/subwork/surrogate[not(image)]",
          "//group/subwork/surrogate/image",
          "//group/subwork/image"
      };

      try {
          for(String v2mp : via2ModsPaths) {
              XPathExpression xpe = xpath.compile(v2mp);
              nodes.add((NodeList) xpe.evaluate(doc, XPathConstants.NODESET));
          }
      } catch (XPathExpressionException e) {
        e.printStackTrace();
        throw e;
      }

      System.out.println(nodes.getLength());

      return nodes;
  }

}
