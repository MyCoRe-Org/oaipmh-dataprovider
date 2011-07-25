package org.mycore.oai.pmh.provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mycore.oai.pmh.Description;
import org.mycore.oai.pmh.Granularity;
import org.mycore.oai.pmh.Identify;

public class SimpleIdentify implements Identify {

	private String repositoryName;

	private String baseURL;

	private String protocolVersion;

	private Date earliestDatestamp;

	private DeletedRecordPolicy deletedRecordPolicy;

	private Granularity granularity;

	private List<String> adminEmailList;
	
	private List<Description> descriptionList;

	public SimpleIdentify(String repositoryName, String baseURL, Date earliestDatestamp, DeletedRecordPolicy delRecPol, Granularity granularity, String adminMail) {
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

	public List<String> getAdminEmailList() {
		return adminEmailList;
	}
	public String getBaseURL() {
		return baseURL;
	}
	public DeletedRecordPolicy getDeletedRecordPolicy() {
		return deletedRecordPolicy;
	}
	public Date getEarliestDatestamp() {
		return earliestDatestamp;
	}
	public Granularity getGranularity() {
		return granularity;
	}
	public String getProtocolVersion() {
		return protocolVersion;
	}
	public String getRepositoryName() {
		return repositoryName;
	}
	public List<Description> getDescriptionList() {
		return descriptionList;
	}
}
