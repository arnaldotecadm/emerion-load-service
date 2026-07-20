# Data Mapper Skill

## Overview
This skill provides templates and patterns for mapping Firebird projections to internal domain models in the emerion-load-service project.

## Mapping Pattern: Query → Projection → Mapper → Model → Send

```
┌─────────────────┐
│ Firebird Query  │
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  Projection DTO │ (Exactly matches query result)
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│   Mapper Class  │ (Handles transformations)
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  Domain Model   │ (Business object)
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  Send to API    │ (REST call)
└─────────────────┘
```

## Step 1: Define Projection (from query result)

The projection should exactly match what the SQL query returns:

```kotlin
// File: repository/projection/CustomerProjection.kt
data class CustomerProjection(
    val code: Int,
    val name: String,
    val fantasyName: String? = null,
    val address: String? = null,
    val number: String? = null,
    val district: String? = null,
    val zipCode: String? = null,
    val state: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val lastUpdate: LocalDateTime? = null
)
```

## Step 2: Define Domain Model

The domain model represents your business logic:

```kotlin
// File: model/Customer.kt
@Entity
@Table(name = "customers", schema = "public")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "legacy_code", nullable = false, unique = true)
    val legacyCode: Int,
    
    @Column(name = "name", nullable = false)
    val name: String,
    
    @Column(name = "fantasy_name")
    val fantasyName: String? = null,
    
    @Column(name = "address")
    val address: String? = null,
    
    @Column(name = "phone")
    val phone: String? = null,
    
    @Column(name = "email")
    val email: String? = null,
    
    @Column(name = "migrated_at")
    val migratedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "legacy_updated_at")
    val legacyUpdatedAt: LocalDateTime? = null,
    
    @Column(name = "active")
    val active: Boolean = true
)
```

## Step 3: Manual Mapper Class

For simple mappings or when you want full control:

```kotlin
// File: repository/mapper/CustomerMapper.kt
@Component
class CustomerMapper {
    
    fun projectToModel(projection: CustomerProjection): Customer {
        return Customer(
            legacyCode = projection.code,
            name = normalizeName(projection.name),
            fantasyName = projection.fantasyName?.trim().takeIfNotBlank(),
            address = normalizeAddress(projection.address),
            phone = normalizePhone(projection.phone),
            email = normalizeEmail(projection.email),
            legacyUpdatedAt = projection.lastUpdate
        )
    }
    
    fun projectionsToModels(projections: List<CustomerProjection>): List<Customer> {
        return projections.map { projectToModel(it) }
    }
    
    // Transformation Functions
    private fun normalizeName(name: String): String {
        return name.trim()
            .replace(Regex("\\s+"), " ")  // Multiple spaces to single
            .replaceFirstChar { it.uppercase() }
    }
    
    private fun normalizeAddress(address: String?): String? {
        return address?.trim()?.takeIfNotEmpty()
    }
    
    private fun normalizePhone(phone: String?): String? {
        if (phone == null) return null
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return if (cleaned.length >= 10) cleaned else null
    }
    
    private fun normalizeEmail(email: String?): String? {
        if (email == null) return null
        val trimmed = email.trim().lowercase()
        return if (trimmed.contains("@")) trimmed else null
    }
}

// Extension Functions
private fun String?.takeIfNotBlank(): String? = if (this?.isNotBlank() == true) this else null
private fun String?.takeIfNotEmpty(): String? = if (this?.isNotEmpty() == true) this else null
```

## Step 4: MapStruct Mapper (Alternative - Recommended for Complex Mappings)

For automatic mapping with customization:

```kotlin
// File: repository/mapper/CustomerMapStruct.kt
@Mapper(componentModel = "spring")
interface CustomerMapStructMapper {
    
    @Mapping(source = "code", target = "legacyCode")
    @Mapping(source = "name", target = "name", qualifiedByName = "normalizeName")
    @Mapping(source = "fantasyName", target = "fantasyName", qualifiedByName = "trimString")
    @Mapping(source = "email", target = "email", qualifiedByName = "normalizeEmail")
    fun projectToModel(projection: CustomerProjection): Customer
    
    @IterableMapping(qualifiedByName = "projectToModel")
    fun projectionsToModels(projections: List<CustomerProjection>): List<Customer>
    
    @Named("normalizeName")
    fun normalizeName(name: String): String {
        return name.trim()
            .replace(Regex("\\s+"), " ")
            .replaceFirstChar { it.uppercase() }
    }
    
    @Named("trimString")
    fun trimString(value: String?): String? {
        return value?.trim().takeIfNotEmpty()
    }
    
    @Named("normalizeEmail")
    fun normalizeEmail(email: String?): String? {
        if (email == null) return null
        val trimmed = email.trim().lowercase()
        return if (trimmed.contains("@")) trimmed else null
    }
}

private fun String?.takeIfNotEmpty(): String? = if (this?.isNotEmpty() == true) this else null
```

Add to `build.gradle.kts`:
```kotlin
dependencies {
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")
}
```

## Step 5: Data Validation in Mapper

```kotlin
@Component
class CustomerMapper {
    
    fun projectToModel(projection: CustomerProjection): Customer {
        validateProjection(projection)
        return Customer(
            legacyCode = projection.code,
            name = normalizeName(projection.name),
            email = normalizeEmail(projection.email),
            // ... other fields
        )
    }
    
    private fun validateProjection(projection: CustomerProjection) {
        require(projection.code > 0) { "Customer code must be positive" }
        require(projection.name.isNotBlank()) { "Customer name cannot be blank" }
        
        // Optional: Check for suspicious values
        if (projection.email?.contains("invalid") == true) {
            throw DataValidationException("Invalid email detected: ${projection.email}")
        }
    }
}
```

## Step 6: Batch Mapping for Performance

When migrating large datasets:

```kotlin
@Component
class CustomerMapper {
    
    fun projectionsToModelsBatch(projections: List<CustomerProjection>, batchSize: Int = 1000): Sequence<List<Customer>> {
        return projections
            .asSequence()
            .chunked(batchSize)
            .map { batch -> batch.map { projectToModel(it) } }
    }
}

// Usage in Service
@Service
class CustomerMigrationService(
    private val repository: FirebirdCustomerRepository,
    private val pgRepository: CustomerRepository,
    private val mapper: CustomerMapper
) {
    
    fun migrateBatch(batchSize: Int = 1000) {
        val projections = repository.findAllCustomers()
        
        mapper.projectionsToModelsBatch(projections, batchSize).forEach { batch ->
            pgRepository.saveAll(batch)
            logger.info("Migrated batch of ${batch.size} customers")
        }
    }
}
```

## Step 7: Error Handling in Mapper

```kotlin
@Component
class CustomerMapper {
    
    data class MappingResult(
        val success: Customer?,
        val error: MappingError? = null,
        val projectionCode: Int? = null
    )
    
    data class MappingError(
        val message: String,
        val code: Int,
        val cause: Exception? = null
    )
    
    fun projectToModelSafe(projection: CustomerProjection): MappingResult {
        return try {
            MappingResult(
                success = projectToModel(projection),
                projectionCode = projection.code
            )
        } catch (e: Exception) {
            MappingResult(
                success = null,
                error = MappingError(
                    message = "Failed to map customer: ${e.message}",
                    code = projection.code,
                    cause = e
                ),
                projectionCode = projection.code
            )
        }
    }
    
    fun projectionsToModelsSafe(projections: List<CustomerProjection>): Pair<List<Customer>, List<MappingError>> {
        val results = projections.map { projectToModelSafe(it) }
        
        val successful = results.mapNotNull { it.success }
        val errors = results.mapNotNull { it.error }
        
        if (errors.isNotEmpty()) {
            logger.warn("${errors.size} records failed mapping out of ${projections.size}")
            errors.forEach { logger.debug("Mapping error for code ${it.code}: ${it.message}") }
        }
        
        return Pair(successful, errors)
    }
}
```

## Step 8: Testing Mappers

```kotlin
@ExtendWith(MockitoExtension::class)
class CustomerMapperTest {
    
    private lateinit var mapper: CustomerMapper
    
    @BeforeEach
    fun setup() {
        mapper = CustomerMapper()
    }
    
    @Test
    fun `should map projection to model`() {
        // Arrange
        val projection = CustomerProjection(
            code = 123,
            name = "  John Doe  ",
            email = "JOHN@EXAMPLE.COM",
            phone = "(11) 9999-9999"
        )
        
        // Act
        val model = mapper.projectToModel(projection)
        
        // Assert
        assertThat(model.legacyCode).isEqualTo(123)
        assertThat(model.name).isEqualTo("John Doe")  // Trimmed, single space
        assertThat(model.email).isEqualTo("john@example.com")  // Lowercase
        assertThat(model.phone).isEqualTo("1199999999")  // Only digits
    }
    
    @Test
    fun `should handle null email`() {
        val projection = CustomerProjection(code = 123, name = "Test", email = null)
        val model = mapper.projectToModel(projection)
        assertThat(model.email).isNull()
    }
    
    @Test
    fun `should reject empty name`() {
        val projection = CustomerProjection(code = 123, name = "   ")
        assertThrows<IllegalArgumentException> {
            mapper.projectToModel(projection)
        }
    }
}
```

## Step 9: Integration with Service

```kotlin
@Service
@Transactional
class CustomerMigrationService(
    private val firebirdbRepository: FirebirdCustomerRepository,
    private val postgresRepository: CustomerRepository,
    private val mapper: CustomerMapper
) {
    
    fun migrateCustomers(): MigrationResult {
        logger.info("Starting customer migration")
        
        val projections = firebirdbRepository.findAllCustomers()
        logger.info("Found ${projections.size} customers in Firebird")
        
        val (models, errors) = mapper.projectionsToModelsSafe(projections)
        logger.info("Successfully mapped ${models.size} customers, ${errors.size} errors")
        
        val saved = postgresRepository.saveAll(models)
        logger.info("Saved ${saved.size} customers to PostgreSQL")
        
        return MigrationResult(
            totalProcessed = projections.size,
            successCount = saved.size,
            errorCount = errors.size,
            errors = errors
        )
    }
}

data class MigrationResult(
    val totalProcessed: Int,
    val successCount: Int,
    val errorCount: Int,
    val errors: List<CustomerMapper.MappingError> = emptyList()
)
```

## Mapping Strategies by Data Type

### String Normalization
```kotlin
// Trim and normalize spaces
.trim().replace(Regex("\\s+"), " ")

// Remove special characters
.replace(Regex("[^a-zA-Z0-9]"), "")

// Standardize case
.lowercase()
.replaceFirstChar { it.uppercase() }
```

### Number Handling
```kotlin
// Firebird NUMERIC(15, 4) to Kotlin
val bigDecimal: BigDecimal = rs.getBigDecimal("price")
val asBigDecimal: BigDecimal? = projection.price

// Safe conversion to Int/Double
val asInt = projection.quantity?.toInt() ?: 0
val asDouble = projection.amount?.toDouble() ?: 0.0
```

### Date/Time
```kotlin
// Firebird TIMESTAMP to LocalDateTime
val fireDate: Timestamp = rs.getTimestamp("date_field")
val localDate: LocalDateTime = fireDate.toLocalDateTime()

// Handle null dates
val safeDateMapped: LocalDateTime? = projection.date?.takeIf { it != LocalDateTime.MIN }
```

### NULL Handling Best Practices
```kotlin
// Use Elvis operator
val value = projection.field ?: "default"

// Use takeIf for conditional inclusion
val email = projection.email?.takeIf { it.contains("@") }

// Use Optional in Java style
val optional = Optional.ofNullable(projection.value)
```

## Idempotency & Upsert Patterns

```kotlin
@Service
class CustomerMigrationService(
    private val mapper: CustomerMapper,
    private val repository: CustomerRepository
) {
    
    fun migrateIdempotent(projection: CustomerProjection) {
        val model = mapper.projectToModel(projection)
        
        // Check if already migrated
        val existing = repository.findByLegacyCode(model.legacyCode)
        
        if (existing != null) {
            // Update only if legacy data changed
            if (existing.legacyUpdatedAt != model.legacyUpdatedAt) {
                repository.save(existing.copy(
                    name = model.name,
                    email = model.email,
                    legacyUpdatedAt = model.legacyUpdatedAt
                ))
            }
        } else {
            // Insert new
            repository.save(model)
        }
    }
}
```

---

**Next Steps**: 
1. Create mappers for each Firebird table you want to migrate
2. Write tests for each mapper
3. Use the api-integrator agent to send mapped models to the new service
