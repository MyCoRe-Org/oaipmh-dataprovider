package org.mycore.oai.pmh.dataprovider.jaxb;

import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIDataList;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.openarchives.oai.pmh.ListIdentifiersType;
import org.openarchives.oai.pmh.OAIPMHtype;

public class ListIdentifiersHandler extends ListDataHandler {

    public ListIdentifiersHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    @Override
    public OAIPMHtype handle(OAIRequest request) throws OAIException {
        OAIDataList<Header> headerList = null;
        // get header list from oai adapter
        if (request.isResumptionToken()) {
            headerList = this.oaiAdapter.getHeaders(request.getResumptionToken());
        } else {
            MetadataFormat format = this.oaiAdapter.getMetadataFormat(request.getMetadataPrefix());
            Set set = null;
            if (request.isSet()) {
                set = this.oaiAdapter.getSet(request.getSet());
            }
            headerList = this.oaiAdapter.getHeaders(format, set, request.getFrom(), request.getUntil());
        }

        // create jaxb element
        ListIdentifiersType listIdentifiersType = new ListIdentifiersType();
        // add headers
        for (Header header : headerList) {
            listIdentifiersType.getHeader().add(toJAXBHeader(header));
        }
        // set resumption token
        if (headerList.isResumptionTokenSet()) {
            listIdentifiersType.setResumptionToken(toJAXBResumptionToken(headerList.getResumptionToken()));
        }
        // return oaipmh
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.setListIdentifiers(listIdentifiersType);
        return oaipmh;
    }

}
