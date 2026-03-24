# 💼 Job Portal Backend

A full-featured **Job Portal REST API** built with **Spring Boot 3**, **Spring Security**, **JWT Authentication**, and **PostgreSQL**. Supports three roles — **Admin**, **Employer**, and **Job Seeker** — with complete job listing, application, and profile management flows.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5 |
| Language | Java 21 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Containerization | Docker + Docker Compose |
| Boilerplate Reduction | Lombok |

---

## 📁 Project Structure

```
src/main/java/com/saurabh/
├── configuration/          # Security, JWT filter, CORS, Swagger
│   ├── JWTAuthenticationFilter.java
│   ├── SecurityFilterChainConfig.java
│   ├── AuthenticationConfig.java
│   ├── CorsConfig.java
│   ├── SwaggerConfig.java
│   └── CustomAuthenticationEntryPoint.java
├── Controller/             # REST Controllers
│   ├── AuthController.java
│   ├── JobController.java
│   ├── JobSeekerController.java
│   ├── EmployerController.java
│   ├── ApplicationController.java
│   ├── AdminController.java
│   ├── SkillController.java
│   └── UserController.java
├── service/                # Business Logic
├── repository/             # Spring Data JPA Repositories
├── Entity/                 # JPA Entities
├── DTOs/                   # Request & Response DTOs
├── ENUMS/                  # Enumerations
├── exception/              # Custom Exceptions & Global Handler
├── util/                   # JWT Utilities
├── AdminInitializer.java   # Seeds default admin on startup
└── myapp.java              # Spring Boot Entry Point
```

---

## 🔐 Authentication

The API uses **stateless JWT-based authentication**.

- Register at `POST /api/v1/auth/sign-up`
- Login at `POST /api/v1/auth/login` to receive a JWT token
- Pass the token in all protected requests as:
  ```
  Authorization: Bearer <your_token>
  ```

### Roles

| Role | Description |
|---|---|
| `JOBSEEKER` | Can browse jobs, apply, manage profile & skills |
| `EMPLOYER` | Can post/manage jobs, view & update applications |
| `ADMIN` | Can manage all users and verify employers |

> ⚠️ Admin account is auto-seeded on startup:
> - Email: `admin@gmail.com`
> - Password: `admin123`

---

## 📡 API Endpoints

### 🔓 Auth — `/api/v1/auth`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/sign-up` | Register as Job Seeker or Employer | Public |
| POST | `/login` | Login and receive JWT token | Public |

**Sign-up Request Body:**
```json
{
  "fname": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "phone": "9876543210",
  "role": "JOBSEEKER"
}
```

---

### 💼 Jobs — `/api/v1/jobs`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/jobs` | List all jobs (paginated) | Public |
| GET | `/jobs/{id}` | Get job by ID | Public |
| POST | `/jobs` | Create a new job | Employer |
| PUT | `/jobs/{id}` | Update job listing | Employer (owner) |
| DELETE | `/jobs/{id}` | Delete job listing | Employer (owner) |
| PATCH | `/jobs/{id}/status` | Update job status | Employer (owner) |

**Job Status Values:** `OPEN`, `CLOSED`, `PAUSED`, `FILLED`

---

### 👤 Job Seeker — `/api/v1/jobseekers`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/me` | Get own profile | Job Seeker |
| PUT | `/me` | Update profile | Job Seeker |
| GET | `/me/skills` | List own skills | Job Seeker |
| POST | `/me/skills` | Add a skill | Job Seeker |
| DELETE | `/me/skills/{skillId}` | Remove a skill | Job Seeker |

---

### 🏢 Employer — `/api/v1/employer`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/me` | Get own company profile | Employer |
| PUT | `/me` | Update company info | Employer |
| GET | `/jobs` | List own job postings | Employer |
| GET | `/jobs/{jobId}/applications` | View applications for a job | Employer |

---

### 📋 Applications — `/api/v1/applications`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/{jobId}/apply` | Apply for a job | Job Seeker |
| GET | `/my` | View my applications | Job Seeker |
| DELETE | `/{id}` | Withdraw application | Job Seeker |
| PATCH | `/{id}/status` | Update application status | Employer |

**Application Status Values:** `APPLIED`, `SHORTLISTED`, `INTERVIEW`, `REJECTED`, `HIRED`

---

### 🛠️ Admin — `/api/v1/admin`

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/users` | List all users (paginated) | Admin |
| DELETE | `/users/{email}` | Delete a user | Admin |
| POST | `/employers/{id}/verify` | Verify an employer | Admin |

---

### 🔧 Other Endpoints

| Method | Endpoint | Description | Access |
|---|---|---|---|
| GET | `/api/v1/skills` | List all available skills | Public |
| GET | `/api/v1/users/me` | Get current user profile | Job Seeker |
| PUT | `/api/v1/users/me` | Update user profile | Job Seeker |
| PUT | `/api/v1/users/password` | Change password | Job Seeker |
| DELETE | `/api/v1/users/me` | Delete own account | Job Seeker |

---

## 🗃️ Database Entities

```
User
 ├── JobSeeker (1:1) → JobSeekerSkill (Many) → Skill
 └── Employer  (1:1) → Job (Many) → Application (Many) → JobSeeker

Supporting Entities:
 ├── Notification
 ├── Review
 └── SavedJob
```

### Key ENUMs

| ENUM | Values |
|---|---|
| `Role` | `JOBSEEKER`, `EMPLOYER`, `ADMIN` |
| `JobType` | `FULL_TIME`, `PART_TIME`, `CONTRACT`, `FREELANCE`, `INTERNSHIP`, `TEMPORARY` |
| `WorkMode` | `ONSITE`, `REMOTE`, `HYBRID` |
| `ExperienceLevel` | `INTERN`, `ENTRY_LEVEL`, `JUNIOR`, `MID_LEVEL`, `SENIOR`, `LEAD`, `MANAGER` |
| `ApplicationStatus` | `APPLIED`, `SHORTLISTED`, `INTERVIEW`, `REJECTED`, `HIRED` |
| `Availability` | `OPEN_TO_WORK`, `NOT_LOOKING`, `ACTIVELY_LOOKING` |
| `CompanySize` | `STARTUP`, `SMALL`, `MEDIUM`, `LARGE`, `ENTERPRISE` |
| `ProficiencyLevel` | `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT` |

---

## ⚙️ Configuration

Set the following environment variables before running:

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/jobportal` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `yourpassword` |
| `JWT_SECRET` | Secret key for signing JWTs | `your-very-long-secret-key` |
| `JWT_EXPIRATION` | Token expiry in minutes | `60` |

For local development, create an `application.properties` or `application.yml` in `src/main/resources/`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/jobportal
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

jwt.secret=your-very-long-secret-key-at-least-32-chars
jwt.expiration=60
```

---

## 🐳 Running with Docker

### Prerequisites
- Docker & Docker Compose installed

### Steps

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd job-portal-backend
   ```

2. Create a `.env` file in the project root:
   ```env
   DB_URL=jdbc:postgresql://db:5432/jobportal
   DB_USERNAME=postgres
   DB_PASSWORD=yourpassword
   JWT_SECRET=your-very-long-secret-key
   JWT_EXPIRATION=60
   ```

3. Start the app:
   ```bash
   docker-compose up --build
   ```

The API will be available at `http://localhost:8080`.

---

## 🏃 Running Locally (Without Docker)

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL running locally

### Steps

```bash
# Clone the repo
git clone <repo-url>
cd job-portal-backend

# Set environment variables or update application.properties

# Build and run
mvn clean package -DskipTests
java -jar target/job-portal-backend-0.0.1-SNAPSHOT.jar
```

---

## 📖 API Documentation (Swagger UI)

Once the application is running, visit:

```
http://localhost:8080/swagger-ui/index.html
```

- Click **Authorize** and paste your JWT token to test protected endpoints.

Live API docs (production):
```
https://job-portal-backend-production-1bc7.up.railway.app/swagger-ui/index.html
```

---

## 🔒 Security Design

- **Stateless sessions** — No server-side session storage; every request is authenticated via JWT.
- **Role-based access control** — Enforced at both the filter chain level (`SecurityFilterChainConfig`) and method level (`@PreAuthorize`).
- **Ownership checks** — Employers can only modify their own jobs and applications; Job Seekers can only withdraw their own applications.
- **BCrypt password hashing** — All passwords stored as BCrypt hashes.
- **CORS configured** — Allows requests from `localhost:3000` and the production frontend.

---

## 🌐 Deployed On

- **Backend:** [Railway](https://railway.app)
- **Live URL:** `https://job-portal-backend-production-1bc7.up.railway.app`

---

## 👨‍💻 Author

**Saurabh Kawatra**

---

## 📄 License

This project is for educational/portfolio purposes.
