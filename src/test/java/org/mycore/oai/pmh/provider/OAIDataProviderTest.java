package org.mycore.oai.pmh.provider;

import org.junit.Test;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.Verb;
import org.mycore.oai.pmh.dataprovider.OAIRequest;
import org.mycore.oai.pmh.dataprovider.OAIResponse;
import org.mycore.oai.pmh.dataprovider.OAIResponse.Format;
import org.mycore.oai.pmh.dataprovider.OAIXMLProvider;
import org.mycore.oai.pmh.dataprovider.jaxb.JAXBOAIProvider;

public class OAIDataProviderTest {

    @Test
    public void getRecord() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIXMLProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.GetRecord.name());
        req.setArgument(Argument.identifier, oaiAdapter.modsRecord.getHeader().getId());
        req.setArgument(Argument.metadataPrefix, oaiAdapter.modsFormat.getPrefix());
        OAIResponse response = oaiProvider.handleRequest(req);
//        print(response);
    }

    @Test
    public void identify() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIXMLProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.Identify.name());
        OAIResponse response = oaiProvider.handleRequest(req);
//      print(response);
    }

    @Test
    public void listIdentifiers() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIXMLProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListIdentifiers.name());
        req.setArgument(Argument.metadataPrefix, oaiAdapter.modsFormat.getPrefix());
        req.setArgument(Argument.set, oaiAdapter.set1.getSpec());
        OAIResponse response = oaiProvider.handleRequest(req);
//        print(response);
    }

    @Test
    public void listMetadataFormats() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIXMLProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListMetadataFormats.name());
        OAIResponse response = oaiProvider.handleRequest(req);
//        print(response);
    }

    @Test
    public void listRecords() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIXMLProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListRecords.name());
        req.setArgument(Argument.metadataPrefix, oaiAdapter.modsFormat.getPrefix());
        req.setArgument(Argument.until, "2010-11-11");
        OAIResponse response = oaiProvider.handleRequest(req);
//        print(response);
    }

    @Test
    public void listSets() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIXMLProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListSets.name());
        OAIResponse response = oaiProvider.handleRequest(req);
//        print(response);
    }

    private void print(OAIResponse response) throws Exception {
        System.out.print(response.toString(Format.formatted));
    }

}
