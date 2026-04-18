# Quiz-App-Repository

Minimal Spring Boot microservice quiz application using:
- **Microservices** (`question-service`, `quiz-service`)
- **OpenFeign** (quiz-service calls question-service)
- **Kafka** (quiz-service publishes `quiz-created` events)

## Services

### 1) question-service (port 8081)
- `GET /api/questions`
- `GET /api/questions/{id}`

### 2) quiz-service (port 8082)
- `POST /api/quizzes` with:
  ```json
  {
    "title": "Backend Quiz",
    "questionIds": [1, 2]
  }
  ```
- `GET /api/quizzes/{id}`

## Build and test

From repository root:

```bash
mvn test
```

## Run services

In separate terminals:

```bash
mvn -pl question-service spring-boot:run
mvn -pl quiz-service spring-boot:run
```

Kafka should be available at `localhost:9092` for event publishing.
