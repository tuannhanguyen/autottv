package com.ttv.at.test.action;

import org.json.JSONObject;

public class response {

	private int status;
	private JSONObject data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	
	
}
