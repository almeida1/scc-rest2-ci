package com.fatec.scelv1.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class ApplicationUser {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	String username;
	String password;
	
	public ApplicationUser() {
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	@Override
	public String toString() {
		return "ApplicationUser [id=" + id + ", username=" + username + ", password=" + password + "]";
	}
	
	
}
