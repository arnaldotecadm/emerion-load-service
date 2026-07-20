# Copilot Quick Examples - Common Requests

Use these examples as templates for requesting code generation from Copilot.

## Example 1: Create a New Migration Service

**Request to Copilot:**
```
I need to create a migration service for the ORDERS table. Create:

1. Firebird entity in `model/firebird/FirebirdOrder.kt`
   - Fields: id, customerId, orderNumber, amount, orderDate, status
   - All fields read-only
   
2. PostgreSQL entity in `model/Order.kt`
   - Same fields plus createdAt, updatedAt (auto-set)
   
3. Firebird repository in `repository/firebird/FirebirdOrderRepository.kt`
   
4. PostgreSQL repository in `repository/OrderRepository.kt`
   
5. Migration service in `service/OrderMigrationService.kt`
   - Batch migration (1000 records per batch)
   - Handle NULL values
   - Log progress
   - Idempotent (safe to re-run)
   - Follow patterns from firebird-postgres.instructions.md

6. Controller endpoint in `controller/MigrationController.kt` to trigger the migration
   - POST /api/v1/migrations/orders
   - Return migration status and record count

Follow all patterns from firebird-postgres.instructions.md and spring-configuration.instructions.md
```

**Expected Output:** 
Copilot generates all 6 files with:
- Correct Firebird dialect configuration
- Proper transaction management
- Error handling
- Logging at appropriate levels
- Batch processing logic

---

## Example 2: Add a Paginated List Endpoint

**Request to Copilot:**
```
Add a REST endpoint to list customers with:

Endpoint: GET /api/v1/customers
Features:
- Pagination: ?page=0&size=20&sort=name,asc
- Filtering: ?status=active&type=premium
- Error handling following api-structure.instructions.md
- Swagger documentation
- Proper HTTP status codes (200, 400, 404)

Response format:
{
  "data": [...],
  "pagination": { "total": 100, "page": 0, "size": 20, "totalPages": 5 },
  "timestamp": "..."
}

Add the method to CustomerController.kt
Modify CustomerService.kt if needed
```

**Expected Output:**
Copilot adds:
- `@GetMapping` method with proper parameters
- Service layer filtering/pagination
- DTO response object
- Exception handling
- Swagger annotations

---

## Example 3: Add Input Validation to Controller

**Request to Copilot:**
```
Update CreateCustomerDTO in repository/projection/CustomerDTO.kt to add validation:
- name: required, non-blank, max 100 chars
- email: required, valid email format
- age: optional, min 18 if provided

Add error handling in CustomerController.kt using @ControllerAdvice pattern
Return 400 Bad Request with validation errors in this format:

{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": ["name: must not be blank", "email: must be a valid email"]
  },
  "timestamp": "...",
  "status": "error"
}

Follow patterns from api-structure.instructions.md
```

**Expected Output:**
- Data class with annotations (@NotBlank, @Email, @Min, etc.)
- Exception handler in @ControllerAdvice
- Proper error response mapping

---

## Example 4: Handle Firebird NULL Values

**Request to Copilot:**
```
When migrating customers from Firebird to PostgreSQL:
- Firebird records may have NULL in many fields
- PostgreSQL requires some fields to be NOT NULL

Create logic in CustomerMigrationService.kt to:
1. Read all customers from Firebird (including NULL fields)
2. Transform/validate:
   - If name is NULL, use "Unknown" as default
   - If email is NULL, set to empty string
   - Keep createdAt as-is (can be NULL)
3. Write to PostgreSQL with proper error handling
4. Log validation warnings for records with defaults applied

Follow patterns from firebird-postgres.instructions.md for NULL handling
```

**Expected Output:**
- Service method with proper NULL checks
- Default value logic
- Logging of transformations
- Error handling for non-nullable violations

---

## Example 5: Add Integration Tests

**Request to Copilot:**
```
Create integration tests for CustomerMigrationService in 
`src/test/kotlin/br/com/vercel/emerionloadservice/service/CustomerMigrationServiceTest.kt`

Tests should:
1. Test successful migration of customers from Firebird to PostgreSQL
2. Test batch processing (1000 per batch)
3. Test NULL value handling
4. Test idempotency (re-running migration doesn't duplicate records)
5. Test error handling for invalid data
6. Use TestContainers for PostgreSQL
7. Mock Firebird repository

Use @SpringBootTest and follow testing patterns from spring-configuration.instructions.md
```

**Expected Output:**
- Setup/teardown logic
- Test cases for each scenario
- Assertions on record counts and data
- Mock objects for Firebird

---

## Example 6: Add Exception Handling

**Request to Copilot:**
```
Create a custom exception handler in controller/ExceptionHandler.kt

Handle these exceptions:
1. ResourceNotFoundException - entity not found (404)
2. DataTransformationException - migration error (500)
3. ValidationException - data validation failed (400)
4. MethodArgumentNotValidException - controller input validation (400)
5. Generic Exception - catch-all (500)

Return consistent error format:
{
  "error": {
    "code": "ERROR_CODE",
    "message": "User-friendly message",
    "timestamp": "..."
  },
  "status": "error"
}

Use @ControllerAdvice and follow patterns from api-structure.instructions.md
```

**Expected Output:**
- Custom exception classes
- @ExceptionHandler methods for each exception
- Consistent error response DTO
- Proper HTTP status codes

---

## Example 7: Update Build Configuration

**Request to Copilot:**
```
Update build.gradle.kts to add:
1. Firebird JDBC driver (jaybird 4.0.8)
2. PostgreSQL driver (latest stable)
3. Testing dependencies (TestContainers for PostgreSQL)
4. Gradle plugin for Kotlin

Follow patterns from spring-configuration.instructions.md

Also create or update application.yml with:
- PostgreSQL connection config
- Firebird connection config (separate datasource)
- JPA properties for both databases
- Logging configuration (DEBUG for our package)
```

**Expected Output:**
- Updated build.gradle.kts with all dependencies
- application.yml with both database configs
- Separate application profiles if needed (dev, test, prod)

---

## Example 8: Add Batch Processing

**Request to Copilot:**
```
Enhance the product migration service to handle batch processing:

Requirements:
- Read 1000 products at a time from Firebird
- Transform each batch
- Save batch to PostgreSQL in one transaction
- Continue until all products are migrated
- Log progress: "Migrated X/Y products (Z%)"
- Handle partial batch failures gracefully
- Make it idempotent

Location: service/ProductMigrationService.kt

Follow patterns from firebird-postgres.instructions.md sections:
- Large Data Migrations
- Error Handling During Migration
- Idempotency & Re-runs
```

**Expected Output:**
- Loop with offset-based pagination
- Batch save logic
- Progress logging
- Error handling per batch
- Skip logic for already-migrated records

---

## Example 9: API Documentation with Swagger

**Request to Copilot:**
```
Add Swagger/OpenAPI documentation to CustomerController.kt for:
1. GET /api/v1/customers - list all with pagination
2. GET /api/v1/customers/{id} - get specific customer
3. POST /api/v1/customers - create new customer
4. PUT /api/v1/customers/{id} - update customer
5. DELETE /api/v1/customers/{id} - delete customer

Each endpoint should have:
- @Operation summary and description
- @Parameters for query/path params
- @ApiResponse for each status code (200, 201, 400, 404, 500)
- Example values

Enable Swagger UI at /swagger-ui.html

Follow patterns from api-structure.instructions.md
```

**Expected Output:**
- @Tag annotation on controller
- @Operation on each method
- @Parameters documentation
- @ApiResponse for error cases
- Spring Boot Swagger dependency in build.gradle.kts
- Swagger UI accessible

---

## Quick Reference: How to Ask

**Good Request Format:**
```
Create [WHAT]
Location: [WHERE in project structure]
Requirements:
- Requirement 1
- Requirement 2
- Requirement 3

Follow patterns from [INSTRUCTIONS FILE]
```

**Example:**
```
Create an order processor service
Location: service/OrderProcessorService.kt
Requirements:
- Read orders from Firebird ORDERS table
- Filter for status = 'PENDING'
- Transform to PostgreSQL Order entity
- Save with transaction management
- Log processing statistics

Follow patterns from firebird-postgres.instructions.md and spring-configuration.instructions.md
```

---

## Common Patterns to Reference

When asking Copilot, you can cite these patterns directly:

- **"Following the migration pattern from firebird-postgres.instructions.md"**
- **"Use the REST endpoint structure from api-structure.instructions.md"**
- **"Apply the dual-database configuration from spring-configuration.instructions.md"**
- **"Follow the error handling approach from copilot-instructions.md"**

---

## If Copilot Gets Off Track

Remind it:
```
"Remember, this is a Kotlin/Spring Boot project with Firebird→PostgreSQL migration.
Follow the patterns in the .github/instructions/ files.
Specifically, use the firebird-postgres.instructions.md for database migration logic."
```

This will refocus Copilot on your project context.

---

**Tip:** Keep a copy of these examples in your IDE or terminal for quick reference!
