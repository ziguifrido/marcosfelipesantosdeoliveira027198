# SEPLAG-MT 2026 Backend Challenge - Marcos Oliveira

![GitHub top language](https://img.shields.io/github/languages/top/ziguifrido/marcosfelipesantosdeoliveira027198)
![GitHub License](https://img.shields.io/github/license/ziguifrido/marcosfelipesantosdeoliveira027198)
![GitHub Issues or Pull Requests](https://img.shields.io/github/issues/ziguifrido/marcosfelipesantosdeoliveira027198)
![GitHub last commit](https://img.shields.io/github/last-commit/ziguifrido/marcosfelipesantosdeoliveira027198)

Backend API solution (Java/Spring Boot) for the SEPLAG-MT 2026 challenge. Implements JWT authentication, image upload to MinIO, flyway migration, rate limiting, health check, websocket notification and comprehensive API documentation.

- **Name**: Marcos Felipe Santos de Oliveira
- **Registration Number**: 16461
- **Position**: Analista de Tecnologia da Informação - Engenheiro da Computação Sênior

## Documentation

### Architecture Documentation
For detailed information about the Domain-Driven Design (DDD) architecture, including strategic mapping, bounded contexts, and layer intentions, see the [Strategic Mapping - DDD](docs/Strategic-Mapping-DDD.md) documentation.

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

| Service              | Description                                        | Port(s) | Credentials / URL                                                                                                                                                                     |
|----------------------|----------------------------------------------------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **PostgreSQL**       | Relational database for storing data.              | `5432`  | **Database**: `discography_db`<br>**User**: `postgres-user`<br>**Password**: `postgres-password`                                                                                      |
| **pgAdmin**          | Web-based administration console for PostgreSQL.   | `8083`  | **Email**: `pgadmin@mail.com`<br>**Password**: `pgadmin-password`<br>**URL**: http://localhost:8083                                                                                   |
| **MinIO**            | S3-compatible object storage (internal).           | `9001`  | **User**: `minio-admin`<br>**Password**: `minio-admin-password`<br>**Admin Console**: http://localhost:9001                                                                           |
| **nginx-minio**      | Reverse proxy for MinIO presigned URLs.            | `9000`  | Proxies requests from localhost:9000 to MinIO container                                                                                                                               |
| **Backend**          | Spring Boot REST API.                              | `8080`  | **URL**: http://localhost:8080<br>**Swagger UI**: http://localhost:8080/swagger-ui.html<br>**Scalar**: http://localhost:8080/scalar<br>**WebSocket**: ws://localhost:8080/ws-registry |

The `minio-setup` service will automatically create buckets named `album-cover` and `artist-profile-image` and set their access policies to public.

### Testing

To test the API endpoints there are two options available:

- Swagger UI:  http://localhost:8080/swagger-ui.html
- Scalar: http://localhost:8080/scalar

There are two default users, with different roles, ready for testing:

| Username | Password | Role  | Permissions            |
|----------|----------|-------|------------------------|
| admin    | admin123 | ADMIN | GET, POST, PUT, DELETE |
| user     | user123  | USER  | GET                    |

### MinIO Nginx Proxy

To ensure presigned URLs work correctly from both the backend and browser, the setup uses an **nginx reverse proxy**:

**The Problem:**
- Backend connects to MinIO using the internal Docker hostname (`minio.dev.local:9000`)
- MinIO generates presigned URLs signed for that hostname
- Browser cannot resolve `minio.dev.local`, and changing the URL hostname invalidates the signature

**The Solution:**
An nginx proxy container listens on `localhost:9000` and proxies requests to the MinIO container:

```
Browser/Backend → localhost:9000 → nginx → minio.dev.local:9000
```

**Configuration:**
- MinIO is configured with `MINIO_SERVER_URL: http://localhost:9000` to generate presigned URLs with localhost
- The backend connects to `http://localhost:9000` (resolved via `extra_hosts: - "localhost:host-gateway"`)
- Both browser and backend use the same hostname, so signatures remain valid

**Key Files:**
- `nginx-minio.conf` - Nginx configuration
- `docker-compose.yml` - Service orchestration

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
- **Authentication**: Authenticate to gains access to the other endpoints
- **Artist Management**: Create, read, update, delete artists with pagination support and image uploads
- **Album Management**: Full CRUD operations for albums with release date filtering and cover image uploads
- **Regional Synchronization**: Triggers the Regional synchronization routine
- **Request/Response Models**: Detailed schemas with validation rules, examples, and field descriptions
- **HTTP Status Codes**: Complete list of possible responses (200, 201, 204, 400, 404, 415, 429, 500)

The API documentation is automatically generated from the Spring annotations (`@Operation`, `@ApiResponse`, `@Schema`) in the controller and DTO classes.

## Rate Limiting

The API implements rate limiting using **Bucket4J** to protect against abuse and ensure fair usage across all clients.

### Configuration

- **Limit**: 10 requests per minute per IP address
- **Algorithm**: Token bucket with interval refill
- **Library**: Bucket4J 8.16.0 

### How It Works

1. Each unique IP address gets its own token bucket
2. The bucket starts with 10 tokens and refills 10 tokens every minute
3. Each API request consumes 1 token
4. When the bucket is empty (0 tokens), subsequent requests receive HTTP 429 (Too Many Requests)
5. The filter runs with highest precedence, executing before security filters

### Implementation Details

The rate limiting is implemented via `RateLimitFilter` (`infrastructure/security/RateLimitFilter.java`):

- Automatically registered as a Servlet Filter with `@Order(Ordered.HIGHEST_PRECEDENCE)`
- Uses `Bandwidth.builder()` with `refillIntervally()` (non-deprecated API)
- Supports `X-Forwarded-For` header for proxy environments
- In-memory bucket storage using `ConcurrentHashMap`

### Response When Limit Exceeded

```json
{
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again later."
}
```

**Status Code**: 429

## Database Migrations (Flyway)

The application uses **Flyway** for database version control and migration management, ensuring consistent schema evolution across all environments.

### Overview

- **Library**: Flyway 11.x with PostgreSQL support
- **Location**: `backend/src/main/resources/db/migration/`
- **Naming Convention**: `V{version}__{Description}.sql`
- **Strategy**: Versioned migrations with automatic execution on startup
- **Baseline**: Migrations start from empty database

### Migration Files

| Version | File                                    | Description                                                            |
|---------|-----------------------------------------|------------------------------------------------------------------------|
| **V1**  | `V1__Create_Tables.sql`                 | Initial schema creation - users, artists, albums, and junction tables  |
| **V2**  | `V2__Default_Users_and_Sample_Data.sql` | Default users (admin/user) and sample data (Linkin Park similar bands) |

### Schema Structure

#### V1 - Database Schema

Creates the following tables:

- **`users`**: JWT authentication table with username, password (BCrypt), and role
- **`artist`**: Artist information with profile image metadata (bucket, object key, content type)
- **`album`**: Album information with cover image metadata and release date
- **`album_artists`**: Many-to-many junction table linking albums to artists

**Indexes created**:
- `idx_artist_name` - For fast artist searches
- `idx_album_title` - For fast album searches
- `idx_album_release_date` - For date filtering
- `idx_users_username` - For authentication lookups

#### V2 - Sample Data

**Default Users**:

| Username | Password | Role  |
|----------|----------|-------|
| `admin`  | admin123 | ADMIN |
| `user`   | user123  | USER  |

**Sample Artists**:
- Linkin Park, Breaking Benjamin, Three Days Grace, Seether, Disturbed, Chevelle, Staind, Papa Roach

**Sample Albums**:
- Linkin Park: Hybrid Theory, Meteora, Minutes to Midnight
- Breaking Benjamin: Phobia, Dear Agony
- Three Days Grace: One-X, Life Starts Now

### Configuration

Flyway is auto-configured by Spring Boot with these properties:

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

### Migration Behavior

1. **On Application Startup**: Flyway automatically checks and executes pending migrations
2. **Version Tracking**: Successfully executed migrations are recorded in `flyway_schema_history` table
3. **Validation**: Migrations are validated for integrity before execution
4. **Idempotency**: All migrations use `IF NOT EXISTS` to prevent errors on re-runs
5. **Rollback**: No automatic rollback; migrations must be carefully tested before deployment

### Best Practices Followed

- **Idempotent DDL**: All `CREATE` statements use `IF NOT EXISTS`
- **Explicit Constraints**: Foreign keys with `ON DELETE CASCADE` for referential integrity
- **Performance**: Strategic indexes on frequently queried columns
- **Data Integrity**: UUID primary keys and proper data types
- **Documentation**: Each migration file includes descriptive comments

### Monitoring

View migration status via Actuator endpoint:
- **URL**: http://localhost:8080/actuator/flyway
- Shows: Applied migrations, pending migrations, execution history, success/failure status

## Health Check

The application includes comprehensive health monitoring through **Spring Boot Actuator**.

### Endpoints

| Endpoint               | Description                           | Access |
|------------------------|---------------------------------------|--------|
| `/actuator/health`     | Overall application health status     | Public |
| `/actuator/info`       | Application information               | Public |
| `/actuator/metrics`    | Detailed metrics (JVM, HTTP, etc.)    | Public |
| `/actuator/prometheus` | Prometheus-compatible metrics         | Public |
| `/actuator/flyway`     | Database migration status and history | Public |

### Health Indicators

The health endpoint provides status for:
- **Database**: PostgreSQL connection status
- **Disk Space**: Available storage
- **Application**: Overall system health

### Example Response

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500107862016,
        "free": 354234232832,
        "threshold": 10485760
      }
    }
  }
}
```

### Access

All actuator endpoints are publicly accessible at:
- **Base URL**: http://localhost:8080/actuator
- **Health**: http://localhost:8080/actuator/health

**Flyway Endpoint**: http://localhost:8080/actuator/flyway
- View database migration status, history, and pending changes

## WebSocket Real-Time Notifications

The application implements a **WebSocket notification system** using STOMP protocol to provide real-time updates when new albums are created.

### Overview

- **Protocol**: STOMP (Simple Text Oriented Messaging Protocol)
- **Message Broker**: Simple in-memory broker for topic-based messaging
- **Architecture**: Domain-Driven Design (DDD) with clean separation of concerns
- **Transaction Awareness**: Notifications only sent after successful database commit

### WebSocket Endpoints

| Endpoint        | Description                                       | Access       |
|-----------------|---------------------------------------------------|--------------|
| `/ws-registry`  | WebSocket handshake endpoint with SockJS fallback | WebSocket    |
| `/topic/albums` | Topic for receiving album creation notifications  | Subscription |

### Message Format

When a new album is created, clients receive a message on `/topic/albums` with the following structure:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "title": "Hybrid Theory",
  "releaseDate": "2000-10-24",
  "coverUrl": "http://localhost:9000/album-cover/album_123.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256...",
  "artistNames": ["Linkin Park"]
}
```

### Architecture Implementation

The WebSocket system follows **Domain-Driven Design (DDD)** principles:

1. **Domain Layer** (`domain/event/`):
   - `AlbumCreatedEvent`: Domain event carrying album data after creation

2. **Application Layer** (`application/service/`):
   - `AlbumService`: Publishes `AlbumCreatedEvent` using `ApplicationEventPublisher` after successful album persistence

3. **Infrastructure Layer** (`infrastructure/`):
   - `WebSocketConfig`: STOMP broker configuration with `/app` prefix and `/topic` broker
   - `AlbumNotificationHandler`: Listens for `AlbumCreatedEvent` and broadcasts to `/topic/albums` using `@TransactionalEventListener(phase = AFTER_COMMIT)`

### Key Features

- **Transaction Safety**: Notifications only sent after database transaction commit
- **Decoupled Design**: Domain events separate business logic from messaging concerns
- **Clean Architecture**: Infrastructure layer handles messaging protocol, keeping domain pure
- **Payload Consistency**: Uses `AlbumResponseDTO` for consistent API and WebSocket messages
- **Error Resilience**: Automatic reconnection via SockJS fallback

## License

- [MIT](LICENSE)