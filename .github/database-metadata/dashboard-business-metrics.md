# Dashboard Business Metrics

This document defines the business meaning of the main entities and the rules that should be followed when proposing metrics, KPIs, charts, tables, filters, and other analytical features for the React dashboard.

The dashboard is customer-facing. Suggestions should prioritize information that is understandable, useful, and actionable for a business user rather than exposing legacy database implementation details.

## Core Business Entities

### Customer — FINCLI

`FINCLI` is the master entity for customers.

A customer is uniquely identified by:

`FINCLI.codcli`

It contains the customer's identity, fiscal information, commercial information, credit information, contact information, and several types of addresses.

Important business concepts include:

- Customer name / trade name
- CPF/CNPJ
- Customer registration status
- Credit limit
- Credit available through returns
- Seller associated with the customer
- Attendant associated with the customer
- Carrier associated with the customer
- Customer type
- Customer tax regime
- Customer ICMS taxpayer status
- Customer registration and update dates
- Customer contact information
- Customer addresses

The customer tax regime is represented by `regtrb`:

- `1` — Simples Nacional
- `2` — Simples Nacional - excesso de sublimite de receita bruta
- `3` — Regime Normal

`indic_ie` represents the customer's ICMS taxpayer status:

- `1` — Contribuinte
- `2` — Isento
- `9` — Não Contribuinte

Do not assume that a customer has only one address. The legacy system distinguishes between:

- Faturamento
- Compra
- Entrega
- Cobrança

These are different address purposes belonging to the same customer.

---

## Order — PEDRES

`PEDRES` is the order master/header entity.

An order is uniquely identified by:

`(codemp, dteres, numres)`

Where:

- `codemp` — company
- `dteres` — order issue date
- `numres` — order number

The order always has a customer.

Relationship:

`PEDRES.codcli -> FINCLI.codcli`

Therefore, `PEDRES.codcli` identifies the customer who placed the order.

An order can contain multiple order items.

Relationship:

`PEDRES (codemp, dteres, numres) -> PEDRE2 (codemp, dteres, numres)`

Important order-level business information includes:

- Order number
- Order date
- Customer
- Seller
- Attendant
- Expected delivery information
- Order status
- Order totals
- Tax totals
- Freight
- Insurance
- Other expenses
- Discounts
- Customer fiscal information
- Commercial information

### Order Status

The primary order lifecycle field for dashboard purposes is:

`PEDRES.sitres`

Known possible statuses include:

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

These statuses represent the order's business lifecycle.

Do not infer order lifecycle from other flags such as `flgres`, `flgfin`, `flgpro`, `flgfec`, etc. unless their business meaning is explicitly established.

For the current dashboard requirements, `sitres` is the authoritative order-status field.

---

## Order Item — PEDRE2

`PEDRE2` contains the individual items belonging to an order.

Each record represents one order line.

The item is identified within the order by:

`seqre2`

The order relationship is:

`PEDRE2 (codemp, dteres, numres) -> PEDRES (codemp, dteres, numres)`

One order can have many items.

Items that were cancelled/deleted in the legacy system may simply not exist in `PEDRE2`. Do not assume that the absence of an item represents a particular cancellation status.

Important item-level business information includes:

- Product
- Quantity
- Unit of measure
- Unit price
- Net item value
- Gross item value
- Discounts
- Additional charges
- Cost
- Profit/margin-related values
- Tax bases
- Tax amounts
- Freight
- Insurance
- Other expenses

---

## Product — ESTPRO

`ESTPRO` is the master entity for products.

A product is uniquely identified by:

`(codclp, codgru, codsub, codpro)`

The relationship from an order item is:

`PEDRE2 (codclp, codgru, codsub, codpro) -> ESTPRO (codclp, codgru, codsub, codpro)`

Every normal order item corresponds to one product.

Important product concepts include:

- Product description
- Product reference
- Product classification
- Product group
- Product subgroup
- Product category
- Brand
- Units of measure
- Packaging
- Weight
- Dimensions
- NCM
- CEST
- Product tax configuration
- Product status

### Size and Colour

`PEDRE2.codtam` represents size.

`PEDRE2.codcor` represents colour.

However, **size and colour are not part of product identity and are not relevant business dimensions for the dashboard**.

Do not:

- Treat size as a separate product
- Treat colour as a separate product
- Use size as a product identifier
- Use colour as a product identifier
- Propose size-based or colour-based KPIs unless explicitly requested

For business analytics, the product identity is always:

`(codclp, codgru, codsub, codpro)`

---

# Important Relationships

The main business relationship graph is:

```text
FINCLI
  |
  | codcli
  |
  v
PEDRES
  |
  | (codemp, dteres, numres)
  |
  v
PEDRE2
  |
  | (codclp, codgru, codsub, codpro)
  |
  v
ESTPRO

This relationship should be used when reasoning about dashboard metrics.

For example:

Customer sales → FINCLI + PEDRES
Order volume → PEDRES
Order status → PEDRES.sitres
Product sales → PEDRE2 + ESTPRO
Product profitability → PEDRE2
Customer product purchasing → FINCLI + PEDRE2 + ESTPRO
Sales by seller → PEDRES.codven
Sales by customer → PEDRES.codcli
Sales by product → PEDRE2 grouped by product identity
Revenue and Order Value

When analyzing orders, distinguish between:

Item values
Taxes
Discounts
Freight
Insurance
Other expenses
Final order value

PEDRES.totger is documented as the total order value, representing the sum of items and applicable taxes.

PEDRES.totres represents the total value of the order items.

PEDRES.totipi represents total IPI.

PEDRES.toticm represents total ICMS.

PEDRES.totsub represents total ICMS substitution tax.

PEDRES.totpis represents total PIS.

PEDRES.totcof represents total COFINS.

PEDRES.totfrt represents total freight.

PEDRES.totseg represents total insurance.

PEDRES.totoutdesp represents other expenses.

PEDRES.totdescinc represents unconditional discounts.

When calculating dashboard metrics, avoid blindly adding these fields together. Use the documented total fields according to their business meaning.

Product Sales Metrics

When calculating product performance, the primary product key is:

(codclp, codgru, codsub, codpro)

Useful metrics may include:

Total quantity sold
Number of orders containing the product
Gross sales
Net sales
Discounts
Taxes
Cost
Gross profit
Profit margin
Average selling price
Sales trend over time
Number of customers purchasing the product

Product descriptions should normally come from ESTPRO, while transaction values should normally come from PEDRE2.

Customer Metrics

Useful customer-level metrics may include:

Total orders
Total sales
Average order value
Last order date
Number of products purchased
Total quantity purchased
Credit limit
Available credit
Customer status
Sales evolution
Order status distribution

Customer analytics should use FINCLI as the customer master and PEDRES/PEDRE2 for transactional information.

Order Metrics

Useful order-level metrics may include:

Total orders
Orders by status
Orders by date
Orders by customer
Orders by seller
Average order value
Total order value
Total item value
Orders awaiting action
Orders ready for invoicing
Partially invoiced orders
Cancelled orders
Completed/invoiced orders

The status dimension should use PEDRES.sitres.

Dashboard Design Principles

When proposing dashboard features:

Prefer business-oriented metrics over raw database fields.
Use PEDRES for order-level metrics.
Use PEDRE2 for item-level metrics.
Use FINCLI for customer master information.
Use ESTPRO for product master information.
Respect the documented primary keys and relationships.
Do not invent business meanings for undocumented flags.
Do not infer lifecycle states from undocumented fields when sitres is available.
Do not expose legacy database terminology to end users unless necessary.
Prefer trends, comparisons, rankings, distributions, and actionable indicators over simple raw data dumps.
When calculating monetary KPIs, clearly distinguish between gross, net, tax, discount, cost, and final order values.
When a metric's definition is ambiguous, do not silently invent a formula. Flag the ambiguity for review.
Business-Relevant Dimensions

The following dimensions are potentially useful for dashboard analytics:

Date
Customer
Seller
Order status
Product
Product group
Product subgroup
Product category
Product brand
Customer type
Customer tax regime
Company
Fiscal/tax classification where appropriate

The following are currently not considered business-relevant dashboard dimensions:

Product size (codtam)
Product colour (codcor)
Undocumented technical flags
Internal database identifiers that have no business meaning
Historical Data Considerations

Some information is duplicated between the master entities and transactional entities.

For example, PEDRES stores customer fiscal information such as:

cgccli
inscli
ufecli

These values should be treated as information associated with the order, while FINCLI represents the current customer master record.

Do not automatically assume that the current FINCLI values are identical to the values stored on historical orders.

This distinction may be important when displaying historical fiscal information.

Recommendations for AI-Assisted Dashboard Development

When Copilot or another AI assistant proposes dashboard functionality, it should first reason from the business relationships defined in this document.

A proposed metric should identify:

The business entity being measured.
The source table.
The relevant field(s).
The aggregation or calculation.
Any filters required.
Any known limitations or ambiguities.

For example:

Metric:
Average Order Value


Entity:
Order


Source:
PEDRES


Calculation:
SUM(PEDRES.totger) / COUNT(DISTINCT order)


Grouping:
Optional by month, customer, seller, or status

AI-generated suggestions must not assume that an undocumented field has a particular business meaning simply because its name appears suggestive.

When the existing information is insufficient to define a metric confidently, the assistant should ask for clarification rather than inventing business rules.