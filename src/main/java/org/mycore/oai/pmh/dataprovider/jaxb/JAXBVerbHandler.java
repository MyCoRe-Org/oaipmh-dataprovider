package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.Map;

import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.VerbHandler;
import org.mycore.oai.pmh.dataprovider.OAIRequest.Argument;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.OAIPMHtype;

public abstract class JAXBVerbHandler implements VerbHandler<OAIPMHtype> {

	protected OAIAdapter oaiAdapter;

	public JAXBVerbHandler(OAIAdapter oaiAdapter) {
		this.oaiAdapter = oaiAdapter;
	}

	/**
	 * 
	 * @param request
	 * @param oaipmh
	 */
	public abstract OAIPMHtype handle(OAIRequest request) throws OAIException;

	public abstract Map<Argument, ArgumentType> getArgumentMap();

}
