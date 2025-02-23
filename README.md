# IT Support System

A comprehensive IT support ticket management system with a Spring Boot backend, Oracle database, and Java Swing client application.

## Features

- User authentication (Regular users and IT Support staff)
- Ticket creation and management
- Real-time ticket status updates
- Priority-based ticket handling
- Modern and intuitive Swing-based user interface
- Secure Oracle database backend

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose
- Oracle Container Registry account (for Oracle XE image)

## Project Structure

```
support/
├── client/             # Swing client application
├── server/             # Spring Boot backend
├── docker-compose.yml  # Docker configuration
├── Dockerfile         # Backend Docker build file
└── init.sql           # Database initialization script
```

## Setup Instructions

### 1. Oracle Container Registry Authentication

Before running the application, you need to authenticate with the Oracle Container Registry:

```bash
docker login container-registry.oracle.com
```

### 2. Building and Running with Docker

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd support
   ```

2. Start the containers:
   ```bash
   docker-compose up -d
   ```

   This will start both the Oracle database and the Spring Boot backend.

### 3. Running the Client Application

1. Build the client JAR:
   ```bash
   cd client
   mvn clean package
   ```

2. Run the client:
   ```bash
   java -jar target/support-client.jar
   ```

## Development Setup

### Backend Development

1. Navigate to the server directory:
   ```bash
   cd server
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Client Development

1. Navigate to the client directory:
   ```bash
   cd client
   ```

2. Run the Swing application:
   ```bash
   ./mvnw exec:java
   ```

## Building Executable JARs

### Backend JAR
```bash
cd server
mvn clean package
```

### Client JAR
```bash
cd client
mvn clean package
```

The executable client JAR will be created in `client/target/support-client.jar`

## Configuration

### Backend Configuration
The backend configuration can be found in `server/src/main/resources/application.properties`

### Database Configuration
Database initialization script is located in `init.sql`

## Security Considerations

- Database credentials should be changed in production
- HTTPS should be enabled for production deployment
- Proper firewall rules should be configured
- Regular security updates should be applied

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details 