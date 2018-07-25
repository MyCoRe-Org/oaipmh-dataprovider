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

    private static Map<Argument, ArgumentType> ARGUMENT_MAP;

    static {
        ARGUMENT_MAP = new HashMap<Argument, OAIRequest.ArgumentType>();
        ARGUMENT_MAP.put(Argument.from, ArgumentType.optional);
        ARGUMENT_MAP.put(Argument.until, ArgumentType.optional);
        ARGUMENT_MAP.put(Argument.metadataPrefix, ArgumentType.required);
        ARGUMENT_MAP.put(Argument.set, ArgumentType.optional);
        ARGUMENT_MAP.put(Argument.resumptionToken, ArgumentType.exclusive);
    }

    @Override
    public Map<Argument, ArgumentType> getArgumentMap() {
        return ARGUMENT_MAP;
    }

    public ListDataHandler(OAIAdapter oaiAdapter) {
        super(oaiAdapter);
    }

}
