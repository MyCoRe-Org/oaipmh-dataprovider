package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.JDOMException;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.Description;
import org.mycore.oai.pmh.OAIDataList;
import org.mycore.oai.pmh.OAIException;
import org.mycore.oai.pmh.OAIUtils;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIImplementationException;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;
import org.openarchives.oai.pmh.DescriptionType;
import org.openarchives.oai.pmh.ListSetsType;
import org.openarchives.oai.pmh.OAIPMHtype;
import org.openarchives.oai.pmh.SetType;

public class ListSetsHandler extends ListRequestsHandler {

    private static Map<Argument, ArgumentType> argumentMap = null;
    static {
        argumentMap = new HashMap<>();
        argumentMap.put(Argument.resumptionToken, ArgumentType.exclusive);
    }

    @Override
    public Map<Argument, ArgumentType> getArgumentMap() {
        return argumentMap;
    }

    public ListSetsHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

    @Override
	public OAIPMHtype handle(OAIRequest request) throws OAIException {
	    OAIDataList<? extends Set> setList;
	    if(request.isResumptionToken()) {
	        setList = this.oaiAdapter.getSets(request.getResumptionToken());
	    } else {
	        setList = this.oaiAdapter.getSets();
	    }
	    
	    ListSetsType listSetsType = new ListSetsType();
	    for(Set set : setList) {
	        SetType setType = new SetType();
	        setType.setSetName(set.getName());
	        setType.setSetSpec(set.getSpec());
	        
	        for(Description description : set.getDescription()) {
	            DescriptionType descriptionType = new DescriptionType();
	            try {
                    descriptionType.setAny(OAIUtils.jdomToDOM(description.toXML()));
                    setType.getSetDescription().add(descriptionType);
                } catch (JDOMException exc) {
                    throw new OAIImplementationException(exc);
                }
            }
	        listSetsType.getSet().add(setType);
	    }
        // set resumption token
        if (setList.isResumptionTokenSet()) {
            listSetsType.setResumptionToken(toJAXBResumptionToken(setList.getResumptionToken()));
        }
	    OAIPMHtype oaipmh = new OAIPMHtype();
	    oaipmh.setListSets(listSetsType);
	    return oaipmh;
	}
}
