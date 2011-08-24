package org.mycore.oai.pmh.provider;

import java.util.ArrayList;
import java.util.Date;

import org.mycore.oai.pmh.Description;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.SimpleIdentify;

public class SimpleTestIdentify extends SimpleIdentify {

    public SimpleTestIdentify(String repositoryName, String baseURL, Date earliestDatestamp, DeletedRecordPolicy delRecPol, Granularity granularity, String adminMail) {
		this.repositoryName = repositoryName;
		this.baseURL = baseURL;
		this.earliestDatestamp = earliestDatestamp;
		this.deletedRecordPolicy = delRecPol;
		this.granularity = granularity;
		this.protocolVersion = "2.0";
		this.adminEmailList = new ArrayList<String>();
		this.adminEmailList.add(adminMail);
		this.descriptionList = new ArrayList<Description>();
	}

}
