package com.arjun.impala;


/**
 * 
 * QueryMetadata contains profile metadata like query id,start-end time,status,query plan etc..
 * @author arajan
 *
 */
public class ProfileMetadata {

	private String QueryId;
	private String querystartTime;
	private String queryEndTime;
	private String queryType;
	private String queryStatus;
	private String user;
	private String defaultDb;
	private String queryString;
	private String coordinator;
	private String queryPlan;
	private String execSummary;

	public String getQueryId() {
		return QueryId;
	}

	public void setQueryId(String queryId) {
		QueryId = queryId;
	}

	public String getQuerystartTime() {
		return querystartTime;
	}

	public void setQuerystartTime(String querystartTime) {
		this.querystartTime = querystartTime;
	}

	public String getQueryEndTime() {
		return queryEndTime;
	}

	public void setQueryEndTime(String queryEndTime) {
		this.queryEndTime = queryEndTime;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getQueryStatus() {
		return queryStatus;
	}

	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDefaultDb() {
		return defaultDb;
	}

	public void setDefaultDb(String defaultDb) {
		this.defaultDb = defaultDb;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(String coordinator) {
		this.coordinator = coordinator;
	}

	public String getQueryPlan() {
		return queryPlan;
	}

	public void setQueryPlan(String queryPlan) {
		this.queryPlan = queryPlan;
	}

	public String getExecSummary() {
		return execSummary;
	}

	public void setExecSummary(String execSummary) {
		this.execSummary = execSummary;
	}

	@Override
	public String toString() {
		return "QueryId: " + QueryId + "\nquerystartTime: " + querystartTime + "\nqueryEndTime: " + queryEndTime
				+ "\nqueryType: " + queryType + "\nqueryStatus: " + queryStatus + "\nuser: " + user + "\ndefaultDb: "
				+ defaultDb + "\nqueryString: " + queryString + "\ncoordinator: " + coordinator + "\nqueryPlan: "
				+ queryPlan + "\nexecSummary: " + execSummary;
	}

}
