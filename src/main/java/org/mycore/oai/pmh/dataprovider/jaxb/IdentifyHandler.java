package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.JDOMException;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.Description;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.OAIUtils;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.DeletedRecordType;
import org.openarchives.oai.pmh.DescriptionType;
import org.openarchives.oai.pmh.GranularityType;
import org.openarchives.oai.pmh.IdentifyType;
import org.openarchives.oai.pmh.OAIPMHtype;

public class IdentifyHandler extends JAXBVerbHandler {

    public IdentifyHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    @Override
    public Map<Argument, ArgumentType> getArgumentMap() {
        return new HashMap<Argument, OAIRequest.ArgumentType>();
    }

    @Override
    public OAIPMHtype handle(OAIRequest request) {
        Identify id = this.oaiAdapter.getIdentify();

        IdentifyType returnId = new IdentifyType();
        returnId.setRepositoryName(id.getRepositoryName());
        returnId.setBaseURL(id.getBaseURL());
        returnId.setProtocolVersion(id.getProtocolVersion());
        returnId.setEarliestDatestamp(id.getEarliestDatestamp());
        returnId.setDeletedRecord(DeletedRecordType.fromValue(id.getDeletedRecordPolicy().value()));
        returnId.setGranularity(GranularityType.valueOf(id.getGranularity().name()));
        for (String adminMail : id.getAdminEmailList()) {
            returnId.getAdminEmail().add(adminMail);
        }
        for (Description description : id.getDescriptionList()) {
            try {
                DescriptionType descriptionType = new DescriptionType();
                descriptionType.setAny(OAIUtils.jdomToDOM(description.toXML()));
                returnId.getDescription().add(descriptionType);
            } catch (JDOMException exc) {
                throw new OAIImplementationException(exc);
            }
        }
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.setIdentify(returnId);
        return oaipmh;
    }

}
