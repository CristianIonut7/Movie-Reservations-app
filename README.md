# Movie Reservation App

A full-stack cinema reservation system that allows users to browse movies, view showtimes, and book seats. Administrators can manage movies, users and schedules.

![Application Dashboard Placeholder](path/to/dashboard-screenshot.png)
*Replace this with a screenshot of the main dashboard*

## Features

*   **User Authentication**: Secure registration and login for clients and admins.
*   **Movie Management**: Browse current listings, view details, and search by genre.
*   **Interactive Booking**: Select cinema rooms and specific seats visually.
*   **Role-Based Access**:
    *   **Clients**: Book tickets, view history, manage profile.
    *   **Admins**: Add movies, manage showtimes, configure rooms.

## Technologies Used

*   **Backend**: Java 17, Spring Boot 3.5.7, Hibernate/JPA
*   **Frontend**: Angular 20.3.0, TypeScript, SCSS
*   **Database**: Microsoft SQL Server 2022
*   **Build Tools**: Maven (Backend), npm/Angular CLI (Frontend)

## Prerequisites

Ensure you have the following installed:

*   [Java JDK 17](https://www.oracle.com/java/technologies/downloads/#java17) or higher
*   [Node.js](https://nodejs.org/) (LTS version recommended)
*   [Microsoft SQL Server](https://www.microsoft.com/en-us/sql-server/sql-server-downloads) (Express or Standard)

## Installation & Setup

### 1. Database Setup

1.  Open SQL Server Management Studio (SSMS) or your preferred SQL tool.
2.  Create a new database named `MovieReservationDB` (or run the scripts directly as they might handle it).
3.  Execute the script `database/01_create_tables.sql` to create the schema.
4.  Execute the script `database/02_seed_tables.sql` to populate initial data.

![Database Schema Placeholder](path/to/db-schema-screenshot.png)
*Replace with a screenshot of your database diagram or tables*

### 2. Backend Setup

1.  Navigate to the `backend` directory:
    ```bash
    cd backend
    ```
2.  Open `src/main/resources/application.properties` and configure your database credentials:
    ```properties
    spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS01;databaseName=MovieReservationDB;trustServerCertificate=true
    spring.datasource.username=YOUR_USERNAME  # Default: sa
    spring.datasource.password=YOUR_PASSWORD  # Default in project: aplicatienebuna
    ```
    > **Note**: The default password in the project is `aplicatienebuna`. Update it to match your local SQL Server instance.

3.  Run the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    ```
    The backend server will start on port 8080 (default).

### 3. Frontend Setup

1.  Navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm start
    ```
    Or if you have Angular CLI installed globally:
    ```bash
    ng serve
    ```
4.  Open your browser and navigate to `http://localhost:4200`.

![Login Page Placeholder](path/to/login-screenshot.png)
*Replace with a screenshot of the Login page*

## Application Screenshots

Here you can add more screenshots to showcase the application flow.

### Movie Selection
![Movie Selection Placeholder](path/to/movie-selection-screenshot.png)

### Seat Booking
![Seat Booking Placeholder](path/to/seat-booking-screenshot.png)
