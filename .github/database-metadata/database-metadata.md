# Database Metadata

## Purpose

This document provides the business and semantic meaning of the legacy database entities that are relevant to the Emerion Dashboard.

The objective is not to reproduce the complete database schema. Instead, this document explains the entities, their important attributes, relationships, keys, and business meaning so that AI-assisted development can correctly interpret the legacy data when proposing dashboard features, APIs, queries, and UI components.

The legacy system uses abbreviated Portuguese column names. Column names should therefore not be interpreted literally when their business meaning is documented here.

---

# 1. Core Business Model

The currently documented business model revolves around four main entities:

```text
FINCLI
  │
  │ 1:N
  ▼
PEDRES
  │
  │ 1:N
  ▼
PEDRE2
  │
  │ N:1
  ▼
ESTPRO

PEDRES
  │
  │ N:1
  ▼
FINVEN

Conceptually:

FINCLI represents customers.
PEDRES represents orders.
PEDRE2 represents the individual items belonging to an order.
ESTPRO represents products.
FINVEN represents salespeople.
An order belongs to one customer.
An order can contain multiple items.
Each order item normally refers to one product.
A salesperson can be associated with many orders.
Customer, order, product, and salesperson relationships are the primary business dimensions currently identified for dashboard analysis.
2. FINCLI — Customers
Business purpose

FINCLI is the main customer master table.

It contains the customer's identity, fiscal information, credit information, contact information, multiple address types, and relationships with other commercial entities.

The customer identifier is codcli.

Primary key
codcli

codcli is the internal customer identifier used throughout the system.

Important customer attributes
Identity
Column	Business meaning
codcli	Internal customer identifier
nomcli	Customer's full name
apecli	Customer's nickname or trade name
cgccli	Customer CPF or CNPJ
tipo_pessoa	Type of person/entity
dtncli	Date of birth for an individual or foundation date for a legal entity

cgccli is the Brazilian fiscal identification document. An 11-digit value represents CPF; longer values represent CNPJ.

Credit
Column	Business meaning
limcli	Available/customer credit limit
clidev	Credit available as a result of returns
dtlcre	Date limit associated with the credit value

Credit-related information can potentially support customer financial/risk dashboards.

Customer status
Column	Business meaning
flbcli	Indicates whether the customer's registration is blocked or not blocked

Do not assume undocumented values for flbcli. The meaning of individual flag values must be determined from actual system data or application logic.

Commercial relationships
Column	Related entity	Meaning
codven	FINVEN	Salesperson associated with the customer
codtra	Transport entity	Transport company associated with the customer
codusu	User entity	User who registered the customer
codatd	Attendant entity	Attendant associated with the customer
codtcl	Customer type entity	Customer type
codgcl	Customer group entity	Customer group
Tax information
Column	Business meaning
regtrb	Tax regime
indic_ie	State registration indicator
insc_municipal	Municipal registration
cnae	Brazilian economic activity classification
cest	Tax classification
indic_estrangeiro	Foreign/non-resident customer indicator

regtrb has documented values:

1 = Simples Nacional
2 = Simples Nacional - excesso de sublimite de receita bruta
3 = Regime Normal

indic_ie has documented values:

1 = Contribuinte
2 = Isento
9 = Não Contribuinte
Addresses

FINCLI stores several addresses for the same customer.

The addresses are attributes of the customer and do not represent different customer identities.

Known address categories include:

Billing/invoicing address
Purchase address
Delivery address
Collection/billing address

Examples:

cefcli / tefcli / enfcli / rffcli / nrfcli / bafcli / cifcli / uffcli

represent the invoicing address.

ceacli / teacli / enacli / rfacli / nracli / baacli / ciacli / ufacli

represent the purchase address.

ceecli / teecli / enecli / rfecli / nrecli / baecli / ciecli / ufecli

represent the delivery address.

ceccli / teccli / enccli / rfccli / nrccli / baccli / ciccli / ufccli

represent the collection/billing address.

These addresses should not be interpreted as separate customers.

Contact information

Telephone fields exist for the different address contexts:

pt1cli / fo1cli — invoicing address phone
pt2cli / fo2cli — collection/billing address phone
pt3cli / fo3cli — purchase address phone
pt4cli / fo4cli — delivery address phone

Email and web information include:

em1cli
em2cli
em3cli
webcli
email_interno_xml
Customer notes

obscli contains general customer observations.

obsfin contains observations intended for the financial department when an order is launched.

3. FINVEN — Salespeople
Business purpose

FINVEN contains salespeople associated with customers and orders.

Primary key
codven
Important attributes
Column	Business meaning
codven	Internal salesperson identifier
nomven	Salesperson name
apeven	Salesperson nickname/trade name
cgcven	CPF/CNPJ identifying the salesperson
insven	State registration or RG
cepven	Postal code
endven	Address
cidven	City
sigufe	State
id_finpai	Country reference

FINVEN can be used to provide salesperson-level analysis of orders and sales.

4. PEDRES — Orders
Business purpose

PEDRES is the order header/master table.

It stores general information about orders, including:

customer
salesperson
issue date
delivery information
order status
financial totals
tax totals
freight
other order-level information
Primary key

The order identifier is:

(codemp, dteres, numres)

This is the documented primary key of the order.

The fields mean:

Column	Meaning
codemp	Company identifier
dteres	Order issue date
numres	Order number

numres is widely used by other modules to refer to the order.

Customer relationship
PEDRES.codcli → FINCLI.codcli

codcli identifies the customer who placed the order.

Confirmed business rules:

Every order has a customer.
An order cannot exist without a customer.
There is no separate billing customer, purchasing customer, or delivery customer entity in this model.
Address differences represent different addresses belonging to the same customer.

Therefore:

PEDRES → FINCLI

is a direct customer relationship.

Salesperson relationship
PEDRES.codven → FINVEN.codven

codven identifies the salesperson associated with the order.

This relationship can support sales performance and order distribution analysis.

5. PEDRES — Important Order Attributes
Order date
dteres

Date on which the order was issued.

This is one of the most important dimensions for temporal analysis.

Possible dashboard uses include:

daily order volume
monthly order volume
sales evolution
seasonal patterns
year-over-year comparisons
Expected delivery
prfres
dtfres

prfres represents the delivery period in days.

dtfres represents the expected delivery date.

These fields may support delivery-related analysis, although actual delivery completion semantics should not be inferred without additional documented data.

Order state
sitres

sitres is the authoritative order lifecycle/status field currently identified for dashboard purposes.

Known possible values include:

Nao Concluido
Processo de Alteracao
Rejeitado
Aguardando Periodo de Programacao
Aguardando Liberacao do Depto de Compras
Aguardando Liberacao do Depto Comercial
Aguardando Consultas de Cadastro
Aguardando Liberacao do Depto Financeiro
Aguardando Separacao de Estoque
Aguardando Complemento
Aguardando Liberacao para Faturamento
Aguardando Separacao dos Itens a Faturar
Aguardando Confirmacao do Pagamento
Pronto para Faturar
Parcialmente Faturado
Faturado
Cancelado
Faturado com Saldo nao Atendido

Not every company necessarily uses every possible status.

Dashboard logic should use sitres rather than attempting to derive order state from the other undocumented flags in PEDRES.

Important status rule

Fields such as:

flgres
flgfin
flgpro
flgfec
flgreq
flgcmp
flgimp
flgger
flgsld

may have operational significance in the legacy application, but their exact business responsibility is currently not established.

Therefore:

Do not infer order lifecycle state from these fields.

For dashboard purposes, use sitres unless additional business documentation establishes otherwise.

6. PEDRES — Order Financial Information

Important order-level financial fields include:

Column	Business meaning
totres	Total value of the order items
totger	Total order value including applicable taxes
totven	Order sales value; exact distinction from other totals should follow application semantics
totcst	Cost-related total
totren	Revenue-related total; exact accounting interpretation should not be assumed without further validation
totipi	Total IPI
toticm	Total ICMS
totsub	Total ICMS substitution tax
totpis	Total PIS
totcof	Total COFINS
totfrt	Total freight
totseg	Total insurance
totoutdesp	Total other expenses
totdescinc	Total unconditional discount

The distinction between some similarly named financial totals should not be inferred solely from their abbreviated names.

When calculating business KPIs, prefer fields whose meaning has been explicitly documented.

7. PEDRES — Tax Information

Important tax fields include:

totipi
basicm
toticm
bassub
totsub
totpis
totcof

These represent order-level tax bases and totals.

Tax-related fields are useful for fiscal analysis but should generally not be exposed as primary commercial KPIs unless the dashboard has a specific fiscal purpose.

8. PEDRE2 — Order Items
Business purpose

PEDRE2 contains the individual items belonging to orders.

An order can have multiple PEDRE2 records.

Order relationship
PEDRE2.codemp → PEDRES.codemp
PEDRE2.dteres → PEDRES.dteres
PEDRE2.numres → PEDRES.numres

The order reference is:

(codemp, dteres, numres)

The item additionally has:

seqre2

which identifies the order item/line.

Conceptually:

PEDRES
  1
  │
  ├── seqre2 = 1
  ├── seqre2 = 2
  ├── seqre2 = 3
  └── ...
Item identity

The documented order-item identifier is:

(codemp, dteres, numres, seqre2)

seqre2 is the item/line number within the order.

Cancelled/deleted items

Some order items may be cancelled or deleted in the legacy system.

When this happens, the item simply does not exist in PEDRE2.

Therefore:

Absence of a PEDRE2 record must not automatically be interpreted as an explicit cancellation status.

9. PEDRE2 → ESTPRO — Product Relationship

The product is identified by:

(codclp, codgru, codsub, codpro)

This combination is the product key in ESTPRO.

Relationship:

PEDRE2
  │
  │ (codclp, codgru, codsub, codpro)
  ▼
ESTPRO

Every normal order item corresponds to one product.

Product identification rule

The following fields together identify the product:

codclp
codgru
codsub
codpro

Do not treat codtam or codcor as part of the product identity.

10. PEDRE2 — Product Attributes

codtam and codcor represent:

codtam — product size
codcor — product colour

These are merely attributes associated with the order item.

Important business rule

Size and colour are irrelevant for the business analysis currently being developed.

They:

do not distinguish products
do not participate in the product key
should not be treated as separate product dimensions
should not be used to create product variants in dashboard logic

Unless future requirements explicitly change this, dashboard analysis should ignore these fields.

11. PEDRE2 — Important Item Attributes
Column	Business meaning
seqre2	Item/line number
codeit	Internal item-related identifier
desre2	Item description
obsre2	Item observation
codund	Product unit
qtpre2	Quantity ordered
vlure2	Unit price
vlqre2	Net item value
dscre2	Discount percentage
pacre2	Additional charge percentage
totre2	Total net item value
totge2	Total item value including taxes
totliq	Total net weight
totbrt	Total gross weight
totfrt	Item freight
totpis	Item PIS
totcof	Item COFINS

These fields provide the basis for item-level sales and product analysis.

12. ESTPRO — Products
Business purpose

ESTPRO is the product master table.

It contains product descriptions, classification, packaging, units, weights, dimensions, tax configuration, and other product-related attributes.

Product key

The product identity is:

(codclp, codgru, codsub, codpro)

Where:

Column	Meaning
codclp	Product classification code
codgru	Product group
codsub	Product subgroup
codpro	Product code

Together these fields identify the product.

Important product attributes
Column	Business meaning
dscpro	Product description
dsrpro	Secondary/short description
cbapro	Product barcode
cbaemb	Packaging barcode
qtdemb	Quantity per package
catpro	Product category
locpro	Product location
refpro	Product reference
codune	Unit
qtepro	Quantity-related product information
coduns	Secondary unit
qtspro	Secondary quantity
pesliq	Net weight
pesbrt	Gross weight
codmrc	Brand
codcat	Category identifier
codncm	NCM classification
codanp	ANP classification
cest	CEST tax classification
flgatu	Product active/update flag
Product classification

The product hierarchy can potentially be represented as:

Classification
    │
    └── Group
          │
          └── Subgroup
                │
                └── Product

using:

codclp
codgru
codsub
codpro

This hierarchy may be useful for dashboard aggregation if the corresponding classification tables are available.

13. Product and Order Analysis

For commercial dashboards, the most important relationship is:

PEDRES
   │
   │ order
   ▼
PEDRE2
   │
   │ product key
   ▼
ESTPRO

This allows analysis such as:

sales by product
quantity sold by product
revenue by product
orders containing a product
product sales over time
sales by product category
sales by product group/subgroup
average selling price
product contribution to total sales

The exact financial metric used must be explicitly selected rather than assuming that every tot* field represents the same concept.

14. Customer → Order Analysis

The primary relationship is:

FINCLI.codcli
       │
       │
       ▼
PEDRES.codcli

This supports:

sales by customer
orders by customer
average order value
customer purchase frequency
customer activity over time
top customers
customer concentration
customer distribution by region
customer order-status distribution

Because an order cannot exist without a customer, customer-level order analysis can use PEDRES.codcli as the authoritative relationship.

15. Salesperson → Order Analysis

The primary relationship is:

FINVEN.codven
       │
       │
       ▼
PEDRES.codven

This supports:

sales by salesperson
orders by salesperson
average order value by salesperson
customer portfolio by salesperson
salesperson performance over time
16. Order Status Analysis

PEDRES.sitres is currently the authoritative status field.

For dashboard purposes, orders can be grouped according to the actual values of sitres.

Possible analytical groupings may include:

Open / in process

Statuses such as:

Nao Concluido
Processo de Alteracao
Aguardando Periodo de Programacao
Aguardando Liberacao do Depto de Compras
Aguardando Liberacao do Depto Comercial
Aguardando Consultas de Cadastro
Aguardando Liberacao do Depto Financeiro
Aguardando Separacao de Estoque
Aguardando Complemento
Aguardando Liberacao para Faturamento
Aguardando Separacao dos Itens a Faturar
Aguardando Confirmacao do Pagamento
Ready for invoicing
Pronto para Faturar
Partially completed
Parcialmente Faturado
Completed/invoiced
Faturado
Cancelled
Cancelado
Completed with outstanding quantity
Faturado com Saldo nao Atendido

These groupings are analytical interpretations of the documented status values. The raw sitres value should remain available when detailed status information is required.

17. Multi-Company Consideration

The legacy system is designed as a multi-company system.

codemp identifies the company associated with an order.

The order key therefore includes:

codemp

Any query or aggregation involving orders should consider whether the dashboard is intended to:

show all companies combined,
filter by company, or
display company-specific results.

Do not assume that numres alone uniquely identifies an order across the entire database.

The complete order identity is:

(codemp, dteres, numres)
18. Recommended Analytical Grain

When working with this data, the following grains should be respected.

Customer grain
FINCLI

One record represents one customer.

Order grain
PEDRES

One record represents one order.

Unique identifier:

(codemp, dteres, numres)
Order-item grain
PEDRE2

One record represents one item in an order.

Unique identifier:

(codemp, dteres, numres, seqre2)
Product grain
ESTPRO

One record represents one product identified by:

(codclp, codgru, codsub, codpro)
Salesperson grain
FINVEN

One record represents one salesperson.

19. Important Data Interpretation Rules

The following rules should be treated as established business knowledge.

Rule 1 — Customer identity

FINCLI.codcli is the customer identifier.

Rule 2 — Order customer

PEDRES.codcli identifies the customer who placed the order.

Rule 3 — Orders require customers

An order cannot exist without a customer.

Rule 4 — No separate customer roles

The system does not currently distinguish between customer, billing customer, purchasing customer, and delivery customer.

Different addresses are attributes of the same customer.

Rule 5 — Order identity

An order is identified by:

(codemp, dteres, numres)
Rule 6 — Order items

An order can contain multiple PEDRE2 records.

Rule 7 — Item identity

An order item is identified by:

(codemp, dteres, numres, seqre2)
Rule 8 — Product identity

A product is identified by:

(codclp, codgru, codsub, codpro)
Rule 9 — Size and colour

codtam and codcor are attributes only.

They do not participate in product identification.

They are irrelevant to current business analysis.

Rule 10 — Order status

PEDRES.sitres is the authoritative order status field currently identified.

Rule 11 — Unknown flags

Do not infer business meaning from undocumented flags.

Rule 12 — Missing order items

The absence of a PEDRE2 record should not automatically be interpreted as cancellation.

Rule 13 — Abbreviated column names

Do not infer precise business meaning solely from an abbreviated legacy column name when documented semantics are available.

Rule 14 — Financial totals

Do not assume that similarly named monetary columns have identical meanings.

Use documented business definitions.

Rule 15 — Multi-company

Do not assume numres is globally unique.

Include codemp when identifying orders.

20. Current Confidence Levels

The following relationships are explicitly confirmed:

Relationship	Confidence
PEDRES.codcli → FINCLI.codcli	Confirmed
PEDRES.codven → FINVEN.codven	Confirmed
PEDRES → PEDRE2	Confirmed
(codemp, dteres, numres) as order key	Confirmed
seqre2 as order item sequence	Confirmed
PEDRE2 → ESTPRO	Confirmed
(codclp, codgru, codsub, codpro) as product key	Confirmed
codtam = size	Confirmed
codcor = colour	Confirmed
Size/colour irrelevant to business analysis	Confirmed
PEDRES.sitres as order status	Confirmed

The following areas remain incompletely documented:

exact meaning of several legacy flags
exact semantics of some financial totals
complete product classification relationships
complete salesperson hierarchy
detailed meaning of several operational fields
historical behavior of deleted/cancelled records
additional entities involved in invoicing, payments, inventory, delivery, and financial transactions

These should not be invented by AI-assisted development.

21. Guidance for AI-Assisted Development

When generating queries, APIs, services, or dashboard components:

Start from the documented entity relationships.
Respect the documented keys.
Use PEDRES for order-level analysis.
Use PEDRE2 for item-level analysis.
Join products through the four-field product key.
Join customers through codcli.
Join salespeople through codven.
Use sitres for order status.
Do not treat size or colour as product dimensions.
Do not invent semantics for undocumented flags.
Do not infer cancellation merely from missing PEDRE2 records.
Preserve the distinction between order-level and item-level measures.
Be careful when aggregating monetary fields to avoid double-counting order-level values after joining with PEDRE2.
Prefer business-meaningful metrics over exposing raw database columns.
When a required business meaning is unknown, explicitly identify the uncertainty rather than guessing.
22. Dashboard-Relevant Data Model

The currently established model can be summarized as:

                         FINVEN
                           │
                           │ codven
                           │
                           ▼
FINCLI ────────< PEDRES >────────
  │               │
  │ codcli        │ order key
  │               │
  │               ▼
  │             PEDRE2
  │               │
  │               │ product key
  │               ▼
  │             ESTPRO
  │
  └── Customer information
      ├── Identity
      ├── Credit
      ├── Addresses
      ├── Contact
      ├── Tax information
      └── Commercial relationships

This model provides the current foundation for building the customer-facing dashboard.

The primary analytical path is:

Customer
   ↓
Orders
   ↓
Order Items
   ↓
Products

with salesperson analysis connected at the order level.

This is the primary data model that Copilot should use when proposing da