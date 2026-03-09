# 🏢 Job Portal Backend

> A production-ready Job Portal REST API built with **Spring Boot**, **Java**, and **Maven**.
> Supports Job Seekers, Employers, and Admins with JWT-based authentication, full job lifecycle management, and application tracking.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
  - [Entity Relationship Overview](#entity-relationship-overview)
  - [Table: users](#table-users)
  - [Table: jobseeker_profiles](#table-jobseeker_profiles)
  - [Table: employer_profiles](#table-employer_profiles)
  - [Table: jobs](#table-jobs)
  - [Table: skills](#table-skills)
  - [Table: job_skills](#table-job_skills)
  - [Table: jobseeker_skills](#table-jobseeker_skills)
  - [Table: applications](#table-applications)
  - [Table: saved_jobs](#table-saved_jobs)
  - [Table: notifications](#table-notifications)
  - [Table: reviews](#table-reviews)
- [API Reference](#-api-reference)
  - [Authentication](#authentication)
  - [Users](#users)
  - [Jobseeker Profile](#jobseeker-profile)
  - [Employer Profile](#employer-profile)
  - [Jobs](#jobs)
  - [Applications](#applications)
  - [Saved Jobs](#saved-jobs)
  - [Skills](#skills)
  - [Notifications](#notifications)
  - [Reviews](#reviews)
- [Authorization Roles](#-authorization-roles)
- [Enums Reference](#-enums-reference)
- [Spring Boot Implementation Notes](#-spring-boot-implementation-notes)

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| Build Tool | Maven |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8 / PostgreSQL 15 |
| File Storage | AWS S3 / Local Multipart |
| API Docs | Springdoc OpenAPI (Swagger UI) |

---

## 📁 Project Structure

```
job-portal/
├── src/main/java/com/jobportal/
│   ├── auth/               # JWT filters, token service
│   ├── user/               # User entity + controller
│   ├── jobseeker/          # Jobseeker profile + skills
│   ├── employer/           # Employer profile + reviews
│   ├── job/                # Job listings + search
│   ├── application/        # Applications pipeline
│   ├── notification/       # Notification service
│   ├── skill/              # Master skills taxonomy
│   └── common/             # Enums, base entities, utils
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/       # Flyway SQL migrations
└── pom.xml
```

---

## 🗄 Database Schema

### Entity Relationship Overview

```
users ──────────────── jobseeker_profiles ──── jobseeker_skills ──── skills
  │                           │                                         │
  │                     applications                               job_skills
  │                           │                                         │
  └──── employer_profiles ── jobs ──────────────────────────────────────┘
              │
           reviews
              │
        notifications
```

> **Design Pattern:** `users` is the base authentication table. `jobseeker_profiles` and `employer_profiles` are extension tables linked 1:1 to `users` — they hold role-specific fields without polluting the core auth table.

---

### Table: `users`

Base identity and authentication table. Every person in the system has exactly one row here.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Unique user ID |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Login email |
| `password_hash` | VARCHAR(255) | NOT NULL | Bcrypt hashed password |
| `role` | ENUM | NOT NULL | `JOBSEEKER`, `EMPLOYER`, `ADMIN` |
| `full_name` | VARCHAR(255) | NOT NULL | Display name |
| `phone` | VARCHAR(20) | — | Optional contact number |
| `profile_picture_url` | TEXT | — | Avatar/photo URL |
| `is_verified` | BOOLEAN | DEFAULT FALSE | Email verified flag |
| `is_active` | BOOLEAN | DEFAULT TRUE | Account active/banned flag |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Registration timestamp |
| `updated_at` | TIMESTAMP | ON UPDATE NOW() | Last update timestamp |

---

### Table: `jobseeker_profiles`

Extended career profile for users with role `JOBSEEKER`. 1:1 with `users`.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK | Profile ID |
| `user_id` | BIGINT | FK → users.id, UNIQUE | Owning user |
| `headline` | VARCHAR(255) | — | e.g. "Senior Java Developer" |
| `summary` | TEXT | — | About/bio section |
| `resume_url` | TEXT | — | Uploaded PDF resume URL |
| `location` | VARCHAR(255) | — | City, Country |
| `years_of_experience` | INT | — | Total years working |
| `expected_salary` | DECIMAL(12,2) | — | Desired salary |
| `availability` | ENUM | — | `IMMEDIATE`, `1_MONTH`, `3_MONTHS`, `NOT_LOOKING` |
| `linkedin_url` | TEXT | — | LinkedIn profile link |
| `github_url` | TEXT | — | GitHub profile link |

---

### Table: `employer_profiles`

Company/employer details for users with role `EMPLOYER`. 1:1 with `users`.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK | Profile ID |
| `user_id` | BIGINT | FK → users.id, UNIQUE | Owning user |
| `company_name` | VARCHAR(255) | NOT NULL | Legal company name |
| `company_logo_url` | TEXT | — | Logo image URL |
| `website` | TEXT | — | Company website |
| `industry` | VARCHAR(100) | — | e.g. "Fintech", "Healthcare" |
| `company_size` | ENUM | — | `1_10`, `11_50`, `51_200`, `201_500`, `500_PLUS` |
| `founded_year` | INT | — | Year company was founded |
| `description` | TEXT | — | Company overview |
| `headquarters` | VARCHAR(255) | — | Primary office location |
| `is_verified` | BOOLEAN | DEFAULT FALSE | Admin-verified badge |

---

### Table: `jobs`

Core job listing table. Central entity of the entire system.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Job ID |
| `employer_id` | BIGINT | FK → employer_profiles.id | Posting company |
| `title` | VARCHAR(255) | NOT NULL | Job title |
| `description` | TEXT | NOT NULL | Full job description |
| `requirements` | TEXT | — | Required qualifications |
| `responsibilities` | TEXT | — | Day-to-day duties |
| `job_type` | ENUM | — | `FULL_TIME`, `PART_TIME`, `CONTRACT`, `INTERNSHIP`, `FREELANCE` |
| `work_mode` | ENUM | — | `ON_SITE`, `REMOTE`, `HYBRID` |
| `location` | VARCHAR(255) | — | Office city/country |
| `salary_min` | DECIMAL(12,2) | — | Salary range minimum |
| `salary_max` | DECIMAL(12,2) | — | Salary range maximum |
| `currency` | VARCHAR(10) | DEFAULT 'USD' | Salary currency code |
| `experience_level` | ENUM | — | `ENTRY`, `MID`, `SENIOR`, `LEAD`, `EXECUTIVE` |
| `status` | ENUM | DEFAULT 'DRAFT' | `DRAFT`, `ACTIVE`, `PAUSED`, `CLOSED`, `EXPIRED` |
| `deadline` | DATE | — | Application closing date |
| `views_count` | INT | DEFAULT 0 | Incremented on each view |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Posted timestamp |
| `updated_at` | TIMESTAMP | ON UPDATE NOW() | Last modified timestamp |

---

### Table: `skills`

Master taxonomy of all skills/technologies available in the system.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Skill ID |
| `name` | VARCHAR(100) | UNIQUE, NOT NULL | e.g. "Java", "React", "AWS" |
| `category` | VARCHAR(100) | — | e.g. "Backend", "Frontend", "Cloud" |

---

### Table: `job_skills`

Many-to-many join table linking jobs to required/preferred skills.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `job_id` | BIGINT | FK → jobs.id | Target job |
| `skill_id` | BIGINT | FK → skills.id | Required skill |
| `is_required` | BOOLEAN | DEFAULT TRUE | Required vs. nice-to-have |

> **PK:** Composite `(job_id, skill_id)`

---

### Table: `jobseeker_skills`

Many-to-many join table linking job seekers to their skills.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `jobseeker_id` | BIGINT | FK → jobseeker_profiles.id | Owning seeker |
| `skill_id` | BIGINT | FK → skills.id | Skill they have |
| `proficiency` | ENUM | — | `BEGINNER`, `INTERMEDIATE`, `ADVANCED`, `EXPERT` |
| `years_used` | INT | — | How many years using the skill |

> **PK:** Composite `(jobseeker_id, skill_id)`

---

### Table: `applications`

Tracks every job application and its status lifecycle.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Application ID |
| `job_id` | BIGINT | FK → jobs.id | Applied job |
| `jobseeker_id` | BIGINT | FK → jobseeker_profiles.id | Applicant |
| `cover_letter` | TEXT | — | Optional cover letter text |
| `resume_url` | TEXT | — | Snapshot of resume at apply time |
| `status` | ENUM | DEFAULT 'APPLIED' | `APPLIED`, `SCREENING`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN` |
| `employer_notes` | TEXT | — | Private employer notes |
| `applied_at` | TIMESTAMP | DEFAULT NOW() | Submission timestamp |
| `updated_at` | TIMESTAMP | ON UPDATE NOW() | Last status change |

> **Unique constraint:** `(job_id, jobseeker_id)` — prevents duplicate applications.

---

### Table: `saved_jobs`

Bookmarked jobs by job seekers (wishlist).

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK | Row ID |
| `jobseeker_id` | BIGINT | FK → jobseeker_profiles.id | Seeker who saved |
| `job_id` | BIGINT | FK → jobs.id | Saved job |
| `saved_at` | TIMESTAMP | DEFAULT NOW() | When it was saved |

> **Unique constraint:** `(jobseeker_id, job_id)`

---

### Table: `notifications`

System-generated notifications for all user types.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Notification ID |
| `user_id` | BIGINT | FK → users.id | Recipient |
| `type` | ENUM | — | `APPLICATION_UPDATE`, `NEW_JOB`, `MESSAGE`, `SYSTEM` |
| `title` | VARCHAR(255) | — | Short notification title |
| `body` | TEXT | — | Full notification message |
| `reference_id` | BIGINT | — | ID of related entity (job, application, etc.) |
| `is_read` | BOOLEAN | DEFAULT FALSE | Read/unread flag |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Created timestamp |

---

### Table: `reviews`

Company reviews submitted by job seekers.

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Review ID |
| `employer_id` | BIGINT | FK → employer_profiles.id | Reviewed company |
| `reviewer_id` | BIGINT | FK → users.id | Who wrote the review |
| `rating` | TINYINT | CHECK (1–5) | Star rating 1 to 5 |
| `title` | VARCHAR(255) | — | Review headline |
| `body` | TEXT | — | Full review text |
| `is_anonymous` | BOOLEAN | DEFAULT FALSE | Hide reviewer identity |
| `created_at` | TIMESTAMP | DEFAULT NOW() | Submitted timestamp |

---

## 🌐 API Reference

> **Base URL:** `/api/v1`
> **Auth:** Bearer JWT token in `Authorization` header
> **Format:** `Content-Type: application/json`

---

### Authentication

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/auth/register` | Register new user (seeker or employer) | Public |
| `POST` | `/auth/login` | Login and receive JWT access + refresh tokens | Public |
| `POST` | `/auth/refresh` | Exchange refresh token for new access token | Public |
| `POST` | `/auth/logout` | Invalidate refresh token | User |
| `POST` | `/auth/forgot-password` | Send password reset email | Public |
| `POST` | `/auth/reset-password` | Reset password using emailed token | Public |
| `GET` | `/auth/verify-email` | Verify email address via token link | Public |

**POST `/auth/register` — Request Body:**
```json
{
  "email": "jane@example.com",
  "password": "SecurePass123!",
  "fullName": "Jane Doe",
  "role": "JOBSEEKER"
}
```

**POST `/auth/login` — Response:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### Users

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/users/me` | Get current user's profile | User |
| `PUT` | `/users/me` | Update name, phone, profile picture | User |
| `PUT` | `/users/me/password` | Change password (requires current password) | User |
| `POST` | `/users/me/avatar` | Upload profile picture (multipart/form-data) | User |
| `DELETE` | `/users/me` | Permanently delete own account | User |
| `GET` | `/admin/users` | List all users with pagination | Admin |
| `DELETE` | `/admin/users/{id}` | Ban or delete a user | Admin |

---

### Jobseeker Profile

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/jobseekers/me` | Get own seeker profile | Jobseeker |
| `PUT` | `/jobseekers/me` | Update headline, summary, location, salary, etc. | Jobseeker |
| `POST` | `/jobseekers/me/resume` | Upload resume PDF (multipart/form-data) | Jobseeker |
| `GET` | `/jobseekers/me/skills` | List own skills with proficiency | Jobseeker |
| `POST` | `/jobseekers/me/skills` | Add a skill to profile | Jobseeker |
| `DELETE` | `/jobseekers/me/skills/{skillId}` | Remove a skill from profile | Jobseeker |
| `GET` | `/jobseekers/{id}` | View a seeker's public profile | Employer |

**POST `/jobseekers/me/skills` — Request Body:**
```json
{
  "skillId": 42,
  "proficiency": "ADVANCED",
  "yearsUsed": 4
}
```

---

### Employer Profile

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/employers/me` | Get own company profile | Employer |
| `PUT` | `/employers/me` | Update company info (name, description, etc.) | Employer |
| `POST` | `/employers/me/logo` | Upload company logo (multipart/form-data) | Employer |
| `GET` | `/employers/{id}` | Get public employer/company profile | Public |
| `GET` | `/employers/{id}/reviews` | List reviews for a company | Public |
| `POST` | `/admin/employers/{id}/verify` | Grant verified badge to company | Admin |

---

### Jobs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/jobs` | Search and list jobs (filters, pagination, sort) | Public |
| `GET` | `/jobs/{id}` | Get job details (increments `views_count`) | Public |
| `POST` | `/jobs` | Create a new job listing | Employer |
| `PUT` | `/jobs/{id}` | Update job listing | Employer (owner) |
| `DELETE` | `/jobs/{id}` | Delete a job listing | Employer (owner) |
| `PATCH` | `/jobs/{id}/status` | Change status: ACTIVE / PAUSED / CLOSED | Employer (owner) |
| `GET` | `/employers/me/jobs` | List own job postings with stats | Employer |
| `GET` | `/jobs/recommended` | Get skill-matched job recommendations | Jobseeker |

**GET `/jobs` — Query Parameters:**

| Param | Type | Example | Description |
|---|---|---|---|
| `keyword` | string | `"Java Developer"` | Full-text search on title/description |
| `location` | string | `"Berlin"` | Filter by job location |
| `jobType` | ENUM | `FULL_TIME` | Filter by job type |
| `workMode` | ENUM | `REMOTE` | Filter by work mode |
| `experienceLevel` | ENUM | `SENIOR` | Filter by experience level |
| `salaryMin` | decimal | `50000` | Minimum salary filter |
| `salaryMax` | decimal | `120000` | Maximum salary filter |
| `skills` | string[] | `Java,Spring` | Filter by required skills |
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `20` | Page size |
| `sort` | string | `createdAt,desc` | Sort field and direction |

**POST `/jobs` — Request Body:**
```json
{
  "title": "Senior Java Backend Engineer",
  "description": "We are looking for...",
  "requirements": "5+ years Java experience...",
  "jobType": "FULL_TIME",
  "workMode": "HYBRID",
  "location": "Berlin, Germany",
  "salaryMin": 80000,
  "salaryMax": 110000,
  "currency": "EUR",
  "experienceLevel": "SENIOR",
  "deadline": "2025-06-30",
  "skillIds": [1, 5, 12]
}
```

---

### Applications

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/jobs/{jobId}/apply` | Submit application for a job | Jobseeker |
| `DELETE` | `/applications/{id}` | Withdraw own application | Jobseeker (owner) |
| `GET` | `/jobseekers/me/applications` | List own applications with current status | Jobseeker |
| `GET` | `/jobs/{jobId}/applications` | List all applicants for a job | Employer |
| `GET` | `/applications/{id}` | Get full application details | Owner |
| `PATCH` | `/applications/{id}/status` | Advance/decline application status | Employer |
| `PUT` | `/applications/{id}/notes` | Add/update private notes on an application | Employer |

**POST `/jobs/{jobId}/apply` — Request Body:**
```json
{
  "coverLetter": "I am excited to apply for...",
  "resumeUrl": "https://cdn.example.com/resume/jane-doe.pdf"
}
```

**PATCH `/applications/{id}/status` — Request Body:**
```json
{
  "status": "INTERVIEW"
}
```

---

### Saved Jobs

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/jobseekers/me/saved-jobs` | List all saved/bookmarked jobs | Jobseeker |
| `POST` | `/jobs/{jobId}/save` | Bookmark a job listing | Jobseeker |
| `DELETE` | `/jobs/{jobId}/save` | Remove a bookmarked job | Jobseeker |

---

### Skills

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/skills` | Search/list skills for autocomplete | Public |
| `POST` | `/admin/skills` | Add a new skill to master list | Admin |
| `DELETE` | `/admin/skills/{id}` | Remove a skill from master list | Admin |

**GET `/skills` — Query Parameters:**

| Param | Type | Example | Description |
|---|---|---|---|
| `q` | string | `"Spr"` | Prefix search for autocomplete |
| `category` | string | `"Backend"` | Filter by category |
| `page` | int | `0` | Pagination |
| `size` | int | `20` | Page size |

---

### Notifications

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/notifications` | Get own notifications (paginated) | User |
| `PATCH` | `/notifications/{id}/read` | Mark a single notification as read | User |
| `PATCH` | `/notifications/read-all` | Mark all notifications as read | User |
| `DELETE` | `/notifications/{id}` | Delete a notification | User |

---

### Reviews

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/employers/{id}/reviews` | Submit a review for a company | Jobseeker |
| `PUT` | `/reviews/{id}` | Edit own review | Jobseeker (owner) |
| `DELETE` | `/reviews/{id}` | Delete a review | Owner / Admin |
| `GET` | `/employers/{id}/reviews` | List all reviews for a company | Public |

**POST `/employers/{id}/reviews` — Request Body:**
```json
{
  "rating": 4,
  "title": "Great work culture, slow growth",
  "body": "The team is collaborative and management is transparent...",
  "isAnonymous": false
}
```

---

## 🔐 Authorization Roles

| Role | Description | Spring Security Expression |
|---|---|---|
| `Public` | No authentication required | No `@PreAuthorize` needed |
| `User` | Any authenticated user | `isAuthenticated()` |
| `Jobseeker` | Role = JOBSEEKER | `hasRole('JOBSEEKER')` |
| `Employer` | Role = EMPLOYER | `hasRole('EMPLOYER')` |
| `Admin` | Role = ADMIN | `hasRole('ADMIN')` |
| `Owner` | Resource owner check | Custom `@PreAuthorize` with SpEL + service check |

**Example owner check:**
```java
@PreAuthorize("hasRole('EMPLOYER') and @jobService.isOwner(#id, authentication.name)")
@DeleteMapping("/jobs/{id}")
public ResponseEntity<Void> deleteJob(@PathVariable Long id) { ... }
```

---

## 📌 Enums Reference

```java
// User role
enum Role { JOBSEEKER, EMPLOYER, ADMIN }

// Job type
enum JobType { FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE }

// Work mode
enum WorkMode { ON_SITE, REMOTE, HYBRID }

// Experience level
enum ExperienceLevel { ENTRY, MID, SENIOR, LEAD, EXECUTIVE }

// Job status
enum JobStatus { DRAFT, ACTIVE, PAUSED, CLOSED, EXPIRED }

// Application status
enum ApplicationStatus { APPLIED, SCREENING, INTERVIEW, OFFER, REJECTED, WITHDRAWN }

// Seeker availability
enum Availability { IMMEDIATE, ONE_MONTH, THREE_MONTHS, NOT_LOOKING }

// Skill proficiency
enum Proficiency { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }

// Company size
enum CompanySize { SIZE_1_10, SIZE_11_50, SIZE_51_200, SIZE_201_500, SIZE_500_PLUS }

// Notification type
enum NotificationType { APPLICATION_UPDATE, NEW_JOB, MESSAGE, SYSTEM }
```

---

## ⚙️ Spring Boot Implementation Notes

### Dependencies (`pom.xml`)
```xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  <dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
  </dependency>
  <dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
  </dependency>
</dependencies>
```

### Key Implementation Patterns

**1. JWT Authentication Filter**
```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        // Extract Bearer token → validate → set SecurityContext
    }
}
```

**2. Pagination on list endpoints**
```java
@GetMapping("/jobs")
public Page<JobResponse> searchJobs(JobSearchRequest filters, Pageable pageable) {
    return jobService.search(filters, pageable);
}
```

**3. 1:1 JPA Relationship (User ↔ JobseekerProfile)**
```java
@Entity
public class JobseekerProfile {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

**4. Auto-expire jobs via scheduler**
```java
@Scheduled(cron = "0 0 0 * * *") // midnight daily
public void expireDeadlinedJobs() {
    jobRepository.expireJobsPastDeadline(LocalDate.now());
}
```

**5. File upload (multipart)**
```java
@PostMapping("/jobseekers/me/resume")
public ResponseEntity<String> uploadResume(@RequestParam MultipartFile file) {
    String url = fileStorageService.upload(file, "resumes/");
    jobseekerService.updateResumeUrl(url);
    return ResponseEntity.ok(url);
}
```

### `application.yml` (key settings)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_portal
    username: ${DB_USER}
    password: ${DB_PASS}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true

jwt:
  secret: ${JWT_SECRET}
  access-token-expiry: 3600       # 1 hour
  refresh-token-expiry: 604800    # 7 days

springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

---

*Generated for Job Portal Backend — Spring Boot 3.x · Java 17 · Maven*
