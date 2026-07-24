# API Integration (Compact)

## Single Source of Tenant Identity
- Inject `CompanyProvider` in the ingestion client.
- Resolve `companyProvider.getCompanyCnpj()` and pass it to all `toIngestionDto(...)` mappers.
- Keep this value in DTO field `cnpjEmpresa`.

## Identity Contract for Ingestion DTOs
- Retailer scope: `cnpjEmpresa` (from `geremp.cgcemp`).
- Customer scope:
  - `cpfCnpj` when available (document-level business id),
  - `customerExternalId` for relational linkage with customer entity.

## Customer Order Payload Contract
- Must include: `externalId`, `customerExternalId`, `cpfCnpj`, `cnpjEmpresa`, `dteres` (date), totals, items.

## Error Handling
- Log request intent + external identifiers.
- Propagate request failures (do not swallow).
- Keep send methods thin; mapping should be complete before HTTP call.
