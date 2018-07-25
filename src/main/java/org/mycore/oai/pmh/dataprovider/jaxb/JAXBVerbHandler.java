package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.Map;

import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.mycore.oai.pmh.dataprovider.OAIVerbHandler;
import org.openarchives.oai.pmh.OAIPMHtype;

/**
 * JAXB implementation of a verb handler. Uses a {@link OAIPMHtype} as result.
 */
public abstract class JAXBVerbHandler implements OAIVerbHandler<OAIPMHtype> {

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
