# Firebird to PostgreSQL Migration - Technical Patterns

## Data Type Mapping
Map Firebird data types to PostgreSQL correctly:

| Firebird | PostgreSQL | Kotlin |
|----------|------------|--------|
| SMALLINT | SMALLINT | Short |
| INTEGER | INTEGER | Int |
| BIGINT | BIGINT | Long |
| FLOAT | FLOAT4 | Float |
| DOUBLE PRECISION | DOUBLE PRECISION | Double |
| DECIMAL(p,s) | NUMERIC(p,s) | java.math.BigDecimal |
| VARCHAR(n) | VARCHAR(n) | String |
| CHAR(n) | CHAR(n) | String |
| DATE | DATE | java.time.LocalDate |
| TIME | TIME | java.time.LocalTime |
| TIMESTAMP | TIMESTAMP | java.time.LocalDateTime |
| BLOB | BYTEA | ByteArray |
| CLOB | TEXT | String |
| BOOLEAN | BOOLEAN | Boolean |

## Handling NULL Values
Firebird often has different NULL semantics than PostgreSQL:
- Use `nullable = true` in JPA for columns that can be NULL
- In Kotlin: Use `String?` for nullable properties
- In queries: Handle NULL explicitly: `LEFT JOIN` vs `INNER JOIN`
- Example: `@Column(nullable = true) val description: String?`

## ID/Primary Key Strategy
- Firebird: Often uses GENERATOR/sequence-based IDs
- PostgreSQL: Use SERIAL or IDENTITY type
- JPA Configuration: `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Example:
```kotlin
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
val id: Long = 0
```

## Foreign Key Constraints
- Maintain referential integrity during migration
- Load parent entities before child entities
- Handle orphaned records (delete or nullify FK)
- In JPA: Use `@ManyToOne`, `@OneToMany`, etc.
- Consider cascade delete policies carefully
- Test cascade behavior thoroughly before production

## Sequence/Generator Handling
Firebird uses GENERATORs, PostgreSQL uses SERIAL/IDENTITY:
- For Firebird entities: `@GeneratedValue(strategy = GenerationType.AUTO)`
- For PostgreSQL entities: `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Or use explicit sequence: `@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "...Seq")`

## Transaction Management
Separate transactions for Firebird (read) and PostgreSQL (write):

```kotlin
@Service
class MigrationService(
    @Qualifier("firebird") val firebirdsql: JdbcTemplate,
    val postgresRepository: CustomerRepository
) {
    @Transactional("firebird")
    fun readFromFirebird(): List<FirebirdCustomer> {
        // Read from Firebird (different DataSource)
    }

    @Transactional("postgres")  // or just @Transactional (default)
    fun writeToPostgres(data: List<PostgresCustomer>) {
        // Write to PostgreSQL
    }
}
```

## Character Encoding
- Firebird: Often uses ISO-8859-1 (Latin-1)
- PostgreSQL: UTF-8 by default
- Handle string conversion: Consider charset encoding/decoding
- Test with special characters and accents

## Numeric Precision
- Firebird DECIMAL(9,2) != PostgreSQL NUMERIC(9,2) exactly
- Use BigDecimal for financial data (never Float/Double)
- Test rounding behavior after migration
- Firebird may truncate, PostgreSQL may round

## Date/Time Handling
- Firebird: DATE, TIME, TIMESTAMP separate types
- PostgreSQL: Similar but precision differences
- Use java.time.LocalDate, LocalTime, LocalDateTime
- Avoid java.util.Date (legacy)
- Test timezone handling if applicable

## Large Data Migrations
For large Firebird tables (millions of rows):
- Implement batch processing: Read in chunks, insert in batches
- Use `saveAll(List)` for batch inserts
- Monitor memory usage
- Log progress: "Migrated 50000/1000000 records..."
- Consider parallel processing with caution (transaction isolation)
- Implement idempotency (migrations must be re-runnable)

```kotlin
@Service
class BatchMigrationService(val firebirdsql: JdbcTemplate, val repository: Repository) {
    fun migrateInBatches(batchSize: Int = 1000) {
        var offset = 0
        while (true) {
            val batch = firebirdsql.query(
                "SELECT * FROM FIREBIRD_TABLE OFFSET $offset ROWS FETCH FIRST $batchSize ROWS ONLY"
            ) { rs -> mapRow(rs) }
            if (batch.isEmpty()) break
            repository.saveAll(batch)
            offset += batchSize
        }
    }
}
```

## Duplicate Detection & De-duplication
Firebird data may have duplicates:
- Implement uniqueness constraints in PostgreSQL
- Query for duplicates before migration: `GROUP BY ... HAVING COUNT(*) > 1`
- Decide strategy: keep first, keep last, merge
- Log duplicates and affected IDs for auditing

## Data Validation Rules
After migration, implement checks:
- Count comparison: FirebirdCount == PostgresCount
- Checksum on critical fields
- Referential integrity validation
- NULL values validation
- Date range validation
- Numeric range validation

## Idempotency & Re-runs
Design migrations to be safe to re-run:
- Don't assume records don't exist
- Use UPSERT pattern (INSERT ... ON CONFLICT ... DO UPDATE)
- Or: Check if exists, skip if yes, insert if no
- Track migration state (migration version, last processed ID)
- Log what was migrated and skipped

## Error Handling During Migration
```kotlin
@Service
class RobustMigrationService(...) {
    fun migrate() {
        val errors = mutableListOf<MigrationError>()
        
        firebirdsql.queryForList("SELECT id FROM FIREBIRD_TABLE").forEach { row ->
            try {
                val data = transformData(row)
                repository.save(data)
            } catch (e: Exception) {
                errors.add(MigrationError(row["id"], e.message))
                logger.error("Failed to migrate record ${row["id"]}", e)
            }
        }
        
        if (errors.isNotEmpty()) {
            logger.error("Migration completed with ${errors.size} errors")
            // Store errors in log table or file
        }
    }
}
```

## API Exposure
After migration, expose data via REST:
- Don't expose raw Firebird/PostgreSQL entities
- Use DTOs for API responses
- Support filtering, pagination, sorting
- Document API with Swagger/SpringDoc OpenAPI
- Cache frequently accessed data (Redis) if needed

## Testing Strategy
```kotlin
// Test Firebird read
@DataJpaTest
class FirebirdRepositoryTest { ... }

// Test transformation
class TransformerTest { ... }

// Test PostgreSQL write
@DataJpaTest
class PostgresRepositoryTest { ... }

// Integration test (full flow with test containers)
@SpringBootTest
@Testcontainers
class MigrationIntegrationTest { ... }
```

## Monitoring & Logging
- Log start/end times
- Log record counts: read, transformed, written, failed
- Track data quality metrics
- Alert on errors or anomalies
- Store migration audit trail (timestamps, counts, errors)

Example:
```kotlin
logger.info("Starting migration of CUSTOMERS table")
logger.info("Read ${readCount} records from Firebird")
logger.info("Migrated ${successCount} records to PostgreSQL")
logger.error("Failed to migrate ${errorCount} records")
```
