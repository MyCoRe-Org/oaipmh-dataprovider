package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.OAIUtils;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.AboutType;
import org.openarchives.oai.pmh.HeaderType;
import org.openarchives.oai.pmh.MetadataType;
import org.openarchives.oai.pmh.RecordType;
import org.openarchives.oai.pmh.StatusType;

public abstract class ListDataHandler extends ListRequestsHandler {

    protected final static Logger LOGGER = LogManager.getLogger(ListDataHandler.class);
    
    private static Map<Argument, ArgumentType> argumentMap = null;
    static {
        argumentMap = new HashMap<Argument, OAIRequest.ArgumentType>();
        argumentMap.put(Argument.from, ArgumentType.optional);
        argumentMap.put(Argument.until, ArgumentType.optional);
        argumentMap.put(Argument.metadataPrefix, ArgumentType.required);
        argumentMap.put(Argument.set, ArgumentType.optional);
        argumentMap.put(Argument.resumptionToken, ArgumentType.exclusive);
    }

    @Override
    public Map<Argument, ArgumentType> getArgumentMap() {
        return argumentMap;
    }

    public ListDataHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    protected RecordType toJAXBRecord(Record record) {
        RecordType recordType = new RecordType();
        recordType.setHeader(toJAXBHeader(record.getHeader()));

        if (record.getMetadata() != null) {
            try {
                MetadataType metadataType = new MetadataType();
                metadataType.setAny(OAIUtils.jdomToDOM(record.getMetadata().toXML()));
                recordType.setMetadata(metadataType);
            } catch (JDOMException exc) {
                throw new OAIImplementationException(exc);
            }
        }
        if(record.getAboutList() != null) {
            for (Element about : record.getAboutList()) {
                AboutType aboutType = new AboutType();
                try {
                    aboutType.setAny(OAIUtils.jdomToDOM(about));
                    recordType.getAbout().add(aboutType);
                } catch (JDOMException exc) {
                    throw new OAIImplementationException(exc);
                }
            }
        }
        return recordType;
    }

    protected HeaderType toJAXBHeader(Header header) {
        HeaderType headerType = new HeaderType();
        String id = header.getId();
        // check if id is correct
        if(!OAIUtils.checkIdentifier(id, this.oaiAdapter.getIdentify())) {
            LOGGER.warn("invalid OAI-PMH id " + id);
        }
        headerType.setIdentifier(id);
        headerType.setDatestamp(header.getDatestamp());
        if (header.isDeleted()) {
            headerType.setStatus(StatusType.DELETED);
        }
        for (Set set : header.getSetList()) {
            headerType.getSetSpec().add(set.getSpec());
        }
        return headerType;
    }

}
