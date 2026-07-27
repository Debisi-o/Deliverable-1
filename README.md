# Course Compass

Course Compass is a Spring Boot web application for discovering and publishing practical courses.

## Requirements

- Java 17 or newer
- Maven 3.9 or newer (or use an IDE with Maven support)

## Run

```bash
mvn spring-boot:run
```

Open <http://localhost:8080>. The file-backed H2 database is created in `./data/`, and realistic
sample courses are loaded from `data.sql` on the first run.

## Features

- Semantic, responsive Thymeleaf pages styled with Bootstrap
- Validated course creation form
- Spring Data JPA persistence with generated IDs and timestamps
- Server-side filtering by category and difficulty
- Server-side sorting and pagination
- Seed data and a record confirmation/details page
