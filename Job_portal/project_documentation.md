# Job Portal Management System - Comprehensive Documentation

## 1. Project Overview
The **Job Portal Management System** is a robust, full-stack web application designed to bridge the gap between employers and job seekers. It modernizes the recruitment process by providing a centralized, automated platform where employers can post job openings with precise geographic locations, and candidates can securely register, browse, and apply for these opportunities. A core focus of the project is security (via email OTP verification) and user experience (via interactive maps and modern UI design).

---

## 2. Technology Stack & How It Works

The system is built on a modern, robust Full-Stack architecture utilizing Java for the backend and standard web technologies for the frontend.

### Frontend (Client-Side)
*   **HTML5 & CSS3:** Provides the core structure and styling of the application. It utilizes a modern "glassmorphism" aesthetic with animated gradients to provide a premium feel.
*   **JavaScript (Vanilla JS):** Handles client-side interactivity, DOM manipulation, form validation, and powers the floating AI Chatbot widget.
*   **Thymeleaf:** A modern server-side Java template engine. **How it works:** Before a webpage is sent to the user's browser, Thymeleaf intercepts the HTML file on the server, reads dynamic data passed from the Spring Controllers (like a user's name or a list of jobs), injects that data into the HTML tags, and sends a fully rendered, dynamic webpage to the client.
*   **Leaflet.js & OpenStreetMap:** Open-source JavaScript libraries. **How it works:** When an employer posts a job, the Leaflet map renders on the page. The user can click to drop a pin. JavaScript captures the exact latitude and longitude, converts it to an address (Reverse Geocoding), and sends this data to the backend to be stored.

### Backend (Server-Side)
*   **Java 17:** The foundational, object-oriented programming language used for all backend business logic.
*   **Spring Boot (v3+):** The core framework holding the application together. **How it works:** It provides auto-configuration, meaning developers don't have to write boilerplate setup code. It uses an embedded **Apache Tomcat** web server, meaning the application can run standalone on port `8080` without needing external server software installations.
*   **Spring Security:** The security layer of the application. **How it works:** It intercepts every incoming HTTP request. If a user tries to access a restricted page (like posting a job) without being logged in or without the `EMPLOYER` role, Spring Security blocks the request. It also uses **BCrypt** hashing algorithms to encrypt user passwords before saving them to the database, ensuring that even if the database is breached, passwords remain unreadable.
*   **Spring Boot Starter Mail (JavaMailSender):** The email module. **How it works:** It connects to Google's SMTP servers using application-specific passwords. When a user registers, this service automatically generates a random 6-digit OTP code, constructs an email payload, and dispatches it in real-time to verify the user's identity.

### Database
*   **MySQL:** A relational database management system. **How it works:** It stores all persistent data in heavily structured, normalized tables (Users, Jobs, Applications) ensuring data integrity and fast retrieval.
*   **Spring Data JPA & Hibernate:** The ORM (Object-Relational Mapping) framework. **How it works:** Instead of writing raw, complex SQL queries (`INSERT INTO...`), JPA allows you to create standard Java classes (Entities). Hibernate automatically translates these Java classes into MySQL tables and translates basic Java method calls (like `repository.save(user)`) into secure SQL queries behind the scenes.

---

## 3. Application Architecture & Data Flow

The project strictly follows the **MVC (Model-View-Controller)** design pattern.

1.  **View (Thymeleaf/HTML):** The user clicks "Register" on the frontend browser.
2.  **Controller:** The `AuthController` receives this HTTP POST request. It extracts the data (email, password) and passes it down to the Service layer.
3.  **Service:** The `UserService` receives the data. It applies business logic: it checks if the email already exists, encrypts the password, generates a 6-digit OTP, and calls the `EmailService` to send the OTP.
4.  **Repository:** Once the user inputs the correct OTP, the Service tells the `UserRepository` to save the user.
5.  **Model/Database:** The Repository uses Spring Data JPA to execute an SQL `INSERT` statement, saving the `User` Entity into the MySQL database.
6.  **Response:** The Controller redirects the user to the Login page via the View.

---

## 4. Package Structure & Core Classes

The codebase is organized modularly by function to ensure separation of concerns.

### `com.jobportal.entity` (The Models)
These classes represent the structure of your database tables.
*   **`User.java`**: Represents the `users` table. Contains fields for `id`, `email`, `password` (hashed), `role` (STUDENT or EMPLOYER), and `isVerified` (boolean for OTP status).
*   **`Job.java`**: Represents the `jobs` table. Contains `id`, `title`, `description`, `location` (string/coordinates from the map), and `requiredSkills`.
*   **`Application.java`**: A mapping entity representing the `applications` table. It establishes a Many-To-One relationship, linking a specific `User` (the candidate) to a specific `Job`, and stores the path to their uploaded resume.

### `com.jobportal.repository` (The Data Access Layer)
These interfaces extend `JpaRepository`, automatically giving you access to database operations (save, findById, delete) without writing SQL.
*   **`UserRepository.java`**: Contains custom query methods like `findByEmail(String email)`.
*   **`JobRepository.java`**: Handles fetching lists of jobs for the candidate dashboard.
*   **`ApplicationRepository.java`**: Handles fetching all applications submitted for a specific employer's job.

### `com.jobportal.service` (The Business Logic Layer)
This is where the actual "thinking" and processing of the application happens.
*   **`UserService.java`**: Handles hashing passwords with BCrypt, managing the temporary storage of OTPs (often using a HashMap or Cache), and validating if the entered OTP matches the generated one.
*   **`EmailService.java`**: Contains the `sendOtpEmail()` method. It injects `JavaMailSender` to construct the subject, body, and recipient of the verification emails.
*   **`JobService.java`**: Handles the logic of validating job data before saving it, and retrieving jobs to be displayed.
*   **`ApplicationService.java`**: Manages the complex logic of accepting a Multipart File (resume pdf), saving it locally to the server's file system, and saving the file path string to the database.

### `com.jobportal.controller` (The Routing Layer)
These classes listen for URLs and direct traffic.
*   **`AuthController.java`**: Listens for `/login`, `/register`, and `/verify-otp`. It handles unauthenticated traffic.
*   **`JobController.java`**: Listens for `/dashboard`, `/post-job`, and `/job-details`. It handles authenticated traffic. If a student hits `/post-job`, this controller (backed by Spring Security) will deny them access.
*   **`ApplicationController.java`**: Listens for the "Apply Now" button clicks and handles the display of the `/applicants` dashboard for employers.

### `com.jobportal.config` (The Configurations)
*   **`SecurityConfig.java`**: The most critical configuration file. It sets up the `SecurityFilterChain`. It explicitly defines which URLs are public (like login/register) and which require specific roles (like `.requestMatchers("/post-job/**").hasAuthority("EMPLOYER")`). It also defines the `BCryptPasswordEncoder` bean.
*   **`DataSeeder.java`**: (Optional) A class that runs exactly once when the application starts up, used to inject default mock jobs or a default Admin account into the database for testing purposes.
