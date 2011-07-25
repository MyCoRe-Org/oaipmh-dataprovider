package org.mycore.oai.pmh.dataprovider;

import java.util.Map;

import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.dataprovider.OAIRequest.Argument;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;

public interface VerbHandler<Verb> {

    public Verb handle(OAIRequest request) throws OAIException;

    public Map<Argument, ArgumentType> getArgumentMap();

}
