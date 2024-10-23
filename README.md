# Moonwalk Backend API

Moonwalk project's backend API built with Spring Boot.

## Getting Started

### Prerequisites
- Java 21+
- Gradle
- Docker (for PostgreSQL)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/moonwalk-backend.git
   
2. Navigate into the project directory:
   ```bash
   cd moonwalk-backend
   
3. Set up environment variables:
   ```bash
   cp .env.example .env

### Running the Application
1. Start the PostgreSQL container with Docker:
   ```bash
   make up

2. Run the backend server:
   ```bash
   make run

The server will start at http://localhost:8080.

### Database
Uses PostgreSQL via Docker. Ensure the PostgreSQL container is running and your .env file has correct credentials.

### Swagger Documentation
API documentation is available via Swagger:

http://localhost:8080/swagger-ui.html