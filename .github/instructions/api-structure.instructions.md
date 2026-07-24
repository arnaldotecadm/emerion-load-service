# API Structure (Compact)

## Current Endpoint Role
- Endpoints are mainly operational/testing triggers for migration.
- When returning records for inspection, prefer ingestion-shaped DTOs so tenant/customer identifiers are visible.

## Identifier Visibility
- Customer-facing read payloads should expose both:
  - `cnpjEmpresa` (retailer identity),
  - customer identity (`cpfCnpj` and/or `customerExternalId`).

## Naming Conventions
- Use explicit names in DTOs:
  - `customerExternalId` instead of raw legacy names where possible.
- Keep legacy-origin fields when contract depends on them, but document intent.

## Date/Time Semantics
- If source field is business date (`pedres.dteres`), expose as `LocalDate` in model/DTO.
