package com.brane.spring.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//ADDING VALIDATION RULES
public class CrmUser {

	
	//Can't have null value and must have min 1 char
	@NotNull(message="is required")
	@Size(min=1, message="is required")	
	private String userName;
	
	
	//Can't have null value and must have min 1 char
	@NotNull(message="is required")
	@Size(min=1, message="is required")
	private String password;
	
	
	
	//default constructor
	public CrmUser() {
		
	}

	
	//getters and setters
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
