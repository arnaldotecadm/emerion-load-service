# Emerion Load Service — Token-Optimized Copilot Context

## Purpose
Kotlin/Spring Boot loader that reads legacy Firebird data and sends normalized payloads to the ingestion API.

Core flow: **Query → Projection → Mapper → Model → Ingestion DTO → Send**.

## High-Value Rules (Repository Contract)
1. **Multi-tenant segregation is mandatory.**
   - Retailer identifier: `geremp.cgcemp` → `cnpjEmpresa`.
   - Resolve once via `CompanyProvider`, configured by `company.codemp` (default `1`).
   - Stamp `cnpjEmpresa` on every ingestion DTO.

2. **Do not conflate identifiers.**
   - Retailer: `cnpjEmpresa` (from `geremp`).
   - Customer identity: `cpfCnpj` (from `fincli.cgccli`).
   - Customer external key in relationships: `customerExternalId` (legacy `codCli`).

3. **Customer order payload must carry both retailer and customer identifiers.**
   - `CustomerOrderIngestionDto` includes:
     - `cnpjEmpresa`
     - `cpfCnpj`
     - `customerExternalId`
     - order key `externalId` (`numres`)

4. **`pedres.dteres` is DATE semantics.**
   - In projection: use `LocalDateTime` for JDBC/projection compatibility.
   - In model/DTO: map to `LocalDate`.

5. **Firebird/Jaybird compatibility constraints.**
   - Avoid direct projection `TIMESTAMP -> LocalDate` in Spring Data native interface projections.
   - On old Jaybird drivers, avoid `getObject(column, Class)`; prefer `getInt + wasNull` or `getBigDecimal`.

## Endpoint Intent
- This service is ingestion-oriented.
- Current GET endpoints are for testing/inspection and may return ingestion-shaped DTOs so `cnpjEmpresa` is visible.

## Implementation Anchors
- `service/CompanyProvider.kt`
- `client/IngestionServiceClient.kt`
- `client/dto/*IngestionDto.kt`
- `repository/*QueryRepository.kt` + `repository/mapper/*Mapper.kt`

## Preferred Working Style for Changes
- Keep SQL native for Firebird extraction.
- Keep transformations in mappers.
- Reuse existing DTO/mapper patterns before adding new abstractions.
- Make field semantics explicit in names (`customerExternalId`, `cpfCnpj`, `cnpjEmpresa`).
