# Spring Configuration (Compact)

## Current Runtime Shape
- Single Firebird datasource for extraction.
- Ingestion service target configured under `ingestion-service.*`.
- Tenant resolver config:
  - `company.codemp` (default `1`)
  - used by `CompanyProvider` to resolve `geremp.cgcemp`.

## Required Components
- `CompanyProvider` must remain injectable and reusable.
- `IngestionServiceClient` must use `CompanyProvider` for every outgoing payload.

## Config Rules
- Keep `company.codemp` environment-overridable per retailer deployment.
- Do not hardcode tenant CNPJ in code.
