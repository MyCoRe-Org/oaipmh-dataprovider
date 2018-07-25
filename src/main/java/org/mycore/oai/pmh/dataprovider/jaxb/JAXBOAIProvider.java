package org.mycore.oai.pmh.dataprovider.jaxb;

import java.time.Instant;

import org.mycore.oai.pmh.BadVerbException;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.OAIException.ErrorCode;
import org.mycore.oai.pmh.Verb;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIBaseProvider;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIResponse;
import org.openarchives.oai.pmh.OAIPMHerrorType;
import org.openarchives.oai.pmh.OAIPMHerrorcodeType;
import org.openarchives.oai.pmh.OAIPMHtype;
import org.openarchives.oai.pmh.RequestType;
import org.openarchives.oai.pmh.VerbType;

/**
 * Implementation of a {@link OAIBaseProvider} using the JAXB API.
 * 
 * @author Matthias Eichner
 */
public class JAXBOAIProvider extends OAIBaseProvider<JAXBVerbHandler> {

    public JAXBOAIProvider(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    @Override
    protected JAXBVerbHandler getVerbHandler(Verb verb) throws BadVerbException {
        if (Verb.GetRecord.equals(verb)) {
            return new GetRecordHandler(this.getAdapter());
        } else if (Verb.Identify.equals(verb)) {
            return new IdentifyHandler(this.getAdapter());
        } else if (Verb.ListIdentifiers.equals(verb)) {
            return new ListIdentifiersHandler(this.getAdapter());
        } else if (Verb.ListMetadataFormats.equals(verb)) {
            return new ListMetadataFormatsHandler(this.getAdapter());
        } else if (Verb.ListRecords.equals(verb)) {
            return new ListRecordsHandler(this.getAdapter());
        } else if (Verb.ListSets.equals(verb)) {
            return new ListSetsHandler(this.getAdapter());
        }
        throw new BadVerbException(verb.name());
    }

    @Override
    protected OAIResponse handle(OAIRequest request, JAXBVerbHandler verbHandler) throws OAIException {
        OAIPMHtype oaipmh = verbHandler.handle(request);
        oaipmh.setRequest(getRequestType(request, false));
        oaipmh.setResponseDate(getResponseDate());
        return new JAXBOAIResponse(oaipmh);
    }

    protected RequestType getRequestType(OAIRequest req, boolean errorOccur) {
        RequestType rt = new RequestType();
        rt.setValue(this.getAdapter().getIdentify().getBaseURL());
        if (!errorOccur) {
            rt.setVerb(VerbType.fromValue(req.getVerb()));
            rt.setIdentifier(req.getIdentifier());
            rt.setMetadataPrefix(req.getMetadataPrefix());
            rt.setFrom(req.getFrom());
            rt.setUntil(req.getUntil());
            rt.setSet(req.getSet());
            if (req.isResumptionToken())
                rt.setResumptionToken(req.getResumptionToken());
        }
        return rt;
    }

    @Override
    protected OAIResponse getErrorResponse(OAIException oaiExc, OAIRequest request) {
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.getError().add(getError(oaiExc.getCode(), oaiExc.getMessage()));
        oaipmh.setRequest(getRequestType(request, true));
        oaipmh.setResponseDate(getResponseDate());
        return new JAXBOAIResponse(oaipmh);
    }

    protected OAIPMHerrorType getError(ErrorCode errorCode, String errorDescription) {
        OAIPMHerrorType error = new OAIPMHerrorType();
        error.setCode(OAIPMHerrorcodeType.fromValue(errorCode.name()));
        error.setValue(errorDescription);
        return error;
    }

    protected Instant getResponseDate() {
        return Instant.now();
    }

}
