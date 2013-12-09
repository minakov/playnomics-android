package com.playnomics.android.messaging;

import java.util.Map;

public class Target {
	public enum TargetType {
		URL, EXTERNAL, DATA
	}

	private TargetType targetType;
	private String targetUrl;
	private Map<String, Object> targetData;
	private String targetDataJson;

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public Map<String, Object> getTargetData() {
		return targetData;
	}
	

	public void setTargetData(Map<String, Object> targetData) {
		this.targetData = targetData;
	}
	
	public void setTargetDataJson(String targetDataJson){
		this.targetDataJson = targetDataJson;
	}
	
	public String getTargetDataJson(){
		return this.targetDataJson;
	}

	public Target(TargetType targetType) {
		this.targetType = targetType;
	}

	public Target(TargetType targetType, Map<String, Object> targetData, String targetDataJson) {
		this.targetType = targetType;
		this.targetData = targetData;
		this.targetDataJson = targetDataJson;
	}

	public Target(TargetType targetType, String targetUrl) {
		this.targetType = targetType;
		this.targetUrl = targetUrl;
	}
}
