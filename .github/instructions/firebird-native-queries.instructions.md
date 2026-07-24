# Firebird Native Queries (Compact)

## Query Standards
- Use native SQL + `JdbcTemplate` for Firebird.
- Keep extraction read-only and deterministic (`order by` stable key).
- For paged queries, use project helper (`FirebirdPagination`) and Firebird-compatible syntax.

## Identifier Requirements
- Always preserve customer identity (`fincli.cgccli` as `cpfCnpj`) when needed by downstream DTOs.
- Do not fetch retailer CNPJ in each row using correlated subquery.
  - Retailer CNPJ is resolved centrally from `geremp.cgcemp` via `CompanyProvider`.

## Customer Order Header Requirements
- Header query must expose:
  - `codCli`
  - `cpfCnpj` (join `fincli` by `codcli`)
  - `numres` and financial/order fields
- `dteres` must be treated as **date**:
  - read as `Timestamp`/`LocalDateTime` in query mapping,
  - convert to `LocalDate` in mapper/domain.

## Firebird Driver Constraints
- Prefer:
  - `rs.getBigDecimal("col")?.toDouble()`
  - nullable ints via `getInt` + `wasNull()`
- Avoid `getObject("col", SomeClass::class.java)` on older Jaybird.

## Nullability
- Keep nullable fields nullable in projections.
- Normalize and default in mapper layer, not in SQL unless needed for correctness.
