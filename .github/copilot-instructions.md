# Emerion Load Service - Copilot Instructions

## Project Overview
**Emerion Load Service** is a Kotlin/Spring Boot ETL microservice responsible for:
- **Extracting** data from a legacy Firebird database using native SQL queries
- **Transforming** Firebird projections into internal domain models
- **Loading** transformed data into a PostgreSQL database
- **Sending** migrated data to the new Spring Boot API service via REST calls
- **Minimal REST endpoints** (testing only) that trigger data migration to the new service

**Key Architecture**: Query → Projection → Map → Model → Send (no data retrieval endpoints)

## Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 3.x
- **Build Tool**: Gradle (Kotlin DSL)
- **Database Access**: 
  - Firebird: Native SQL (JdbcTemplate, no JPA)
  - PostgreSQL: JPA (Hibernate) + Spring Data JPA
- **Source Database**: Firebird (legacy, read-only)
- **Target Database**: PostgreSQL
- **API Integration**: REST Client (RestTemplate or WebClient)
- **Mapping**: Manual or MapStruct
- **Testing**: JUnit 5

## Key Architectural Pattern: Query → Projection → Map → Model → Send

### Data Flow
```
1. NATIVE QUERY (Firebird)
   └─ SELECT with complex joins/conditions
   
2. PROJECTION (Interface/DTO)
   └─ Simple data holder for query results
   └─ Matches query result set exactly
   
3. MAPPER (Manual or MapStruct)
   └─ Converts projection to internal model
   └─ Handles transformations, validations, defaults
   
4. INTERNAL MODEL
   └─ Domain object (data class in Kotlin)
   └─ Business logic and validation
   
5. SEND TO NEW SERVICE
   └─ HTTP call to new API service
   └─ POST /api/v1/resources with batch of models
   └─ Return migration status
```

### Why This Pattern?
- **Legacy DB is read-only** - Use native queries for complex legacy schema
- **Query complexity** - Some Firebird queries are too complex for JPA/JPQL
- **Type safety** - Projections and models are strongly typed in Kotlin
- **Transformation point** - Mappers handle data cleaning, normalization, enrichment
- **Clear separation** - Each layer has a single responsibility

## Key Conventions & Patterns

### 1. Database Entities & JPA Mapping
- Entities are immutable/data class patterns in Kotlin
- Use `@Entity`, `@Table`, `@Id` annotations
- Firebird entities typically map to `@Table(schema = "firebird")`
- PostgreSQL entities typically map to `@Table(schema = "postgres")` or default schema
- Use constructors for all fields (primary constructor in data classes)

### 2. Repositories
- Extend `CrudRepository<T, ID>` or `JpaRepository<T, ID>`
- Use `@Transactional(readOnly = true)` for queries
- Custom query methods use naming conventions or `@Query` annotations
- Example: `fun findByNameIgnoreCase(name: String): List<Customer>`

### 3. Service Layer
- Contains business logic and data transformation
- Uses repositories for database access
- Handles data validation and error handling
- Methods are typically `@Transactional` for write operations
- Example: `fun fetchCustomersFromFirebird(): List<CustomerDTO>`

### 4. Controllers
- Use `@RestController` and `@RequestMapping`
- Endpoints follow REST conventions: `/api/v1/<resource>`
- Return DTOs (not entities) from endpoints
- Use appropriate HTTP status codes
- Example: `@GetMapping("/customers") fun getAllCustomers(): ResponseEntity<List<CustomerDTO>>`

### 5. Data Transfer Objects (DTOs)
- Located in `repository/projection/` or `dto/`
- Use Kotlin data classes: `data class CustomerDTO(...)`
- Immutable by default (use `val`, not `var`)
- Include validation annotations if needed

### 6. Error Handling
- Use custom exceptions (e.g., `ResourceNotFoundException`, `DataTransformationException`)
- Implement `@ControllerAdvice` for centralized exception handling
- Return consistent error responses with HTTP status codes

## Data Migration Strategy

### Firebird to PostgreSQL Flow
1. **Extract**: Query entities from Firebird database (read-only)
2. **Transform**: Map Firebird entities to intermediate models
3. **Validate**: Check data integrity and constraints
4. **Load**: Insert/update into PostgreSQL
5. **Expose**: Serve via REST API to React frontend

### Key Considerations
- Handle NULL values and type mismatches
- Maintain referential integrity (foreign keys)
- Manage transaction boundaries (Firebird queries in one tx, Postgres writes in another)
- Log migration progress and errors
- Support batch processing for large datasets
- Idempotent operations (safe to re-run migrations)

## REST API Conventions

### Endpoint Design
- Use HTTP verbs correctly (GET, POST, PUT, DELETE)
- Paginate list endpoints: `?page=0&size=20&sort=name,asc`
- Use query parameters for filtering: `?status=active&type=premium`
- Version APIs: `/api/v1/...`

### Response Format
- Success: `{ "data": {...}, "timestamp": "..." }`
- Error: `{ "error": "...", "status": 400, "timestamp": "..." }`
- Use appropriate HTTP status codes (200, 201, 400, 404, 500, etc.)

### Pagination
- Include metadata in list responses: `{ "data": [...], "total": 100, "page": 0, "size": 20 }`

## Logging & Debugging
- Use SLF4J + Logback (Spring Boot default)
- Log levels: TRACE (detailed), DEBUG (development), INFO (general), WARN (warnings), ERROR (errors)
- Log transformation steps, data counts, and timings
- Use MDC (Mapped Diagnostic Context) for request tracking

## Configuration Management
- Use `application.yml` for Spring Boot configuration
- Separate profiles: `application-dev.yml`, `application-prod.yml`, etc.
- Firebird connection: Separate DataSource with `@Configuration`
- PostgreSQL connection: Default Spring Boot DataSource

## Testing
- Unit tests: Services and business logic (no DB calls)
- Integration tests: Repositories and full flow (with test DB containers)
- Use `@DataJpaTest` for repository tests
- Use `@SpringBootTest` for full application tests
- Mocking: Mockito for dependencies

## Important Guidelines for Copilot
1. **Always ask clarifying questions** when requirements are ambiguous
2. **Follow existing code patterns** in the project
3. **Maintain backward compatibility** unless explicitly asked to break it
4. **Write tests** alongside features
5. **Document complex logic** with clear comments
6. **Use meaningful variable and function names** in English
7. **Keep services thin** - push complexity to service layer
8. **Handle exceptions gracefully** - never let them bubble up unhandled
9. **Be defensive** about null values (use Optional, Elvis operator, etc.)
10. **Consider performance** - indexes, query optimization, batch processing

## Common Tasks & Implementation Notes

### Adding a New Entity
1. Create JPA entity in `model/`
2. Create JpaRepository in `repository/`
3. Create DTO/Projection in `repository/projection/`
4. Optionally create mapper in `repository/mapper/`
5. Create service methods for querying/transforming
6. Create controller endpoints

### Adding a New REST Endpoint
1. Create or modify controller in `controller/`
2. Add service method if needed
3. Return DTO (not entity)
4. Add appropriate HTTP status codes
5. Test with curl or Postman

### Connecting to Firebird
- Use separate datasource configuration
- Connection properties in `application.yml`
- JDBC driver dependency in `build.gradle.kts`
- Dialect: `org.hibernate.dialect.FirebirdDialect`

## Dependencies to Know
- `spring-boot-starter-web`: REST & MVC support
- `spring-boot-starter-data-jpa`: JPA & Hibernate
- `spring-boot-starter-validation`: Bean validation
- `spring-boot-starter-actuator`: Health checks and metrics
- Firebird JDBC driver: `org.firebirdsql.jdbc:jaybird:...`
- PostgreSQL driver: included in Spring Boot starter

## When to Ask for Clarification
- Ambiguous requirements or edge cases
- When multiple valid approaches exist (ask for preference)
- Complex business logic that needs validation
- Performance concerns or large-scale operations
- Security implications (auth, encryption, etc.)
