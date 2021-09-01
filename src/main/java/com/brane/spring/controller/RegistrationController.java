package com.brane.spring.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/register")
public class RegistrationController {
	
	//we need to inject object userDetailsManager, which we made in config class DemoSecurityConfig.
	//We are going to use this userDetailsManager for our registration form,
	//to save new user to the database with username, encrypted password(bcrypt) and authority EMPLOYEE.
	//We will also use userDetailsManager to check if user with that username already exists in database.
	//We can also call this methods:updateUser, deleteUser, changePassword.
	@Autowired
	private UserDetailsManager userDetailsManager;
	
	//we are going to use BCRYPT algorithm for registration form, to storage passwords into database.
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	//we are using logger just for debugging
	private Logger logger = Logger.getLogger(getClass().getName());
	
	
	
		//We have a problem here, for example, if we try to enter for the username Brane and
		//for the password all white spaces, this will work and we will process the form, which is wrong.
		//1.@InitBinder pre-process all web requests coming into our Controller.
		//2.This method removes all whitespaces, from the left and from the right side.
		//3.If string only have white space, trim it to null.
		@InitBinder
		public void initBinder(WebDataBinder dataBinder) {
			
			//This object removes whitespace from the left and from the right side.
			//true value means trim string to null if is all whitespace.
			StringTrimmerEditor StringTrimmerEditor=new StringTrimmerEditor(true);
			
			//And we need to register this as a custom editor.
			//For every string class, apply StringTrimmerEditor.
			dataBinder.registerCustomEditor(String.class, StringTrimmerEditor);
		}	
	


}
