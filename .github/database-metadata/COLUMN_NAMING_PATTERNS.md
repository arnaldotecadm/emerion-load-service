# Firebird Column Naming Patterns

Quick reference for decoding 6-character column names in emerion legacy database.

---

## "tot" Prefix = Total/Amount Fields

Used extensively in PEDRES, PEDRE2, FATPED, and other transaction tables.

### PEDRES (Orders) - Tot Fields

| Prefix | Column | Meaning | Calculation | Use |
|--------|--------|---------|-------------|-----|
| `tot` | `TOTRES` | **Total do Pedido** | Sum of all PEDRE2 items (no tax) | Dashboard: `valorTotal` for order |
| `tot` | `TOTGER` | **Total Geral** | TOTRES + all taxes (ICMS, IPI, PIS, COFINS) | Phase 4+ (fiscal module) |
| `tot` | `TOTDSC` | **Total Descontos** | Sum of line item discounts | Included in TOTRES |
| `tot` | `TOTQTE` | **Total Quantidade** | Sum of PEDRE2 quantities | Not needed (use PEDRE2 items) |

**Key Point**: PEDRES `tot*` columns are **aggregates** from PEDRE2. Don't sum PEDRE2 yourself; use PEDRES totals.

### PEDRE2 (Order Items) - Tot Fields

| Column | Meaning | Calculation | Use |
|--------|---------|-------------|-----|
| `TOTITEM` | **Total do Item** | Qty × Unit Price | Verify item = qty × price |
| `TOTIPI` | **Total IPI** | Tax calculated | Skip (Phase 4+) |
| `TOTICM` | **Total ICMS** | Tax calculated | Skip (Phase 4+) |

### FATPED (Invoices) - Tot Fields

| Column | Meaning | Calculation | Use |
|--------|---------|-------------|-----|
| `TOTFAT` | **Total Faturado** | Invoice amount | Invoice total (may differ from PEDRES if partial billing) |

---

## Other Common Prefixes

### "val" = Value (Currency Fields)

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `val` | `VALFRT` | Freight/Shipping | Skip (Phase 4+) |
| `val` | `VALSEG` | Insurance | Skip (Phase 4+) |
| `val` | `VALDSC` | Discount (per line) | Included in line total |
| `val` | `VALACR` | Surcharge | Included in line total |

**Pattern**: All `val*` = monetary amounts in NUMERIC(15,2) format.

### "cod" = Code/ID (Identifier Fields)

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `cod` | `CODPED` | INTEGER | Order ID (primary key) |
| `cod` | `CODCLI` | INTEGER | Customer ID (FK to FINCLI) |
| `cod` | `CODPRO` | CHAR(5) | Product ID (FK to ESTPRO) |
| `cod` | `CODFOR` | CHAR(5) | Supplier ID (FK to FINFOR) |

**Pattern**: All `cod*` = identifiers/foreign keys.

### "dt" or "dte" = Date/Timestamp

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `dt` / `dte` | `DTOPED` | TIMESTAMP | Order date |
| `dt` / `dte` | `DTEENT` | TIMESTAMP | Delivery/due date |
| `dt` / `dte` | `DTAFAT` | TIMESTAMP | Invoice date |
| `dt` / `dte` | `DTACAD` | TIMESTAMP | Creation date |

**Pattern**: `dt` = date, `dte` = date/timestamp. Both are usually TIMESTAMP in Firebird.

### "nom" or "ape" = Name/Description

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `nom` | `NOMCLI` | CHAR(50) | Customer name (TRIM before use) |
| `ape` | `APECLI` | CHAR(50) | Customer trade name (TRIM before use) |
| `nom` | `NOMPRO` | CHAR(30) | Product name (TRIM before use) |
| `des` | `DESPED` | VARCHAR(600) | Order description/notes |

**Pattern**: All text fields need TRIM() to remove padding. CHAR = fixed width, VARCHAR = variable.

### "seq" = Sequence/Order

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `seq` | `SEQITEM` | INTEGER | Line item sequence in order (1, 2, 3...) |

**Pattern**: Used for ordering within parent table.

### "flg" = Flag/Boolean

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `flg` | `FLGEXC` | SMALLINT | Excluded flag (0=active, 1=deleted) |

**Pattern**: SMALLINT (0/1), use `WHERE TABLE.FLGEXC = 0` to get active records only.

### "qte" or "qty" = Quantity

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `qte` | `QUANTIDADE` | NUMERIC(15,4) | Item quantity ordered |

**Pattern**: NUMERIC(15,4) for precision (e.g., 10.5000).

### "preco" = Price

| Prefix | Column | Type | Use |
|--------|--------|------|-----|
| `preco` | `PRECOS` | NUMERIC(15,4) | Unit price per item |

**Pattern**: NUMERIC(15,4) for precision (e.g., 1500.0000).

---

## Full Example: Decoding PEDRES Columns

Given:
```
CODPED, CODCLI, DTOPED, DTEENT, TOTRES, TOTGER, TOTDSC, TOTQTE, FLGEXC
```

| Column | Decoded | Type | Use |
|--------|---------|------|-----|
| `CODPED` | **cod**-ped = Order Code | INTEGER | Order ID |
| `CODCLI` | **cod**-cli = Customer Code | INTEGER | Customer ID |
| `DTOPED` | **dte**-ped = Date (Order Date) | TIMESTAMP | When ordered |
| `DTEENT` | **dte**-ent = Date (Entry/Due Date) | TIMESTAMP | When due |
| `TOTRES` | **tot**-res = Total Result | NUMERIC(15,2) | Order total (use this) |
| `TOTGER` | **tot**-ger = Total General (with tax) | NUMERIC(15,2) | Skip (Phase 4+) |
| `TOTDSC` | **tot**-dsc = Total Discount | NUMERIC(15,2) | Already in TOTRES |
| `TOTQTE` | **tot**-qte = Total Quantity | NUMERIC(15,4) | Skip (use PEDRE2) |
| `FLGEXC` | **flg**-exc = Excluded Flag | SMALLINT | Filter = 0 |

---

## Quick Lookup: Phase 1 Columns

**PEDRES** (6 fields):
- `CODPED` (cod = order ID)
- `CODCLI` (cod = customer ID)
- `DTOPED` (dte = order date)
- `DTEENT` (dte = due date)
- `TOTRES` (tot = order total) ⭐ use this, not TOTGER
- `FLGEXC` (flg = active flag)

**PEDRE2** (7 fields):
- `CODPED` (cod = order ID, FK)
- `SEQITEM` (seq = line number)
- `CODPRO` (cod = product ID)
- `QUANTIDADE` (qte = qty ordered)
- `PRECOS` (preco = unit price)
- `TOTITEM` (tot = line total = qty × price)
- `FLGEXC` (flg = active flag)

**FATPED** (4 fields):
- `CODPED` (cod = order ID, FK)
- `DTAFAT` (dte = invoice date)
- `TOTFAT` (tot = invoice amount)
- `FLGEXC` (flg = active flag)

---

## Summary

| Pattern | Meaning | Data Type | Example |
|---------|---------|-----------|---------|
| `cod*` | Code/ID | INTEGER, CHAR(5) | CODPED, CODCLI |
| `dte*` / `dt*` | Date/Timestamp | TIMESTAMP | DTOPED, DTEENT |
| `tot*` | Total/Amount | NUMERIC(15,2/4/6) | TOTRES, TOTITEM |
| `val*` | Value/Currency | NUMERIC(15,2) | VALFRT, VALDSC |
| `nom*` / `ape*` | Name/Text | CHAR, VARCHAR | NOMCLI, DESPED |
| `qte*` | Quantity | NUMERIC(15,4) | QUANTIDADE |
| `preco*` | Price | NUMERIC(15,4) | PRECOS |
| `seq*` | Sequence | INTEGER | SEQITEM |
| `flg*` | Flag/Boolean | SMALLINT (0/1) | FLGEXC |

**Remember**: 
- **TRIM()** all CHAR/VARCHAR fields (they're padded)
- **FLGEXC = 0** filters active records (1 = deleted)
- **TOTRES, not TOTGER** for dashboard (taxes are Phase 4+)
- **Parent before child**: Migrate PEDRES → PEDRE2, not vice versa
