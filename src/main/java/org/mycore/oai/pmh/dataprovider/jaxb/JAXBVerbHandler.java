package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.Map;

import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.mycore.oai.pmh.dataprovider.VerbHandler;
import org.openarchives.oai.pmh.OAIPMHtype;

/**
 * Base class to handle all OAI-PMH verbs.
 */
public abstract class JAXBVerbHandler implements VerbHandler<OAIPMHtype> {

    protected OAIAdapter oaiAdapter;

    public JAXBVerbHandler(OAIAdapter oaiAdapter) {
        this.oaiAdapter = oaiAdapter;
    }

    /**
     * Handles the request.
     *
     * @param request the request to handle
     */
    public abstract OAIPMHtype handle(OAIRequest request) throws OAIException;

    public abstract Map<Argument, ArgumentType> getArgumentMap();

}
