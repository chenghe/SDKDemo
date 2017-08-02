package com.cgnb;

public class HttpObject {

	private byte[] workKey;
	private String sessionId;
	private HttpRequestAes request;
	public byte[] getWorkKey() {
		return workKey;
	}
	public void setWorkKey(byte[] workKey) {
		this.workKey = workKey;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public HttpRequestAes getRequest() {
		return request;
	}
	public void setRequest(HttpRequestAes request) {
		this.request = request;
	}
	
	
}
