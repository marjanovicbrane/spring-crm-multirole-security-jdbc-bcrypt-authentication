package com.brane.spring.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brane.spring.entity.Customer;

//THIS IS DAO LAYER
@Repository
public class CustomerDAOImpl implements CustomerDAO {

	//private field sessionFactory, so we can do dependency injection on this field
	//to get data from database.This sessionFactory object we created in xml config file.
	@Autowired
	private SessionFactory sessionFactory;
	
	
	
	@Override
	public List<Customer> getCustomers() {
		
		//get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
				
		//create a query and sort by last name
		//WE ARE USING HERE HIBERNATE API and HQL
		Query<Customer> theQuery =currentSession.createQuery("from Customer order by lastName",Customer.class);
		
		//execute query and get result list
		List<Customer> customers = theQuery.getResultList();
				
		//return the results		
		return customers;
	}

	
	
	@Override
	public void saveCustomer(Customer theCustomer) {

		//Get current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		//Save or upate the customer
		//if we have id then will execute update, otherwise will execute save
		currentSession.saveOrUpdate(theCustomer);
		
	}
	
	

	@Override
	public Customer getCustomer(int theId) {

		//get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		//now retrieve/read from database using the primary key
		Customer theCustomer = currentSession.get(Customer.class, theId);
		
		return theCustomer;
	}

	
	
	@Override
	public void deleteCustomer(int theId) {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		//delete object with primary key
		//WE ARE ALSO USING HERE HIBERNATE API with HQL
		//first we need to define parameter customerId
		Query theQuery =currentSession.createQuery("delete from Customer where id=:customerId");
		
		//and then we need to set the parameter to primary key theId
		theQuery.setParameter("customerId", theId);
		
		//execute query
		theQuery.executeUpdate();		
	}

}











