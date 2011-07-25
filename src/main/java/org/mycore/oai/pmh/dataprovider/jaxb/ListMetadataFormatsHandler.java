package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.OAIConstants;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.Argument;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.ListMetadataFormatsType;
import org.openarchives.oai.pmh.MetadataFormatType;
import org.openarchives.oai.pmh.OAIPMHtype;

public class ListMetadataFormatsHandler extends JAXBVerbHandler {

    private static Map<Argument, ArgumentType> argumentMap = null;
    static {
        argumentMap = new HashMap<OAIRequest.Argument, OAIRequest.ArgumentType>();
        argumentMap.put(Argument.identifier, ArgumentType.optional);
    }

    @Override
    public Map<Argument, ArgumentType> getArgumentMap() {
        return argumentMap;
    }

    public ListMetadataFormatsHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    @Override
    public OAIPMHtype handle(OAIRequest request) throws OAIException {
        String id = request.getIdentifier();
        List<MetadataFormat> formatList;
        // get format list
        if (id == null) {
            // get all metadata formats
            formatList = this.oaiAdapter.getMetadataFormats();
            if (!checkOAIDublicCore(formatList)) {
                throw new OAIImplementationException(
                        "OAI adapter has to support oai_dc (http://www.openarchives.org/OAI/openarchivesprotocol.html#MetadataNamespaces)."
                                + "You can use DCMetadataFormat.");
            }
        } else {
            // get metadata formats of single object
            formatList = this.oaiAdapter.getMetadataFormats(id);
        }
        // to jaxb element
        ListMetadataFormatsType listMetadataFormatsType = new ListMetadataFormatsType();
        for (MetadataFormat format : formatList) {
            MetadataFormatType mft = new MetadataFormatType();
            mft.setMetadataPrefix(format.getPrefix());
            mft.setSchema(format.getSchema());
            mft.setMetadataNamespace(format.getNamespace());
            listMetadataFormatsType.getMetadataFormat().add(mft);
        }
        // return oaipmh
        OAIPMHtype oaipmh = new OAIPMHtype();
        oaipmh.setListMetadataFormats(listMetadataFormatsType);
        return oaipmh;
    }

    /**
     * Checks if the list contains the OAI_DC metadata format. This is required by specification see <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#MetadataNamespaces">here</a>
     * 
     * @param formatList
     *            list of metadata formats
     * @return true if list contains dublic core format
     */
    private boolean checkOAIDublicCore(List<MetadataFormat> formatList) {
        for (MetadataFormat f : formatList) {
            if (f.getNamespace().equals(OAIConstants.NS_OAI_DC.getURI()))
                return true;
        }
        return false;
    }
}
