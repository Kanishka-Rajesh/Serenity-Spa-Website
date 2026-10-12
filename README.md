# Serenity Spa — Full-Stack Application

A comprehensive, production-grade full-stack web application for **Serenity Spa & Wellness Retreat**, retaining the original frontend design while introducing a modern Spring Boot + Java 21 backend and React + TypeScript client.

---

## Architecture & Technology Stack

### Backend
- **Language**: Java 21 LTS
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 with stateless JWT (HMAC-SHA256) & BCrypt password hashing
- **Persistence**: Spring Data JPA & Hibernate
- **Database**: MySQL 8.0 (production/default) / H2 In-Memory (development & testing fallback)
- **Validation**: Bean Validation (Jakarta Validation)
- **Documentation**: OpenAPI 3.0 & Swagger UI (`springdoc-openapi`)
- **Testing**: JUnit 5, Mockito

### Frontend
- **Framework**: React 19 + TypeScript
- **Bundler & Server**: Vite
- **Routing**: React Router DOM 7
- **HTTP Client**: Axios with JWT request interceptor & 401 handling
- **Styling**: Original serif aesthetic (Cormorant Garamond), custom CSS faithfully preserved

---

## Prerequisites

1. **Java Development Kit (JDK)**: Java 21 (`java -version`)
2. **Apache Maven**: Version 3.9+ (`mvn -version`)
3. **Node.js**: Version 18+ & npm (`node -v`, `npm -v`)
4. **MySQL Server**: MySQL 8.0+ running on port 3306

---

## Quick Start Guide

### 1. Database Setup (MySQL)

Create the MySQL database using the provided schema script or let Hibernate create tables automatically on first startup:

```bash
mysql -u root -p < backend/schema.sql
```

Configure your MySQL connection in `backend/src/main/resources/application-mysql.properties` or provide environment variables:

```bash
export DB_URL="jdbc:mysql://localhost:3306/serenity_spa?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export DB_USERNAME="root"
export DB_PASSWORD="your_password"
```

> **Note**: For instant evaluation without configuring MySQL credentials, activate the `h2` profile by passing `-Dspring.profiles.active=h2`.

---

### 2. Backend Execution

```powershell
cd backend
mvn clean package -DskipTests
java "-Dspring.profiles.active=h2" -jar "target\serenity-spa-backend-1.0.0.jar"
```

The Spring Boot application starts on **http://localhost:8080**.

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI 3.0 Specs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

#### Pre-seeded Default Accounts:
- **Admin**: `admin@serenityspa.com` / `Admin@123` (Full permissions)
- **Customer**: `guest@serenityspa.com` / `Guest@123`

---

### 3. Frontend Execution

```powershell
cd frontend
npm install
npm run dev
```

The React frontend runs at **http://localhost:5174** (or `5173`).

---

## REST API Specification

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register customer account |
| `POST` | `/api/auth/login` | Public | Authenticate and obtain JWT |
| `GET` | `/api/auth/me` | Authenticated | Get profile for currently authenticated user |

### Treatments (`/api/treatments` & `/api/admin/treatments`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/treatments` | Public | List all active spa treatments |
| `GET` | `/api/treatments/{id}` | Public | Get single treatment details |
| `POST` | `/api/admin/treatments` | `ROLE_ADMIN` | Create new treatment |
| `PUT` | `/api/admin/treatments/{id}` | `ROLE_ADMIN` | Update treatment attributes |
| `DELETE` | `/api/admin/treatments/{id}` | `ROLE_ADMIN` | Deactivate treatment |

### Appointments (`/api/appointments` & `/api/admin/appointments`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/appointments` | Customer | Book appointment (includes conflict validation) |
| `GET` | `/api/appointments/my` | Customer | List logged-in customer's appointments |
| `GET` | `/api/appointments/{id}` | Owner / Admin | View specific appointment details |
| `PATCH` | `/api/appointments/{id}/cancel` | Owner / Admin | Cancel existing booking |
| `GET` | `/api/admin/appointments` | `ROLE_ADMIN` | List all customer appointments |
| `PATCH` | `/api/admin/appointments/{id}/status` | `ROLE_ADMIN` | Update status (`CONFIRMED`, `COMPLETED`, etc.) |

### Membership (`/api/memberships`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/memberships/plans` | Public | List all active membership plans |
| `GET` | `/api/memberships/my` | Authenticated | Get current user's membership details |
| `POST` | `/api/memberships/activate` | Authenticated | Activate or renew a membership (demo flow if payment not integrated) |

### Price Calculation (`/api/appointments/calculate-price`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/appointments/calculate-price` | Authenticated | Calculate final price for a treatment considering active membership discount |

### Reviews (`/api/reviews`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/reviews` | Public | List public reviews for treatments |
| `GET` | `/api/reviews/my` | Authenticated | List reviews submitted by the current user |
| `POST` | `/api/reviews` | Authenticated | Submit a review for a completed appointment (ownership and status validated) |

**Note:** The frontend route for My Bookings was renamed from `/my-appointments` to `/my-bookings` and the navigation link updated accordingly.

### Contact (`/api/contact`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/contact` | Public | Submit guest enquiry |

---

## Testing

Run the automated JUnit 5 and Mockito test suite:

```powershell
cd backend
mvn test
```

### Verified Tests:
- `AuthServiceTest`:
  - `register_Success`
  - `register_ThrowsConflict_WhenEmailExists`
  - `login_Success`
- `AppointmentServiceTest`:
  - `bookAppointment_Success`
  - `bookAppointment_ThrowsConflict_WhenSlotAlreadyBooked`
  - `bookAppointment_ThrowsException_WhenDateInPast`

---

## Troubleshooting

1. **Port 8080 already in use**:
   Stop any process occupying port 8080:
   ```powershell
   Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force
   ```
2. **CORS issues**:
   `SecurityConfig.java` allows origins `http://localhost:5173` and `http://localhost:5174`.
