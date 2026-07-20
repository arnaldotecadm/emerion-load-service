# Database Metadata Reference

This folder contains comprehensive metadata and documentation about the Firebird database used in the emerion-load-service project.

## Files in This Folder

### 1. `REACT_API_CONTRACTS.md` ⭐ **START HERE**
Exact JSON schemas for all 11 API endpoints needed by the React dashboard.

**Contains**:
- Response shapes for each endpoint (customer, orders, sales summary, etc.)
- Which fields are used on the dashboard
- Which Firebird tables map to each response
- Implementation roadmap (Phase 1, 2, 3)
- Field-by-field rationale for what data is needed

**When to use**: 
- Planning API development for Phase 1/2/3
- Understanding what the React app actually needs
- Writing REST endpoint contracts
- Validating query results match expected response shape

**Key insight**: Only 2 endpoints are live (customer), everything else still mocked. **Phase 1 (orders)** = 70% of dashboard value.

---

### 2. `firebird-schema-portal_2024_01.md`
Complete reference for the `portal_2024_01.fdb` Firebird database.

**Contains**:
- Connection details (SYSDBA user, Dialect 1)
- All custom domains and data types
- Key tables (FINCLI, ESTPRO, PEDRES, etc.)
- Generators (sequences) for auto-increment
- External functions available in UdfSade.dll
- Character set information
- Data type mapping (Firebird → PostgreSQL → Kotlin)
- Common query patterns
- Migration path dependencies
- Important considerations for queries

**When to use**: 
- Planning which tables to migrate
- Understanding data types for entity creation
- Creating native SQL queries
- Mapping Firebird data to Kotlin models

**How Copilot uses it**:
- Reference when creating SQL queries
- Look up table structures and field names
- Understand domain/custom type definitions
- Validate NULL handling patterns
- Check data type conversions

---

### 3. `ESSENTIAL_FIELDS_FOR_REACT_APP.md` ⭐ **NEW**
Minimum viable field set for Phase 1 implementation (orders).

**Contains**:
- 6 essential PEDRES fields (vs 50+ total)
- 7 essential PEDRE2 fields (vs 40+ total)  
- 4 essential FATPED fields (vs 40+ total)
- SQL template showing exact query structure
- Field exclusion rationale (why certain fields are excluded)
- Implementation roadmap (which fields to add in Phase 2/3)

**When to use**:
- Writing queries for orders, order items, and invoices
- Creating Order/OrderItem/Invoice entities
- Validating your query results against dashboard needs
- Understanding what NOT to include (to reduce payload size)

**Key insight**: 87% field reduction achieved by excluding tax, cost, GL, and audit fields.

---

### 4. `TABLE_NAMING_CONVENTION.md` ⭐ **NEW**
Guide to Firebird's "2" suffix detail table pattern (critical for orders).

**Contains**:
- Explanation of parent-child relationships (PEDRES ↔ PEDRE2, FATPED ↔ FATPE2)
- Migration order requirements (always parent before child)
- Composite primary key patterns
- FLGEXC soft-delete flag handling
- SQL examples for parent-child queries
- Kotlin entity relationship examples
- Testing patterns for detail tables

**When to use**:
- Understanding why PEDRE2 exists and how it relates to PEDRES
- Writing queries that join parent + child tables
- Creating entity relationships in Kotlin
- Debugging missing order items

**Key insight**: All tables ending with "2" follow same pattern; migrate parent first to maintain referential integrity.



## Quick Reference: Key Tables

⚠️ **Remember**: Tables ending with "2" are detail/item tables for the main table!

| Main Table | Detail Table | Purpose | Primary Key | Notes |
|-----------|-------------|---------|-------------|-------|
| **FINCLI** | - | Customers | CODCLI | Most important, filter FLGEXC=0 |
| **ESTPRO** | - | Products | CODPRO | Use TRIM() on names |
| **PEDRES** | **PEDRE2** | Orders & Items | CODPED | Migrate PEDRES before PEDRE2 |
| - | **PEDRE2** | Order Items (detail) | CODPED + SEQITEM | Always migrate parent first |
| **FINFOR** | - | Suppliers | CODFOR | Reference table |
| **CMPAEN** | - | Supplier Receipts | CODEMP + DTEENT + NUMENT | Complex key |
| **CFOP** | - | Tax Codes | CODCFO | Reference table |

---

## Important Patterns (Copy-Paste)

### Filter Active Records
```sql
WHERE TABLE.FLGEXC = 0
```

### Trim String Fields
```sql
SELECT TRIM(NOMCLI) FROM FINCLI
```

### Pagination (Firebird No LIMIT)
```sql
SELECT FIRST 100 SKIP 0 * FROM TABLE ORDER BY ID
```

### NULL Handling
```sql
WHERE EMAIL IS NOT NULL
COALESCE(FIELD, '') as field_default
```

### Date Conversion
```sql
SELECT DATETOSTR(DTACAD) FROM TABLE
```

---

## Data Type Quick Reference

| Firebird Domain | Firebird Type | PostgreSQL | Kotlin | Notes |
|-----------------|---------------|-----------|--------|-------|
| BOOLEAN | SMALLINT | SMALLINT | Boolean | 0=false, 1=true |
| NUMERIC_15_2 | NUMERIC(15,2) | NUMERIC(15,2) | BigDecimal | Currency, prices |
| NUMERIC_15_4 | NUMERIC(15,4) | NUMERIC(15,4) | BigDecimal | Quantities |
| NUMERIC_15_6 | NUMERIC(15,6) | NUMERIC(15,6) | BigDecimal | Precision |
| DOM_STROBS | VARCHAR(600) | VARCHAR(600) | String | Observations |
| DOM_DATE | TIMESTAMP | TIMESTAMP | LocalDateTime | With time |
| SIG_UF | CHAR(2) | CHAR(2) | String | State code, trim |

---

## Migration Checklist

Before migrating a table, ensure:
- [ ] **Start with `REACT_API_CONTRACTS.md`** - understand what endpoint you're building
- [ ] Reference `ESSENTIAL_FIELDS_FOR_REACT_APP.md` - know which fields are actually needed
- [ ] Check `TABLE_NAMING_CONVENTION.md` (if detail table like PEDRE2, FATPE2)
- [ ] Look up table in `firebird-schema-portal_2024_01.md`
- [ ] All domains/fields mapped to Kotlin types
- [ ] FLGEXC filter applied (if applicable)
- [ ] TRIM() applied to CHAR fields
- [ ] NULL handling defined
- [ ] Projection interface created (matches query result)
- [ ] Mapper created (projection → model)
- [ ] Entity created in PostgreSQL package
- [ ] Repository created for PostgreSQL
- [ ] Tests written for mapper
- [ ] Migration service created
- [ ] REST endpoint created (matching `REACT_API_CONTRACTS.md` shape)

---

## Common Gotchas

### 1. CHAR Fields Are Padded
```kotlin
// WRONG
val name = rs.getString("name")  // Has trailing spaces

// CORRECT
val name = rs.getString("name")?.trim() ?: ""
```

### 2. No LIMIT in Firebird
```sql
-- WRONG
SELECT * FROM TABLE LIMIT 10

-- CORRECT
SELECT FIRST 10 FROM TABLE
SELECT FIRST 10 SKIP 20 FROM TABLE  -- For pagination
```

### 3. NULL Comparisons
```sql
-- WRONG
WHERE EMAIL = NULL

-- CORRECT
WHERE EMAIL IS NULL
WHERE EMAIL IS NOT NULL
```

### 4. Firebird Uses WIN1252 by Default
Be aware of encoding when migrating text data. May need normalization.

### 5. Transactions Span Multiple DBs
- Firebird: Read-only, no transactional boundary needed
- PostgreSQL: Wrap saves in @Transactional
- API calls: Handle separately

---

## Using This with Skills

The database metadata integrates with the project's skills:

### With `firebird-query-skill.md`
Use the table/field names from here when writing native queries.

### With `data-mapper-skill.md`
Use the data type mappings from here when creating mappers.

**Example flow**:
1. Look up table in `firebird-schema-portal_2024_01.md`
2. Write query using `firebird-query-skill.md` patterns
3. Create projection matching query result
4. Map to model using `data-mapper-skill.md` patterns

---

## Updating This Metadata

As you discover new information about the database:
1. Add new domains or fields to `firebird-schema-portal_2024_01.md`
2. Document any gotchas or special handling needed
3. Add new query patterns as examples
4. Update this README if structure changes

---

**Next Step**: Start with the FINCLI table using the skills and this metadata.
