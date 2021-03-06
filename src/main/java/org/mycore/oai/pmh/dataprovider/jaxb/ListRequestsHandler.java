package org.mycore.oai.pmh.dataprovider.jaxb;

import java.math.BigInteger;

import org.mycore.oai.pmh.DefaultResumptionToken;
import org.mycore.oai.pmh.ResumptionToken;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.openarchives.oai.pmh.ResumptionTokenType;

/**
 * Abstract class for the ListRecords, ListIdentifiers and ListSets handlers implementation.
 * 
 * @author Matthias Eichner
 */
public abstract class ListRequestsHandler extends JAXBVerbHandler {

    public ListRequestsHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    protected ResumptionTokenType toJAXBResumptionToken(ResumptionToken resumptionToken) {
        ResumptionTokenType rtt = new ResumptionTokenType();
        rtt.setValue(resumptionToken.getToken());
        if (resumptionToken instanceof DefaultResumptionToken) {
            DefaultResumptionToken drt = (DefaultResumptionToken) resumptionToken;
            if(drt.getCompleteListSize() != null) {
                rtt.setCompleteListSize(BigInteger.valueOf(drt.getCompleteListSize()));
            }
            if(drt.getCursor() != null) {
                rtt.setCursor(BigInteger.valueOf(drt.getCursor()));
            }
            if(drt.getExpirationDate() != null) {
                rtt.setExpirationDate(drt.getExpirationDate());
            }
        }
        return rtt;
    }

}
