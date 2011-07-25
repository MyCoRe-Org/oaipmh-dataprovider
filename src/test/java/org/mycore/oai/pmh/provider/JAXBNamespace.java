package org.mycore.oai.pmh.provider;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class JAXBNamespace {
    
    private static JAXBContext jaxbContext;
 
    private static Marshaller marshaller;
 
    static {
        try {
            jaxbContext = JAXBContext.newInstance(TestJAXB.class);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
    
    public void nsTest() throws Exception {
        // create dom
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = builder.newDocument();
        org.w3c.dom.Element e = doc.createElement("dom");
        e.setAttribute("xmlns", "www.google.de");
        doc.appendChild(e);
        // print dom
        System.out.println("So möchte ich das haben, ein Element ohne Prefix mit Namespace Attribut");
        xmlOut(doc); // perfect - only one xmlns tag
        System.out.println();
        System.out.println();
 
        // create jaxb stuff
        TestJAXB testJAXB = new TestJAXB();
        testJAXB.desciption.any = e;
        System.out.println("Und hier mit Prefix 'dom' und extra Namespace 'xmlns:dom'");
        marshaller.marshal(testJAXB, System.out); // why dom:dom and xmlns:dom? 
    }
 
    @XmlRootElement(name="test")
    public static class TestJAXB {
        public DescriptionType desciption = new DescriptionType();
    }
 
    @XmlType(name = "descriptionType")
    public static class DescriptionType {
        @XmlAnyElement(lax = false)
        public Object any;
    }
 
    public static void xmlOut(org.w3c.dom.Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource(doc);
        Result output = new StreamResult(System.out);
        transformer.transform(source, output);
    }
 
    public static void main(String[] args) throws Exception {
        JAXBNamespace nsTest = new JAXBNamespace();
        nsTest.nsTest();
    }
}