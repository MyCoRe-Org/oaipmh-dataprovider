package org.mycore.oai.pmh.dataprovider.jaxb;

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

    private String xmlns = "";

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if(uri.equals(xmlns)) {
            super.startElement(uri, localName, localName, atts);
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if(prefix == null || prefix.length() == 0) {
            xmlns = uri;
        }
    }

}
