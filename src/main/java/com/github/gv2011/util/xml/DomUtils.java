package com.github.gv2011.util.xml;

import static com.github.gv2011.util.CollectionUtils.filter;
import static com.github.gv2011.util.CollectionUtils.toOpt;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.icol.Opt;

public final class DomUtils {

  private DomUtils(){staticClass();}

  public static Document newDocument(){
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(true);
    final DocumentBuilder docBuilder = call(()->dbf.newDocumentBuilder());
    final DOMImplementation domImpl = docBuilder.getDOMImplementation();
    final DocumentType docType = domImpl.createDocumentType(
      "html", "-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    final Document document = domImpl.createDocument("http://www.w3.org/1999/xhtml", "html", docType);
    verifyEqual(document.getDoctype(), docType);
    return document;
//    return docBuilder.newDocument();
  }

  public static String toString(final Document doc){
    return toBytes(doc).utf8ToString();
  }

  public static TypedBytes toTypedBytes(final Document doc){
    return toBytes(doc).typed(DataTypes.XHTML);
  }

  public static Bytes toBytes(final Document doc){
    final BytesBuilder b = ByteUtils.newBytesBuilder(4096);
    write(doc, b);
    return b.build();
  }

  public static long write(final Document doc, final OutputStream out) {
    final DOMImplementationLS dom =
      (DOMImplementationLS) call(()->DOMImplementationRegistry.newInstance()).getDOMImplementation("LS")
    ;
    final LSSerializer serializer = dom.createLSSerializer();
    serializer.setNewLine("\n");
    final LSOutput destination = dom.createLSOutput();
    destination.setEncoding(UTF_8.name());
    final AtomicLong counter = new AtomicLong();
    destination.setByteStream(StreamUtils.countingStream(out, counter::addAndGet));
    serializer.write(doc, destination);
    return counter.get();
  }


  public static String toString1(final Document doc){
    final DocumentType doctype = notNull(doc.getDoctype());
    final DOMSource domSource = new DOMSource(doc);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    final StreamResult result = new StreamResult(bos);
    final TransformerFactory tf = TransformerFactory.newInstance();
    final Transformer transformer = call(()->tf.newTransformer());
    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
    transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8.name());
    call(()->transformer.transform(domSource, result));
    return new String(bos.toByteArray(), UTF_8);
  }

  public static void setChild(final Element e, final Element child) {
    final String tag = child.getTagName();
    final Opt<Element> previous = getChild(e, tag);
    if(previous.isPresent()){
      e.replaceChild(child, previous.get());
    }
    else e.appendChild(child);
  }

  public static Opt<Element> getChild(final Element e, final String tag) {
    return childNodes(e)
      .filter(n->n.getNodeType()==Node.ELEMENT_NODE)
      .map(n->(Element)n)
      .filter(ce->ce.getTagName().equals(tag))
      .collect(toOpt())
    ;
  }

  public static Stream<Node> childNodes(final Node node) {
    final NodeList childNodes = node.getChildNodes();
    return IntStream.range(0, childNodes.getLength()).mapToObj(childNodes::item);
  }

  public static Stream<Element> childElements(final Node node) {
    return childNodes(node).flatMap(filter(Element.class));
  }

  public static Stream<Attr> attributes(final Node node) {
    final NamedNodeMap attributes = node.getAttributes();
    return IntStream.range(0, attributes.getLength()).mapToObj(i->(Attr)attributes.item(i));
  }

}
