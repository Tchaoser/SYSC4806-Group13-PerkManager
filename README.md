Project Status:
[![Java CI with Maven](https://github.com/Tchaoser/SYSC4806-Group13-PerkManager/actions/workflows/maven.yml/badge.svg)](https://github.com/Tchaoser/SYSC4806-Group13-PerkManager/actions/workflows/maven.yml)

Project Members:
Kyle Foisy 101215573
Peter Grose 101219562
Sapthakeerthi Udayakumar 101289957

# PerkManager – Developer Database Setup (Windows)

## Prerequisites

* Java 17+
* Maven
* IntelliJ IDEA
* Internet access (for shared Aiven DB)
* **Docker** (only needed if you want a local DB)

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

## Quick reminders / rationale

* **Shared Aiven DB**: easy collaboration, everyone sees the same data. Fill only the Aiven password locally.
* **Local Docker DB**: safe environment for experiments, start it with `docker-compose up -d`. The local
  config is committed and uses `devpass`.
* **Switching**: Use IntelliJ Run Configuration (`SPRING_PROFILES_ACTIVE=local`) to pick local; remove it (
  `SPRING_PROFILES_ACTIVE=`) to use shared. No edits to property files are required to switch.

