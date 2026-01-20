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

- **PostgreSQL**: A relational database.
- **pgAdmin**: A web interface for managing PostgreSQL.
- **MinIO**: An S3-compatible object storage service.

### Services

The `docker-compose.yml` file defines the following services:

| Service      | Description                                      | Port(s)    | Credentials / URL                                                                                           |
|--------------|--------------------------------------------------|------------|-------------------------------------------------------------------------------------------------------------|
| **PostgreSQL** | Relational database for storing data.            | `5432`     | **Database**: `discography_db`<br>**User**: `postgres-user`<br>**Password**: `postgres-password`            |
| **pgAdmin**    | Web-based administration console for PostgreSQL. | `8083`     | **Email**: `pgadmin@mail.com`<br>**Password**: `pgadmin-password`<br>**URL**: http://localhost:8083         |
| **MinIO**      | S3-compatible object storage.                    | `9000:9001`  | **User**: `minio-admin`<br>**Password**: `minio-admin-password`<br>**Admin Console**: http://localhost:9001 |

The `minio-setup` service will automatically create a bucket named `album-covers` and set its access policy to public.
