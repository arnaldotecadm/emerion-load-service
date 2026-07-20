# Firebird Native Query Skill

## Description
Guidance for working with native SQL queries against the legacy Firebird database using JdbcTemplate, projections, and mappers.

## When to Use
- Writing SQL SELECT queries for Firebird extraction
- Creating projection interfaces for query results
- Implementing mapper classes to transform projections to models
- Handling NULL values and type conversions
- Building batch query logic with offsets/limits

## Key Patterns

### 1. Firebird Repository Interface (Native Queries)
```kotlin
package br.com.vercel.emerionloadservice.repository.firebird

interface IFirebirdCustomerQueries {
    fun findAllCustomers(): List<CustomerProjection>
    fun findCustomersWithOrders(): List<CustomerWithOrdersProjection>
    fun findCustomersByStatus(status: String): List<CustomerProjection>
    fun findCustomersInBatch(offset: Int, limit: Int): List<CustomerProjection>
}
```

### 2. Firebird Repository Implementation (JdbcTemplate)
```kotlin
package br.com.vercel.emerionloadservice.repository.firebird

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class FirebirdCustomerRepository(val jdbcTemplate: JdbcTemplate) : IFirebirdCustomerQueries {
    
    override fun findAllCustomers(): List<CustomerProjection> {
        val sql = """
            SELECT 
                c.ID, c.NAME, c.EMAIL, c.CREATED_AT, c.STATUS
            FROM CUSTOMER c
            WHERE c.DELETED = 0
            ORDER BY c.ID
        """
        return jdbcTemplate.query(sql, customerRowMapper())
    }
    
    override fun findCustomersInBatch(offset: Int, limit: Int): List<CustomerProjection> {
        val sql = """
            SELECT 
                c.ID, c.NAME, c.EMAIL, c.CREATED_AT, c.STATUS
            FROM CUSTOMER c
            WHERE c.DELETED = 0
            ORDER BY c.ID
            OFFSET $offset ROWS FETCH FIRST $limit ROWS ONLY
        """
        return jdbcTemplate.query(sql, customerRowMapper())
    }
    
    private fun customerRowMapper(): RowMapper<CustomerProjection> {
        return RowMapper { rs, _ ->
            CustomerProjection(
                id = rs.getLong("ID"),
                name = rs.getString("NAME"),
                email = rs.getString("EMAIL"), // May be NULL
                createdAt = rs.getTimestamp("CREATED_AT")?.toLocalDateTime(),
                status = rs.getString("STATUS")
            )
        }
    }
}
```

### 3. Projection Interface (Query Result Holder)
```kotlin
package br.com.vercel.emerionloadservice.repository.projection

// Simple interface representing Firebird query result
interface CustomerProjection {
    fun getId(): Long
    fun getName(): String
    fun getEmail(): String?  // Nullable
    fun getCreatedAt(): java.time.LocalDateTime?
    fun getStatus(): String
}

// Or as a data class
data class CustomerProjectionImpl(
    val id: Long,
    val name: String,
    val email: String?,
    val createdAt: java.time.LocalDateTime?,
    val status: String
) : CustomerProjection {
    override fun getId() = id
    override fun getName() = name
    override fun getEmail() = email
    override fun getCreatedAt() = createdAt
    override fun getStatus() = status
}
```

### 4. Internal Model (Kotlin Data Class - No JPA)
```kotlin
package br.com.vercel.emerionloadservice.model

import java.time.LocalDateTime

// NOT a JPA entity - just a business domain model
data class Customer(
    val id: Long,
    val name: String,
    val email: String,  // Non-nullable after mapping
    val createdAt: LocalDateTime,
    val status: String,
    val migrationId: String? = null  // For tracking
)
```

### 5. Mapper (Projection → Model)
```kotlin
package br.com.vercel.emerionloadservice.mapper

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.projection.CustomerProjection
import org.springframework.stereotype.Component

@Component
class CustomerMapper {
    
    fun projectToModel(projection: CustomerProjection): Customer {
        return Customer(
            id = projection.getId(),
            name = projection.getName().trim(),  // Clean whitespace
            email = projection.getEmail()?.trim() ?: "unknown@example.com",  // Default if NULL
            createdAt = projection.getCreatedAt() ?: java.time.LocalDateTime.now(),
            status = normalizeStatus(projection.getStatus())
        )
    }
    
    fun projectionsToModels(projections: List<CustomerProjection>): List<Customer> {
        return projections.map { projectToModel(it) }
    }
    
    private fun normalizeStatus(status: String?): String {
        return when (status?.uppercase()) {
            "A", "ACTIVE" -> "ACTIVE"
            "I", "INACTIVE" -> "INACTIVE"
            else -> "UNKNOWN"
        }
    }
}
```

### 6. Service (Orchestrates Query → Map → Send)
```kotlin
package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.mapper.CustomerMapper
import br.com.vercel.emerionloadservice.repository.firebird.FirebirdCustomerRepository
import br.com.vercel.emerionloadservice.client.ApiServiceClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CustomerMigrationService(
    val firebirdsqlRepository: FirebirdCustomerRepository,
    val mapper: CustomerMapper,
    val apiClient: ApiServiceClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    fun migrateAllCustomers() {
        logger.info("Starting customer migration")
        
        // 1. QUERY - Extract from Firebird (returns projections)
        val projections = firebirdsqlRepository.findAllCustomers()
        logger.info("Extracted ${projections.size} customer projections from Firebird")
        
        // 2. MAP - Convert projections to internal models
        val models = mapper.projectionsToModels(projections)
        logger.info("Mapped projections to internal models")
        
        // 3. SEND - Send to new API service
        apiClient.sendCustomers(models)
        logger.info("Sent ${models.size} customers to new API service")
    }
    
    fun migrateInBatches(batchSize: Int = 1000) {
        logger.info("Starting batch migration (batch size: $batchSize)")
        
        var offset = 0
        var totalMigrated = 0
        
        while (true) {
            // 1. QUERY - Extract batch from Firebird
            val projections = firebirdsqlRepository.findCustomersInBatch(offset, batchSize)
            if (projections.isEmpty()) break
            
            // 2. MAP - Convert batch to models
            val models = mapper.projectionsToModels(projections)
            
            // 3. SEND - Send batch to new service
            try {
                apiClient.sendCustomers(models)
                totalMigrated += models.size
                logger.info("Migrated batch: offset=$offset, count=${models.size}, total=$totalMigrated")
            } catch (e: Exception) {
                logger.error("Failed to migrate batch at offset $offset", e)
                // Decide: throw or continue?
            }
            
            offset += batchSize
        }
        
        logger.info("Batch migration complete: total migrated = $totalMigrated")
    }
}
```

## SQL Query Patterns for Firebird

### Simple Query
```sql
SELECT ID, NAME, EMAIL, CREATED_AT
FROM CUSTOMER
WHERE DELETED = 0
ORDER BY ID
```

### Query with JOINs
```sql
SELECT 
    c.ID, c.NAME, c.EMAIL,
    COUNT(o.ID) AS ORDER_COUNT
FROM CUSTOMER c
LEFT JOIN ORDERS o ON c.ID = o.CUSTOMER_ID
WHERE c.DELETED = 0
GROUP BY c.ID, c.NAME, c.EMAIL
ORDER BY c.ID
```

### Query with Batch/Pagination
```sql
SELECT ID, NAME, EMAIL, CREATED_AT
FROM CUSTOMER
WHERE DELETED = 0
ORDER BY ID
OFFSET ? ROWS FETCH FIRST ? ROWS ONLY
```

### Query with NULL Handling
```sql
SELECT 
    ID,
    NAME,
    COALESCE(EMAIL, 'N/A') AS EMAIL,
    CAST(CREATED_AT AS TIMESTAMP) AS CREATED_AT
FROM CUSTOMER
WHERE DELETED = 0
```

## Best Practices

### DO's
✓ Use JdbcTemplate for native queries (more control, no ORM overhead)
✓ Create projections as interfaces or simple data classes
✓ Map projections to models before sending (validation point)
✓ Handle NULL values explicitly in mappers
✓ Use batch queries with OFFSET/FETCH for large datasets
✓ Log query execution and transformation steps
✓ Test SQL queries separately (they're complex!)

### DON'Ts
✗ Don't use JPA entities for Firebird (legacy schema may not work)
✗ Don't send projections directly to API (they're internal)
✗ Don't leave NULL values unhandled (cause NPE later)
✗ Don't skip mapping (validation and enrichment happen here)
✗ Don't assume Firebird column names match PostgreSQL
✗ Don't forget OFFSET/FETCH for large tables (memory issues)

## Testing Native Queries

```kotlin
@DataJpaTest
class FirebirdCustomerRepositoryTest {
    @Autowired
    val jdbcTemplate: JdbcTemplate
    
    val repository = FirebirdCustomerRepository(jdbcTemplate)
    
    @Test
    fun testFindAllCustomers() {
        val results = repository.findAllCustomers()
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.getName().isNotBlank() })
    }
}
```

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| NULL pointer in mapper | Unhandled NULL from Firebird | Use Elvis operator: `email ?: "default"` |
| Type mismatch | Firebird timestamp vs Java LocalDateTime | Use `rs.getTimestamp().toLocalDateTime()` |
| SQL syntax error | Firebird dialect differences | Check Firebird SQL docs, use OFFSET...FETCH |
| Large memory usage | Loading entire table at once | Use batch queries with OFFSET/LIMIT |
| Projection data wrong | RowMapper column names incorrect | Verify column names match SQL SELECT |

## When to Create a New Query

1. Check if existing repository has the query
2. If not, add to interface with clear method name
3. Implement RowMapper for result set
4. Add corresponding mapper in Mapper class
5. Test with sample data
6. Document in service layer comments
