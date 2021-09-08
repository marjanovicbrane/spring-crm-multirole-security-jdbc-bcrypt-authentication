# Spring CRM with multi-role and security using JDBC authentication with BCRYPT algorithm
In this web app we can register new user and select appropriate role for that user, so the user can have multiroles (drop-down list).
User can have 3 roles:EMPLOYEE,MANAGER and ADMIN.

In this project we also have security authorization, so the user with role EMPLOYEE can only get the list of customers from the database.
User with role MANAGER can see all the customers, can add a new customer and can UPDATE the customers, the user with role ADMIN can see all the customers, 
can add a new customer, can UPDATE customers and can DELETE customers.

In this project I was using JDBC authentication with bcrypt algorithm for passwords in database.
