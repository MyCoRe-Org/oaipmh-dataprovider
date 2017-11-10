package org.mycore.oai.pmh.dataprovider.jaxb;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.sax.SAXHandler;
import org.jdom2.output.XMLOutputter;
import org.mycore.oai.pmh.OAIConstants;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIResponse;
import org.openarchives.oai.pmh.OAIPMHtype;

/**
 * JAXB implementation of a OAI response.
 *
 * @author Matthias Eichner
 */
public class JAXBOAIResponse implements OAIResponse {

    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(OAIPMHtype.class);
        } catch (Exception exc) {
            LogManager.getLogger().error("Unable to create jaxb context for OAIPMHType", exc);
        }
    }

    private OAIPMHtype oaipmh;

    public JAXBOAIResponse(OAIPMHtype oaipmh) {
        this.oaipmh = oaipmh;
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, OAIConstants.SCHEMA_LOC_OAI);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        return marshaller;
    }

    /**
     * Converts the oaipmh pojo to a jdom representation.
     *
     * @return the oaipmh as jdom
     * @throws JDOMException the conversion went wrong
     */
    private Element marshal() throws JDOMException {
        try {
            NamespaceFilter outFilter = new NamespaceFilter();
            SAXHandler saxHandler = new SAXHandler();
            outFilter.setContentHandler(saxHandler);
            Marshaller marshaller = createMarshaller();
            marshaller.marshal(new JAXBElement<>(new QName(OAIConstants.NS_OAI.getURI(), OAIConstants.XML_OAI_ROOT), OAIPMHtype.class,
                    this.oaipmh), outFilter);
            Element rootElement = saxHandler.getDocument().detachRootElement();
            moveNamespacesUp(rootElement);
            return rootElement;
        } catch (Exception exc) {
            throw new JDOMException("while marshalling", exc);
        }
    }

    /**
     * Moves all namespace declarations in the children of target to the target.
     *
     * @param target the namespace are bundled here
     */
    private void moveNamespacesUp(Element target) {
        Map<String, Namespace> existingNamespaces = getNamespaceMap(target);
        Map<String, Namespace> newNamespaces = new HashMap<>();
        target.getDescendants(new ElementFilter()).forEach(child -> {
            Map<String, Namespace> childNamespaces = getNamespaceMap(child);
            childNamespaces.forEach((prefix, ns) -> {
                if (existingNamespaces.containsKey(prefix) || newNamespaces.containsKey(prefix)) {
                    return;
                }
                newNamespaces.put(prefix, ns);
            });
        });
        newNamespaces.forEach((prefix, ns) -> target.addNamespaceDeclaration(ns));
    }

    private Map<String, Namespace> getNamespaceMap(Element element) {
        Map<String, Namespace> map = new HashMap<>();
        map.put(element.getNamespace().getPrefix(), element.getNamespace());
        element.getAdditionalNamespaces().forEach(ns -> map.put(ns.getPrefix(), ns));
        return map;
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
            XMLOutputter out = new XMLOutputter(Format.formatted.equals(format) ? org.jdom2.output.Format.getPrettyFormat() : org.jdom2.output.Format.getRawFormat());
            StringWriter writer = new StringWriter();
            out.output(e, writer);
            return writer.toString();
        } catch (Exception exc) {
            throw new OAIImplementationException(exc);
        }
    }

}
