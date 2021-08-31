package com.brane.spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.brane.spring.entity.Customer;
import com.brane.spring.service.CustomerService;

//CONTROLLER LAYER
@Controller
@RequestMapping("/customer")
public class CustomerController {

	// need to inject our customer service
	@Autowired
	private CustomerService customerService;
	
	
	//Making request mapping /list and then we will store list of objects into attribute model called customers.
	//This model we will use in our JSP PAGE to show all data in table with for each loop.
	@GetMapping("/list")
	public String listCustomers(Model theModel) {
		
		//delegate calls from controller to service layer
		//get customers from db
		List<Customer> theCustomers = customerService.getCustomers();
				
		//add the customers to the model
		theModel.addAttribute("customers", theCustomers);
		
		//we'll return list-customers.jsp
		return "list-customers";
	}
	
	
	//Form for adding a new customer
	@GetMapping("/showFormForAdd")
	public String showFormForAdd(Model theModel) {
		
		//create model attribute to bind form data
		Customer theCustomer = new Customer();
		
		theModel.addAttribute("customer", theCustomer);
		
		return "customer-form";
	}
	
	
	

}










