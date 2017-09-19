package org.mycore.oai.pmh.dataprovider;

import java.util.Map;

import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;

public interface VerbHandler<Verb> {

    Verb handle(OAIRequest request) throws OAIException;

    Map<Argument, ArgumentType> getArgumentMap();

}
