package org.mycore.oai.pmh.dataprovider;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.mycore.oai.pmh.Description;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.OAIIdentifierDescription;

/**
 * Provides some usefull OAI-PMH util methods.
 * 
 * @author Matthias Eichner
 */
public abstract class OAIUtils {

    /**
     * Checks if the identifier is valid. For more information see <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#UniqueIdentifier">here</a>.
     * 
     * @param identifier id to check
     * @param identify identify object which can contain a {@link OAIIdentifierDescription}
     * @return true if identifier is valid, otherwise false
     */
    public static boolean checkIdentifier(String identifier, Identify identify) {
        OAIIdentifierDescription idDescription = getIdentifierDescription(identify);
        if (idDescription != null) {
            return idDescription.isValid(identifier);
        } else {
            return identifier.startsWith("oai:");
        }
    }

    public static OAIIdentifierDescription getIdentifierDescription(Identify identify) {
        for (Description d : identify.getDescriptionList()) {
            if (d instanceof OAIIdentifierDescription)
                return (OAIIdentifierDescription) d;
        }
        return null;
    }

    /**
     * Converts a jdom element to a org.w3c.dom.Element.
     * 
     * @param jdomElement the jdom element to convert
     * @return 
     * @throws JDOMException if a parsing exception occur
     */
    public static org.w3c.dom.Element jdomToDom(Element jdomElement) throws JDOMException {
        DOMOutputter outputter = new DOMOutputter();
        org.w3c.dom.Document doc = outputter.output(new Document(jdomElement));
        return doc.getDocumentElement();
    }

}
