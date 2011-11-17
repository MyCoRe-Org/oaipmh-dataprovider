package org.mycore.oai.pmh.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.mycore.oai.pmh.BadResumptionTokenException;
import org.mycore.oai.pmh.CannotDisseminateFormatException;
import org.mycore.oai.pmh.DefaultResumptionToken;
import org.mycore.oai.pmh.FriendsDescription;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.Header;
import org.mycore.oai.pmh.IdDoesNotExistException;
import org.mycore.oai.pmh.Identify;
import org.mycore.oai.pmh.Metadata;
import org.mycore.oai.pmh.MetadataFormat;
import org.mycore.oai.pmh.NoMetadataFormatsException;
import org.mycore.oai.pmh.NoRecordsMatchException;
import org.mycore.oai.pmh.NoSetHierarchyException;
import org.mycore.oai.pmh.OAIDataList;
import org.mycore.oai.pmh.DateUtils;
import org.mycore.oai.pmh.OAIIdentifierDescription;
import org.mycore.oai.pmh.Record;
import org.mycore.oai.pmh.ResumptionToken;
import org.mycore.oai.pmh.Set;
import org.mycore.oai.pmh.Header.Status;
import org.mycore.oai.pmh.Identify.DeletedRecordPolicy;
import org.mycore.oai.pmh.SimpleResumptionToken;
import org.mycore.oai.pmh.dataprovider.OAIAdapter;
import org.mycore.oai.pmh.dc.DCMetadataFormat;

public class SimpleOAIAdapter implements OAIAdapter {

    private static final String SCHEMA_LOC_MODS = "http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd";

    private static final Namespace NS_XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    private static final Namespace NS_MODS = Namespace.getNamespace("mods", "http://www.loc.gov/mods/v3");
    
    private static final Namespace NS_DC = Namespace.getNamespace("dc", "http://purl.org/dc/elements/1.1/");
    
    private static final Namespace NS_DDB = Namespace.getNamespace("ddb", "http://www.d-nb.de/standards/ddb/");

    public Identify id;

    public Set set1, set2;

    public OAIDataList<Set> setList = new OAIDataList<Set>();

    public MetadataFormat dcFormat, modsFormat;

    public List<MetadataFormat> metadataFormatList = new ArrayList<MetadataFormat>();

    public Record modsRecord, deletedRecord;

    public OAIDataList<Record> recordList = new OAIDataList<Record>();

    public SimpleOAIAdapter() {
        // set id
        String reposName = "Test OAI Provider";
        String baseURL = "www.mycore.de";
        Date earliestDate = DateUtils.parseUTC("2010-10-10");
        String adminMail = "sampleuser@bugmenot.de";
        this.id = new SimpleTestIdentify(reposName, baseURL, earliestDate, DeletedRecordPolicy.No, Granularity.YYYY_MM_DD_THH_MM_SS_Z, adminMail);
        this.id.getDescriptionList().add(new OAIIdentifierDescription("sample.de", "sampleobject"));
        this.id.getDescriptionList().add(new FriendsDescription("www.spiegel.de", "www.golem.de"));
        // sets
        this.set1 = new Set("sampleset1", "Only for test purpose");
        this.set2 = new Set("sampleset2", "Only for test purpose");
        this.setList.add(this.set1);
        this.setList.add(this.set2);
        // metadata format
        this.dcFormat = new DCMetadataFormat();
        this.modsFormat = new MetadataFormat(NS_MODS, "http://www.loc.gov/standards/mods/v3/mods-3-3.xsd");
        this.metadataFormatList.add(this.dcFormat);
        this.metadataFormatList.add(this.modsFormat);
        // mods record
        this.modsRecord = new Record("oai:sample.de:modsrecord", DateUtils.parseUTC("2010-10-12"), new Metadata() {
            @Override
            public Element toXML() {
                Element mods = new Element("mods", NS_MODS);
                mods.addNamespaceDeclaration(NS_DDB);
                mods.addNamespaceDeclaration(NS_DC);
                mods.setAttribute("schemaLocation", SCHEMA_LOC_MODS, NS_XSI);
                mods.addContent(new Element("note", NS_MODS).setText("sample note").setAttribute("attr", "sample"));
                mods.addContent(new Element("title", NS_DC).setText("title").setAttribute("type", "ddb:titleISO639-2", NS_XSI));
                return mods;
            }
        });
        this.modsRecord.getHeader().getSetList().add(set1);
        this.deletedRecord = new Record("oai:sample.de:deletedrecord", DateUtils.parseUTC("2009-11-11"), Status.deleted);
        this.recordList.add(this.modsRecord);
        this.recordList.add(this.deletedRecord);
    }

    @Override
    public Identify getIdentify() {
        return id;
    }

    @Override
    public OAIDataList<Set> getSets() throws NoSetHierarchyException {
        return setList;
    }

    @Override
    public Set getSet(String setSpec) throws NoRecordsMatchException{
        for(Set s : this.setList) {
            if(s.getSpec().equals(setSpec)) {
                return s;
            }
        }
        throw new NoRecordsMatchException();
    }

    @Override
    public OAIDataList<Set> getSets(String resumptionToken) throws NoSetHierarchyException, BadResumptionTokenException {
        throw new BadResumptionTokenException(resumptionToken);
    }

    @Override
    public List<MetadataFormat> getMetadataFormats() {
        return this.metadataFormatList;
    }

    @Override
    public MetadataFormat getMetadataFormat(String prefix) throws CannotDisseminateFormatException {
        for (MetadataFormat f : this.metadataFormatList) {
            if (f.getPrefix().equals(prefix)) {
                return f;
            }
        }
        throw new CannotDisseminateFormatException();
    }

    @Override
    public List<MetadataFormat> getMetadataFormats(String identifier) throws IdDoesNotExistException, NoMetadataFormatsException {
        OAIDataList<MetadataFormat> fList = new OAIDataList<MetadataFormat>();
        for (MetadataFormat f : this.metadataFormatList) {
            try {
                this.getRecord(identifier, f);
                fList.add(f);
            } catch (CannotDisseminateFormatException cdf) {
                continue;
            }
        }
        return fList;
    }

    @Override
    public Record getRecord(String identifier, MetadataFormat format) throws CannotDisseminateFormatException, IdDoesNotExistException {
        Record r = getRecord(identifier);
        if (r.equals(this.modsRecord) && !format.equals(modsFormat)) {
            CannotDisseminateFormatException cdf = new CannotDisseminateFormatException();
            cdf.setMetadataPrefix(format.getPrefix());
            cdf.setId(identifier);
            throw cdf;
        }
        return r;
    }

    private Record getRecord(String identifier) throws IdDoesNotExistException {
        for (Record r : this.recordList) {
            if (r.getHeader().getId().equals(identifier))
                return r;
        }
        throw new IdDoesNotExistException(identifier);
    }

    @Override
    public OAIDataList<Record> getRecords(String resumptionToken) throws BadResumptionTokenException {
        throw new BadResumptionTokenException(resumptionToken);
    }

    @Override
    public OAIDataList<Record> getRecords(MetadataFormat format, Set set, Date from, Date until) throws CannotDisseminateFormatException,
            NoSetHierarchyException, NoRecordsMatchException {
        if (!(format.equals(modsFormat) || format.equals(dcFormat))) {
            throw new CannotDisseminateFormatException().setMetadataPrefix(format.getPrefix());
        }
        if (format.equals(modsFormat)) {
            OAIDataList<Record> l = new OAIDataList<Record>();
            if (set == null) {
                l.add(modsRecord);
                l.add(deletedRecord);
                return l;
            } else if (set.equals(set1)) {
                l.add(modsRecord);
                return l;
            }
        }
        throw new NoRecordsMatchException();
    }

    @Override
    public OAIDataList<Header> getHeaders(MetadataFormat format, Set set, Date from, Date until) throws CannotDisseminateFormatException,
            NoSetHierarchyException, NoRecordsMatchException {
        if (!(format.equals(modsFormat) || format.equals(dcFormat))) {
            throw new CannotDisseminateFormatException().setMetadataPrefix(format.getPrefix());
        }
        if (format.equals(modsFormat)) {
            OAIDataList<Header> l = new OAIDataList<Header>();
            l.setResumptionToken(getRsToken());
            if (set == null) {
                l.add(modsRecord.getHeader());
                l.add(deletedRecord.getHeader());
                return l;
            } else if (set.equals(set1)) {
                l.add(modsRecord.getHeader());
                return l;
            }
        }
        throw new NoRecordsMatchException();
    }

    private ResumptionToken getRsToken() {
        DefaultResumptionToken rsToken = new DefaultResumptionToken();
        rsToken.setCompleteListSize(1);
        rsToken.setCursor(0);
        rsToken.setToken("token");
        return rsToken;
    }

    @Override
    public OAIDataList<Header> getHeaders(String resumptionToken) throws BadResumptionTokenException {
        throw new BadResumptionTokenException(resumptionToken);
    }

}