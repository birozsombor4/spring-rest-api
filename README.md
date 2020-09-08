### Spring REST API template

 - [Overview](#overview)
 - [Technologies](#technologies)
 - [Dependencies](#dependencies)
 - [Environment variables](#environment-variables)
 - [Predefined user](#predefined-user)
 - [Usage without verification](#use-without-user-verification)
 - [Implemented Endpoints](#implemented-endpoints)
    - [POST /register](#post-register)
    - [POST /login](#post-login)
    - [GET /verify](#get-verifytoken)
    - [GET /avatar](#get-avataruser_id)
    - [POST /avatar](#post-avataruser_id)
    - [JWT authentication errors](#jwt-authentication-errors)
 - [Deployed version](#deployed-version-of-application)

#### Overview

This project is a template for Spring REST API application. You can register and store users in MySQL database and
 send verification emails for new registered users. After
 successfully registration and verification you can login and get a JWT token for your user. You can reach
  authenticated endpoints with this token. This implementation can handle avatar logic for users (get and update avatars for user).
 
 All endpoints and service logic is tested by several unit and integration tests.
 
#### Technologies

Application uses following technologies:
- JAVA 8
- REST API
- Spring Boot
- Gradle
- JPA
- Hibernate
- MySQL
- H2
- JUnit
- Mockito
- Spring Security
- Spring Mail
- Thymeleaf
- JWT
- CheckStyle plugin as a linter

#### Dependencies

It uses following dependencies:
 - spring-boot-starter-web
 - spring-boot-devtools
 - spring-boot-starter-security
 - spring-boot-starter-mail
 - spring-boot-starter-data-jpa
 - mysql-connector-java
 - h2database
 - flyway
 - jsonwebtoken
 - jaxb-api
 - modelmapper
 - commons-validator
 
 For testing purposes:
 - spring-boot-starter-test
 - spring-security-test
 - greenmail
 
 #### Environment variables
 
 If you would like to use this project with all features you have to register to an email service provider. I
  recommend to use [MailTrap](https://mailtrap.io/) which is free for develop purposes and you can get easily required values for "EMAIL_
 \*" variables.
 
 | Environment variable  | Value |
 | ------------- | ------------- |
 | ACTIVE_PROFILE | prod / develop / test |
 | DATASOURCE_URL | jdbc:mysql://localhost/${*your local mysql database name*}?serverTimezone=UTC |
 | DATASOURCE_USERNAME | *your local mysql username* |
 | DATASOURCE_PASSWORD | *your local mysql password* |
 | HIBERNATE_DIALECT | org.hibernate.dialect.MySQL5Dialect |
 | EMAIL_HOST | *your email host* |
 | EMAIL_USERNAME | *your email username* |
 | EMAIL_PASSWORD | *your email password* |
 | SECRET_KEY | *it can be any string locally* |
 | APP_LOG_LVL | info / debug |
 
  #### Predefined user

There is one predefined user in database which is free to use and you can test the endpoints with it.
    
  | Username | Password |
  | ------------- | ------------- |
  | user | fakePassword |
  
 #### Use without user verification
 
 You can use easily this template without user verification. You have to provide some gibberish texts for "EMAIL_
 /*" environment variables and also you have to configure UserDetailsImpl.java.
 
 You have to modify this implementation by change return value to "true". This means, Spring Security won't see
  any user related fields to get the actual user is verified or not. Users will be always verified.
  
Original code:
 ```java
UserDetailsImpl.java

  @Override
  public boolean isEnabled() {
    return user.isVerified();
  }
```

Use without verification:
 ```java
UserDetailsImpl.java

  @Override
  public boolean isEnabled() {
    return true;
  }
```

#### Implemented endpoints

#### POST /register

##### Request

```json
{
  "username": "birozsombor4",
  "email": "birozsombor4@gmail.com",
  "password": "myPassword"
}
```

##### Responses
	
If all required parameters are valid, returns HTTP 200 status with following object:

```json
{
  "id": 1,
  "username": "birozsombor4",
  "email": "birozsombor4@gmail.com",
  "verified": false,
  "avatar": "default.png"
}
```

If username is null/empty, returns HTTP 400 status with following object:

```json
{
  "status": "error",
  "message": "Username is missing!"
}
```

If password is null/empty or shorter than 6 character, returns HTTP 400 status with following object:

```json
{
  "status": "error",
  "message": "Password is too short. Please use at least 6 characters!"
}
```

If email is null or doesn't contain "@" or "." or shorter than 6 character, returns HTTP 400 status with following
 object:

```json
{
  "status": "error",
  "message": "Email is not correct!"
}
```

If username is not unique, returns HTTP 400 status with following object:

```json
{
  "status": "error",
  "message": "User name is already taken. Please choose another one!"
}
```

If email is not unique, returns HTTP 400 status with following object:

```json
{
  "status": "error",
  "message": "E-mail is already taken. Please choose another one!"
}
```

If registration object contains more than one invalid field, returns message field with ";" delimiter and HTTP 400
 status with following object
 
 ```json
 {
   "status": "error",
   "message": "Username is missing!; Email is not correct!"
 }
 ```

#### POST /login

##### Request

```json
{
  "username": "birozsombor4",
  "password": "myPassword"
}
```

##### Responses
	
If all required parameters are valid, returns HTTP 200 status with JWT token:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
}
```

If username is null/empty, returns HTTP 400 status with following object:

```json
{
  "status": "error",
  "message": "Username is required!"
}
```

If password is null/empty or shorter than 6 character, returns HTTP 400 status with following object:

```json
{
  "status": "error",
  "message": "Password is required!"
}
```

If username or password is invalid, returns HTTP 401 status with following object:

```json
{
  "status": "error",
  "message": "Username or password is incorrect."
}
```

If user is not verified, returns HTTP 403 status with following object:

```json
{
  "status": "error",
  "message": "User is not verified."
}
```
#### GET /verify?token

##### Request

```json
parameter: token
example: http://localhost:8080/verify?token=f271fbd3-d7f8-4b28-b795-78499e11eb9e
```

##### Responses

If user is not verified and verification token exist, returns HTTP 200 status with following object:

```json
{
  "status": "ok",
  "message": "birozsombor4 has verified."
}
```

If user has already verified and verification token exist, returns HTTP 403 status with following object:

```json
{
  "status": "error",
  "message": "birozsombor4 has already verified."
}
```

If verification token doesn't exist, returns HTTP 404 status with following object:

```json
{
  "status": "error",
  "message": "Verification token does not exist."
}
```

If verification token expired, returns HTTP 200 status with following object:

```json
{
  "status": "ok",
  "message": "Email verification link has expired. We'll send another for your email: birozsombor4@gmail.com"
}
```

#### GET /avatar/{user_id}

##### Request

```json
path variable: user_id
example: http://localhost:8080/avatar/1
header: "Authorization" : "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiaXJvenNvbWJvcjQiLCJ1c2VyX2lkIjoyLCJleHAiOjE1OTk1MzY4MTMsImlhdCI6MTU5OTUwMDgxM30.gnALl-Cp7w7_0J3bJvdCWzggJlZ55P-4hN1PKPvLH6U"
```

##### Responses

If user exist and JWT token is valid, returns HTTP 200 status and user's avatar as a multipart file with following
 headers:

```json
Content-Type: image/png
Content-Disposition: filename=default.png
```

Is user doesn't exist with given id, returns HTTP 400 status and following object:

```json
{
  "status": "error",
  "message": "User does not exist with given id/name: 666"
}
```

#### POST /avatar/{user_id}

##### Request

```json
path variable: user_id
example: http://localhost:8080/avatar/1
headers: "Authorization" : "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiaXJvenNvbWJvcjQiLCJ1c2VyX2lkIjoyLCJleHAiOjE1OTk1MzY4MTMsImlhdCI6MTU5OTUwMDgxM30.gnALl-Cp7w7_0J3bJvdCWzggJlZ55P-4hN1PKPvLH6U"
         "Content-Type" : "multipart/form-data; boundary=<calculated when request is sent>"
         "Content-Length" : "<calculated when request is sent>"
form-data body:
        "image" : multipart_file_from_your_local_machine.png
```

##### Responses

If user exist, JWT token is valid and multipart file is valid, returns HTTP 200 status and following object:
```json
{
  "status": "ok",
  "message": "Avatar has updated for user: birozsombor4"
}
```

If user try to change other user's avatar, returns HTTP 405 status and following object:

```json
{
  "status": "error",
  "message": "User id: 1 doesn't belongs to user: birozsombor4"
}
```

If image is greater than 2MB, returns HTTP 413 status and following object:

```json
{
  "status": "error",
  "message": "Maximum image size: 2MB"
}
```

If content type is not jpeg/png, image/png or image/jpeg, returns HTTP 400 status and following object:

```json
{
  "status": "error",
  "message": "Unsupported Content-Type: text/plain"
}
```

If image doesn't have extension, returns HTTP 400 status and following object:

```json
{
  "status": "error",
  "message": "Unsupported filename: Filename should has extension."
}
```

#### JWT authentication errors

##### Responses

If Authorization header or "Bearer " prefix is missing, returns HTTP 401 status and following object:

```json
{
  "status": "error",
  "message": "Invalid Authorization or missing JWT token."
}
```

If JWT token is not valid, returns HTTP 401 status and following object:

```json
{
  "status": "error",
  "message": "Invalid JWT format."
}
```

If JWT token is expired, returns HTTP 401 status and following object:

```json
{
  "status": "error",
  "message": "Expired JWT."
}
```

If JWT contains not registered username, returns HTTP 401 status and following object:

```json
{
  "status": "error",
  "message": "Username not found."
}
```

If JWT doesn't contains username, returns HTTP 401 status and following object:

```json
{
  "status": "error",
  "message": "Username is missing from JWT."
}
```

#### Deployed version of application

This template is deployed to Heroku and you can reach it on this URL: [https://spring-rest-api-template.herokuapp.com/](https://spring-rest-api-template.herokuapp.com/)

The deployed application doesn't contain email verification logic so you can try it out via [Potman](https://www.postman.com/) or [Hoppscotch
](https://hoppscotch.io/).

If you don't want to register you use and try without and use [predefined user](#predefined-user).