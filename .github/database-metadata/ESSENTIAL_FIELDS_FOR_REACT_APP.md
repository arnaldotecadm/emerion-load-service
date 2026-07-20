# Essential Fields for React Dashboard

## Why This Matters
The React app has many static mock datasets. This document identifies the **minimum set of Firebird fields** needed to replace each mock area with real data. Excludes tax, GL, and internal audit fields.

---

## 1. PEDRES (Orders/Sales)

**Used by**: Sales KPIs, order history, top customers by revenue, pipeline, order details

### Essential Fields Only

| Field | Type | Why needed |
|-------|------|-----------|
| `CODPED` | INTEGER | Order ID (primary key, unique identifier) |
| `CODCLI` | INTEGER | Customer ID (join to FINCLI for customer name) |
| `DTOPED` | TIMESTAMP | Order date (for sales trends by day/week/month, pipeline) |
| `DTEENT` | TIMESTAMP | Delivery/due date (for "delayed orders" KPI, pipeline health) |
| `TOTRES` | NUMERIC(15,2) | **Order total** (sum of PEDRE2 items; "Vendas Totais" KPI, avg ticket, revenue) |
| `FLGEXC` | SMALLINT | Excluded flag (0=active, 1=deleted; filter active orders only) |

**Note on tot-columns**:
- `TOTRES` = total do pedido (order total without taxes) — use this for dashboard
- `TOTGER` = total geral com impostos — for future Phase 4+ (fiscal module)
- `TOTDSC` = total descontos — included in TOTRES calculation
- PEDRES tot-columns are **aggregates** of PEDRE2 line totals, so query PEDRES only for order totals

**NOT needed** (too detailed for dashboard):
- Tax fields (TOTICM, TOTIPI, TOTPIS, TOTCOF, TOTSUB, etc.)
- Internal cost fields (CUSTAPROD, CUSTAPEC, etc.)
- GL/accounting fields (SEQCONTAL, etc.)
- Freight/insurance (VALFRT, VALSEG, etc.)
- Quantity tracking (TOTQTE, SLDQTE, etc.)

**Total**: 6 fields

---

## 2. PEDRE2 (Order Items/Lines)

**Used by**: Item details, product mix breakdown, average order size, line-item revenue

### Essential Fields Only

| Field | Type | Why needed |
|-------|------|-----------|
| `CODPED` | INTEGER | Order ID (join to PEDRES) |
| `SEQITEM` | INTEGER | Line sequence (sort items within order, unique key) |
| `CODPRO` | CHAR(5) | Product ID (join to ESTPRO for name, category) |
| `QUANTIDADE` | NUMERIC(15,4) | Qty ordered (for product mix %, total line count) |
| `PRECOS` | NUMERIC(15,4) | Unit price (for line value calculation) |
| `TOTITEM` | NUMERIC(15,2) | Line total = qty × price (verify data integrity) |
| `FLGEXC` | SMALLINT | Excluded flag (filter active items) |

**NOT needed**:
- Tax/cost per item (TOTIPI, TOTICM, etc.)
- Discount/surcharge per line (VALDSC, VALACR, etc.)
- Stock/inventory fields (SLDQTE, etc.)

**Total**: 7 fields

---

## 3. FATPED (Invoices/Billing)

**Used by**: "Faturado" order status, billing KPIs, recent invoices per customer, revenue recognition date

### Essential Fields Only

| Field | Type | Why needed |
|-------|------|-----------|
| `CODPED` | INTEGER | Order ID (join PEDRES, group by order) |
| `DTAFAT` | TIMESTAMP | Invoice date (when "Faturado" status applies; sales by invoice date) |
| `TOTFAT` | NUMERIC(15,2) | Invoice total (billing KPI, revenue sum) |
| `FLGEXC` | SMALLINT | Excluded flag (filter active invoices) |

**Rationale**: 
- Maps orders to "Faturado" (invoiced) status
- Groups multiple PEDRES invoices if one order has partial billing
- Provides invoice date (different from order date)

**NOT needed**:
- Tax detail (TOTIPI, TOTICM, TOTPIS, TOTCOF, TOTSUB, etc.)
- GL/accounting (SEQCONTAL, etc.)
- Discount/surcharge (VALDSC, VALACR, etc.)
- NF-e/XML fields (if present)

**Total**: 4 fields

---

## Query Template: Complete Order Data for Dashboard

```sql
SELECT 
    -- Order
    ped.CODPED,
    ped.CODCLI,
    ped.DTOPED,
    ped.DTEENT,
    ped.TOTRES,        -- Order total (sum of PEDRE2 items, no taxes)
    
    -- Customer (join)
    cli.NOMCLI,
    cli.APECLI,
    
    -- Items (separate join)
    pei.SEQITEM,
    pei.CODPRO,
    pei.QUANTIDADE,
    pei.PRECOS,
    pei.TOTITEM,
    
    -- Invoice status (outer join — may be NULL if not yet invoiced)
    fat.DTAFAT,
    fat.TOTFAT,
    
    -- Computed
    CASE 
        WHEN fat.CODPED IS NOT NULL THEN 'Faturado'
        ELSE 'Pendente'
    END AS status,
    
    DATEDIFF(DAY, ped.DTEENT, CURRENT_DATE) AS dias_atrasado
    
FROM PEDRES ped
LEFT JOIN FINCLI cli ON ped.CODCLI = cli.CODCLI
LEFT JOIN PEDRE2 pei ON ped.CODPED = pei.CODPED
LEFT JOIN FATPED fat ON ped.CODPED = fat.CODPED
WHERE ped.FLGEXC = 0 
    AND cli.FLGEXC = 0 
    AND pei.FLGEXC = 0
ORDER BY ped.CODPED, pei.SEQITEM
```

---

## Field Count Summary

| Table | Original fields (typical) | Essential fields | % Reduction |
|-------|--------------------------|------------------|------------|
| PEDRES | 50+ | 6 | 88% less |
| PEDRE2 | 40+ | 7 | 82% less |
| FATPED | 40+ | 4 | 90% less |
| **Total** | **130+** | **17** | **87% less** |

---

## Implementation Priority

### Phase 1: Core Orders (enables 70% of dashboard)
- [ ] Map PEDRES (6 fields) → Order entity/DTO
- [ ] Map PEDRE2 (7 fields) → OrderItem entity/DTO
- [ ] Firebird query + projection + mapper (following `firebird-query-skill.md` pattern)
- [ ] `/order/all` paginated endpoint
- [ ] `/order/{id}` detail endpoint with items

### Phase 2: Billing Status (enables order history, faturado status)
- [ ] Map FATPED (4 fields) → Invoice entity
- [ ] Join PEDRES → FATPED to compute status
- [ ] Add `status: "Faturado" | "Pendente" | "Cancelado"` to order endpoint

### Phase 3: Product Mix (uses PEDRE2 + ESTPRO)
- [ ] Map ESTPRO essential fields (category, name)
- [ ] Aggregate PEDRE2.QUANTIDADE by product category
- [ ] Endpoint to supply product-mix pie chart

---

## Benefits of This Focused Set

1. **Minimal mapping overhead** — 17 fields is 1 small mapper class
2. **No complex tax logic** — fiscal/ICMS/IPI fields deferred to separate financials module
3. **Clear separation** — sales data vs. accounting/GL data
4. **Copilot-friendly** — easier for Copilot to generate correct queries/mappers with fewer fields
5. **Fast queries** — fewer columns = smaller result sets, faster Firebird native query execution
6. **Type-safe DTOs** — simpler Kotlin models, less chance of NULL handling errors

---

## What This Set Does NOT Support (Yet)

- Fiscal/tax details (ICMS, DIFAL, PIS, COFINS)
- Delinquency risk (link to FINCONTA/receivables aging)
- Credit limits (link to FINCLI credit fields)
- Stock/inventory (link to ESTPRO stock fields)
- Salesperson attribution (if not already in order header)
- Discounts/surcharges beyond total
- Cost/margin analysis
- Commission calculation

**These can be added in Phase 4+ once core order flow works.**

---

## Recommendation

Start with **Phase 1 only** (PEDRES + PEDRE2, 13 fields). This:
- Enables `GET /order/all` and `GET /order/{id}` APIs
- Powers sales KPIs, order lists, order detail views
- Takes ~2-3 days to implement with Copilot
- Provides immediate ROI for React dashboard
- Establishes the pattern for later phases
