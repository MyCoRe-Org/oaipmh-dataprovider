package org.mycore.oai.pmh.dataprovider;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * A <code>OAIRespones</code> provides methods to get the generated xml of an {@link OAIXMLProvider}.
 * 
 * @author Matthias Eichner
 */
public interface OAIResponse {

    public enum Format {
        unformatted, formatted
    }

    /**
     * Returns the oai pmh xml as jdom.
     * 
     * @return oai pmh as jdom
     * @throws JDOMException if a parsing exception occur
     */
    public Element toXML() throws JDOMException;

    /**
     * Returns the oai pmh xml as unformatted text.
     * 
     * @return oai pmh response as string
     */
    public String toString();

    /**
     * Returns the oai pmh xml as formatted text.
     * 
     * @param format format of the text
     * @return oai pmh response as string
     */
    public String toString(Format format);

}
