# Spring CRM with multi-role and security using JDBC authentication with BCRYPT algorithm

In this project I used Spring Security for JDBC authentication and authorization with custom login page.I also applied authorization for appropriate user roles (EMPLOYEE, MANAGER or ADMIN).

![Capture](https://user-images.githubusercontent.com/61464267/133890917-7d0f68cd-bc29-4bdc-8cfb-13697a50c999.PNG)

I also made registration form, so we can register a new user and choose appropriate role for that user, we can save it to the database.User can have multiroles (drop-down list).User can have 3 roles:EMPLOYEE,MANAGER and ADMIN.With appropriate role, only that user can access some additional information and pages on the web app.


![2](https://user-images.githubusercontent.com/61464267/133891429-8dea3f61-6a71-444b-a285-98989bc99d28.PNG)


In this example I made 2 separated databases.In first database we have all customers that we are going to show when we are log in our app.

![1](https://user-images.githubusercontent.com/61464267/133890482-d3f4b878-4774-42be-ae0e-fadc9b03914d.PNG)

In second database we have all users and authorities.
![2 second db table 1](https://user-images.githubusercontent.com/61464267/133890652-89ef513e-0ce2-4c57-965f-1bcc4b61aa6a.PNG)![3 second db table 2](https://user-images.githubusercontent.com/61464267/133890656-8fb9ed0f-802e-4a46-ba24-0f3479bc5738.PNG)

In this project as we can see I used bcrypt algorithm for password encryption, this is one-way encrypted hashing, so the password in the database can never be decrypted.
To protect against CSRF attacks I used additional authentication data/token into all HTML forms.On this way we can prevent evil website to tricks us into executing an action on a web application that you are currently logged in.For each request we have randomly generated token and Spring Security verifies token before processing.

When we are logged in our app with appropriate username and password, we have security authorization, so the user with role EMPLOYEE can only get the list of customers from the database.

![6](https://user-images.githubusercontent.com/61464267/133891991-43bf479e-0dbf-4b85-99d7-7d4a2869524b.PNG)

User with role MANAGER can see all the customers, can add a new customer and can UPDATE the customers.

![7](https://user-images.githubusercontent.com/61464267/133892134-586c53b7-5c85-4edf-9d9a-84cd9f48b182.PNG)

Adding a new customer and saving to the database with MANAGER role.

![8](https://user-images.githubusercontent.com/61464267/133892523-f6682f44-b6f3-42d3-85b8-4eacda439246.PNG)

Updating existing customers and saving to the database with MANAGER role.We have now pre-populated form.

![9](https://user-images.githubusercontent.com/61464267/133892469-9846a801-56fd-4ec8-b6c2-bb4c1f77372e.PNG)

User with role ADMIN can see all the customers (READ), can CREATE, UPDATE and DELETE the customers from the database.User with ADMIN role can perform all CRUD methods.

![10](https://user-images.githubusercontent.com/61464267/133892720-24723eb3-d5db-40f0-b41e-cdaf8964731b.PNG)

I also added logout button, because we want to logout the user from the system, on that way we also removing http session, cookies, etc…

If some other user which is not authorized trys to access some additional information and pages, he will get access denied page with message.

![11](https://user-images.githubusercontent.com/61464267/133892833-aa8a7d26-b910-4857-97f2-7d6372f3f857.PNG)

