# Parking System
A command line app for managing the parking system. 
This app uses Java to run and stores the data in Mysql DB.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

- Java 1.8
- Maven 3.6.2
- Mysql 8.0.17

### Installing

A step by step series of examples that tell you how to get a development env running:

1.Install Java:

https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

2.Install Maven:

https://maven.apache.org/install.html

3.Install MySql:

https://dev.mysql.com/downloads/mysql/

After downloading the mysql 8 installer and installing it, you will be asked to configure the password for the default `root` account.
This code uses the default root account to connect and the password can be set as `rootroot`. 

For greater security the mySql connection data have been placed in a separate folder called `database.properties`
If you add another user/credentials make sure to change the same in this file configurations.

### Running App

Post installation of MySQL, Java and Maven, you will have to set up the tables and data in the data base.
For this, please run the sql commands present in the `Data.sql` file under the `resources` folder in the code base.

You can import the code into an IDE of your choice and run the App.java to launch the application or

you can ready application with terminal. 

Enter in the folder : `/parkingsystem`

and to compile application utilise the command : `mvn package`

Finally, you can run the application with the command: 

 `java -jar target/parking-system-1.0-SNAPSHOT-jar-with-dependencies.jar`





### Testing

The app has unit tests and integration tests written. More of these need to be added and in some places that can be seen mentioend as `TODO` comments. The existing tests need to be triggered from maven-surefire plugin while we try to generate the final executable jar file.

To run the tests from maven, go to the folder that contains the pom.xml file and execute the below command.

`mvn test` : Runs the tests against the compiled source code using a suitable unit testing framework. These tests should not require the code be packaged or deployed.

`mvn verify`: Runs all integration tests found in the project.

`mvn site` :  The generated site also includes the project's reports that were configured in the POM.
