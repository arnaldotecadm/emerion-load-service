# Copilot Context for emerion-load-service

This `.github` directory was streamlined to keep Copilot context **high signal** and **low token cost**.

## Primary Contract
- Retailer identity: `cnpjEmpresa` from `geremp.cgcemp` (via `CompanyProvider`).
- Customer identity: `cpfCnpj` from customer tables.
- Relationship key: `customerExternalId` (legacy `codCli`).
- Order business date: `pedres.dteres` → `LocalDate` in model/DTO.

## Recommended References in Prompts
- `.github/copilot-instructions.md`
- `.github/instructions/api-integration.instructions.md`
- `.github/skills/tenant-identity-skill.md`

## Navigation
See `.github/INDEX.md`.
