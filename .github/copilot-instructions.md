# GitHub Copilot Instructions — Emerion Dashboard

## Project Overview

This repository contains the React frontend for the Emerion Dashboard.

The application provides a modern customer-facing interface over business data originating from a legacy ERP system.

The legacy system was originally implemented in Delphi and uses a Firebird database. A new PostgreSQL-based backend is being developed to expose the relevant data to the React application.

The React application should not reproduce the legacy application's screens literally. Instead, it should use the available business data to provide a modern, clear, useful, and visually effective dashboard experience.

---

## Primary Goal

The primary goal of this project is to transform existing Emerion business data into useful dashboard functionality.

When implementing or proposing functionality, prioritize:

- Business value
- Clear information hierarchy
- Actionable information
- Useful KPIs
- Trends and comparisons
- Customer insights
- Order insights
- Product insights
- Financial/commercial insights
- Operational visibility
- Good UX

Do not simply expose database tables as CRUD screens unless there is a clear business requirement for doing so.

---

## Business Context

The main business entities currently understood are:

### FINCLI

Customer master.

Primary key:

`codcli`

Represents customers and contains customer identity, fiscal information, addresses, credit information, commercial relationships, and other customer attributes.

### PEDRES

Order master/header.

Primary key:

`(codemp, dteres, numres)`

Represents the general information about an order, including:

- Customer
- Seller
- Order date
- Delivery information
- Order status
- Order totals
- Tax totals
- Freight
- Insurance
- Other expenses
- Discounts

The customer relationship is:

`PEDRES.codcli -> FINCLI.codcli`

An order always has a customer.

### PEDRE2

Order item/detail.

Represents individual products/items belonging to an order.

The order relationship is:

`PEDRE2 (codemp, dteres, numres) -> PEDRES (codemp, dteres, numres)`

`seqre2` identifies the item/line within the order.

An order can have many items.

### ESTPRO

Product master.

Primary key:

`(codclp, codgru, codsub, codpro)`

The product relationship from `PEDRE2` is:

`PEDRE2 (codclp, codgru, codsub, codpro) -> ESTPRO (codclp, codgru, codsub, codpro)`

Size (`codtam`) and colour (`codcor`) are attributes of an order item but are **not business-relevant dimensions and do not identify a different product**.

Do not create dashboard concepts around product size or colour unless explicitly requested.

---

## Order Status

For dashboard purposes, `PEDRES.sitres` is the authoritative order lifecycle/status field.

Known statuses include:

- `Nao Concluido`
- `Processo de Alteracao`
- `Rejeitado`
- `Aguardando Periodo de Programacao`
- `Aguardando Liberacao do Depto de Compras`
- `Aguardando Liberacao do Depto Comercial`
- `Aguardando Consultas de Cadastro`
- `Aguardando Liberacao do Depto Financeiro`
- `Aguardando Separacao de Estoque`
- `Aguardando Complemento`
- `Aguardando Liberacao para Faturamento`
- `Aguardando Separacao dos Itens a Faturar`
- `Aguardando Confirmacao do Pagamento`
- `Pronto para Faturar`
- `Parcialmente Faturado`
- `Faturado`
- `Cancelado`
- `Faturado com Saldo nao Atendido`

Do not infer order status from undocumented flags when `sitres` is available.

Other legacy flags such as `flgres`, `flgfin`, `flgpro`, `flgfec`, etc. should not be assigned business meanings unless their semantics are explicitly documented.

---

## Source of Truth

Use the files under `.github/database-metadata/` as the primary source of business and database knowledge.

Before making assumptions about a table or column:

1. Check the relevant metadata file.
2. Check `dashboard-business-metrics.md`.
3. Check the documented relationships.
4. Check the actual application/backend code when relevant.

Do not invent business meanings for undocumented fields.

If the metadata explicitly defines a field, prefer that definition over assumptions based on the field name.

---

## Dashboard Design Philosophy

The application should favor analytical and decision-support functionality over simple data presentation.

Good dashboard functionality includes:

- KPI cards
- Trends
- Rankings
- Comparisons
- Distribution charts
- Customer segmentation
- Order status monitoring
- Product performance
- Sales performance
- Revenue analysis
- Margin/profitability analysis when reliable data exists
- Customer purchasing behavior
- Order pipeline visibility
- Drill-down from summary information to underlying records

Avoid creating large numbers of decorative charts that do not provide meaningful information.

Every visualization should answer a recognizable business question.

Examples:

- How much did we sell?
- How is sales evolving?
- Which customers generate the most revenue?
- Which customers have stopped buying?
- Which products sell the most?
- Which products generate the most profit?
- How many orders are waiting for action?
- What is the distribution of orders by status?
- What is the average order value?
- Which sellers generate the most business?
- How concentrated are sales among customers?
- How is the customer base evolving?

---

## KPI Guidelines

When proposing a KPI:

1. Identify the business entity.
2. Identify the source table.
3. Identify the source fields.
4. Define the calculation.
5. Define the relevant filters.
6. Define the time period.
7. Consider whether the metric can be reliably calculated from the available data.

Do not create KPIs merely because a numeric column exists.

For example, the existence of a numeric tax field does not automatically mean that it should become a dashboard KPI.

---

## Financial Metrics

Be careful when interpreting monetary fields.

Distinguish between:

- Item value
- Net value
- Gross value
- Taxes
- Discounts
- Freight
- Insurance
- Other expenses
- Cost
- Profit
- Margin

Do not blindly add monetary columns together.

Use documented total fields where available.

For order-level financial metrics, `PEDRES` should generally be the primary source.

For item-level financial metrics, `PEDRE2` should generally be the primary source.

---

## Customer Analytics

Customer analytics should use `FINCLI` as the customer master and `PEDRES` / `PEDRE2` for transactional information.

Potential customer metrics include:

- Total sales
- Number of orders
- Average order value
- Last order date
- Purchase frequency
- Number of products purchased
- Total quantity purchased
- Customer sales trend
- Credit limit
- Customer status
- Order status distribution

Do not assume that current customer master information is identical to historical information stored on an order.

`PEDRES` may contain historical customer fiscal information captured at the time of the order.

---

## Product Analytics

Product analytics should use:

- `ESTPRO` for product master information
- `PEDRE2` for transactional information

The product identity is:

`(codclp, codgru, codsub, codpro)`

Potential metrics include:

- Quantity sold
- Revenue
- Net sales
- Number of orders
- Number of customers
- Average selling price
- Cost
- Profit
- Margin
- Sales trend

Do not treat size or colour as separate products.

Do not create size/colour dashboards unless explicitly requested.

---

## Order Analytics

Order analytics should primarily use `PEDRES`.

Potential metrics include:

- Total orders
- Order value
- Average order value
- Orders by status
- Orders over time
- Orders by customer
- Orders by seller
- Orders awaiting action
- Orders ready for invoicing
- Partially invoiced orders
- Cancelled orders
- Invoiced orders

The authoritative status field is:

`PEDRES.sitres`

---

## UI / UX Principles

The dashboard should be modern and professional.

Prefer:

- Clear visual hierarchy
- Consistent spacing
- Responsive layouts
- Meaningful empty states
- Loading states
- Error states
- Accessible controls
- Consistent typography
- Consistent number formatting
- Consistent currency formatting
- Appropriate date formatting
- Useful filtering
- Drill-down navigation where appropriate

Avoid:

- Excessive visual decoration
- Unnecessary animations
- Dense tables without hierarchy
- Huge numbers of simultaneous charts
- Technical database terminology in user-facing UI
- Exposing internal IDs unless useful to the user

---

## Existing Application Architecture

Before creating a new component or page:

1. Inspect existing pages.
2. Inspect existing components.
3. Reuse established patterns.
4. Reuse existing API/service conventions.
5. Reuse existing hooks and types where appropriate.
6. Follow the existing styling and component library.
7. Avoid introducing a new pattern when an established project pattern already exists.

The application already contains customer-related functionality and API integration.

Do not rewrite working authentication, API integration, or established components without a clear reason.

---

## Backend/API Assumptions

The React application consumes data through the backend API.

Do not make the frontend directly access the legacy Firebird database.

Do not introduce database-specific logic into React components when the backend is responsible for data access and business logic.

Prefer:

`React component -> hook/service -> API -> backend -> PostgreSQL`

rather than embedding business/database queries in frontend code.

---

## Existing Functionality

Before proposing or implementing new functionality, inspect the existing application to avoid duplicating features that already exist.

Existing customer functionality includes:

- Customer listing
- Customer KPIs
- Customer rankings
- Customer risk information
- Customer region information
- Credit utilization information
- Customer growth information
- Customer directory
- Customer detail view

New functionality should complement the existing dashboard rather than duplicate it.

---

## AI-Assisted Dashboard Suggestions

When asked to suggest improvements to the dashboard, do not limit suggestions to fields already displayed by the application.

Use the database metadata and business metrics documentation to identify potentially valuable information that is not currently being displayed.

Suggestions should be prioritized according to:

### High priority

Information that:

- Supports business decisions
- Reveals important trends
- Identifies problems
- Helps users take action
- Uses reliable data already available

### Medium priority

Information that:

- Improves understanding
- Provides useful segmentation
- Adds context to existing KPIs
- Enables useful drill-downs

### Low priority

Information that:

- Is mainly decorative
- Adds little business value
- Requires complex implementation for limited benefit
- Exposes technical database details

When suggesting a new dashboard feature, explain:

- What business question it answers
- Why it is useful
- Which entities/tables provide the data
- Which fields are involved
- How the metric should be calculated
- Where it would fit in the existing UI

---

## Handling Uncertainty

The legacy database contains many fields whose business meaning is not yet fully understood.

Never invent a meaning for an undocumented field.

If a potentially valuable dashboard feature depends on an ambiguous field:

1. Identify the field.
2. Explain what information is missing.
3. Ask for clarification.
4. Do not implement the assumption as fact.

It is acceptable to recommend investigating a field before using it.

---

## Legacy System Context

The source system is a legacy Delphi/Firebird ERP.

The database contains historical naming conventions and many abbreviated field names.

Examples:

- `tot` often refers to a total
- `bas` often refers to a tax base
- `aliq` often refers to an aliquot/rate
- `dsc` often refers to discount
- `cod` often refers to a code/identifier
- `flg` often refers to a flag
- `qtd` often refers to quantity

These naming patterns can help with investigation, but they must not override explicit business documentation.

---

## Important Rule

The goal is not to reproduce the legacy database.

The goal is to use the legacy business data to create a better modern dashboard.

When there is a choice between:

1. faithfully exposing a legacy database concept, and
2. presenting a clearer business concept derived from the same data,

prefer the business-oriented presentation.

When analyzing the legacy database, do not limit recommendations to direct column-to-column mappings. Consider how combinations of fields, relationships between entities, historical records, aggregations, trends, ratios and derived metrics can produce meaningful business insights.

When suggesting data for the dashboard API, distinguish between:

raw data that should be extracted from the legacy system;
derived data that can be calculated by the loader;
business metrics that should be calculated by the API;
presentation concerns that belong exclusively to the React dashboard.

Prefer preserving sufficiently granular data in the API when doing so enables multiple future insights, rather than prematurely extracting only one pre-calculated metric.