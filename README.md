# Extra Credit
- Automated Frontend Testing
- Additional User Role
- Anonymous Orders
- Order History
- [Badges](https://github.com/ncstate-csc-coursework/csc326-2025-fall-tp-204-3?tab=readme-ov-file#wolfcafe)
- UI Enhancements
- [GitHub Projects](https://github.com/orgs/ncstate-csc-coursework/projects/43)

# WolfCafe
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Node.js](https://img.shields.io/badge/node.js-6DA55F?style=for-the-badge&logo=node.js&logoColor=white)

![License](https://img.shields.io/badge/license-Academic-blue?style=for-the-badge)
![NC State](https://img.shields.io/badge/NC%20State-CSC326-red?style=for-the-badge)
![Course](https://img.shields.io/badge/Course-Software%20Engineering-purple?style=for-the-badge)

![Version](https://img.shields.io/badge/version-1.0.0-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/status-active-success?style=for-the-badge)
![Maintenance](https://img.shields.io/badge/maintained-yes-green?style=for-the-badge)

![REST API](https://img.shields.io/badge/REST-API-orange?style=for-the-badge)
![Security](https://img.shields.io/badge/security-JWT-green?style=for-the-badge)
![Authentication](https://img.shields.io/badge/auth-role%20based-blue?style=for-the-badge)

## Install Lombok
Lombok is a library that lets us use annotations to automatically generate getters, setters, and constructors.  For Lombok to work in Eclipse (and other IDEs like IntelliJ or VS Code), you need to set up Lombok with the IDE in addition to including in the pom.xml file.

Follow the [instructions for setting up Lombok in Eclipse](https://projectlombok.org/setup/eclipse).  Make sure you download the laste version of Lombok from [Maven Repository](https://mvnrepository.com/artifact/org.projectlombok/lombok) as a jar file.

## Configuration

Update `application.properties` in `src/main/resources/` and `src/test/resources/`.

  * Set `spring.datasource.password` to your local MySQL password`
  * Set `app.jwt-secret` as described below.
  * Set `app.admin-user-password` to a plain text string that you will use as the admin password.
  
### Set `app.jwt-secret`

We will create a secret key that will be used for JWT authentication.  Think of a secret key phrase.  You'll want to encrypt it using SHA256 encryption.  You can use a tool like:  https://emn178.github.io/online-tools/sha256.html to generate the encrypted text.  Copy that into your `application.properties` file.

## Setup
The rest of the setup for WolfCafe is the same as for [CoffeeMaker](https://pages.github.ncsu.edu/engr-csc326-staff/326-course-page/onboarding/setup).


## User Roles

User roles are defined and initialized in `config.Roles`.  The `ADMIN` role is a constant.  All other roles are listed in the `UserRoles` enumeration. You can add new roles by adding the role name to the enumeration.

### Initializing the Roles/Admin user in the DB

`config.SetupDataLoader` initializes the DB with roles and creates a default user with the `ADMIN` role.  This class is automatically run when the application starts.  

The admin user has the user name of "admin" and an email address of "admin@admin.edu".  You will specify the password for the admin user in the `application.properties` file.  The password is read in from the `application.properties` and then encrypted using the password encoder.  

## Testing User Authentication in Postman

The following provides examples of how to work with user authentication in Postman.

### Create a New User

Endpoint: `POST http://localhost:8080/api/auth/register`

Body:

```
{
    "name": "Sarah Heckman",
    "username": "sheckman",
    "email": "sheckman@ncsu.edu",
    "password": "sarah"
}
```

Response: 201 Created

```
User registered successfully.
```

### Login with User

Endpoint: `POST http://localhost:8080/api/auth/login`

Body: 

```
{
    "usernameOrEmail": "sheckman",
    "password": "sarah"
}
```

Response: 200 OK

```
{
    "accessToken": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJzaGVja21hbiIsImlhdCI6MTcyOTEyNjg1MiwiZXhwIjoxNzI5NzMxNjUyfQ.WiPROZAMhNbiB8H3fhNJdiC-XX5RJEcHXzmGPEH7aMEFvsjbsvk2m1ZcAKi-lTdt",
    "tokenType": "Bearer",
    "role": "ROLE_CUSTOMER"
}
```

Note the accessToken will vary with each login.  You'll want to save this for testing endpoints that require authentication.

### Get Items

Roles: STAFF, CUSTOMER

Authorization:
  * Bearer
  * Token - copy from the response of an authenticated User
  
Response: 200 OK

```
JSON list of items
```
