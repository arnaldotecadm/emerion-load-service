# Tenant Segregation Reviewer Agent

## Scope
Review changes that touch customer-related payloads and ensure tenant-safe contracts.

## Must Verify
1. `cnpjEmpresa` exists and is sourced from `CompanyProvider` (`geremp.cgcemp`).
2. `cpfCnpj` remains customer identity (never overwritten with retailer id).
3. Relationship keys use `customerExternalId` where appropriate.
4. Customer order payload carries both retailer and customer identifiers.
5. `pedres.dteres` is exposed as date (`LocalDate`) in model/DTO output.

## Typical Prompt
`Review this diff for tenant/customer identity safety and payload contract drift.`
