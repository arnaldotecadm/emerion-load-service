# Agents Guide (Token-Optimized)

## Existing Domain Agents
- `database-specialist`
- `data-mapper`
- `api-integrator`
- `migration-architect`
- `testing-expert`
- `dev-assistant`

## New Focused Agent
- `tenant-segregation-reviewer`
  - Validates that retailer/customer identifiers are correctly separated and propagated.
  - Use before merging changes touching orders/customers/addresses/credits/vendedores.

## Recommended Workflow
1. `database-specialist`: query/projection changes.
2. `data-mapper`: model + DTO mappings.
3. `api-integrator`: ingestion payload contract.
4. `tenant-segregation-reviewer`: final segregation check.
