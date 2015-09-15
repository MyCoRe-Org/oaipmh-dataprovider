package org.mycore.oai.pmh.dataprovider.jaxb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter removes the annoying empty xmlns tags and auto generated namespaces by jaxb.
 * 
 * with filter:
 * <mets:dmdSec ID="MY_DMD_SEC_ID">
 * 
 * without filter:
 * <mets:dmdSec ID="MY_DMD_SEC_ID" xmlns="" xmlns:ns4="http://www.openarchives.org/OAI/2.0/">
 * TODO check on side effects
 * 
 * @author Matthias Eichner
 */
public class NamespaceFilter extends XMLFilterImpl {

    private static Logger LOGGER = LogManager.getLogger(NamespaceFilter.class);

    private String xmlns = "";

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start element (" + xmlns + "): " + uri + " " + localName + " " + qName);
        }
        if (uri.equals(xmlns)) {
            super.startElement(uri, localName, localName, atts);
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start prefix (" + xmlns + "): " + prefix + " " + uri);
        }
        if (uri == null || uri.equals("")) {
            return;
        }
        if (prefix == null || prefix.length() == 0) {
            xmlns = uri;
        } else if (!uri.equals(xmlns)) {
            super.startPrefixMapping(prefix, uri);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("end prefix (" + xmlns + "): " + prefix);
        }
    }

}
