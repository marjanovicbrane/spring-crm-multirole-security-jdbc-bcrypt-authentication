package com.brane.spring.config;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

//THIS IS JAVA CONFIG CLASS.WE ARE USING PURE JAVA CONFIG, NO XML
@Configuration
//with this annotation we enable Spring MVC
@EnableWebMvc
//so we can do hibernate transaction in the background(@Transactional)
@EnableTransactionManagement
//base package where to scan components for Controller class,Service class,Reporistory class...
@ComponentScan("com.brane.spring")
//Classpath is a parameter in the Java Virtual Machine or the Java compiler 
//that specifies the location of user-defined classes and packages. 
//The parameter may be set either on the command-line, or through an environment variable.
@PropertySource({ "classpath:persistence-mysql.properties", "classpath:security-persistence-mysql.properties" })
//with this interface WebMvcConfigurer we can make our custom configuration for SPRING MVC
//all methods are default methods, which are not implemented in this class
//Evrey class which have this annotation @EnableWebMvc can implement this interface WebMvcConfigurer
public class DemoAppConfig implements WebMvcConfigurer {

	//this variable env holds properties from the persistence-mysql.properties file
	//THIS IS GLOBAL ENVIRONMENT VARIABLE
	//Annotation @PropertySource READS DATA FROM THE PROPERTIES FILE AND 
	//WE ARE GOING TO INJECT THAT DATA INTO VARIABLE env
	//ENVIRONMENT INTERFACE REPRESENTS THE ENVIRONMENT IN WHICH THE CURRENT APPLICATION IS LAUNCHED 
	//we are going to inject Environment object so we can get properties from the files 
	//persistence-mysql.properties and security-persistence-mysql.properties
	@Autowired
	private Environment env;
	
	//we are going to set logger just for diagnostics
	private Logger logger = Logger.getLogger(getClass().getName());
	
	
	//define a bean for ViewResolver, because we are using JSP pages
	@Bean
	public ViewResolver viewResolver() {
		
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		
		//set prefix
		viewResolver.setPrefix("/WEB-INF/view/");
		
		//set suffix
		viewResolver.setSuffix(".jsp");
		
		return viewResolver;
	}
	
	//DataSource 1 for database web_customer_tracker
	//we created a method which returns DataSource object
	//in this method we set all our props for our database web_customer_tracker
	@Bean
	public DataSource myDataSource() {
		
		// create connection pool, this class is from c3p0 package
		ComboPooledDataSource myDataSource = new ComboPooledDataSource();

		// set the jdbc driver
		try {
			myDataSource.setDriverClass("com.mysql.jdbc.Driver");		
		}
		catch (PropertyVetoException exc) {
			throw new RuntimeException(exc);
		}
		
		//let's log url and user ... just to make sure we are reading the data
		logger.info("jdbc.url=" + env.getProperty("jdbc.url"));
		logger.info("jdbc.user=" + env.getProperty("jdbc.user"));
		
		// set database connection props
		myDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
		myDataSource.setUser(env.getProperty("jdbc.user"));
		myDataSource.setPassword(env.getProperty("jdbc.password"));
		
		// set connection pool props
		myDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		myDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		myDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));		
		myDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));

		return myDataSource;
	}
	
	
	//DataSource 2 for database spring_security_demo_bcrypt
	//we created a method which returns DataSource object
	//in this method we set all our props for our database spring_security_demo_bcrypt
	@Bean
	public DataSource securityDataSource() {
		
		// create connection pool, this class is from c3p0 package
		ComboPooledDataSource securityDataSource= new ComboPooledDataSource();
				
		// set the jdbc driver
		try {
			securityDataSource.setDriverClass(env.getProperty("security.jdbc.driver"));
		} catch (PropertyVetoException exc) {
			throw new RuntimeException(exc);
		}
		
		//let's log url and user ... just to make sure we are reading the data
		logger.info(">>> security.jdbc.url=" + env.getProperty("security.jdbc.url"));
		logger.info(">>> security.jdbc.user=" + env.getProperty("security.jdbc.user"));
		
		
		// set database connection props
		securityDataSource.setJdbcUrl(env.getProperty("security.jdbc.url"));
		securityDataSource.setUser(env.getProperty("security.jdbc.user"));
		securityDataSource.setPassword(env.getProperty("security.jdbc.password"));
		
		// set connection pool props
		securityDataSource.setInitialPoolSize(getIntProperty("security.connection.pool.initialPoolSize"));
		securityDataSource.setMinPoolSize(getIntProperty("security.connection.pool.minPoolSize"));
		securityDataSource.setMaxPoolSize(getIntProperty("security.connection.pool.maxPoolSize"));
		securityDataSource.setMaxIdleTime(getIntProperty("security.connection.pool.maxIdleTime"));
		
		return securityDataSource;
	}
	
	
	
	//need a helper method, read environment property and convert to int
	private int getIntProperty(String propName) {
		
		String propVal = env.getProperty(propName);
		
		// now convert to int
		int intPropVal = Integer.parseInt(propVal);
		
		return intPropVal;
	}
	
	

	
	//this method contains all what we have in myDataSource() method
	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		
		//create session factory
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		// set the properties
		//this sessionFactory object now contains all what we have in myDataSource() method
		sessionFactory.setDataSource(myDataSource());
		
		//we are going to set package for all our entity classes which need to be scaned (com.luv2code.springdemo.entity)
		sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
		
		//we are calling method for hibernate dialect and hibernate show sql
		sessionFactory.setHibernateProperties(getHibernateProperties());
		
		return sessionFactory;
	}
	
	
	//this method will load  2 props and this method we calling in sessionFactory() method
	private Properties getHibernateProperties() {

		// set hibernate properties
		Properties props = new Properties();

		//set property for hibernate.dialect and hibernate.show_sql
		props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		
		return props;				
	}
		

	
	@Bean
	@Autowired
	//WE ARE GOING TO DO HERE DEPENDENCY INJECTION, BECAUSE OF THAT WE CREATE ABOVE SPRING BEAN OBJECTS
	//WE WANT TO HAVE ALL WHAT WE CREATED HERE IN OUR METHOD transactionManager()
	//ALL WHAT WE CREATED FOR NOW WE HAVE IN METHOD sessionFactory() AND NOW WE INJECT THAT BEAN ID
	//IN OUR transactionManager() METHOD LIKE ARGUMENT
		
	//THIS METHOD WILL USE US FOR TRANSACTION,EVERY CHANGE ON DB IS TRANSACTION(CREATE,UPDATE,DELETE)
	//THIS METHOD WILL TAKE CARE OF @TRANSACTIONAL ANNOTATION, SO WE DON'T NEED TO
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		
		// setup transaction manager based on session factory
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);

		return txManager;
	}	
	
	
	//Add resource handler for loading css, images, etc
	//OVERRIDE method from the WebMvcConfigurer interface
	//This method is like handler for static resources like:pictures, js, css...
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	
        registry.addResourceHandler("/resources/**")
        
          .addResourceLocations("/resources/"); 
    }	
}
