package org.mycore.oai.pmh.dataprovider.jaxb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.OAIUtils;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.openarchives.oai.pmh.AboutType;
import org.openarchives.oai.pmh.HeaderType;
import org.openarchives.oai.pmh.MetadataType;
import org.openarchives.oai.pmh.RecordType;
import org.openarchives.oai.pmh.StatusType;

public abstract class JAXBUtils {

    protected final static Logger LOGGER = LogManager.getLogger(JAXBUtils.class);

    public static RecordType toJAXBRecord(Record record, Identify identify) {
        RecordType recordType = new RecordType();
        recordType.setHeader(toJAXBHeader(record.getHeader(), identify));

        if (record.getMetadata() != null) {
            try {
                MetadataType metadataType = new MetadataType();
                metadataType.setAny(OAIUtils.jdomToDOM(record.getMetadata().toXML()));
                recordType.setMetadata(metadataType);
            } catch (JDOMException exc) {
                throw new OAIImplementationException(exc);
            }
        }
        if (record.getAboutList() != null) {
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

    public static HeaderType toJAXBHeader(Header header, Identify identify) {
        HeaderType headerType = new HeaderType();
        String id = header.getId();
        // check if id is correct
        if (!OAIUtils.checkIdentifier(id, identify)) {
            LOGGER.warn("invalid OAI-PMH id {}", id);
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
