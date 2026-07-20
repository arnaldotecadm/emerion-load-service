# REST API Structure & Design Patterns

## API Versioning
Use URL-based versioning for clarity:
```
GET /api/v1/customers
GET /api/v2/customers  (future version)
```

## Endpoint Naming Conventions
- Resources should be **nouns** (not verbs)
- Use **lowercase** with hyphens for multi-word resources
- Singular for single resource, plural for collections

```
GET    /api/v1/customers              # List all
POST   /api/v1/customers              # Create new
GET    /api/v1/customers/{id}         # Get specific
PUT    /api/v1/customers/{id}         # Replace
PATCH  /api/v1/customers/{id}         # Partial update
DELETE /api/v1/customers/{id}         # Delete

GET    /api/v1/customers/{id}/orders  # Nested resources
```

## HTTP Status Codes
```
200 OK                  - GET, PUT, PATCH successful
201 Created             - POST successful, resource created
204 No Content          - DELETE successful, DELETE with no response body
400 Bad Request         - Invalid input, validation error
401 Unauthorized        - Authentication failed
403 Forbidden           - Authenticated but not authorized
404 Not Found           - Resource not found
409 Conflict            - Resource already exists (duplicate)
500 Internal Server Error - Server error
503 Service Unavailable - Server is down/maintenance
```

## Request/Response Format

### Standard Response Structure
```json
{
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "timestamp": "2026-07-18T19:45:39Z",
  "status": "success"
}
```

### List Response with Pagination
```json
{
  "data": [
    { "id": 1, "name": "Item 1" },
    { "id": 2, "name": "Item 2" }
  ],
  "pagination": {
    "total": 100,
    "page": 0,
    "size": 20,
    "totalPages": 5
  },
  "timestamp": "2026-07-18T19:45:39Z"
}
```

### Error Response
```json
{
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Customer with id 999 not found",
    "details": "Please check the customer ID and try again"
  },
  "timestamp": "2026-07-18T19:45:39Z",
  "status": "error"
}
```

## Pagination Query Parameters
```
GET /api/v1/customers?page=0&size=20&sort=name,asc
```

Parameters:
- `page`: Zero-based page number (default: 0)
- `size`: Records per page (default: 20, max: 100)
- `sort`: Sort by field and direction, comma-separated (default: id,asc)

Implementation:
```kotlin
@GetMapping
fun listCustomers(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "20") size: Int,
    @RequestParam(defaultValue = "id,asc") sort: String
): ResponseEntity<PagedResponse<CustomerDTO>> {
    val pageable = PageRequest.of(page, size, parseSort(sort))
    val result = service.findAll(pageable)
    return ResponseEntity.ok(PagedResponse(
        data = result.content,
        pagination = PaginationInfo(
            total = result.totalElements,
            page = page,
            size = size,
            totalPages = result.totalPages
        )
    ))
}
```

## Filtering
Use query parameters for filtering:
```
GET /api/v1/customers?status=active&type=premium
GET /api/v1/customers?createdAfter=2026-01-01&createdBefore=2026-12-31
GET /api/v1/customers?name=john  (partial match)
```

Implementation:
```kotlin
@GetMapping
fun listCustomers(
    @RequestParam(required = false) status: String?,
    @RequestParam(required = false) type: String?,
    @RequestParam(required = false) name: String?
): ResponseEntity<List<CustomerDTO>> {
    val result = service.findByFilters(status, type, name)
    return ResponseEntity.ok(result)
}
```

## Search
For complex searches, use a `search` query parameter:
```
GET /api/v1/customers?search=john+doe&filter=active
```

## Request Validation
Always validate input in controller or service:
```kotlin
@PostMapping
fun createCustomer(@Valid @RequestBody dto: CreateCustomerDTO): ResponseEntity<CustomerDTO> {
    // Validation happens automatically via @Valid
    val created = service.create(dto)
    return ResponseEntity.status(HttpStatus.CREATED).body(created)
}

data class CreateCustomerDTO(
    @field:NotBlank(message = "Name is required")
    val name: String,
    
    @field:Email(message = "Email must be valid")
    val email: String,
    
    @field:Min(18)
    val age: Int
)
```

## POST/PUT Distinction
- **POST**: Creates new resource, server generates ID, returns 201 Created
- **PUT**: Replaces entire resource, client specifies ID, returns 200 OK
- **PATCH**: Partial update of resource, returns 200 OK

```kotlin
@PostMapping
fun createCustomer(@Valid @RequestBody dto: CreateCustomerDTO): ResponseEntity<CustomerDTO> {
    val created = service.create(dto)
    return ResponseEntity.status(HttpStatus.CREATED)
        .location(URI("/api/v1/customers/${created.id}"))
        .body(created)
}

@PutMapping("/{id}")
fun updateCustomer(
    @PathVariable id: Long,
    @Valid @RequestBody dto: UpdateCustomerDTO
): ResponseEntity<CustomerDTO> {
    val updated = service.update(id, dto)
    return ResponseEntity.ok(updated)
}

@PatchMapping("/{id}")
fun partialUpdateCustomer(
    @PathVariable id: Long,
    @RequestBody dto: PartialUpdateCustomerDTO
): ResponseEntity<CustomerDTO> {
    val updated = service.partialUpdate(id, dto)
    return ResponseEntity.ok(updated)
}
```

## DELETE Response
```kotlin
@DeleteMapping("/{id}")
fun deleteCustomer(@PathVariable id: Long): ResponseEntity<Void> {
    service.delete(id)
    return ResponseEntity.noContent().build()  // 204 No Content
}
```

## Exception Handling
Use `@ControllerAdvice` for centralized error handling:

```kotlin
@ControllerAdvice
@RestController
class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(e: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = "RESOURCE_NOT_FOUND",
            message = e.message,
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = e.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val error = ErrorResponse(
            code = "VALIDATION_ERROR",
            message = "Validation failed",
            details = errors,
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericError(e: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "An unexpected error occurred",
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
```

## Documentation with Swagger/OpenAPI
Add annotations to document endpoints:

```kotlin
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers", description = "Customer management endpoints")
class CustomerController(val service: CustomerService) {
    
    @GetMapping
    @Operation(summary = "List all customers", description = "Retrieve a paginated list of customers")
    @Parameters(
        Parameter(name = "page", description = "Page number (0-based)", example = "0"),
        Parameter(name = "size", description = "Records per page", example = "20"),
        Parameter(name = "sort", description = "Sort by field, asc/desc", example = "name,asc")
    )
    fun listCustomers(...): ResponseEntity<PagedResponse<CustomerDTO>> { ... }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found")
    fun getCustomer(@PathVariable id: Long): ResponseEntity<CustomerDTO> { ... }

    @PostMapping
    @Operation(summary = "Create new customer")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    fun createCustomer(@Valid @RequestBody dto: CreateCustomerDTO): ResponseEntity<CustomerDTO> { ... }
}
```

## Authentication & Authorization
If needed for API endpoints:

```kotlin
@GetMapping
@PreAuthorize("hasRole('ADMIN')")  // Spring Security
fun adminOnlyEndpoint(): ResponseEntity<...> { ... }

@GetMapping
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
fun userOrAdminEndpoint(): ResponseEntity<...> { ... }
```

## Rate Limiting (Optional)
Consider adding rate limiting for production:
```kotlin
@RateLimiter("customerApi")  // Custom annotation
@GetMapping
fun listCustomers(...): ResponseEntity<...> { ... }
```

## CORS Configuration (for React Frontend)
In Spring Boot configuration:
```kotlin
@Configuration
class CorsConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:5173", "https://yourdomain.com")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600)
    }
}
```

## Caching (Optional Performance Optimization)
```kotlin
@GetMapping("/{id}")
@Cacheable(value = "customers", key = "#id")
fun getCustomer(@PathVariable id: Long): ResponseEntity<CustomerDTO> { ... }

@PutMapping("/{id}")
@CacheEvict(value = "customers", key = "#id")
fun updateCustomer(@PathVariable id: Long, @RequestBody dto: UpdateCustomerDTO): ResponseEntity<CustomerDTO> { ... }
```

## Testing REST Endpoints
```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest(@Autowired val mockMvc: MockMvc) {
    
    @Test
    fun testListCustomers() {
        mockMvc.perform(get("/api/v1/customers?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.pagination.total").exists())
    }

    @Test
    fun testCreateCustomer() {
        val dto = CreateCustomerDTO("John Doe", "john@example.com", 30)
        mockMvc.perform(post("/api/v1/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(dto)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.id").exists())
    }

    @Test
    fun testGetNonExistentCustomer() {
        mockMvc.perform(get("/api/v1/customers/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error.code").value("RESOURCE_NOT_FOUND"))
    }
}
```
