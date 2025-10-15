# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

CCLEMON is a Spring Boot 3.5.3 microservices architecture built with Java 21, utilizing Spring Cloud 2025.0.0. It's organized as a multi-module Gradle project with Docker containerization and Jenkins CI/CD pipelines.

## Essential Commands

### Build and Test
```bash
# Build all modules
./gradlew build

# Build without tests
./gradlew clean build -x test

# Build specific module
./gradlew :cclemon-auth:build

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :cclemon-core:test

# Clean and rebuild everything
./gradlew clean build
```

### Development
```bash
# Run a single Spring Boot application (example with auth service)
./gradlew :cclemon-auth:bootRun

# Check dependencies
./gradlew dependencies

# View project structure
./gradlew projects
```

### Docker Environment
```bash
# Create required network (must be run first)
docker network create cclemon-network

# Build Docker image for specific module (manual build)
# Example: cd cclemon-auth && cp build/libs/cclemon-auth.jar build/libs/app.jar
# docker build -f ../ci/Dockerfile -t cclemon-auth:dev build/libs
```

## Architecture Overview

### Module Structure
The project follows a modular microservices architecture with clear separation of concerns:

- **cclemon-core**: Shared foundation module containing:
  - `BaseEntity` with JPA auditing (id, version, create/modify timestamps, soft delete)
  - Redis integration and configuration
  - OAuth2 resource server security configuration
  - OpenFeign client configuration

- **cclemon-auth**: OAuth2 Authorization Server
  - Spring Authorization Server implementation
  - User management with JPA repositories
  - OAuth2 client and consent management
  - Federated identity handling
  - Thymeleaf UI templates

- **cclemon-data**: Shared data models and entities
  - System configuration entities (`ConfigKey`, `ConfigValue`, etc.)
  - Internationalization support (`ConfigKeyI18n`)
  - Audit trail entities (`ConfigValueAudit`)

- **cclemon-config-server**: Spring Cloud Config Server
  - Centralized configuration management
  - Kafka bus integration for config refresh
  - Health monitoring

- **cclemon-health**: Service health monitoring
  - Spring Cloud Config client
  - Kafka bus integration
  - Actuator endpoints

- **cclemon-eventBus**: Event-driven communication
  - WebSocket support
  - Kafka integration for messaging

- **cclemon-stream**: Stream processing
  - Kafka Streams implementation

- **cclemon-userEvent**: User event handling
- **cclemon-logging**: Centralized logging utilities
- **cclemon-practice**: Development practice/examples module

### Key Technologies
- **Framework**: Spring Boot 3.5.3 with Spring Cloud 2025.0.0
- **Language**: Java 21 with Lombok
- **Data**: JPA/Hibernate with MySQL, Redis for caching
- **Security**: Spring Security with OAuth2 Authorization Server
- **Messaging**: Apache Kafka with Spring Kafka
- **Build**: Gradle with multi-module setup
- **Deployment**: Docker containers with Jenkins CI/CD

### Inter-Module Dependencies
- Most service modules depend on `cclemon-core` for shared functionality
- `cclemon-auth` depends on `cclemon-data` for user/auth entities
- All modules include common dependencies: Commons Lang3, Collections4, Lombok

### Deployment Architecture
- Each service runs in its own Docker container
- Services communicate through a shared Docker network (`cclemon-network`)
- Health checks verify service readiness via actuator endpoints
- Environment-specific profiles (dev, prod) control configuration

### Configuration Management
- Centralized config via `cclemon-config-server`
- Environment-specific Spring profiles
- Kafka-based config refresh notifications
- Support for encrypted/secret configuration values

## Development Patterns

### Entity Design
- All entities should extend `BaseEntity` for consistent auditing
- Use `@Builder`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` from Lombok
- Follow the established naming conventions for database columns

### Module Communication
- Use OpenFeign clients for synchronous service-to-service calls
- Leverage Kafka for asynchronous event-driven communication
- Implement proper OAuth2 resource server security

### Docker Integration
- Services expect to run in `cclemon-network`
- Health checks are mandatory via `/actuator/health`
- Environment variables control Spring profiles

### CI/CD Pipeline
- Jenkins builds individual modules based on parameters
- Supports both `dev` and `prod` environments
- Auto-deployment for non-prod environments
- Health verification before deployment completion