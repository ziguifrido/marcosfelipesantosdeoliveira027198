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
| **Backend**    | Spring Boot REST API with WebSocket support.     | `8080`     | **URL**: http://localhost:8080<br>**API Docs**: http://localhost:8080/swagger-ui.html (if available) |

The `minio-setup` service will automatically create buckets named `album-cover` and `artist-profile-image` and set their access policies to public.

### API Documentation

The backend service includes OpenAPI specification located in `docs/openapi.yaml`. This file contains comprehensive documentation for all available REST endpoints including:

- Artist management endpoints with pagination support
- Album management endpoints with full CRUD operations
- Proper request/response schemas and validation rules

You can view the OpenAPI specification directly by opening the `docs/openapi.yaml` file or use tools like Postman or Insomnia to generate interactive documentation from the specification file.
