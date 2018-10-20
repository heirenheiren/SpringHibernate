package com.springhibernate.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
	private Integer userId;
	private String username;
	private String password;
	private Integer age;
	private Role role;
	private IDcard idcard;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public IDcard getIdcard() {
		return idcard;
	}
	public void setIdcard(IDcard idcard) {
		this.idcard = idcard;
	}
	
}
