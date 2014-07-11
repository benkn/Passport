Passport
========
Portable User Management

The Passport project is dedicated to creating an easy to use, simple to integrate, and secure user management system. 

It provides the following functionality:
- Encrypted user storage - users passwords are encrypted; email and other attributes may follow later
- Simple interface for accessing and managing users
- Transaction management to prevent erroneous reads or write failures
 
To integrate into your project:

1\. Add __Passport__ to your maven repository:

> mvn install:install-file -Dfile=passport-<version>.jar -DgroupId=ben.kn -DartifactId=passport -Dversion=<version> -Dpackaging=jar

2\. Execute the provided SQL script for creating the schema in your MySQL database.
3\. Execute on the command-line to get the encrypted version of your password 

> $ java -cp <path to jar>/passport-[version number].jar ben.kn.passport.CipherService [database password] 

4\. Using XML-based Spring Configuration, add the following bean definition (variables can be modified according to environment):

	 <bean id="passportConfig" class="ben.kn.passport.config.PassportConfig">
	 	<!-- optional -->
	 	<property name="mailServerHost" value="${mailServer.host_address}"/>
	 	<property name="mailServerPort" value="${mailServer.port}"/>
	 	<property name="emailFromAddress" value="${email_address_to_use_as_from_address_in_emails}"/>
	 	<property name="verficationGracePeriod" value="${days_to_allow_until_user_must_verify_with_confirmation_code}"/>
	 	<property name="showSQLOutput" value="${boolean_whether_to_show_sql_output_in_log}"/>
	 	<property name="passphrase" value="${string_of_passphrase_for_encryption}"/>
	 	<property name="salt" value="${string_of_salt_for_encryption}"/>
	 	
	 	<!-- required -->
	 	<property name="passportDBURL" value="${database.host_address}"/>
	 	<property name="passportDBUsername" value="${database.username}"/>
	 	<property name="passportDBPassword" value="${database.password}"/>
	 </bean>
 