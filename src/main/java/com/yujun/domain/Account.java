package com.yujun.domain;


public class Account {
	private String id;
	private String password;
	private String gddm; //股东代码


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getGddm() {
		return gddm;
	}


	public void setGddm(String gddm) {
		this.gddm = gddm;
	}


	public String toString() {
		return "id : " + id + ",password : " + password + ",gddm : " + gddm;
	}
}
