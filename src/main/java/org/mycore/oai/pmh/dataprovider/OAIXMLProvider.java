package org.mycore.oai.pmh.dataprovider;

/**
 * A <code>OAIXMLProvider</code> handles oai requests to generate valid oai-pmh xml.
 * 
 * @author Matthias Eichner
 */
public interface OAIXMLProvider {

    OAIResponse handleRequest(OAIRequest request);

    OAIAdapter getAdapter();

}
