package com.springhibernate.bean;

import java.io.Serializable;
import java.util.Set;

@SuppressWarnings("serial")
public class Role implements Serializable{
	private Integer roleId;
	private String roleName;
	private Set<User> users;
	
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Set<User> getUsers() {
		return users;
	}
	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
}
