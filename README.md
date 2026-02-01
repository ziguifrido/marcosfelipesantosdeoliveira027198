# SEPLAG-MT 2026 Full Stack Challenge - Marcos Oliveira

Senior Full Stack solution (Java/Angular) architected with DDD for the SEPLAG-MT 2026 challenge. Features real-time WebSocket notifications, Facade pattern, and O(n) data synchronization.

## Getting Started

This project is containerized using Docker and can be easily set up for local development.

### Prerequisites

- docker
- docker-compose

### Running the Development Environment

To start all the services, run the following command from the root of the project:

```bash
docker-compose up -d
```

This will start the following services in the background:

- **PostgreSQL**: A relational database with health checks.
- **pgAdmin**: A web interface for managing PostgreSQL.
- **MinIO**: An S3-compatible object storage service.
- **Backend**: Spring Boot application running on port 8080.

### Services

The `docker-compose.yml` file defines the following services:

| Service      | Description                                      | Port(s)    | Credentials / URL                                                                                           |
|--------------|--------------------------------------------------|------------|-------------------------------------------------------------------------------------------------------------|
| **PostgreSQL** | Relational database for storing data.            | `5432`     | **Database**: `discography_db`<br>**User**: `postgres-user`<br>**Password**: `postgres-password`            |
| **pgAdmin**    | Web-based administration console for PostgreSQL. | `8083`     | **Email**: `pgadmin@mail.com`<br>**Password**: `pgadmin-password`<br>URL**: http://localhost:8083         |
| **MinIO**      | S3-compatible object storage.                    | `9000:9001`  | **User**: `minio-admin`<br>**Password**: `minio-admin-password`<br>**Admin Console**: http://localhost:9001 |
| **Backend**    | Spring Boot REST API with WebSocket support.     | `8080`     | **URL**: http://localhost:8080<br>**Swagger UI**: http://localhost:8080/swagger-ui.html<br>**Scalar**: http://localhost:8080/scalar |

The `minio-setup` service will automatically create buckets named `album-cover` and `artist-profile-image` and set their access policies to public.

### API Documentation

The backend service automatically generates interactive API documentation from the annotated controllers using **SpringDoc OpenAPI**. Two modern interfaces are available:

#### 1. Swagger UI (OpenAPI 3.0)
**URL**: http://localhost:8080/swagger-ui.html

Swagger UI provides a classic interactive interface for exploring and testing the API:
- Browse all available endpoints organized by tags (Artists, Albums)
- View detailed request/response schemas with examples
- Execute requests directly from the browser with "Try it out" feature
- Download the OpenAPI specification in JSON or YAML format
- See all HTTP status codes and validation rules

#### 2. Scalar
**URL**: http://localhost:8080/scalar

Scalar offers a modern, beautiful alternative API documentation interface:
- Clean, intuitive design with dark/light mode support
- Interactive request builder with syntax highlighting
- Real-time response visualization
- Nested schema exploration
- Better mobile responsiveness

Both interfaces provide complete documentation for:
- **Artist Management**: Create, read, update, delete artists with pagination support and image uploads
- **Album Management**: Full CRUD operations for albums with release date filtering and cover image uploads
- **Request/Response Models**: Detailed schemas with validation rules, examples, and field descriptions
- **HTTP Status Codes**: Complete list of possible responses (200, 201, 204, 400, 404, 415)

The API documentation is automatically generated from the Spring annotations (`@Operation`, `@ApiResponse`, `@Schema`) in the controller and DTO classes.
