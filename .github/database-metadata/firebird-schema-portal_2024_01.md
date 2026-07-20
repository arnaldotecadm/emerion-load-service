# Firebird Database Metadata - portal_2024_01.fdb

## Database Connection Details
- **Database**: portal_2024_01.fdb
- **User**: SYSDBA
- **Dialect**: 1 (legacy but supported)
- **Character Set**: NONE, ASCII, WIN1252
- **Located at**: `/path/to/portal_2024_01.fdb`

---

## ⚠️ Important: Table Naming Convention

**"2" Suffix = Detail/Item Tables**

In this database, tables ending with "2" are **detail/item tables** that correspond to a main table:

| Main Table | Detail Table | Purpose | Relationship |
|-----------|-------------|---------|--------------|
| **PEDRES** | **PEDRE2** | Orders → Order Items | 1 Order has many Items |
| **FATPED** | **FATPE2** | Billing Orders → Billing Items | 1 Billing Order has many Items |
| **PEDLIB** | **PEDLB2** | Library/Catalog Orders → Items | 1 Catalog Order has many Items |
| **CMPAEN** | (main table) | Supplier Receipts | Single table (no detail table) |

### How to Query Detail Tables
```sql
-- Get order with all items
SELECT 
    P.CODPED,
    P.DTOPED,
    I.CODPRO,
    I.QUANTIDADE,
    I.PRECOS
FROM PEDRES P
JOIN PEDRE2 I ON P.CODPED = I.CODPED
WHERE P.FLGEXC = 0
    AND I.FLGEXC = 0
ORDER BY P.CODPED, I.SEQITEM
```

### Migration Pattern for Detail Tables
1. Migrate main table first (PEDRES → Orders)
2. Then migrate detail table (PEDRE2 → OrderItems)
3. Establish foreign key relationship in PostgreSQL
4. For batch processing: migrate in parent→child order to maintain referential integrity

---

## Domains (Custom Data Types)

### Boolean Domain
- `BOOLEAN` / `DOM_BOOLEAN` - SMALLINT (0 = false, 1 = true)
- `DOM_BOOLEAN` - Default

### Character Domains
- `CHAR_01` - CHAR(1)
- `CHAR_02`, `CHAR_03` - CHAR(3)
- `CHAR_06` - CHAR(6)
- `CHAR_07` - VARCHAR(7)
- `CHAR_08` - CHAR(8)
- `CHAR_10` - CHAR(10)
- `CHAR_15` - CHAR(15)
- `CHAR_16` - VARCHAR(16)
- `CHAR_2` - CHAR(2)
- `CHAR_20` - VARCHAR(20)
- `CHAR_30` - CHAR(30)
- `CHAR_70` - VARCHAR(70)

### String Domains (Variable Length)
- `DOM_STR005NN` - VARCHAR(5) NOT NULL
- `DOM_STR010` - VARCHAR(10)
- `DOM_STR020` - VARCHAR(20)
- `DOM_STR030` - VARCHAR(30)
- `DOM_STR050` - VARCHAR(50)
- `DOM_STR100` - VARCHAR(100)
- `DOM_STR200` - VARCHAR(200)
- `DOM_STROBS` - VARCHAR(600)
- `DVARCHAR_250` - VARCHAR(250)
- `STR_300` - VARCHAR(300)

### Numeric Domains
- `DOM_4DIG` - NUMERIC(15, 4)
- `NUMERIC_15_2` / `D_NUMERO_15_2` - NUMERIC(15, 2)
- `NUMERIC_15_3` - NUMERIC(15, 3)
- `NUMERIC_15_4` / `PRECISSAO154` - NUMERIC(15, 4)
- `NUMERIC_15_6` / `VL_PRECISSAO6` - NUMERIC(15, 6)
- `NUMERIC_15_8` - NUMERIC(15, 8)
- `DOM_FLOAT` / `DOM_FLOATNN` - FLOAT

### Integer Domains
- `DOM_INT` - INTEGER
- `DOM_INTNN` - INTEGER NOT NULL
- `DOM_ID_NNULL` - INTEGER NOT NULL
- `DOM_USUID` - INTEGER (User ID)

### Date/Time Domains
- `DOM_DATAHORA` - TIMESTAMP
- `DOM_DATAATUAL` - TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `DOM_DATAHORAATUAL` - TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `DOM_DATE` - TIMESTAMP
- `DOM_ATUUSUARIO` - TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Special Domains
- `DOM_CNPJ` - CHAR(14) - Brazilian company ID
- `DOM_USUARIO` - VARCHAR(10) - Username
- `DOM_NOMEUSU` - VARCHAR(60) - User name
- `DOM_SENHAUSU` - VARCHAR(20) - Password
- `DOM_EMAIL` - VARCHAR(70) - Email address
- `DOM_NCM` - CHAR(8) - Product classification
- `DOM_OBSFAT` - VARCHAR(2000) - Invoice observations
- `DOM_OBS_CLP` - VARCHAR(5000) - Large observations
- `SIG_UF` - CHAR(2) - State abbreviation (UF)

---

## Key Tables (Most Important for Migration)

### FINCLI (Customers)
Maps to internal `Customer` model
- **Primary Key**: CODCLI (Integer)
- **Key Fields**:
  - `CODCLI` - Customer code
  - `NOMCLI` - Customer name (VARCHAR(60))
  - `FANTASIA` - Fantasy name (VARCHAR(100))
  - `ENDCLI` - Address (VARCHAR(70))
  - `NUMEND` - Address number (VARCHAR(10))
  - `BRECLI` - District (VARCHAR(25))
  - `CEPCLI` - ZIP code (VARCHAR(10))
  - `SIGUF` - State (CHAR(2))
  - `TELEFONE` - Phone (VARCHAR(14))
  - `CELULAR` - Mobile (VARCHAR(14))
  - `EMAIL` - Email (VARCHAR(70))
  - `STATUS` - Status (VARCHAR(7))
  - `FLGEXC` - Excluded flag (SMALLINT) - 1 = excluded, 0 = active
  - `DTALTE` - Last update (TIMESTAMP)
- **Query Pattern**: Filter by `FLGEXC = 0` for active customers

### ESTPRO (Products)
Maps to internal `Product` model
- **Primary Key**: CODPRO (CHAR(5))
- **Key Fields**:
  - `CODPRO` - Product code
  - `NOMPRO` - Product name (VARCHAR(70))
  - `DESCPRO` - Product description (VARCHAR(255))
  - `PREUNI` - Unit price (NUMERIC(15, 4))
  - `ESTATU` - Status (CHAR(1))
  - `FLGEXC` - Excluded flag
- **Query Pattern**: Use `TRIM(NOMPRO)` for name normalization

### PEDRES (Orders - Main Table)
Maps to internal `Order` model
- **Primary Key**: CODPED (Integer)
- **Foreign Keys**:
  - `CODCLI` → FINCLI
- **Detail Table**: PEDRE2 (Order Items)
- **Key Fields**:
  - `CODPED` - Order code
  - `CODCLI` - Customer code
  - `DTOPED` - Order date (TIMESTAMP)
  - `DTEENT` - Delivery date (TIMESTAMP)
  - `TOTPED` - Total order (NUMERIC(15, 2))
  - `STATUS` - Status
  - `FLGEXC` - Excluded flag
- **Query Pattern**: Join with FINCLI, filter `FLGEXC = 0`

### PEDRE2 (Order Items - Detail Table)
Maps to internal `OrderItem` model
- **Primary Key**: CODPED + SEQITEM
- **Foreign Key**: CODPED → PEDRES
- **Key Fields**:
  - `CODPED` - Order code
  - `SEQITEM` - Item sequence
  - `CODPRO` - Product code
  - `QUANTIDADE` - Quantity (NUMERIC(15, 4))
  - `PRECOS` - Unit price (NUMERIC(15, 4))
  - `TOTITEM` - Item total (NUMERIC(15, 2))
  - `FLGEXC` - Excluded flag
- **Migration**: Always migrate PEDRES first, then PEDRE2

### FINFOR (Suppliers)
Maps to internal `Supplier` model
- **Primary Key**: CODFOR (Integer)
- **Key Fields**:
  - `CODFOR` - Supplier code
  - `NOMFOR` - Supplier name (VARCHAR(60))
  - `CNPJFOR` - CNPJ (CHAR(14))
  - `ENDFOR` - Address (VARCHAR(70))
  - `FLGEXC` - Excluded flag

### CMPAEN (Supplier Receipts/Purchases)
Maps to internal `SupplierReceipt` model
- **Primary Key**: CODEMP + DTEENT + NUMENT + SEQEN2
- **Key Fields**:
  - `CODEMP` - Company code
  - `DTEENT` - Entry date (TIMESTAMP)
  - `NUMENT` - Entry number
  - `CODPRO` - Product code
  - `TOTEN2` - Total (NUMERIC(15, 6))
  - `TOTICM` - ICM total (NUMERIC(15, 6))
  - `TOTIPI` - IPI total (NUMERIC(15, 6))
  - `TOTSUB` - Substitution total (NUMERIC(15, 6))

### CFOP (Tax Operation Codes)
Reference table - Maps to internal `CFOP` model
- **Primary Key**: CODCFO (CHAR(5))
- **Key Fields**:
  - `CODCFO` - CFOP code
  - `DESCFO` - Description (VARCHAR(1000))
  - `APLCFO` - Applicability (VARCHAR(1000))

---

## Generators (Sequences) for Auto-Increment

Common sequences used:
- `CODCLI_GEN` - Customer code
- `CODPRO_GEN` - Product code
- `CODPED_GEN` - Order code
- `CODFOR_GEN` - Supplier code
- `CODEMP_GEN` - Company code
- `CODUSU_GEN` - User code

**Usage in Firebird**:
```sql
SELECT GEN_ID(CODCLI_GEN, 1) FROM RDB$DATABASE;
```

**Mapping to PostgreSQL**: Use SERIAL or IDENTITY columns.

---

## External Functions (Stored Procedures via UdfSade.dll)

These are custom functions available in Firebird:

### String Functions
- `LTRIM(string)` - Left trim
- `RTRIM(string)` - Right trim
- `TRIM(string)` - Both sides trim
- `LENGTHSTR(string)` - Get string length
- `RETSTRING(string, start, length)` - Substring
- `STRING_REPLACE(string, from, to)` - Replace characters
- `LEFTCHAR(string)` - Get left characters
- `FORMATCGCCPF(string)` - Format Brazilian IDs

### Number Functions
- `INTTOSTR(integer)` - Convert int to string
- `FLOATTOSTR(float, decimals)` - Convert float to string
- `FLOATTOSTR_VIRGULA(float, decimals)` - Float to string (comma decimal)
- `INTSTRZEROS(integer, length)` - Int with leading zeros
- `FLOATSTRZEROS(float)` - Float with zeros
- `ROUND(double, decimals)` - Round number
- `TRUNCARVALOR(double, decimals)` - Truncate number
- `INTCALC(double)` - Interest calculation
- `MOD(integer, integer)` - Modulo

### Date Functions
- `DATETOSTR(date)` - Convert date to string (YYYY-MM-DD)
- `DAYYEAR(date)` - Day of year
- `YEAROFDATE(date)` - Year from date
- `MONTHOFYEAR(string)` - Month of year

### Time Functions
- `CONVERTIME(string)` - Convert time string
- `CONVERHORA(string)` - Convert hours
- `CONVERMINUTOS(string)` - Convert minutes
- `DIFERTIME(h1, m1, s1, h2, m2)` - Time difference
- `DIFERHORAS(time1, time2)` - Hours difference
- `SOMAHORAS(time1, time2)` - Add hours
- `HORASVALOR(time)` - Time value

### Other Utilities
- `EANDV(string)` - EAN validation
- `CHECKREG(param1, param2)` - Check register
- `ENCDIRECTORY(path)` - Encode directory path
- `FINDFILES(filename)` - Find file
- `FCHECKFILES(...)` - Check file existence

---

## Character Sets Used in Database

- `NONE` - No specific encoding (binary data)
- `ASCII` - ASCII encoding for specific fields
- `WIN1252` - Windows Latin-1 (Western European)

**Impact**: When reading strings, be aware of encoding. Most text columns use WIN1252.

---

## Important Query Patterns

### 1. Firebird FIRST/SKIP (No LIMIT)
```sql
SELECT FIRST 100 SKIP 0 * FROM FINCLI ORDER BY CODCLI
```

### 2. Handle TRIM and NULL
```sql
SELECT 
    CODCLI,
    TRIM(NOMCLI) as name,
    COALESCE(TRIM(EMAIL), '') as email
FROM FINCLI
WHERE FLGEXC = 0
```

### 3. Format Numbers
```sql
SELECT 
    FLOATTOSTR(PREUNI, 2) as price_formatted
FROM ESTPRO
```

### 4. Date Conversion
```sql
SELECT 
    DATETOSTR(DTOPED) as order_date_str
FROM PEDRES
```

### 5. Aggregates with External Functions
```sql
SELECT 
    TRIM(C.NOMCLI) as customer_name,
    COUNT(P.CODPED) as order_count,
    SUM(P.TOTPED) as total_value
FROM FINCLI C
LEFT JOIN PEDRES P ON C.CODCLI = P.CODCLI AND P.FLGEXC = 0
WHERE C.FLGEXC = 0
GROUP BY C.CODCLI, C.NOMCLI
ORDER BY total_value DESC
```

---

## Data Type Mapping: Firebird → PostgreSQL → Kotlin

| Firebird | PostgreSQL | Kotlin | Notes |
|----------|-----------|--------|-------|
| `CHAR(n)` | `CHAR(n)` | `String` | Trim in code |
| `VARCHAR(n)` | `VARCHAR(n)` | `String` | Handle NULL |
| `NUMERIC(15,2)` | `NUMERIC(15,2)` | `BigDecimal` | Currency |
| `NUMERIC(15,4)` | `NUMERIC(15,4)` | `BigDecimal` | Quantity |
| `NUMERIC(15,6)` | `NUMERIC(15,6)` | `BigDecimal` | Precision |
| `INTEGER` | `INTEGER` | `Int` | Check NULL |
| `SMALLINT` | `SMALLINT` | `Short` | Boolean flags |
| `TIMESTAMP` | `TIMESTAMP` | `LocalDateTime` | UTC handling |
| `FLOAT` | `FLOAT` | `Double` | Avoid for money |
| `BLOB` | `BYTEA`/`TEXT` | `ByteArray`/`String` | Large data |

---

## Important Considerations for Copilot

### 1. Always Trim String Values
Firebird CHAR columns are padded. Always trim.
```sql
SELECT TRIM(NOMCLI) FROM FINCLI  -- Do this!
```

### 2. Use FLGEXC = 0 to Filter Active Records
Most tables have a `FLGEXC` (excluded flag):
- `0` = Active/Not excluded
- `1` = Excluded/Deleted (soft delete)

### 3. Never Use NULL in Comparisons
Use `IS NULL`, not `= NULL`:
```sql
WHERE EMAIL IS NOT NULL  -- Good
WHERE EMAIL != NULL      -- Bad
```

### 4. Transaction Boundaries
Firebird queries are separate from PostgreSQL transactions:
```kotlin
// 1. Read from Firebird (no transaction needed)
val fromFb = firebirdbRepository.find()

// 2. Transform (in-memory)
val mapped = mapper.map(fromFb)

// 3. Write to PostgreSQL (separate @Transactional)
pgRepository.saveAll(mapped)
```

### 5. Batch Processing for Large Tables
For tables with millions of rows, process in batches:
```sql
SELECT FIRST 1000 SKIP 0 * FROM TABLE ORDER BY ID
-- Then in code: SKIP 1000, 2000, 3000, etc.
```

### 6. Use Generators for IDs in Legacy DB
To get the next ID in Firebird:
```sql
SELECT GEN_ID(CODCLI_GEN, 1) FROM RDB$DATABASE
```

But in PostgreSQL, use SERIAL/IDENTITY instead.

---

## Common Query Examples (Copy-Paste Ready)

### Get All Active Customers
```sql
SELECT 
    C.CODCLI,
    TRIM(C.NOMCLI) as name,
    TRIM(C.FANTASIA) as fantasy_name,
    TRIM(C.EMAIL) as email,
    C.DTALTE
FROM FINCLI C
WHERE C.FLGEXC = 0
ORDER BY C.CODCLI
```

### Get Customers with Order Count
```sql
SELECT 
    C.CODCLI,
    TRIM(C.NOMCLI) as name,
    COUNT(P.CODPED) as order_count,
    SUM(P.TOTPED) as total_value
FROM FINCLI C
LEFT JOIN PEDRES P ON C.CODCLI = P.CODCLI AND P.FLGEXC = 0
WHERE C.FLGEXC = 0
GROUP BY C.CODCLI, C.NOMCLI
ORDER BY total_value DESC
```

### Get Recent Orders with Items
```sql
SELECT 
    P.CODPED,
    C.CODCLI,
    TRIM(C.NOMCLI) as customer_name,
    P.DTOPED,
    P.TOTPED,
    I.CODPRO,
    I.QUANTIDADE,
    I.PRECOS
FROM PEDRES P
JOIN FINCLI C ON P.CODCLI = C.CODCLI
JOIN PEDRE2 I ON P.CODPED = I.CODPED
WHERE P.FLGEXC = 0
    AND C.FLGEXC = 0
    AND I.FLGEXC = 0
    AND P.DTOPED >= CURRENT_TIMESTAMP - 30  -- Last 30 days
ORDER BY P.DTOPED DESC
```

### Get Products with Pricing
```sql
SELECT 
    P.CODPRO,
    TRIM(P.NOMPRO) as name,
    P.PREUNI,
    P.ESTATU
FROM ESTPRO P
WHERE P.FLGEXC = 0
    AND P.ESTATU = 'A'  -- Active
ORDER BY TRIM(P.NOMPRO)
```

---

## Migration Path Overview

**⚠️ Important: Migrate Main Tables Before Detail Tables**

1. **FINCLI** → Customers (no dependencies)
2. **ESTPRO** → Products (no dependencies)
3. **FINFOR** → Suppliers (no dependencies)
4. **PEDRES** → Orders (depends on FINCLI)
5. **PEDRE2** → OrderItems (depends on PEDRES)
6. **CMPAEN** → SupplierReceipts (depends on FINFOR)

**Key Rule**: Always migrate the main table first, then its detail/item table (the one ending with "2").

---

## Query Examples for Detail Tables

### Get Orders with All Items
```sql
SELECT 
    P.CODPED,
    P.CODCLI,
    C.NOMCLI,
    P.DTOPED,
    P.TOTPED,
    I.SEQITEM,
    I.CODPRO,
    I.QUANTIDADE,
    I.PRECOS,
    I.TOTITEM
FROM PEDRES P
JOIN FINCLI C ON P.CODCLI = C.CODCLI
JOIN PEDRE2 I ON P.CODPED = I.CODPED
WHERE P.FLGEXC = 0
    AND C.FLGEXC = 0
    AND I.FLGEXC = 0
ORDER BY P.CODPED, I.SEQITEM
```

### Pagination for Detail Tables
```sql
-- Get items for specific order
SELECT 
    I.CODPED,
    I.SEQITEM,
    I.CODPRO,
    I.QUANTIDADE,
    I.PRECOS,
    I.TOTITEM
FROM PEDRE2 I
WHERE I.CODPED = ?
    AND I.FLGEXC = 0
ORDER BY I.SEQITEM
```

---

**For Copilot**: Reference this metadata file when creating queries, projections, and mappers. Always include `FLGEXC = 0` filters and `TRIM()` for string fields.
