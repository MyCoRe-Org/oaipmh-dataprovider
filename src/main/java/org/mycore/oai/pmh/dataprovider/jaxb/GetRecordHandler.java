package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.GetRecordType;
import org.openarchives.oai.pmh.OAIPMHtype;
import org.openarchives.oai.pmh.RecordType;

public class GetRecordHandler extends JAXBVerbHandler {

    public GetRecordHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    private static Map<Argument, ArgumentType> argumentMap = null;

    static {
        argumentMap = new HashMap<Argument, OAIRequest.ArgumentType>();
        argumentMap.put(Argument.identifier, ArgumentType.required);
        argumentMap.put(Argument.metadataPrefix, ArgumentType.required);
    }

    @Override
    public Map<Argument, ArgumentType> getArgumentMap() {
        return argumentMap;
    }

    @Override
    public OAIPMHtype handle(OAIRequest request) throws OAIException {
        String id = request.getIdentifier();
        MetadataFormat mf = this.oaiAdapter.getMetadataFormat(request.getMetadataPrefix());
        Record record = this.oaiAdapter.getRecord(id, mf);

        // convert record to recordtype
        RecordType recordType = JAXBUtils.toJAXBRecord(record, this.oaiAdapter.getIdentify());
        GetRecordType getRecordType = new GetRecordType();
        getRecordType.setRecord(recordType);

        // return oaipmh
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.setGetRecord(getRecordType);
        return oaipmh;
    }

}
