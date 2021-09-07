package com.brane.spring.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

import com.brane.spring.user.CrmUser;

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
	
	//we need to create a collection of roles that will be displayed on the registration form.
	//we will use @PostConstruct annotation to initialize the collection of roles.
	private Map<String, String> roles;
	
	
	//This method will be executed after the spring bean is initialized.
	@PostConstruct
	protected void loadRoles() {
		
		roles = new LinkedHashMap<String, String>();
		
		// key=the role, value=display to user 
		roles.put("ROLE_EMPLOYEE", "Employee");
		roles.put("ROLE_MANAGER", "Manager");
		roles.put("ROLE_ADMIN", "Admin");		
	}
	
	
	
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
	
		
		
	//method for showing the form, with parameter Model
	@GetMapping("/showRegistrationForm")
	public String showMyLoginPage(Model theModel) {
		
		//add USER object to the model
		theModel.addAttribute("crmUser", new CrmUser());
		
		//add roles to the model for form display
		theModel.addAttribute("roles", roles);
		
		return "registration-form";
		
	}
	
	
	//controller method for processing the form for registration-form.jsp
	//We are going to validate the CrmUser object here with annotation @Valid 
	//and we are using BindingResult object to store results of validation into this object.
	@PostMapping("/processRegistrationForm")
	public String processRegistrationForm(
				@Valid @ModelAttribute("crmUser") CrmUser theCrmUser, 
				BindingResult theBindingResult, 
				Model theModel) {
				
		//GET USERNAME WHICH WE ENTERED
		String userName = theCrmUser.getUserName();
		
		//Just for debugging, we are going to print out this information.
		logger.info("Processing registration form for: " + userName);
		
		// form validation
		//We are using now BindingResult object to see if we had errors, if we had 
		//return us to the registration-form again.
		//For validation rules we have only 1 rule and that is:username or password can't have null value.
		//If we had error (null value), we want to create a new CrmUser object 
		//and to add that object to the model attribute.
		//We also want to create one more model attribute registrationError for showing the error
		//message "User name/password can not be empty." if username or password have null value.
		if (theBindingResult.hasErrors()) {

			//we want to have a new user object,if we delete this line we will have populated old object(username)
			theModel.addAttribute("crmUser", new CrmUser());
			
			//add roles to the model for form display,if we delete this line of code we will get
			//empty field for roles, because we need to load data-roles in the model attribute roles.
			theModel.addAttribute("roles", roles);
			
			//we will call this model attribute registrationError in our registration-form.jsp to show an error message.
			theModel.addAttribute("registrationError", "User name/password can not be empty.");

			logger.warning("User name/password can not be empty.");
			
			return "registration-form";	
		}
		
		
		
		// check the database if user already exists user with the same username
		boolean userExists = doesUserExist(userName);
		
		//If user with this username exits in the database, return us to the registration-form again.
		//And again create a new CrmUser object and add that object to the model attribute.
		//We also want to create one more model attribute registrationError for showing the error
		//message "User name already exists." if username alredy exists in the database.
		if (userExists) {
			
			//we want to have a new user object,if we delete this line we will have populated old object(username)
			theModel.addAttribute("crmUser", new CrmUser());
			
			//add roles to the model for form display,if we delete this line of code we will get
			//empty field for roles, because we need to load data-roles in the model attribute roles.
			theModel.addAttribute("roles", roles);
			
			//we will call this model attribute registrationError in our registration-form.jsp to show an error message.
			theModel.addAttribute("registrationError", "User name already exists.");

			logger.warning("User name already exists.");
			
			return "registration-form";			
		}
		

		//we passed all of the validation checks for username and password to can't have null value
		//and username can't already exist in the database.

		
		// encrypt the password using BCRYPT algorithm
        String encodedPassword = passwordEncoder.encode(theCrmUser.getPassword());

        //prepend the encoding algorithm id, because we are using on that way in our database
        encodedPassword = "{bcrypt}" + encodedPassword;
        
        //Method 1
		//We want to give user default role of "EMPLOYEE"
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_EMPLOYEE");
        
        //Method 2
        //List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList();
        //authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        
        //if the user selected role other than employee, 
        //then add that one too (multiple roles)
        String formRole = theCrmUser.getFormRole();

        if (!formRole.equals("ROLE_EMPLOYEE")) {
        		authorities.add(new SimpleGrantedAuthority(formRole));
        }

        //create user object (from Spring Security framework)
        User tempUser = new User(userName, encodedPassword, authorities);

        //save user in the database
        //we are using here object userDetailsManager to save a new user to the database.
        userDetailsManager.createUser(tempUser);		
		
        logger.info("Successfully created user: " + userName);
        
        //now when user is successfully created, return us to the registration-confirmation page
        return "registration-confirmation";		
	}
	
	
	
	//we are using this method to check if the user with this username alredy exsits in the database
	private boolean doesUserExist(String userName) {
		
		logger.info("Checking if user exists: " + userName);
		
		//check the database if the user with this username already exists
		//we are using here object userDetailsManager to check this.
		boolean exists = userDetailsManager.userExists(userName);
		
		logger.info("User: " + userName + ", exists: " + exists);
		
		return exists;
	}

}
