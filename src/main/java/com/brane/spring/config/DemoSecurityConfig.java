package com.brane.spring.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

//THIS IS JAVA CONFIG CLASS.WE ARE USING PURE JAVA CONFIG, NO XML
@Configuration
//@EnableWebSecurity annotation is used to enable SpringSecurity in our project.
@EnableWebSecurity
//We need to extend WebSecurityConfigurerAdapter class.
//It allows configuring things that impact all of web security.
//WebSecurityConfigurerAdapter allows customization to both WebSecurity and HttpSecurity.
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter {

	
	//We are going to do dependency injection object DataSource, which we created in DemoAppConfig class.
	//Because we are using now jdbc authentication, not in memory authentication like before.
	@Autowired
	private DataSource securityDataSource;
	
	
	//JDBC authentication
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		//We are going to set out Data Source object which is injected.
		//Data Source have all information about jdbc connection and connection pooling.
		auth.jdbcAuthentication().dataSource(securityDataSource);

	}

	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		
		http.authorizeRequests()
		
			//EMPLOYEE ROLE:users in this role will only be allowed to list customers.
			//MANAGER ROLE:users in this role will only be allowed to list, add and update customers.
			//ADMIN ROLE:users in this role will only be allowed to list, add, update and delete customers.
			.antMatchers("/customer/showForm*").hasAnyRole("MANAGER", "ADMIN")
			.antMatchers("/customer/save*").hasAnyRole("MANAGER", "ADMIN")
			.antMatchers("/customer/delete").hasRole("ADMIN")
			.antMatchers("/customer/**").hasRole("EMPLOYEE")
			.antMatchers("/resources/**").permitAll()
			.and()
			
			//we are using our custom login form
			.formLogin()
			
				//show our custom login form with this request mapping:/showMyLoginPage.
				//We need to create a method i Controller class with this request mapping.
				.loginPage("/showMyLoginPage")
				
				//On this URL we will send data from our form, so Spring Security can do authentication.
				//Login form need to send POST method for processing data (username and password).
				.loginProcessingUrl("/authenticateTheUser")
				
				//Allow everyone to see login form.
				.permitAll()
				
			//And we are adding LOGOUT SUPPORT for Spring Security
			.and()
			
			//We need to confirm that everyone have access to the login page, when we are logout from the system.
			//Because by default it return us to the home page(login page).
			.logout().permitAll()
			.and()
			
			//we are calling method for exception handling and on that method we are callingmethod for
			//access denied page with this request mapping:/access-denied.
			//We need to create a method i Controller class with this request mapping.
			.exceptionHandling().accessDeniedPage("/access-denied");
		
	}
	
	
	//To create your own JdbcUserDetailsManager you should give your DataSource and 
	//JdbcUserDetailsManager will try to connect your database.
	//We are going to use this userDetailsManager for our registration form,
	//to save new user to the database with username, encrypted password(bcrypt) and authority EMPLOYEE.
	//We will also use userDetailsManager to check if user with that username already exist in database.
	//We can also call this methods:updateUser, deleteUser, changePassword.
	@Bean
	public UserDetailsManager userDetailsManager() {
		
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		
		jdbcUserDetailsManager.setDataSource(securityDataSource);
		
		return jdbcUserDetailsManager; 
	}
		
}






