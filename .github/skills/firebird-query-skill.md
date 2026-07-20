# Firebird Native Query Skill

## Overview
This skill provides templates and best practices for writing native Firebird SQL queries for the emerion-load-service project.

## Firebird SQL Characteristics
- **Dialect 1** used in this project (legacy, but more compatible)
- External functions defined in UdfSade.dll (available as stored procedures)
- Supports GDSCODE for error handling
- TIMESTAMP format: YYYY-MM-DD HH:MM:SS
- String manipulation functions: LTRIM, RTRIM, TRIM, SUBSTR
- NUMERIC precision: NUMERIC(15, 4), NUMERIC(15, 6), etc.
- Character sets: NONE, ASCII, WIN1252
- No LIMIT clause (use FIRST/SKIP instead)

## Query Template: Native Query Pattern

### Basic Read Query
```kotlin
@Repository
class FirebirdCustomerRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun findAllCustomers(): List<CustomerProjection> {
        val sql = """
            SELECT 
                C.CODCLI as code,
                C.NOMCLI as name,
                C.FANTASIA as fantasy_name,
                C.ENDCLI as address,
                C.NUMEND as number,
                C.BRECLI as district,
                C.CEPCLI as zip_code,
                C.SIGUF as state,
                C.DTALTE as last_update
            FROM FINCLI C
            WHERE C.FLGEXC = 0
            ORDER BY C.NOMCLI
        """.trimIndent()
        
        return jdbcTemplate.query(sql) { rs, _ ->
            CustomerProjection(
                code = rs.getInt("code"),
                name = rs.getString("name")?.trim() ?: "",
                fantasyName = rs.getString("fantasy_name")?.trim(),
                address = rs.getString("address")?.trim(),
                number = rs.getString("number")?.trim(),
                district = rs.getString("district")?.trim(),
                zipCode = rs.getString("zip_code")?.trim(),
                state = rs.getString("state")?.trim(),
                lastUpdate = rs.getTimestamp("last_update")?.toLocalDateTime()
            )
        }
    }
}
```

### Query with Filtering and Pagination
```kotlin
fun findCustomersPaginated(
    page: Int = 0,
    pageSize: Int = 100,
    status: String? = null
): List<CustomerProjection> {
    val offset = page * pageSize
    val sql = """
        SELECT FIRST ? SKIP ? 
            C.CODCLI as code,
            C.NOMCLI as name
        FROM FINCLI C
        WHERE 1 = 1
            ${if (status != null) "AND C.STATUS = ?" else ""}
            AND C.FLGEXC = 0
        ORDER BY C.CODCLI
    """.trimIndent()
    
    val params = mutableListOf<Any>(pageSize, offset)
    if (status != null) params.add(status)
    
    return jdbcTemplate.query(sql, params.toTypedArray()) { rs, _ ->
        CustomerProjection(
            code = rs.getInt("code"),
            name = rs.getString("name")
        )
    }
}
```

### Query with Joins and Aggregates
```kotlin
fun findCustomersWithOrderStats(): List<CustomerWithOrdersProjection> {
    val sql = """
        SELECT 
            C.CODCLI as code,
            C.NOMCLI as name,
            COUNT(P.CODPED) as order_count,
            SUM(P.TOTPED) as total_value,
            MAX(P.DTOPED) as last_order_date
        FROM FINCLI C
        LEFT JOIN PEDRES P ON C.CODCLI = P.CODCLI AND P.FLGEXC = 0
        WHERE C.FLGEXC = 0
        GROUP BY C.CODCLI, C.NOMCLI
        HAVING COUNT(P.CODPED) > 0
        ORDER BY total_value DESC
    """.trimIndent()
    
    return jdbcTemplate.query(sql) { rs, _ ->
        CustomerWithOrdersProjection(
            code = rs.getInt("code"),
            name = rs.getString("name"),
            orderCount = rs.getInt("order_count"),
            totalValue = rs.getBigDecimal("total_value"),
            lastOrderDate = rs.getTimestamp("last_order_date")?.toLocalDateTime()
        )
    }
}
```

## Firebird String Functions
These are available as external functions:
- `LTRIM(str)` - Remove leading spaces
- `RTRIM(str)` - Remove trailing spaces
- `TRIM(str)` - Remove both leading and trailing spaces
- `RETSTRING(str, start, length)` - Substring
- `STRING_REPLACE(str, from_char, to_char)` - Replace

### Example with String Manipulation
```kotlin
fun findProductsByNamePattern(pattern: String): List<ProductProjection> {
    val sql = """
        SELECT 
            P.CODPRO as code,
            TRIM(P.NOMPRO) as name,
            P.PREUNI as price
        FROM ESTPRO P
        WHERE UPPER(TRIM(P.NOMPRO)) LIKE UPPER(?)
        ORDER BY P.NOMPRO
    """.trimIndent()
    
    return jdbcTemplate.query(sql, arrayOf("%$pattern%")) { rs, _ ->
        ProductProjection(
            code = rs.getString("code"),
            name = rs.getString("name"),
            price = rs.getBigDecimal("price")
        )
    }
}
```

## NULL Handling in Firebird
Firebird uses three-valued logic with NULL. Always check for NULLs:

```kotlin
// String with potential NULL
val email = rs.getString("email")?.trim() ?: ""

// Number with potential NULL
val quantity = rs.getInt("quantity")
if (rs.wasNull()) 0 else quantity

// Better approach for numbers
val quantity = rs.getBigDecimal("quantity")?.toInt() ?: 0
```

## NUMERIC Precision Types in the Schema
Common types in portal_2024_01.fdb:
- `NUMERIC(15, 2)` - Currency with 2 decimals (e.g., 9999999999999.99)
- `NUMERIC(15, 4)` - Quantity with 4 decimals
- `NUMERIC(15, 6)` - Precise calculations
- `NUMERIC(15, 8)` - Very precise

Map to Kotlin:
```kotlin
NUMERIC(15, 2) -> BigDecimal
NUMERIC(15, 4) -> BigDecimal
VARCHAR(n) -> String
CHAR(n) -> String (trim after reading)
TIMESTAMP -> LocalDateTime
INTEGER -> Int
SMALLINT -> Short
```

## Common Pitfalls & Solutions

### Pitfall 1: Firebird Returns CHAR as Padded Strings
**Problem**: CHAR(10) returns 10 chars always, padded with spaces
**Solution**: Use TRIM() in SQL or .trim() in Kotlin
```kotlin
val value = rs.getString("field")?.trim() ?: ""
```

### Pitfall 2: NULL Dates Are Not January 1, 1900
**Problem**: Old databases have sentinel dates like 1900-01-01
**Solution**: Check and handle dates explicitly
```kotlin
val date = rs.getTimestamp("date_field")
val result = if (rs.wasNull()) null else date?.toLocalDateTime()
```

### Pitfall 3: Dialect 1 Uses Old Date Format
**Problem**: Some queries return dates as strings
**Solution**: Use Firebird's DATETOSTR function or convert explicitly
```kotlin
SELECT DATETOSTR(DTACAD) FROM TABLE  // Returns YYYY-MM-DD
```

## Performance Tips for Large Queries

### Use FIRST/SKIP for Pagination
```kotlin
// Instead of SELECT * ... (no LIMIT)
// Use:
SELECT FIRST 100 SKIP 0 col1, col2 FROM table ORDER BY id
```

### Index Friendly Queries
- Always filter on indexed columns first
- Use IS NULL explicitly (not != NULL)
- Avoid function calls on indexed columns in WHERE

```kotlin
// Good
WHERE C.FLGEXC = 0 AND C.STATUS = 'A'

// Bad
WHERE UPPER(C.NOMPRO) LIKE '%X%'  // Function call, no index use
```

## Projection Interface Pattern

Always create a projection interface that exactly matches query result:

```kotlin
interface CustomerProjection {
    fun getCode(): Int
    fun getName(): String?
    fun getAddress(): String?
    fun getLastUpdate(): Timestamp?
}

// Or use data class for simplicity
data class CustomerProjectionDto(
    val code: Int,
    val name: String,
    val address: String? = null,
    val lastUpdate: LocalDateTime? = null
)
```

## RowMapper Implementation

```kotlin
class CustomerRowMapper : RowMapper<CustomerProjection> {
    override fun mapRow(rs: ResultSet, rowNum: Int): CustomerProjection? {
        return try {
            CustomerProjectionDto(
                code = rs.getInt("code"),
                name = rs.getString("name")?.trim() ?: "",
                address = rs.getString("address")?.trim(),
                lastUpdate = rs.getTimestamp("last_update")?.toLocalDateTime()
            )
        } catch (e: Exception) {
            logger.error("Error mapping customer row", e)
            null
        }
    }
}
```

## Testing Firebird Queries

Use testcontainers or a test database:

```kotlin
@DataJpaTest
class FirebirdRepositoryTest {
    
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    
    @BeforeEach
    fun setup() {
        // Insert test data
        jdbcTemplate.update("INSERT INTO FINCLI VALUES (?, ?, ?)", 1, "Test", "Firebird")
    }
    
    @Test
    fun testFindAllCustomers() {
        val result = customerRepository.findAllCustomers()
        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Test")
    }
}
```

## Useful Firebird External Functions (from schema)

The project has these available:
- `TRIM(str)` - Remove whitespace
- `LTRIM(str)` - Remove left whitespace
- `RTRIM(str)` - Remove right whitespace
- `FLOATTOSTR(value, decimals)` - Format float to string
- `INTTOSTR(value)` - Convert int to string
- `DATETOSTR(date)` - Convert date to string
- `FORMATCGCCPF(value)` - Format CPF/CNPJ (Brazilian IDs)
- `ROUND(value, decimals)` - Round number
- `SUBSTRING/RETSTRING(str, start, len)` - Extract substring

## Key Tables in portal_2024_01.fdb

- **FINCLI** - Customers (CODCLI, NOMCLI, FANTASIA, etc.)
- **ESTPRO** - Products (CODPRO, NOMPRO, PREUNI, etc.)
- **PEDRES** - Orders (CODPED, CODCLI, DTOPED, TOTPED, etc.)
- **CMPAEN** - Supplier purchases/receipts
- **CFOP** - CFOP codes (tax operations)
- **CEPBAI** - Postal codes and neighborhoods
- **CEPLOC** - Locations and municipalities
- **CEPTIT** - Title types

---

**Next Step**: Create projections that match your query results, then use the data-mapper-skill to transform them into internal models.
