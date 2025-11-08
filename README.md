**Project Status:**
[![Java CI with Maven](https://github.com/Tchaoser/SYSC4806-Group13-PerkManager/actions/workflows/maven.yml/badge.svg)](https://github.com/Tchaoser/SYSC4806-Group13-PerkManager/actions/workflows/maven.yml)

**Project Members:**
* Kyle Foisy 101215573  
* Peter Grose 101219562  
* Sapthakeerthi Udayakumar 101289957  
* Lucas Warburton 101276823  
* Aziz Hamad 101232108

---

## Project Overview

**Perk Manager** allows users to create profiles, which may be tied to memberships (e.g., Air Miles, CAA, Visa). The system allows users to view or add perks. Perks may be discounts or benefits tied to specific memberships and products.

**Current Implementation (Partial)**

* Users can **register and log in**.
* Users can **add new perks** and **view existing perks**.
* Backend services for **accounts, perks, memberships, and products** are in place.
* Thymeleaf templates exist for **adding perks, listing perks, and basic navigation**.
* Controllers and views for several features are still under development and will be expanded in future versions.

This version forms the foundation for personalized perk searches, voting, and filtering.

The text below outlines the current Perk Manager project structure, including backend code, templates, tests, configuration, and CI/CD workflows, with high-level comments for each file.

```
perkmanager/
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/perkmanager/
│  │  │  ├─ PerkmanagerApplication.java     # Main Spring Boot entry point
│  │  │  │
│  │  │  ├─ config/
│  │  │  │  └─ SecurityConfig.java          # Login/authentication setup
│  │  │  │
│  │  │  ├─ controllers/
│  │  │  │  ├─ HomeController.java          # Routes homepage and static pages
│  │  │  │  ├─ PerkController.java          # Handles creating, listing, and voting on perks
│  │  │  │  ├─ UserController.java          # Manages user profiles and memberships
│  │  │  │  └─ MembershipController.java    # Lists available memberships, handles add/remove
│  │  │  │
│  │  │  ├─ model/
│  │  │  │  ├─ User.java                    # JPA entity: user (name, email, memberships)
│  │  │  │  ├─ Membership.java              # JPA entity: membership (CAA, Visa, etc.)
│  │  │  │  ├─ Product.java                 # JPA entity: product or service (flight, movie, etc.)
│  │  │  │  └─ Perk.java                    # JPA entity: perk (description, votes, expiry)
│  │  │  │
│  │  │  ├─ repositories/
│  │  │  │  ├─ UserRepository.java              # Spring Data JPA interface for User CRUD
│  │  │  │  ├─ MembershipRepository.java        # JPA repo for Membership entity
│  │  │  │  ├─ ProductRepository.java           # JPA repo for Product entity
│  │  │  │  └─ PerkRepository.java              # JPA repo for Perk entity + custom queries
│  │  │  │
│  │  │  └─ services/
│  │  │     ├─ UserService.java             # Handles profile logic and membership linking
│  │  │     ├─ PerkService.java             # Core business logic for perks (posting, voting, expiry)
│  │  │     ├─ MembershipService.java      	# Manages membership CRUD and validation
│  │  │     └─ ProductService.java      	# Manages product CRUD and validation
│  │  │
│  │  ├─ resources/
│  │  │  ├─ templates/
│  │  │  │  ├─ fragments/
│  │  │  │  │  ├─ navbar.html           	# Reusable navbar component
│  │  │  │  │  └─ footer.html               # Reusable footer component
│  │  │  │  ├─ index.html                   # Homepage showing navigation options
│  │  │  │  ├─ perks.html                   # Displays all perks (sortable by votes or expiry)
│  │  │  │  ├─ add-perk.html                # Form for posting a new perk
│  │  │  │  ├─ login.html                   # login page for existing users
│  │  │  │  └─ signup.html                  # registration page for new users
│  │  │  │
│  │  │  ├─ application.properties          # Base config (active profile, Thymeleaf settings)
│  │  │  └─ application-local.properties    # Local dev settings 
│  │
│  └─ test/java/com/example/perkmanager/
│     ├─ config/
│     │  └─ SecurityConfigTest.java         # Tests authentication and access control setup
│     │
│     ├─ controllers/
│     │  └─  AccountControllerTest.java     # Verifies login, registration, and session routes
│     │
│     ├─ model/
│     │  ├─ AccoutTest.java                 # Tests User entity fields, relationships, validation
│     │  ├─ MembershipTest.java             # Tests Membership entity mapping and constraints
│     │  ├─ ProductTest.java                # Tests Product entity persistence and associations
│     │  └─ PerkTest.java                   # Tests Perk entity logic (expiry, voting count)
│     │
│     └─ services/
│        ├─ AccountServiceTest.java         # Verifies account creation, login, and linking logic
│        ├─ PerkServiceTest.java            # Tests business logic (votes, expiry filters)
│        ├─ MembershipServiceTest.java      # Tests membership CRUD and validation
│        └─ ProductServiceTest.java         # Tests product CRUD and validation
│
├─ pom.xml              # Maven build file (Spring Boot, JPA, Thymeleaf, etc.)
├─ .gitignore           # Ignore build output, logs, local env files
├─ .env                 # Environment variables (DB creds, Azure URL, etc.)
│
├─ .github/
│  └─ workflows/
│     ├─ maven.yml			        # CI workflow: builds Java project using Maven on push/PR to main
│     └─ main-perkmanager.yml       # CD workflow: builds JAR and deploys PerkManager to Azure Web App
│
└─ README.md                        # Project overview, setup, usage, and contribution guide
```

---

# PerkManager – Developer Database Setup (Windows)

## Prerequisites

* Java 17+
* Maven
* IntelliJ IDEA
* Internet access (for shared Aiven DB)
* **Docker** (only needed if you want a local DB)
* Node.js and npm for Jasmine Testing

---

## 1 Shared Aiven Database (first-time)

**Purpose:** shared cloud DB for all devs.

### A. Spring Boot / repo

In `src/main/resources/application.properties` include the password line (everything else is already committed):

```properties
spring.datasource.password=${DATABASE_PASSWORD}   # Enter the Aiven password
```

> Only the password line may need to be filled in locally. 
> The deployment should use the environment variable saved on Azure

---

### B. Verify connection in IntelliJ (shared DB)

1. Open **Database** tool window → **+ → Data Source → PostgreSQL**
2. Use these values:

| Field    | Value                                           |
|----------|-------------------------------------------------|
| Host     | `pg-2257ce90-perkmanager-0641.f.aivencloud.com` |
| Port     | `24494`                                         |
| Database | `defaultdb`                                     |
| User     | `avnadmin`                                      |
| Password | `<AIVEN PASSWORD>`                              |
| SSL Mode | `require`                                       |

3. Click **Test Connection** → OK to save.

---

### C. Run the app (one-time / when changing schema)

* Run `PerkmanagerApplication` (IntelliJ or `mvn spring-boot:run`)
* Hibernate will auto-create/update tables for your JPA entities.
* After the first run, you can browse the shared DB in IntelliJ anytime without running the app (Aiven DB is always
  online).

---

## Optional — Local Docker Database (committed local config)

**Purpose:** isolated testing / offline development.

> `application-local.properties` exists in the repo points at the local DB 

### A. Start the local DB (Windows CMD / PowerShell)

First, ensure Docker Desktop is running.

From the directory containing `docker-compose.yml`:

```cmd
docker-compose up -d
```

* Starts Postgres on `localhost:5432`
* DB: `perkmanager_dev` | User: `devuser` | Password: `devpass` (safe to commit)

### B. Verify connection in IntelliJ (local DB)

1. Open **Database** tool window → **+ → Data Source → PostgreSQL**
2. Use these values:

| Field    | Value             |
|----------|-------------------|
| Host     | `localhost`       |
| Port     | `5432`            |
| Database | `perkmanager_dev` |
| User     | `devuser`         |
| Password | `devpass`         |

3. Click **Test Connection** → OK to save.

To stop the local DB (optional)

```cmd
docker-compose down
```

Data persists in Docker volume and can be restarted with `docker-compose up -d`.

---

## How to switch between Shared and Local

You **do not** edit property files to switch. You change the active Spring profile.

### Windows CMD

* Local DB:

  ```cmd
  set SPRING_PROFILES_ACTIVE=local
  mvn spring-boot:run
  ```
* Back to shared DB:

  ```cmd
  set SPRING_PROFILES_ACTIVE=
  mvn spring-boot:run
  ```

---
## Database Schema
<img width="314" height="519" alt="image" src="https://github.com/user-attachments/assets/69dcc21d-3d38-4618-94fd-76a08066a148" />

## UML Class Diagram
<img width="476" height="885" alt="model" src="https://github.com/user-attachments/assets/f50e3f87-8975-426a-84ee-78bea532eb0f" />


## Quick reminders / rationale


* **Shared Aiven DB**: easy collaboration, everyone sees the same data. Fill only the Aiven password locally.
* **Local Docker DB**: safe environment for experiments, start it with `docker-compose up -d`. The local
  config is committed and uses `devpass`.
* **Switching**: Use IntelliJ Run Configuration (`SPRING_PROFILES_ACTIVE=local`) to pick local; remove it (
  `SPRING_PROFILES_ACTIVE=`) to use shared. No edits to property files are required to switch.

  ---

## Installing and Running Jasmine Client Side Testing:
1. Download and install Node.js and npm: https://docs.npmjs.com/downloading-and-installing-node-js-and-npm
2. Open a terminal and navigate to `src/main/resources/` in your project.
3. Run: npm test
Any test failures will be displayed in the terminal.

## Creating Client-Side Tests:

- Jasmine will run JS files in the directory specified by `spec_files` in `spec/support/jasmine.mjs`.
- By default, in our setup, this points to `templates/**/*.js`.

To create a test suite for a .js file, use:
```javascript
describe("test suite description here", function() {  
it("test case description here", function() {  
 // Your test code here
});  
it("another test case description", function() {  
 // More test code here
});  
```

### Example:

![alt text](image.png)
![alt text](image-1.png)

---

