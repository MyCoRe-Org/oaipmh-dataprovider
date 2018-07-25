package org.mycore.oai.pmh.dataprovider;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;
import org.mycore.oai.pmh.Argument;
import org.mycore.oai.pmh.BadArgumentException;
import org.mycore.oai.pmh.Verb;
import org.mycore.oai.pmh.dataprovider.OAIResponse.Format;
import org.mycore.oai.pmh.dataprovider.jaxb.JAXBOAIProvider;

import static org.junit.Assert.assertTrue;

public class OAIDataProviderTest {

    @Test
    public void getRecord() throws BadArgumentException {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.GetRecord.name());
        req.setArgument(Argument.identifier, oaiAdapter.modsRecord.getHeader().getId());
        req.setArgument(Argument.metadataPrefix, oaiAdapter.modsFormat.getPrefix());
        OAIResponse response = oaiProvider.handleRequest(req);

        assertTrue(response.toString().contains("<identifier>oai:sample.de:modsrecord</identifier>"));
        assertTrue(response.toString().contains("<mods:note attr=\"sample\">sample note</mods:note>"));
        print(response);
    }

    @Test
    public void identify() {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.Identify.name());
        OAIResponse response = oaiProvider.handleRequest(req);

        assertTrue(response.toString().contains("<repositoryIdentifier>sample.de</repositoryIdentifier>"));
        assertTrue(response.toString().contains("<baseURL>www.spiegel.de</baseURL>"));
        print(response);
    }

    @Test
    public void listIdentifiers() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListIdentifiers.name());
        req.setArgument(Argument.metadataPrefix, oaiAdapter.modsFormat.getPrefix());
        req.setArgument(Argument.set, oaiAdapter.set1.getSpec());
        OAIResponse response = oaiProvider.handleRequest(req);

        assertTrue(response.toString().contains("<identifier>oai:sample.de:modsrecord</identifier>"));
        assertTrue(response.toString().contains("<setSpec>sampleset1</setSpec>"));
        print(response);
    }

    @Test
    public void listMetadataFormats() {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListMetadataFormats.name());
        OAIResponse response = oaiProvider.handleRequest(req);

        assertTrue(response.toString().contains("<metadataPrefix>oai_dc</metadataPrefix>"));
        assertTrue(response.toString().contains("<metadataPrefix>mods</metadataPrefix>"));
        print(response);
    }

    @Test
    public void listRecords() throws Exception {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListRecords.name());
        req.setArgument(Argument.metadataPrefix, oaiAdapter.modsFormat.getPrefix());
        req.setArgument(Argument.until, "2010-11-11");
        OAIResponse response = oaiProvider.handleRequest(req);

        assertTrue(response.toString().contains("<identifier>oai:sample.de:modsrecord</identifier>"));
        assertTrue(response.toString().contains("<mods:note attr=\"sample\">sample note</mods:note>"));
        assertTrue(response.toString().contains("<header status=\"deleted\">"));
        assertTrue(response.toString().contains("<identifier>oai:sample.de:deletedrecord</identifier>"));
        print(response);

        // test from > until
        req.setArgument(Argument.from, "2012-01-01");
        response = oaiProvider.handleRequest(req);
        assertTrue(response.toString().contains("<error code=\"badArgument\">"));

        print(response);
    }

    @Test
    public void listSets() {
        SimpleOAIAdapter oaiAdapter = new SimpleOAIAdapter();
        OAIProvider oaiProvider = new JAXBOAIProvider(oaiAdapter);
        OAIRequest req = new OAIRequest(Verb.ListSets.name());
        OAIResponse response = oaiProvider.handleRequest(req);

        assertTrue(response.toString().contains("<setSpec>sampleset1</setSpec>"));
        assertTrue(response.toString().contains("<setSpec>sampleset2</setSpec>"));
        print(response);
    }

    private void print(OAIResponse response) {
        LogManager.getLogger().info(response.toString(Format.formatted));
    }

}
