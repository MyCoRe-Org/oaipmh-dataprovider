package org.mycore.oai.pmh.dataprovider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.BadArgumentException;
import org.mycore.oai.pmh.BadArgumentException.Type;
import org.mycore.oai.pmh.DateUtils;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.NoRecordsMatchException;
import org.mycore.oai.pmh.OAIIdentifierDescription;
import org.mycore.oai.pmh.OAIUtils;

public class OAIRequest {

    public enum ArgumentType {
        optional, required, exclusive
    }

    private String verb;

    private String identifier;

    private String metadataPrefix;

    private String from;

    private String until;

    private String set;

    private String resumptionToken;

    public String getVerb() {
        return verb;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public String getFromAsString() {
        return this.from;
    }

    public String getUntilAsString() {
        return this.until;
    }

    public Instant getFrom() {
        return DateUtils.parse(this.from);
    }

    public Instant getUntil() {
        return DateUtils.parse(this.until);
    }

    public Instant getUntilCalculated() {
        if (this.until == null) {
            return null;
        }
        Granularity untilGranularity = DateUtils.guessGranularity(this.until);
        if (Granularity.YYYY_MM_DD.equals(untilGranularity)) {
            return LocalDate.parse(this.until).atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
        }
        return getUntil();
    }

    public String getResumptionToken() {
        return resumptionToken;
    }

    public String getSet() {
        return set;
    }

    /**
     * Creates a new oai request by verb.
     * 
     * @param verb
     */
    public OAIRequest(String verb) {
        this.verb = verb;
    }

    private BadArgumentException badArgumentException;

    /**
     * Creates a new oai request by a map of url request parameters.
     * 
     * @param parameterMap a map of request parameters
     */
    public OAIRequest(Map<String, List<String>> parameterMap) {
        try {
            this.setArgument(Argument.verb, parameterMap.get(Argument.verb.name()));
            this.setArgument(Argument.identifier, parameterMap.get(Argument.identifier.name()));
            this.setArgument(Argument.metadataPrefix, parameterMap.get(Argument.metadataPrefix.name()));
            this.setArgument(Argument.from, parameterMap.get(Argument.from.name()));
            this.setArgument(Argument.until, parameterMap.get(Argument.until.name()));
            this.setArgument(Argument.set, parameterMap.get(Argument.set.name()));
            this.setArgument(Argument.resumptionToken, parameterMap.get(Argument.resumptionToken.name()));
        } catch (BadArgumentException bae) {
            // is temporary stored until checkBadArgument is called
            this.badArgumentException = bae;
            return;
        }
        // check invalid parameters
        parameterMap.remove(Argument.verb.name());
        parameterMap.remove(Argument.identifier.name());
        parameterMap.remove(Argument.metadataPrefix.name());
        parameterMap.remove(Argument.from.name());
        parameterMap.remove(Argument.until.name());
        parameterMap.remove(Argument.set.name());
        parameterMap.remove(Argument.resumptionToken.name());
        if (!parameterMap.isEmpty()) {
            Set<String> set = parameterMap.keySet();
            // is temporary stored until checkBadArgument is called
            this.badArgumentException = new BadArgumentException(Type.invalid, set.toArray(new String[set.size()]));
            return;
        }
    }

    private void setArgument(Argument arg, List<String> valueList) throws BadArgumentException {
        if (valueList == null || valueList.isEmpty()) {
            return;
        } else if (valueList.size() > 1) {
            throw new BadArgumentException(Type.repeated, arg.name());
        }
        this.setArgument(arg, valueList.get(0));
    }

    /**
     * Sets a new argument.
     * 
     * @param arg the argument to set
     * @param value the new value of the argument
     * @throws BadArgumentException if the argument is invalid
     */
    public void setArgument(Argument arg, String value) throws BadArgumentException {
        if (Argument.verb.equals(arg)) {
            this.verb = value;
        } else if (Argument.identifier.equals(arg)) {
            this.identifier = value;
        } else if (Argument.metadataPrefix.equals(arg)) {
            this.metadataPrefix = value;
        } else if (Argument.from.equals(arg)) {
            this.from = value;
        } else if (Argument.until.equals(arg)) {
            this.until = value;
        } else if (Argument.set.equals(arg)) {
            this.set = value;
        } else if (Argument.resumptionToken.equals(arg)) {
            this.resumptionToken = value;
        } else {
            throw new BadArgumentException(BadArgumentException.Type.invalid, arg.name());
        }
    }

    /**
     * Checks if the request includes illegal arguments or is missing required arguments.
     * This method should be called by the {@link OAIXMLProvider}. There is no need to call
     * it manually. 
     * 
     * @param argMap map of valid arguments and argument types of the current verb
     * @throws BadArgumentException if the request contains a bad argument
     */
    public void checkBadArgument(Map<Argument, ArgumentType> argMap, OAIAdapter oaiAdapter)
        throws BadArgumentException, NoRecordsMatchException {
        if (this.badArgumentException != null) {
            throw this.badArgumentException;
        }
        Collection<Argument> requestArgCollection = getArguments().keySet();
        // check illegal arguments
        for (Argument requestArgument : requestArgCollection) {
            if (!argMap.containsKey(requestArgument))
                throw new BadArgumentException(Type.invalid, requestArgument.toString());
        }
        // check required && exclusive arguments
        List<Argument> missingArgumentList = new ArrayList<Argument>();
        Argument exclusiveArgument = null;
        for (Map.Entry<Argument, ArgumentType> entry : argMap.entrySet()) {
            Argument arg = entry.getKey();
            ArgumentType type = entry.getValue();
            // check for required
            if (type.equals(ArgumentType.required)) {
                if (!requestArgCollection.contains(arg))
                    missingArgumentList.add(arg);
            }
            // check for exclusive
            if (type.equals(ArgumentType.exclusive) && requestArgCollection.contains(arg)) {
                exclusiveArgument = arg;
            }
        }
        // exclusive
        if (exclusiveArgument != null) {
            if (requestArgCollection.size() != 1) {
                ArrayList<Argument> tempList = new ArrayList<Argument>(requestArgCollection);
                tempList.remove(exclusiveArgument);
                throw new BadArgumentException(Type.invalid, toStringArray(tempList));
            }
            return;
        } else {
            // required
            int misArgSize = missingArgumentList.size();
            if (misArgSize > 0) {
                throw new BadArgumentException(Type.missing, toStringArray(missingArgumentList));
            }
        }
        // check from & until dates
        Identify identify = oaiAdapter.getIdentify();
        Instant from = null;
        Instant until = null;
        Granularity granularity = identify.getGranularity();
        if (isFromDateSet()) {
            from = checkDate(this.from, granularity, Argument.from);
        }
        if (isUntilDateSet()) {
            until = checkDate(this.until, granularity, Argument.until);
            Instant earliestDatestamp = identify.getEarliestDatestamp();
            if (Granularity.YYYY_MM_DD.equals(granularity)) {
                earliestDatestamp = DateUtils.startOfDay(earliestDatestamp);
            }
            if (until.compareTo(earliestDatestamp) < 0) {
                throw new NoRecordsMatchException().setMessage(
                    "The until date must be greater or equal the earliest date stamp!");
            }
        }
        if (from != null && until != null) {
            if (from.compareTo(until) > 0) {
                throw new NoRecordsMatchException().setMessage("The from date must be less or equal the until date!");
            }
            Granularity fromGranularity = DateUtils.guessGranularity(this.from);
            Granularity untilGranularity = DateUtils.guessGranularity(this.until);
            if (!fromGranularity.equals(untilGranularity)) {
                throw new BadArgumentException("The from and until date granularity have to be equal!");
            }
        }
        // check identifier
        if (isIdentifier() && !OAIUtils.checkIdentifier(this.identifier, identify)) {
            OAIIdentifierDescription idDesc = OAIUtils.getIdentifierDescription(identify);
            if (idDesc != null) {
                throw new BadArgumentException("Identifier must start with " + idDesc.getPrefix());
            } else {
                throw new BadArgumentException("Identifier must start with oai:");
            }
        }
    }

    private Instant checkDate(String dateAsString, Granularity reposGranularity, Argument argument)
        throws BadArgumentException {
        Granularity dateGranularity = DateUtils.guessGranularity(dateAsString);
        if (Granularity.YYYY_MM_DD_THH_MM_SS_Z.equals(dateGranularity)
            && Granularity.YYYY_MM_DD.equals(reposGranularity)) {
            // if date is YYYY_MM_DD_THH_MM_SS_Z but repository supports only YYYY_MM_DD
            throw new BadArgumentException("Bad date syntax '" + argument.name() + "':" + dateAsString);
        }
        return DateUtils.parse(dateAsString);
    }

    private String[] toStringArray(List<Argument> argList) {
        String[] stringArray = new String[argList.size()];
        for (int i = 0; i < argList.size(); i++) {
            stringArray[i] = argList.get(i).name();
        }
        return stringArray;
    }

    private Map<Argument, String> getArguments() {
        Map<Argument, String> argumentList = new HashMap<Argument, String>();
        if (isIdentifier())
            argumentList.put(Argument.identifier, this.identifier);
        if (isMetadataPrefix())
            argumentList.put(Argument.metadataPrefix, this.metadataPrefix);
        if (isFromDateSet())
            argumentList.put(Argument.from, this.from);
        if (isUntilDateSet())
            argumentList.put(Argument.until, this.until);
        if (isSet())
            argumentList.put(Argument.set, this.set);
        if (isResumptionToken())
            argumentList.put(Argument.resumptionToken, this.resumptionToken);
        return argumentList;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("verb: " + this.verb).append("; ");
        for (Map.Entry<Argument, String> entry : getArguments().entrySet()) {
            b.append(entry.getKey().name()).append(": ").append(entry.getValue()).append("; ");
        }
        return b.toString();
    }

    /**
     * Checks if the identifier is set.
     * 
     * @return true if set
     */
    public boolean isIdentifier() {
        return this.identifier != null && this.identifier.length() > 0;
    }

    /**
     * Checks if the metadata prefix is set.
     * 
     * @return true if set
     */
    public boolean isMetadataPrefix() {
        return this.metadataPrefix != null && this.metadataPrefix.length() > 0;
    }

    /**
     * Checks if the from date is set.
     * 
     * @return true if set
     */
    public boolean isFromDateSet() {
        return this.from != null && this.from.length() > 0;
    }

    /**
     * Checks if the from date is set.
     * 
     * @return true if set
     */
    public boolean isUntilDateSet() {
        return this.until != null && this.until.length() > 0;
    }

    /**
     * Checks if a set is set.
     * 
     * @return true if set
     */
    public boolean isSet() {
        return set != null && set.length() > 0;
    }

    /**
     * Checks if the resumption token is set.
     * 
     * @return true if set
     */
    public boolean isResumptionToken() {
        return resumptionToken != null && this.resumptionToken.length() > 0;
    }

}
