package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.Date;

import org.mycore.oai.pmh.BadArgumentException;
import org.mycore.oai.pmh.BadVerbException;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.OAIException.ErrorCode;
import org.mycore.oai.pmh.Verb;
import org.mycore.oai.pmh.dataprovider.DateUtils;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIResponse;
import org.mycore.oai.pmh.dataprovider.OAIXMLProvider;
import org.openarchives.oai.pmh.OAIPMHerrorType;
import org.openarchives.oai.pmh.OAIPMHerrorcodeType;
import org.openarchives.oai.pmh.OAIPMHtype;
import org.openarchives.oai.pmh.RequestType;
import org.openarchives.oai.pmh.VerbType;

/**
 * Implementation of a {@link OAIXMLProvider} using the JAXB API.
 * 
 * @author Matthias Eichner
 */
public class JAXBOAIProvider implements OAIXMLProvider {

    private OAIAdapter oaiAdapter;

    public JAXBOAIProvider(OAIAdapter oaiAdapter) {
        this.oaiAdapter = oaiAdapter;
    }

    @Override
    public OAIResponse handleRequest(OAIRequest request) {
        // set global granularity
        DateUtils.setGranularity(this.getAdapter().getIdentify().getGranularity());        
        // check verb
        JAXBVerbHandler verbHandler = null;
        Verb verb = null;
        try {
            verb = Verb.valueOf(request.getVerb());
        } catch(Exception exc) {
            return getErrorResponse(new BadVerbException(request.getVerb()), request);
        }
        if (Verb.GetRecord.equals(verb)) {
            verbHandler = new GetRecordHandler(this.getAdapter());
        } else if (Verb.Identify.equals(verb)) {
            verbHandler = new IdentifyHandler(this.getAdapter());
        } else if (Verb.ListIdentifiers.equals(verb)) {
            verbHandler = new ListIdentifiersHandler(this.getAdapter());
        } else if (Verb.ListMetadataFormats.equals(verb)) {
            verbHandler = new ListMetadataFormatsHandler(this.getAdapter());
        } else if (Verb.ListRecords.equals(verb)) {
            verbHandler = new ListRecordsHandler(this.getAdapter());
        } else if (Verb.ListSets.equals(verb)) {
            verbHandler = new ListSetsHandler(this.getAdapter());
        } else {
            return getErrorResponse(new BadVerbException(request.getVerb()), request);
        }
        // check arguments
        try {
            request.checkBadArgument(verbHandler.getArgumentMap(), this.getAdapter());
        } catch (BadArgumentException bae) {
            return getErrorResponse(bae, request);
        }
        // handle request
        try {
            OAIPMHtype oaipmh = verbHandler.handle(request);
            oaipmh.setRequest(getRequestType(request, false));
            oaipmh.setResponseDate(getResponseDate());
            return new JAXBOAIResponse(oaipmh);
        } catch(OAIException oaiExc) {
            return getErrorResponse(oaiExc, request);
        }
    }

    private RequestType getRequestType(OAIRequest req, boolean errorOccur) {
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

    private OAIResponse getErrorResponse(OAIException oaiExc, OAIRequest request) {
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.getError().add(getError(oaiExc.getCode(), oaiExc.getMessage()));
        oaipmh.setRequest(getRequestType(request, true));
        oaipmh.setResponseDate(getResponseDate());
        return new JAXBOAIResponse(oaipmh);
    }

    private OAIPMHerrorType getError(ErrorCode errorCode, String errorDescription) {
        OAIPMHerrorType error = new OAIPMHerrorType();
        error.setCode(OAIPMHerrorcodeType.fromValue(errorCode.name()));
        error.setValue(errorDescription);
        return error;
    }

    private Date getResponseDate() {
        return DateUtils.getCurrentDate();
    }

    @Override
    public OAIAdapter getAdapter() {
        return this.oaiAdapter;
    }

}
