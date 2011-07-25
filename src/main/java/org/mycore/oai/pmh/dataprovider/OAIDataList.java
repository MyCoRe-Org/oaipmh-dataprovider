package org.mycore.oai.pmh.dataprovider;

import java.util.ArrayList;

import org.mycore.oai.pmh.ResumptionToken;

/**
 * OAI-PMH Data container. Is used for the data of list requests which support {@link ResumptionToken} such as ListSets, ListRecords and ListIdentifiers.
 * 
 * @author Matthias Eichner
 * @param <T>
 */
public class OAIDataList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 3081555510128912877L;

    private ResumptionToken resumptionToken;

    /**
     * Creates a new <code>OAIDataList</code> without a resumption token.
     */
    public OAIDataList() {
        this(null);
    }

    /**
     * Creates a new <code>OAIDataList</code> with a resumption token.
     * 
     * @param resumptionToken
     *            the resumption token of the data list
     */
    public OAIDataList(ResumptionToken resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    /**
     * Returns the resumption token of that list.
     * 
     * @return the resumption token
     */
    public ResumptionToken getResumptionToken() {
        return resumptionToken;
    }

    public void setResumptionToken(ResumptionToken resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    /**
     * Checks if a resumption token is set.
     * 
     * @return true if set, otherwise false
     */
    public boolean isResumptionTokenSet() {
        return this.resumptionToken != null;
    }

}
