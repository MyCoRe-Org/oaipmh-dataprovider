package org.mycore.oai.pmh.dataprovider.jaxb;

import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.OAIDataList;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.openarchives.oai.pmh.ListRecordsType;
import org.openarchives.oai.pmh.OAIPMHtype;

public class ListRecordsHandler extends ListDataHandler {

    public ListRecordsHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    @Override
    public OAIPMHtype handle(OAIRequest request) throws OAIException {
        OAIDataList<Record> recordList = null;
        // get header list from oai adapter
        if (request.isResumptionToken()) {
            recordList = this.oaiAdapter.getRecords(request.getResumptionToken());
        } else {
            MetadataFormat format = this.oaiAdapter.getMetadataFormat(request.getMetadataPrefix());
            Set set = null;
            if (request.isSet()) {
                set = this.oaiAdapter.getSet(request.getSet());
            }
            recordList = this.oaiAdapter.getRecords(format, set, request.getFrom(), request.getUntil());
        }

        // create jaxb element
        ListRecordsType listRecordsType = new ListRecordsType();
        // add records
        for (Record record : recordList) {
            listRecordsType.getRecord().add(toJAXBRecord(record));
        }
        // set resumption token
        if (recordList.isResumptionTokenSet()) {
            listRecordsType.setResumptionToken(toJAXBResumptionToken(recordList.getResumptionToken()));
        }
        // return oaipmh
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.setListRecords(listRecordsType);
        return oaipmh;
    }

}
