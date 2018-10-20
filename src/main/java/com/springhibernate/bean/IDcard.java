package com.springhibernate.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class IDcard implements Serializable{
	private int idcardId;
	private String idcardCode;
	private User user;
	
	public int getIdcardId() {
		return idcardId;
	}
	public void setIdcardId(int idcardId) {
		this.idcardId = idcardId;
	}
	public String getIdcardCode() {
		return idcardCode;
	}
	public void setIdcardCode(String idcardCode) {
		this.idcardCode = idcardCode;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
