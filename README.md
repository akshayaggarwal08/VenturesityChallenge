# VenturesityChallenge
This is a project for a coding challenge. All the aspects of the challenge have been successfully completed. :)

This repository has the following design ->

Models folder-
This basically contains classes used to for data handling.
--MODELTO 
1) Gets the data once the ServiceRequest makes "GET" or "POST" request to the server
2) Uses DATATO to set the data into appropriate fields.  

--DATATO
1) Sets the data into appropriate fields. For this, it uses FormField class which stores the data of different "form_fields".
2) It is also used to access, retrieve and modify data in the HomeActivity.class .

--FORMFIELD
1) Stores the "form_field" data passed to it by DATATO 

--SingletonModel
1) This class ensures that there is only one copy (at the class level) for the whole data. This is essential because there needs to be 
prevention against creation of multiple objects which can add to confusion and make different data sets with partial information.
-------------------------------------------------------------------------------

Threads folder -
This one contains ServiceRequest class which basically executes "GET" and "POST" requests ASYNCHRONOUSLY.
-GET request to fetch form
-POST request to submit the user filled form in the desired format.

-------------------------------------------------------------------------------
Parser folder -
This folder contains JSONHandler which basically converts JSON data and stores it into model when the form data is received and Vice versa
when the user submits the form
-------------------------------------------------------------------------------
Callback folder -
This one contains an interface INetworkCallback that declares a method to handle the response from the server in onPostExecute() of the 
Service Request class.
-------------------------------------------------------------------------------
Activity folder -
This one contains the following Activities -
1)BaseActivity -> This is required in case redirections are needed on the basis of data in SharedPreferences. This is just the part of
the general efficient design format but not really doing anything here.
2)HomeActivity -> This one does all the code- getting the form, setting it for inputs from user, Validating the inputs and then submitting
it to the server. This activity makes use of all the above mentioned folders in it to solve the challenge.
3)SubmitResponseActivity -> This activity just echoes the data submitted to the user as asked in the challenge document.




