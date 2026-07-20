# Table Naming Convention Guide

## The "2" Suffix Pattern

In the `portal_2024_01.fdb` database, **tables ending with "2" are detail/item tables** that correspond to a main table.

### Pattern
```
Main Table Name + "2" = Detail/Item Table
```

### Examples

| Main Table | Detail Table | Contains |
|-----------|-------------|----------|
| PEDRES | PEDRE2 | Order items/lines |
| FATPED | FATPE2 | Billing order items/lines |
| PEDLIB | PEDLB2 | Library/catalog order items/lines |

### Why This Matters

1. **Table Structure**: Detail tables have a composite primary key that includes the main table's key
   - PEDRE2: Primary Key = (CODPED + SEQITEM)
   - PEDRES: Primary Key = CODPED

2. **Relationships**: Always 1:N (one-to-many)
   - 1 PEDRES (order) has many PEDRE2 (items)
   - 1 FATPED (billing) has many FATPE2 (items)

3. **Queries**: Must join on the relationship
   ```sql
   SELECT * FROM PEDRES
   JOIN PEDRE2 ON PEDRES.CODPED = PEDRE2.CODPED
   ```

4. **Migration Order**: Always migrate parent first, then child
   - Cannot migrate PEDRE2 before PEDRES (foreign key constraint)
   - Need valid CODPED in PEDRE2 to match PEDRES.CODPED

---

## Known Detail Tables

### PEDRE2 (Order Items)
- **Main Table**: PEDRES
- **Links By**: CODPED (Order Code)
- **Key Sequence Field**: SEQITEM (Item Sequence, 1, 2, 3, ...)
- **What It Contains**: Individual line items in an order
- **Example**:
  - PEDRES: 1 order with code 12345, total $1000
  - PEDRE2: 3 items in that order (SEQITEM 1, 2, 3)

### FATPE2 (Billing Items)
- **Main Table**: FATPED
- **Links By**: CODPED (Billing Code)
- **Key Sequence Field**: SEQITEM
- **What It Contains**: Individual line items in a billing order

### PEDLB2 (Library/Catalog Order Items)
- **Main Table**: PEDLIB
- **Links By**: (varies - check schema)
- **Key Sequence Field**: SEQITEM or similar
- **What It Contains**: Items in a catalog/library order

---

## Migration Pattern for Detail Tables

### Step 1: Migrate Main Table
```kotlin
// 1. Query main table
val orders = firebirdbRepository.findAllPedres()  // PEDRES

// 2. Map to model
val orderModels = mapper.toOrderModels(orders)

// 3. Save to PostgreSQL
val saved = postgresRepository.saveAll(orderModels)
```

### Step 2: Migrate Detail Table
```kotlin
// 1. Query detail table (optionally joined with main for context)
val items = firebirdbRepository.findAllPedre2()  // PEDRE2

// 2. Map to model (include reference to parent order)
val itemModels = mapper.toOrderItemModels(items)

// 3. Save to PostgreSQL (with foreign key to Order)
val savedItems = postgresRepository.saveAll(itemModels)
```

### ⚠️ Critical: Maintain Foreign Keys
When migrating detail tables, ensure:
1. Parent record exists in PostgreSQL before saving child
2. Use `CODPED` (or equivalent) to maintain relationship
3. Handle orphaned records (items with no parent)

---

## Data Structure Example

```
Database: PEDRES (Orders)
┌──────────────────────────────────────┐
│ CODPED | CODCLI | DTOPED | TOTPED   │
├──────────────────────────────────────┤
│ 12345  │ 100    │ 2024-01-15 │ 1000 │
│ 12346  │ 101    │ 2024-01-16 │ 2000 │
└──────────────────────────────────────┘

Database: PEDRE2 (Order Items)
┌──────────────────────────────────────────────────────────┐
│ CODPED | SEQITEM | CODPRO | QUANTIDADE | PRECOS | TOTAL │
├──────────────────────────────────────────────────────────┤
│ 12345  │ 1       │ PR001  │ 10         │ 50     │ 500   │
│ 12345  │ 2       │ PR002  │ 5          │ 100    │ 500   │
│ 12346  │ 1       │ PR003  │ 20         │ 100    │ 2000  │
└──────────────────────────────────────────────────────────┘

JOIN RESULT:
Order 12345 has 2 items totaling $1000
Order 12346 has 1 item totaling $2000
```

---

## SQL Query Patterns for Detail Tables

### Get Main + All Details
```sql
SELECT 
    P.CODPED,
    P.DTOPED,
    P.TOTPED,
    I.SEQITEM,
    I.CODPRO,
    I.QUANTIDADE,
    I.TOTITEM
FROM PEDRES P
JOIN PEDRE2 I ON P.CODPED = I.CODPED
WHERE P.FLGEXC = 0 
    AND I.FLGEXC = 0
ORDER BY P.CODPED, I.SEQITEM
```

### Get Details for Specific Main Record
```sql
SELECT 
    SEQITEM,
    CODPRO,
    QUANTIDADE,
    PRECOS,
    TOTITEM
FROM PEDRE2
WHERE CODPED = ?
    AND FLGEXC = 0
ORDER BY SEQITEM
```

### Count Detail Items per Main Record
```sql
SELECT 
    P.CODPED,
    COUNT(I.SEQITEM) as item_count,
    SUM(I.TOTITEM) as total_amount
FROM PEDRES P
LEFT JOIN PEDRE2 I ON P.CODPED = I.CODPED
WHERE P.FLGEXC = 0
GROUP BY P.CODPED
ORDER BY P.CODPED
```

---

## Firebird-Specific Considerations

### No Referential Integrity Enforcement?
Firebird may not enforce foreign key constraints on legacy databases.
- Check for orphaned records: PEDRE2 items with CODPED that doesn't exist in PEDRES
- Clean data before migration if needed

### FLGEXC Flag on Both Tables
Both main and detail tables have FLGEXC (excluded) flag:
- A main record can be excluded independently
- Detail records can be excluded independently
- Always filter both: `P.FLGEXC = 0 AND I.FLGEXC = 0`

### SEQITEM Ordering
- SEQITEM is the sequence/order within a main record
- Usually starts at 1, increments by 1 (sometimes by 10 or 5)
- Use for ordering results: `ORDER BY P.CODPED, I.SEQITEM`

---

## Common Mistakes to Avoid

### ❌ Mistake 1: Forgetting the Relationship in Query
```sql
-- WRONG: Getting items without context
SELECT * FROM PEDRE2 WHERE FLGEXC = 0

-- RIGHT: Getting items with parent context
SELECT * FROM PEDRE2 
WHERE CODPED IN (SELECT CODPED FROM PEDRES WHERE FLGEXC = 0)
    AND FLGEXC = 0
```

### ❌ Mistake 2: Migrating Detail Before Main
```kotlin
// WRONG: Will fail with foreign key error
pgRepository.saveAll(detailItems)
pgRepository.saveAll(mainOrders)

// RIGHT: Main first, then details
pgRepository.saveAll(mainOrders)
pgRepository.saveAll(detailItems)
```

### ❌ Mistake 3: Not Handling Orphaned Records
```kotlin
// WRONG: Assumes all detail records have a parent
val items = detailRepository.findAll()
itemService.migrate(items)

// RIGHT: Validate parent exists
val items = detailRepository.findAll()
    .filter { mainRepository.existsById(it.parentId) }
itemService.migrate(items)
```

### ❌ Mistake 4: Ignoring FLGEXC on Detail Table
```sql
-- WRONG: Gets excluded items
SELECT * FROM PEDRE2 WHERE CODPED = ?

-- RIGHT: Excludes marked-as-deleted items
SELECT * FROM PEDRE2 
WHERE CODPED = ? 
    AND FLGEXC = 0
```

---

## Testing Detail Table Queries

### Test 1: Count Main vs Detail Records
```sql
SELECT 
    'PEDRES' as table_name,
    COUNT(*) as active_records
FROM PEDRES
WHERE FLGEXC = 0

UNION ALL

SELECT 
    'PEDRE2' as table_name,
    COUNT(*) as active_records
FROM PEDRE2
WHERE FLGEXC = 0
```

Result tells you how many items exist across all orders.

### Test 2: Find Orphaned Detail Records
```sql
-- Detail records with no matching main record
SELECT * FROM PEDRE2 I
WHERE I.FLGEXC = 0
    AND NOT EXISTS (
        SELECT 1 FROM PEDRES P
        WHERE P.CODPED = I.CODPED 
            AND P.FLGEXC = 0
    )
```

If this returns rows, you have data integrity issues to handle.

### Test 3: Verify Completeness
```sql
-- For each main record, verify item total = sum of item totals
SELECT 
    P.CODPED,
    P.TOTPED as order_total,
    COALESCE(SUM(I.TOTITEM), 0) as items_total,
    CASE 
        WHEN P.TOTPED = COALESCE(SUM(I.TOTITEM), 0) THEN 'OK'
        ELSE 'MISMATCH'
    END as status
FROM PEDRES P
LEFT JOIN PEDRE2 I ON P.CODPED = I.CODPED AND I.FLGEXC = 0
WHERE P.FLGEXC = 0
GROUP BY P.CODPED, P.TOTPED
HAVING P.TOTPED != COALESCE(SUM(I.TOTITEM), 0)
```

This finds orders where item totals don't match the order total (data quality issues).

---

## Kotlin Entities & Models

### Main Table Entity
```kotlin
@Entity
@Table(name = "orders")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "legacy_code", unique = true)
    val legacyCode: Int,
    
    @Column(name = "customer_id")
    val customerId: Long,
    
    @Column(name = "order_date")
    val orderDate: LocalDateTime,
    
    @Column(name = "total_amount")
    val totalAmount: BigDecimal,
    
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    val items: List<OrderItem> = emptyList()
)
```

### Detail Table Entity
```kotlin
@Entity
@Table(name = "order_items")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(name = "legacy_code")
    val legacyCode: Int,
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: Order,
    
    @Column(name = "sequence_number")
    val sequenceNumber: Int,
    
    @Column(name = "product_code")
    val productCode: String,
    
    @Column(name = "quantity")
    val quantity: BigDecimal,
    
    @Column(name = "unit_price")
    val unitPrice: BigDecimal,
    
    @Column(name = "total_amount")
    val totalAmount: BigDecimal
)
```

---

## Summary Checklist

Before writing queries or migrations for detail tables:
- [ ] Identified the main table this is a detail of
- [ ] Know the relationship field (e.g., CODPED)
- [ ] Know the sequence field (e.g., SEQITEM)
- [ ] Planning to migrate main table first
- [ ] Filtering by FLGEXC = 0 on both main and detail
- [ ] Joining on correct foreign key
- [ ] Ordered by main key, then sequence
- [ ] Handling potential orphaned records
- [ ] Updating tests to verify totals/counts

---

**This is a critical pattern for this database. Always remember: Tables ending with "2" are detail tables!"**
