# Application Summary

This is a Spring Boot-based RESTful API application designed to manage users, 
    authentication, tasks, and task groups. 
    It provides a secure and modular backend system with role-based access control and rich functionality, 
    including user registration, login, task management, and grouping tasks into task groups. 
    The API follows best practices including validation, exception handling, and detailed OpenAPI/Swagger documentation.

## Key Features

- **Authentication API**
    - User login endpoint with validation and centralized error handling.
    - Secure authentication flow integrated with Spring Security.

- **User API**
    - User registration with input validation.
    - User deletion restricted to administrators.
    - User applies role changes to a user, restricted to administrators

- **Task API**
    - Create, update, and assign tasks to users.
    - Add tasks to task groups.
    - Role-based access control allowing users with "USER" or "ADMIN" roles to manage tasks.

- **Task Group API**
    - Create new task groups.
    - Retrieve paginated, sorted lists of task groups.
    - Role-based access control for users and admins.

- **Security**
    - Integration with Spring Security using role-based authorization.
    - Authentication details exposed through `@AuthenticationPrincipal`.
    - Secure endpoints protected via `@PreAuthorize` annotations.

- **Validation and Error Handling**
    - Input validation using Jakarta Validation (Bean Validation).
    - Centralized exception handling to return meaningful API responses.

- **Documentation**
    - OpenAPI/Swagger annotations to automatically generate interactive API documentation.
    - Clear API operation summaries and descriptions for ease of use.

- **Project Structure**
    - Organized controllers handling different domain concerns.
    - Services encapsulate business logic.
    - DTOs for request and response payloads, ensuring clean API contracts.
    - Logging integrated via Lombok’s `@Slf4j`.

## Getting Started

- Spring Boot 3.5, Java 21.
- Requires database configuration for user and task persistence.
- Security configured with role-based access using Spring Security.
- Run the application and access API documentation via Swagger UI at `/swagger-ui.html` or `/v3/api-docs`.
- Swagger URL: http://localhost:9090/swagger-ui/index.html

## Usage

- Register a new user via `/api/v1/users/register`.
- Authenticate using `/api/v1/auth/login`.
- Create, update, assign, and manage tasks with `/api/v1/tasks`.
- Manage task groups via `/api/v1/task-groups`.

This application serves as a robust foundation for task management systems requiring user authentication 
and role-based permissions, with extensibility for additional features or integrations.
