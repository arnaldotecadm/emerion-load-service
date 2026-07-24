# Firebird → Domain/DTO Mapping (Compact)

## Mapping Priorities
1. Correct semantics (especially identifiers and dates).
2. Null safety.
3. Stable, idempotent output.

## Identifier Mapping Rules
- `geremp.cgcemp` → `cnpjEmpresa` (retailer/tenant).
- `fincli.cgccli` / `finven.cgcven` → `cpfCnpj` (person/customer identity).
- `codCli` → `customerExternalId` when linking orders/credits/addresses to customer entity.

## Date Mapping Rules
- Use `LocalDateTime` in projection if JDBC/projection conversion requires it.
- Convert to `LocalDate` in mapper for business-date fields (e.g., `pedres.dteres`).

## Numeric/Nullable Rules
- Monetary/tax values: preserve precision as needed (`BigDecimal` in read path, convert intentionally).
- Nullable Firebird numeric fields: use null-aware reads, avoid implicit zero unless explicitly required.
