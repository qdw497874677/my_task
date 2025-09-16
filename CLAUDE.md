# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java Spring Boot application using the COLA (Clean Object-oriented Layered Architecture) framework. The project follows a modular structure with the following modules:

- `my-task-client`: DTOs and interfaces for client communication
- `my-task-adapter`: Adapters for different client interfaces (web, mobile, etc.)
- `my-task-app`: Application services and business use cases
- `my-task-domain`: Core domain logic and entities
- `my-task-infrastructure`: Infrastructure implementations (database, external services)
- `start`: Main application entry point and configuration

## Common Development Commands

### Build Project
```bash
# Clean and build the entire project
mvn clean package

# Build without running tests
mvn clean package -DskipTests

# Run tests
mvn test
```

### Run Application
```bash
# Run the application locally
mvn spring-boot:run

# Or run the built JAR file
java -jar start/target/start-*.jar
```

### Docker Build
```bash
# Build the project and create Docker image
./build.sh
```

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AiServiceTest

# Run specific test method
mvn test -Dtest=AiServiceTest#testOpenAi
```

## Architecture Overview

The application follows the COLA architecture pattern:

1. **Client Layer**: Contains DTOs for data transfer and client-facing interfaces
2. **Adapter Layer**: Implements adapters for different client interfaces (web, mobile, Feishu, etc.)
3. **Application Layer**: Contains use case implementations and orchestrates domain operations
4. **Domain Layer**: Core business logic, entities, value objects, and domain services
5. **Infrastructure Layer**: Implements infrastructure concerns like database access, external API clients

### Key Features

1. **Feishu Integration**: Implements Feishu bot functionality with command-based processing system
2. **AI Services**: Integrates with multiple AI providers (Zhipu AI, OpenRouter, Ollama) for chat and image processing
3. **RAG (Retrieval-Augmented Generation)**: Implements document processing and retrieval for AI services
4. **YouTube Downloading**: Implements YouTube video downloading functionality with task processing
5. **Image Processing**: Implements image processing capabilities using AI models
6. **Task Processing**: Implements a task processing system for various operations (YouTube downloads, summaries, etc.)

### Configuration

The application uses Spring Boot configuration files:
- `application.yml`: Default configuration
- `application-dev.yml`: Development environment configuration
- `application-prod.yml`: Production environment configuration

Environment-specific configurations are loaded based on the `SPRING_PROFILES_ACTIVE` environment variable.

## Important Components

### Feishu Command System
Located in `my-task-app/src/main/java/com/qdw/task/feishu/command`, this system provides a flexible way to handle Feishu messages and execute business logic through command implementations.

### AI Services
Located in `my-task-domain/src/main/java/com/qdw/task/domain/ai`, these services provide integration with various AI providers and RAG functionality.

### Task Processing System
Located in `my-task-domain/src/main/java/com/qdw/task/domain/task`, this system implements a task processing framework for handling various operations like YouTube downloads and content summarization.

### Image Processing
Located in `my-task-domain/src/main/java/com/qdw/task/domain/image`, this provides image processing capabilities using AI models.