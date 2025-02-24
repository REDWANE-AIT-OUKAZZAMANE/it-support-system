# IT Support System

A comprehensive IT support ticket management system with a Spring Boot backend, Oracle database, and Java Swing client application.

## Features

- User authentication (Regular users and IT Support staff)
- Ticket creation and management
- Real-time ticket status updates
- Priority-based ticket handling
- Comments system with threaded discussions
- Modern and intuitive Swing-based user interface
- Secure Oracle database backend

## Project Structure
```
it-support-system/
├── server/                     # Backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Java source files
│   │   │   └── resources/    # Application properties
│   │   └── test/             # Test files
│   └── pom.xml               # Backend dependencies
├── client/                    # Desktop client application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/        # Java source files
│   │   │   └── resources/   # Client properties
│   │   └── test/            # Test files
│   └── pom.xml              # Client dependencies
├── docker-compose.yml        # Docker services configuration
├── Dockerfile               # Backend service build
├── init.sql                # Database initialization
├── .env                    # Environment variables
└── pom.xml                # Parent POM file
```

## Prerequisites

- Java 17 or higher
- Maven
- Docker and Docker Compose
- Oracle Container Registry account (for Oracle XE image)

## Environment Setup

### 1. Oracle Container Registry Access
1. Create an account at [Oracle Container Registry](https://container-registry.oracle.com)
2. Accept the Terms and Conditions for the Oracle Database image
3. Login to the registry:
   ```bash
   docker login container-registry.oracle.com
   ```

### 2. Environment Variables
Create a `.env` file in the root directory:
```env
# Database Configuration
DB_USER=C##support_system
DB_PASSWORD=support_password
ORACLE_PWD=oracle

# Application Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

```

### 3. Client Configuration
Create `client.properties` in the client directory:
```properties
server.url=http://localhost:8080
```

### 4. Development Tools
- Recommended IDE: IntelliJ IDEA or Eclipse
- Lombok plugin installation required
- Docker Desktop for container management

## Docker Setup

Our application uses Docker for containerization with the following components:

### 1. Backend Service (Spring Boot)
- Custom Dockerfile with multi-stage build
- Base image: Eclipse Temurin 17
- Exposed port: 8080
- Environment variables for database configuration

### 2. Database Service (Oracle XE 21c)
- Official Oracle Express Edition image
- Exposed ports: 1521 (database), 5500 (EM Express)
- Persistent volume for data storage
- Automatic initialization script

### Docker Files
- `Dockerfile`: Builds the Spring Boot application
- `docker-compose.yml`: Orchestrates all services
- `init.sql`: Database initialization script

## Quick Start

1. **Login to Oracle Container Registry**
   ```bash
   docker login container-registry.oracle.com
   ```

2. **Clone the Repository**
   ```bash
   git clone https://github.com/REDWANE-AIT-OUKAZZAMANE/it-support-system.git
   cd it-support-system
   ```

3. **Start Docker Services**
   ```bash
   docker-compose up -d
   ```
   This will:
   - Build the Spring Boot application
   - Pull the Oracle XE image
   - Create necessary volumes
   - Start all services

4. **Build and Run the Client**
   ```bash
   cd client
   mvn clean package
   java -jar target/support-client.jar
   ```

## Default Credentials

### Application Users
- Admin: username: `admin`, password: `admin`
- Employee: username: `employee`, password: `employee`

### Database
- System Password: `oracle`
- Application Schema: `C##support_system`
- Application Password: `support_password`

## Docker Commands Reference

### Basic Operations
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs

# View logs of specific service
docker-compose logs app    # Backend logs
docker-compose logs oracle # Database logs

# Rebuild services
docker-compose up -d --build
```

### Maintenance
```bash
# Remove volumes (will delete all data)
docker-compose down -v

# Check service status
docker-compose ps

# Check container resources
docker stats
```

### Troubleshooting
```bash
# Restart specific service
docker-compose restart app
docker-compose restart oracle

# View real-time logs
docker-compose logs -f

# Check container health
docker inspect <container_id>
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

## Comments System

The IT Support System includes a comprehensive comments system that allows users and IT support staff to communicate effectively:

### Features
- Add comments to tickets
- View comment history with timestamps
- User attribution for each comment
- Chronological ordering (newest first)
- Real-time updates
- Rich text formatting

### Usage
1. **Adding Comments**
   - Open a ticket
   - Scroll to the comments section
   - Enter your comment in the text area
   - Click "Add Comment"

2. **Viewing Comments**
   - Comments are displayed in chronological order
   - Each comment shows:
     - Author username
     - Timestamp
     - Comment content

3. **Permissions**
   - All users can view comments on their tickets
   - IT Support staff can view comments on all tickets
   - All authenticated users can add comments

### API Endpoints
```bash
# Add a comment
POST /api/comments
Content-Type: application/json
{
    "content": "Comment text",
    "ticketId": 123
}

# Get comments for a ticket
GET /api/comments/ticket/{ticketId}
```

## Security Considerations

- Database credentials should be changed in production
- HTTPS should be enabled for production deployment
- Proper firewall rules should be configured
- Regular security updates should be applied

## Troubleshooting Guide

### Common Issues and Solutions

1. **Docker Container Issues**
   ```bash
   # Issue: Container not starting
   # Solution: Check logs
   docker-compose logs app
   docker-compose logs oracle

   # Issue: Port conflicts
   # Solution: Stop conflicting services or change ports in docker-compose.yml
   netstat -ano | findstr "8080"
   netstat -ano | findstr "1521"
   ```

2. **Database Connection Issues**
   - Error: "ORA-12528: TNS:listener: all appropriate instances are blocking new connections"
     * Solution: Wait for Oracle container to fully initialize (can take 2-3 minutes)
     * Check Oracle container logs: `docker-compose logs oracle`

   - Error: "ORA-00942: table or view does not exist"
     * Solution: Ensure init.sql was properly executed
     * Manually run init.sql inside container:
       ```bash
       docker exec -it support-oracle sqlplus system/oracle@XE
       @/docker-entrypoint-initdb.d/init.sql
       ```

3. **Client Connection Issues**
   - Error: "Connection refused"
     * Verify backend service is running: `docker-compose ps`
     * Check server.url in client.properties
     * Ensure no firewall blocking

   - Error: "Authentication failed"
     * Verify correct credentials
     * Check if database initialized properly
     * Try default credentials: admin/admin

4. **Build Issues**
   - Error: "Maven build failure"
     * Clean Maven cache: `mvn clean`
     * Update Maven dependencies: `mvn dependency:purge-local-repository`
     * Verify Java version: `java -version`

   - Error: "Docker build failure"
     * Check Docker daemon is running
     * Verify Dockerfile syntax
     * Clean Docker cache: `docker system prune`

### Logs Location
- Backend logs: `docker-compose logs app`
- Database logs: `docker-compose logs oracle`
- Client logs: Check console output or `client/logs/` directory

### Getting Help
If you encounter issues not covered here:
1. Check the GitHub Issues page
2. Review Docker and Oracle XE documentation
3. Create a new issue with:
   - Detailed error description
   - Relevant logs
   - Steps to reproduce
   - Environment details

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details 
