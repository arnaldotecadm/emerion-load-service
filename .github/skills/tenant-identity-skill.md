# Tenant Identity Skill

## Goal
Guarantee data segregation by retailer without losing customer-level traceability.

## Canonical Field Meanings
- `cnpjEmpresa`: retailer/tenant id (`geremp.cgcemp`).
- `cpfCnpj`: customer/person id (`fincli.cgccli` or equivalent).
- `customerExternalId`: legacy customer key (`codCli`) for entity linkage.

## Required for Order Payloads
- Include all of the following:
  - `externalId` (order id / `numres`)
  - `cnpjEmpresa`
  - `cpfCnpj`
  - `customerExternalId`

## Implementation Checklist
1. Query returns customer identifiers needed by DTO (`codCli`, `cpfCnpj`).
2. Retailer CNPJ comes from `CompanyProvider`, not row-level SQL subqueries.
3. Mapper preserves semantic names in output DTO.
4. Testing endpoints expose ingestion DTO shape for quick verification.

## Date Rule
- `pedres.dteres` is business date → output as `LocalDate`.
