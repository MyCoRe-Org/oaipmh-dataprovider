package org.mycore.oai.pmh.dataprovider.jaxb;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIRequest.ArgumentType;

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

}
