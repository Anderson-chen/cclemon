# Project Architecture & Agent Guidelines

## 1. Project Overview
**Name**: cclemon
**Type**: Spring Boot Multi-module Backend Application
**Language**: Java 25
**Framework**: Spring Boot 4.0.1, Spring Cloud 2025.1.0
**Build Tool**: Gradle

## 2. Module Structure
The project is divided into the following modules:

| Module | Purpose |
| :--- | :--- |
| `cclemon-auth` | Authorization Server (OAuth2, Security) |
| `cclemon-data` | Data Access Layer (JPA Entities, Repositories) |
| `cclemon-web` | Web Layer (REST Controllers) |
| `cclemon-security` | Shared Security Configurations |
| `cclemon-cache` | Caching implementation (Redis) |
| `cclemon-config-server` | Centralized Configuration Server |
| `cclemon-kafka` | Kafka Messaging Infrastructure |
| `cclemon-utils` | Common Utilities |
| `cclemon-interface` | Shared Interfaces and DTOs |
| `cclemon-logging` | Centralized Logging |
| `cclemon-health` | Health Check & Monitoring |
| `cclemon-stream` | Stream Processing |
| `cclemon-consumer` | Message Consumers |

## 3. Technology Stack
- **Core**: Java 25
- **Spring**: Spring Boot 4.0.1, Spring Security, Spring Data JPA, Spring Cloud
- **Database**: MySQL, H2 (Runtime)
- **Cache**: Redis
- **Messaging**: Kafka
- **Tools**: Lombok

## 4. Development Guidelines for Agent (Gemini)
When assisting with this project, please adhere to the following rules:

### A. Code Style & Standards
- **Java 25 Features**: Utilize modern Java features such as Records, Pattern Matching, and Virtual Threads where appropriate.
- **Lombok**: Continue using Lombok for boilerplate reduction (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`).
- **Dependency Injection**: Prefer constructor injection (often via `@RequiredArgsConstructor`).
- **Module Boundaries**: Respect module dependencies. Do not introduce circular dependencies.

### B. File Operations
- **Verification**: Always verify file existence and path before reading or writing.
- **Incremental Changes**: When possible, use `replace_text` (if available) or write the full file if significant changes are needed, but ensure context is preserved.
- **Path Handling**: Use absolute paths as provided by the environment.

### C. Architecture Consistency
- **Layered Approach**: Maintain the Controller -> Service -> Repository flow.
- **Configuration**: Keep configuration in `config` packages within relevant modules.
- **Entities**: Persist entities in `cclemon-data` or specific domain modules if separated.

### D. Task Execution Strategy
1.  **Analyze**: Understand the request in the context of the multi-module structure.
2.  **Locate**: Find relevant files using `list_files` or `grep`.
3.  **Plan**: Determine which modules need modification.
4.  **Execute**: Apply changes using `write_file`.
