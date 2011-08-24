package org.mycore.oai.pmh.dataprovider;

import java.util.Date;
import java.util.List;

import org.mycore.oai.pmh.BadResumptionTokenException;
import org.mycore.oai.pmh.CannotDisseminateFormatException;
import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.IdDoesNotExistException;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.NoMetadataFormatsException;
import org.mycore.oai.pmh.NoRecordsMatchException;
import org.mycore.oai.pmh.NoSetHierarchyException;
import org.mycore.oai.pmh.OAIDataList;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.Set;

/**
 * General interface to implement a new OAI-PMH data provider.
 * 
 * @author Matthias Eichner
 */
public interface OAIAdapter {

    /**
     * Returns the identify of the repository.
     * 
     * @return {@link Identify}
     */
    public Identify getIdentify();

    /**
     * Returns a list of {@link Set}s.
     * 
     * @return a list of Sets
     * @throws NoSetHierarchyException
     *             The repository does not support sets.
     */
    public OAIDataList<Set> getSets() throws NoSetHierarchyException;

    /**
     * Returns a list of {@link Set}s.
     * 
     * @param resumptionToken
     *            flow control token returned by a previous ListRecords request that issued an incomplete list
     * @return a list of Sets
     * @throws NoSetHierarchyException
     *             The repository does not support sets.
     * @throws BadResumptionTokenException
     *             The value of the resumptionToken argument is invalid or expired.
     */
    public OAIDataList<Set> getSets(String resumptionToken) throws NoSetHierarchyException, BadResumptionTokenException;

    /**
     * Returns the {@link Set} by the given setSpec. If no set is found a {@link NoRecordsMatchException} is thrown.
     * 
     * @param setSpec
     *            short cut of the set
     * @return Set if found
     * @throws NoSetHierarchyException
     *             The repository does not support sets.
     * @throws NoRecordsMatchException
     *             No set matches the setSpec parameter.
     */
    public Set getSet(String setSpec) throws NoSetHierarchyException, NoRecordsMatchException;

    /**
     * Returns a list of metadata formats available in the repository. Be aware that the oai_dc format is <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#MetadataNamespaces">required</a>.
     * 
     * @return metadata formats available from the repository
     */
    public List<MetadataFormat> getMetadataFormats();

    /**
     * Returns the metadata format by prefix.
     * 
     * @param prefix
     *            prefix of the metadata format
     * @return metadata format which is linked with the prefix
     * @throws CannotDisseminateFormatException
     *             The metadata prefix argument is not supported by the repository.
     */
    public MetadataFormat getMetadataFormat(String prefix) throws CannotDisseminateFormatException;

    /**
     * Returns a list of metadata formats available in the repository.
     * 
     * @param identifier
     *            Specifies the unique identifier of the item for which available metadata formats are being requested. The returning list includes all metadata
     *            formats supported by this repository. Note that the fact that a metadata format is supported by a repository does not mean that it can be
     *            disseminated from all items in the repository.
     * @return metadata formats available from the repository
     * @throws IdDoesNotExistException
     *             The value of the identifier argument is unknown or illegal in this repository.
     * @throws NoMetadataFormatsException
     *             There are no metadata formats available for the specified item.
     */
    public List<MetadataFormat> getMetadataFormats(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException;

    /**
     * <p>
     * Retrieve an individual metadata header from the repository. Use {@link #getRecord(String, MetadataFormat)} to get a full record with metadata.
     * </p>
     * <p>
     * You can use this method to create a new record by calling "new Record(header) or new Record(header, metadata)"
     * </p>
     * 
     * @param identifier
     *            specifies the unique identifier of the item in the repository from which the record must be disseminated
     * @return the header of a record from the repository
     * @throws IdDoesNotExistException
     *             The value of the identifier argument is unknown or illegal in this repository.
     */
    public Header getHeader(String identifier) throws IdDoesNotExistException;

    /**
     * Retrieve an individual metadata record from the repository.
     * 
     * @param identifier
     *            specifies the unique identifier of the item in the repository from which the record must be disseminated
     * @param format
     *            format that should be included in the metadata part of the returned record
     * @return record from the repository
     * @throws CannotDisseminateFormatException
     *             The metadata format is not supported by the item identified by the value of the identifier argument.
     * @throws IdDoesNotExistException
     *             The value of the identifier argument is unknown or illegal in this repository.
     */
    public Record getRecord(String identifier, MetadataFormat format) throws CannotDisseminateFormatException, IdDoesNotExistException;

    /**
     * Returns a list of records by the given resumption token.
     * 
     * @param resumptionToken
     *            flow control token returned by a previous ListRecords request that issued an incomplete list
     * @return list of records with or without a resumption token
     * @throws BadResumptionTokenException
     *             The value of the resumptionToken argument is invalid or expired.
     */
    public OAIDataList<Record> getRecords(String resumptionToken) throws BadResumptionTokenException;

    /**
     * Returns a list of records matching the set and/or datestamp.
     * 
     * @param format
     *            (required) {@link MetadataFormat} that should be included in the metadata part of the returned record
     * @param set
     *            (optional) {@link Set} argument, which specifies set criteria for selective harvesting.
     * @param from
     *            (optional) date argument, which specifies a lower bound for datestamp-based selective harvesting.
     * @param until
     *            (optional) date argument, which specifies a upper bound for datestamp-based selective harvesting.
     * @return list of records with or without a resumption token
     * @throws CannotDisseminateFormatException
     *             The metadata format is not supported by the repository.
     * @throws NoSetHierarchyException
     *             The repository does not support sets.
     * @throws NoRecordsMatchException
     *             The combination of the values of the from, until, set and metadataPrefix arguments results in an empty list.
     */
    public OAIDataList<Record> getRecords(MetadataFormat format, Set set, Date from, Date until) throws CannotDisseminateFormatException,
            NoSetHierarchyException, NoRecordsMatchException;

    /**
     * Returns a list of headers.
     * 
     * @param resumptionToken
     *            flow control token returned by a previous ListIdentifiers request that issued an incomplete list
     * @return a list of headers
     * @throws BadResumptionTokenException
     *             The value of the resumptionToken argument is invalid or expired.
     */
    public OAIDataList<Header> getHeaders(String resumptionToken) throws BadResumptionTokenException;

    /**
     * Returns a list of headers.
     * 
     * @param format
     *            (required) {@link MetadataFormat} that should be included in the metadata part of the returned record
     * @param set
     *            (optional) {@link Set} argument, which specifies set criteria for selective harvesting.
     * @param from
     *            (optional) date argument, which specifies a lower bound for datestamp-based selective harvesting.
     * @param until
     *            (optional) date argument, which specifies a upper bound for datestamp-based selective harvesting.
     * @return list of headers with or without a resumption token
     * @throws CannotDisseminateFormatException
     *             The metadata format is not supported by the repository
     * @throws NoSetHierarchyException
     *             The repository does not support sets.
     * @throws NoRecordsMatchException
     *             The combination of the values of the from, until, set and metadataPrefix arguments results in an empty list.
     */
    public OAIDataList<Header> getHeaders(MetadataFormat format, Set set, Date from, Date until) throws CannotDisseminateFormatException,
            NoSetHierarchyException, NoRecordsMatchException;
}
