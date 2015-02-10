package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.Date;

import org.mycore.oai.pmh.BadArgumentException;
import org.mycore.oai.pmh.BadVerbException;
import org.mycore.oai.pmh.DateUtils;
import org.mycore.oai.pmh.NoRecordsMatchException;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.OAIException.ErrorCode;
import org.mycore.oai.pmh.Verb;
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

    protected OAIAdapter oaiAdapter;

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
            verbHandler = getRecordHandler();
        } else if (Verb.Identify.equals(verb)) {
            verbHandler = getIdentifyHandler();
        } else if (Verb.ListIdentifiers.equals(verb)) {
            verbHandler = getListIdentifiersHandler();
        } else if (Verb.ListMetadataFormats.equals(verb)) {
            verbHandler = getListMetadataFormatsHandler();
        } else if (Verb.ListRecords.equals(verb)) {
            verbHandler = getListRecordsHandler();
        } else if (Verb.ListSets.equals(verb)) {
            verbHandler = getListSetsHandler();
        } else {
            return getErrorResponse(new BadVerbException(request.getVerb()), request);
        }
        // check arguments
        try {
            request.checkBadArgument(verbHandler.getArgumentMap(), this.getAdapter());
        } catch (BadArgumentException bae) {
            return getErrorResponse(bae, request);
        } catch (NoRecordsMatchException nrme) {
            return getErrorResponse(nrme, request);
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

    protected JAXBVerbHandler getRecordHandler() {
        return new GetRecordHandler(this.getAdapter());
    }
    
    protected JAXBVerbHandler getIdentifyHandler() {
        return new IdentifyHandler(this.getAdapter());
    }
    
    protected JAXBVerbHandler getListIdentifiersHandler() {
        return new ListIdentifiersHandler(this.getAdapter());
    }
    
    protected JAXBVerbHandler getListMetadataFormatsHandler() {
        return new ListMetadataFormatsHandler(this.getAdapter());
    }
    
    protected JAXBVerbHandler getListRecordsHandler() {
        return new ListRecordsHandler(this.getAdapter());
    }
    
    protected JAXBVerbHandler getListSetsHandler() {
        return new ListSetsHandler(this.getAdapter());
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

    protected Date getResponseDate() {
        return DateUtils.getCurrentDate();
    }

    @Override
    public OAIAdapter getAdapter() {
        return this.oaiAdapter;
    }

}
