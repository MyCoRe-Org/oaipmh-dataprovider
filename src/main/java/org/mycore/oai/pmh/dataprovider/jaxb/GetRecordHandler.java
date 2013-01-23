package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.output.DOMOutputter;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.Metadata;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.GetRecordType;
import org.openarchives.oai.pmh.HeaderType;
import org.openarchives.oai.pmh.MetadataType;
import org.openarchives.oai.pmh.OAIPMHtype;
import org.openarchives.oai.pmh.RecordType;
import org.openarchives.oai.pmh.StatusType;

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

        GetRecordType getRecordType = new GetRecordType();
        RecordType recordType = new RecordType();
        getRecordType.setRecord(recordType);

        // header
        HeaderType headerType = new HeaderType();
        headerType.setDatestamp(record.getHeader().getDatestamp());
        headerType.setIdentifier(record.getHeader().getId());
        if (record.getHeader().isDeleted()) {
            headerType.setStatus(StatusType.DELETED);
        }
        for (Set set : record.getHeader().getSetList()) {
            headerType.getSetSpec().add(set.getSpec());
        }
        recordType.setHeader(headerType);
        // metadata
        if(record.getMetadata() != null) {
            recordType.setMetadata(getMetadataType(record.getMetadata()));
        }
        // return oaipmh
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.setGetRecord(getRecordType);
        return oaipmh;
    }

    protected MetadataType getMetadataType(Metadata metadata) {
        MetadataType metadataType = new MetadataType();
        try {
            DOMOutputter outputter = new DOMOutputter();
            org.w3c.dom.Document doc = outputter.output(new Document(metadata.toXML()));
            metadataType.setAny(doc.getDocumentElement());
            return metadataType;
        } catch (Exception exc) {
            throw new OAIImplementationException(exc);
        }
    }

}
