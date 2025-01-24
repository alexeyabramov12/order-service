# Order Service

Order Service is a RESTful API for managing orders. It includes authentication, metrics, and full Swagger documentation.

## API Documentation

The Swagger API documentation is available at:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Specification**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Servers
- **http://localhost:8080** - Base URL for the API

---

## Features

### Authentication
Authenticate a user and retrieve a token:
- **POST** `/login`

### Metrics
Retrieve custom application metrics:
- **GET** `/metrics`

### Orders
Manage orders:
- **GET** `/orders/{orderId}` - Get an order by ID
- **PUT** `/orders/{orderId}` - Update an existing order
- **DELETE** `/orders/{orderId}` - Soft delete an order by ID
- **GET** `/orders` - Get a list of orders with optional filters
- **POST** `/orders` - Create a new order

---

## Running the Application

Follow these steps to set up and run the application using Docker.

### For **Windows**

#### In PowerShell:
```powershell
$env:DOCKER_BUILDKIT=1
docker-compose up --build -d
```

#### In cmd:
```cmd
set DOCKER_BUILDKIT=1
docker-compose up --build -d
```

### For **macOS and Linux**

#### In Terminal:
```bash
export DOCKER_BUILDKIT=1
docker-compose up --build -d
```

## Preloaded Users and Roles

The application includes preloaded users and roles for testing purposes.

### Roles:
- **Admin**: Full access to the system.
- **User**: Limited access to their own resources.

### Preloaded Users:

| ID  | First Name | Last Name | Email                | Role   | Password   |
|-----|------------|-----------|----------------------|--------|------------|
| 1   | John       | Doe       | admin1@example.com   | Admin  | `password` |
| 2   | Jane       | Smith     | admin2@example.com   | Admin  | `password` |
| 3   | Alice      | Brown     | user1@example.com    | User   | `password` |
| 4   | Bob        | Green     | user2@example.com    | User   | `password` |

### Notes:
- Passwords are stored in hashed format in the database.
- The plaintext password for all preloaded users is `password`.
- Users with the **Admin** role have full access to all API endpoints.
- Users with the **User** role have limited access to resources.

### Stopping the Application

To stop the running application and remove all containers:
```
docker-compose down