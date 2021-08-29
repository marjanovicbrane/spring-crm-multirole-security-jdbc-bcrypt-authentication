package com.brane.spring.service;

import java.util.List;

import com.brane.spring.entity.Customer;

//we created interface with CRUD methods for customer SERVICE to delegate calls to the DAO LAYER
public interface CustomerService {

	public List<Customer> getCustomers();

	public void saveCustomer(Customer theCustomer);

	public Customer getCustomer(int theId);

	public void deleteCustomer(int theId);
	
}
