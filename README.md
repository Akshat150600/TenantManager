# Tenant Manager

A full-stack web application for managing tenant maintenance requests with role-based access control, built with Spring Boot and React.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start with Docker](#quick-start-with-docker)
- [Local Development Setup](#local-development-setup)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Troubleshooting](#troubleshooting)

## ✨ Features

### Tenant Features

- 🔐 Secure authentication with JWT tokens
- 📝 Submit maintenance requests with department categorization
- 📊 View personal maintenance request history
- 🔔 Real-time request status updates

### Admin Features

- 👥 View all maintenance requests from all tenants
- 🔍 Filter requests by status (OPEN, IN_PROGRESS, RESOLVED, REJECTED)
- 🏢 Filter requests by department (PLUMBING, ELECTRICAL, HVAC, etc.)
- ✅ Approve or reject maintenance requests
- 📈 Update request status

### Technical Features

- 🚀 Redis caching for improved performance
- 🔒 BCrypt password encryption
- 🎯 Role-based access control (ADMIN/TENANT)
- 📖 Swagger/OpenAPI documentation
- 🐳 Fully containerized with Docker
- ✅ Comprehensive unit and integration tests

## 🛠️ Tech Stack

### Backend

- **Framework:** Spring Boot 3.5.7
- **Language:** Java 17
- **Database:** PostgreSQL 15
- **Cache:** Redis 7
- **Security:** Spring Security + JWT (jjwt 0.11.5)
- **ORM:** Hibernate/JPA
- **Documentation:** SpringDoc OpenAPI 2.3.0
- **Build Tool:** Maven 3.9.5
- **Testing:** JUnit 5, Mockito

### Frontend

- **Library:** React 18
- **Routing:** React Router v7.9.5
- **HTTP Client:** Axios 1.13.2
- **Styling:** CSS Modules
- **Build Tool:** Create React App
- **Web Server:** Nginx (in production)

### DevOps

- **Containerization:** Docker & Docker Compose
- **CI/CD Ready:** Multi-stage Dockerfile builds

## 🏗️ Architecture

```
┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│   React     │─────▶│  Spring Boot │─────▶│ PostgreSQL   │
│   Frontend  │◀─────│   Backend    │◀─────│   Database   │
└─────────────┘      └──────────────┘      └──────────────┘
                            │
                            ▼
                     ┌──────────────┐
                     │    Redis     │
                     │    Cache     │
                     └──────────────┘
```

### Security Flow

1. User logs in → Backend validates credentials
2. Backend generates JWT token + stores in Redis
3. Frontend stores token in localStorage
4. All requests include JWT in Authorization header
5. Backend validates token against Redis on each request
6. Token expires after 10 hours or on logout

### Caching Strategy

- **Users Cache:** 1 hour TTL
- **Maintenance Requests:** 5 minutes TTL
- **Valid Tokens:** 10 hours TTL

## 📦 Prerequisites

### For Docker (Recommended)

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) for Windows
- WSL 2 (Windows Subsystem for Linux)

### For Local Development

- Java JDK 17+
- Node.js 20+
- PostgreSQL 15+
- Redis 7+
- Maven 3.9+ (or use included Maven wrapper)

## 🚀 Quick Start with Docker

### 1. Clone the Repository

```bash
git clone <repository-url>
cd tenantmanager
```

### 2. Start All Services

```bash
# Build and start all containers
docker compose up --build

# Or run in background
docker compose up -d --build
```

### 3. Access the Application

| Service         | URL                                   |
| --------------- | ------------------------------------- |
| **Frontend**    | http://localhost:3000                 |
| **Backend API** | http://localhost:8080                 |
| **Swagger UI**  | http://localhost:8080/swagger-ui.html |
| **API Docs**    | http://localhost:8080/api-docs        |

### 4. Default Users

| Username | Password    | Role   |
| -------- | ----------- | ------ |
| admin    | admin123    | ADMIN  |
| tenant1  | password123 | TENANT |

### 5. Stop Services

```bash
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

## 💻 Local Development Setup

### Backend Setup

1. **Install PostgreSQL and Redis**

   - PostgreSQL: https://www.postgresql.org/download/
   - Redis: https://redis.io/download/ (or use Docker)

2. **Create Database**

   ```sql
   CREATE DATABASE tenantmanager;
   ```

3. **Configure Application**
   Update `tenantmanagerbackend/src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/tenantmanager
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run Backend**
   ```bash
   cd tenantmanagerbackend
   ./mvnw.cmd spring-boot:run
   ```

### Frontend Setup

1. **Install Dependencies**

   ```bash
   cd tenantmanagerui
   npm install
   ```

2. **Start Development Server**

   ```bash
   npm start
   ```

3. **Access Application**
   - Frontend: http://localhost:3000
   - Backend: http://localhost:8080

## 📚 API Documentation

### Swagger/OpenAPI

Access interactive API documentation at: http://localhost:8080/swagger-ui.html

### Authentication Endpoints

#### POST `/api/auth/signin`

Login with username and password

```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "username": "admin",
    "role": "ADMIN"
  }
}
```

#### POST `/api/auth/logout`

Logout (requires JWT token in header)

```
Authorization: Bearer <token>
```

### Maintenance Request Endpoints

#### POST `/api/maintenance/create` (TENANT only)

Create a new maintenance request

```json
{
  "unitNumber": "101",
  "department": "PLUMBING",
  "description": "Leaking faucet in kitchen"
}
```

#### GET `/api/admin/maintenance` (ADMIN only)

Get all maintenance requests with optional filters

- Query params: `status`, `department`

#### PUT `/api/admin/maintenance/{id}/approve` (ADMIN only)

Approve a maintenance request

#### PUT `/api/admin/maintenance/{id}/reject` (ADMIN only)

Reject a maintenance request

## 🧪 Testing

### Run All Tests

```bash
cd tenantmanagerbackend
./mvnw.cmd test
```

### Run Specific Test Class

```bash
./mvnw.cmd test -Dtest=UserServiceTest
```

### Generate Coverage Report (with JaCoCo)

```bash
./mvnw.cmd clean test jacoco:report
```

Report available at: `target/site/jacoco/index.html`

### Test Coverage

- Unit Tests: Service layer and Repository layer
- Integration Tests: Controller endpoints with full Spring context
- Current Coverage: ~55%+

## 📁 Project Structure

```
tenantmanager/
├── docker-compose.yml
├── tenantmanagerbackend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/projects/tenantmanager/
│   │   │   │   ├── config/           # Configuration classes
│   │   │   │   ├── controller/       # REST controllers
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── model/            # JPA entities
│   │   │   │   ├── repository/       # JPA repositories
│   │   │   │   ├── security/         # JWT & Security config
│   │   │   │   └── service/          # Business logic
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── application-docker.properties
│   │   └── test/
│   │       └── java/                 # Unit & Integration tests
│   ├── Dockerfile
│   └── pom.xml
├── tenantmanagerui/
│   ├── public/
│   ├── src/
│   │   ├── components/               # React components
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── TenantDashboard.jsx
│   │   │   ├── LoginPage.jsx
│   │   │   └── Header.jsx
│   │   ├── css/                      # Component styles
│   │   ├── App.js
│   │   └── index.js
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
└── README.md
```

## 🔧 Environment Variables

### Docker Compose (Production)

Set in `docker-compose.yml`:

```yaml
SPRING_PROFILES_ACTIVE: docker
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/tenantmanager
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: akshat12345
SPRING_DATA_REDIS_HOST: redis
```

### Local Development

Set in `tenantmanagerbackend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tenantmanager
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.data.redis.host=localhost
jwt.secret=yourSecretKey123!@#$%^&*()_+
```

## 🐛 Troubleshooting

### Docker Issues

**Docker Desktop not running:**

```bash
# Start Docker Desktop and wait for it to fully start
# Check status:
docker ps
```

**Port already in use:**

```bash
# Change ports in docker-compose.yml
ports:
  - "3001:80"    # Frontend
  - "8081:8080"  # Backend
```

**Clear Redis cache:**

```bash
docker exec -it tenantmanager-redis redis-cli FLUSHALL
```

**View container logs:**

```bash
docker compose logs -f backend
docker compose logs -f frontend
```

### Database Issues

**Connect to PostgreSQL:**

```bash
docker exec -it tenantmanager-postgres psql -U postgres -d tenantmanager
```

**Reset database:**

```bash
docker compose down -v
docker compose up -d postgres
```

### Backend Issues

**Rebuild backend:**

```bash
docker compose build --no-cache backend
docker compose up -d backend
```

**Check backend logs:**

```bash
docker compose logs -f backend
```

### Frontend Issues

**Clear npm cache and rebuild:**

```bash
cd tenantmanagerui
npm cache clean --force
npm install
docker compose build --no-cache frontend
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful UI library
- Docker for containerization simplicity
