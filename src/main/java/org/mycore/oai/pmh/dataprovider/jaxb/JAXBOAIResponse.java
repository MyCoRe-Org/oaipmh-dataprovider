package org.mycore.oai.pmh.dataprovider.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXHandler;
import org.jdom.output.XMLOutputter;
import org.mycore.oai.pmh.OAIConstants;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIResponse;
import org.openarchives.oai.pmh.OAIPMHtype;

public class JAXBOAIResponse implements OAIResponse {

    private static JAXBContext jaxbContext;

    private static Marshaller marshaller;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(OAIPMHtype.class);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, OAIConstants.SCHEMA_LOC_OAI);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private OAIPMHtype oaipmh;

    public JAXBOAIResponse(OAIPMHtype oaipmh) {
        this.oaipmh = oaipmh;
    }

    private Element marshal() throws JDOMException {
        try {
            NamespaceFilter outFilter = new NamespaceFilter();
            SAXHandler saxHandler = new SAXHandler();
            outFilter.setContentHandler(saxHandler);
            marshaller.marshal(new JAXBElement<OAIPMHtype>(new QName(OAIConstants.NS_OAI.getURI(), OAIConstants.XML_OAI_ROOT), OAIPMHtype.class,
                    this.oaipmh), outFilter);
            return saxHandler.getDocument().detachRootElement();
        } catch (Exception exc) {
            throw new JDOMException("while marshalling", exc);
        }
    }

    @Override
    public Element toXML() throws JDOMException {
        return marshal();
    }

    @Override
    public String toString() {
        return toString(Format.unformatted);
    }

    @Override
    public String toString(Format format) {
        try {
            Element e = marshal();
            XMLOutputter out = new XMLOutputter(Format.formatted.equals(format) ? org.jdom.output.Format.getPrettyFormat() : org.jdom.output.Format.getRawFormat());
            StringWriter writer = new StringWriter();
            out.output(e, writer);
            return writer.toString();
        } catch (Exception exc) {
            throw new OAIImplementationException(exc);
        }
    }

}