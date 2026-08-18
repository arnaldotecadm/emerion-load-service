# Core Business Relationships

This document describes the main business relationships between the four core
entities currently considered most important for the dashboard:

- `PEDRES` — Order
- `PEDRE2` — Order Item
- `FINCLI` — Customer
- `ESTPRO` — Product

These relationships represent the current understanding of the legacy business
domain and should be preferred over assumptions based only on column names.

---

## 1. PEDRES → FINCLI

### Relationship

`PEDRES.codcli = FINCLI.codcli`

`FINCLI.codcli` identifies the customer associated with an order.

The customer identified by `PEDRES.codcli` is always the customer who placed the
order.

### Cardinality

- One customer (`FINCLI`) can have many orders (`PEDRES`).
- Every order (`PEDRES`) must have a customer.
- An order without a customer is not considered valid in the business domain.

Conceptually:

```text
FINCLI (Customer)
    1
    |
    | places
    |
    N
PEDRES (Order)

Customer addresses

There is no separate business entity representing a different billing customer,
delivery customer, or ordering customer.

The customer remains the same (PEDRES.codcli), while the address associated
with the order may vary depending on the situation.

Therefore, do not interpret different addresses stored in PEDRES as different
customers.

2. PEDRES → PEDRE2
Relationship

The order identified by:

PEDRES (
    codemp,
    dteres,
    numres
)

has its items stored in PEDRE2.

The corresponding fields in PEDRE2 are:

PEDRE2 (
    codemp,
    dteres,
    numres,
    seqre2
)

seqre2 identifies the individual item/line within the order.

Cardinality
One PEDRES order can have many PEDRE2 order items.
Every PEDRE2 record belongs to one PEDRES order.

Conceptually:

PEDRES (Order)
    1
    |
    | contains
    |
    N
PEDRE2 (Order Items)
Order key

The business key of an order is:

(codemp, dteres, numres)

Do not assume numres alone uniquely identifies an order.

Item key

Within an order, seqre2 identifies the item/line.

The practical order-item identification is therefore:

(codemp, dteres, numres, seqre2)
Cancelled or deleted items

An item that has been cancelled/deleted from the order does not remain as a
PEDRE2 record.

Therefore:

Absence of a PEDRE2 record means that the item is not currently present in
the order's item dataset.
Do not assume that a missing item can be identified by a cancellation flag
in PEDRE2.
Historical deleted/cancelled items may therefore not be recoverable from
PEDRE2 alone.
3. PEDRE2 → ESTPRO
Relationship

Each order item references a product through:

PEDRE2 (
    codclp,
    codgru,
    codsub,
    codpro
)

which corresponds to the product key in:

ESTPRO (
    codclp,
    codgru,
    codsub,
    codpro
)
Product key

The product business key is the composite:

(codclp, codgru, codsub, codpro)

Do not assume codpro alone identifies a product.

Cardinality

Normally:

One ESTPRO product can appear in many PEDRE2 order items.
Each PEDRE2 item corresponds to exactly one ESTPRO product.

Conceptually:

ESTPRO (Product)
    1
    |
    | appears in
    |
    N
PEDRE2 (Order Items)
Product variants

PEDRE2.codtam represents the size associated with the ordered item.

PEDRE2.codcor represents the colour associated with the ordered item.

These fields describe the variant of the product being ordered.

Therefore, when presenting order-item information in the dashboard, consider
the product identity and its size/colour separately:

Product
  ├── Product code
  ├── Description
  ├── Size
  └── Colour

The size and colour are stored on the order item rather than being part of the
primary product relationship to ESTPRO.

4. Complete Core Relationship

The four main entities form the following business model:

FINCLI
Customer
   |
   | 1:N
   |
PEDRES
Order
   |
   | 1:N
   |
PEDRE2
Order Item
   |
   | N:1
   |
ESTPRO
Product

More explicitly:

FINCLI.codcli
       │
       │
       ▼
PEDRES.codcli


PEDRES.(codemp, dteres, numres)
       │
       │
       ▼
PEDRE2.(codemp, dteres, numres)


PEDRE2.(codclp, codgru, codsub, codpro)
       │
       │
       ▼
ESTPRO.(codclp, codgru, codsub, codpro)

This relationship chain is particularly important for dashboard development
because it allows the application to navigate from:

Customer
   → Orders
      → Order Items
         → Products

and also aggregate information in the opposite direction:

Product
   → Order Items
      → Orders
         → Customers
5. Order Status

For the current dashboard requirements, the relevant order status field is:

PEDRES.sitres

SITRES is the field currently identified as the authoritative source for the
order's business status.

It should be used when determining the order's lifecycle state, such as whether
an order is open, completed, cancelled, invoiced, delivered, or another
business state represented by the legacy system.

The exact values and meanings of SITRES have not yet been documented and
must not be guessed.

Before implementing status-specific dashboard logic, the actual SITRES
values and their meanings should be documented.

6. Unknown Legacy Flags

Several other fields exist in PEDRES and PEDRE2, including fields with names
such as:

flgres
flgfin
flgpro
pedpro
flgfec
flgreq
flgcmp
...

Their exact business responsibilities are currently unknown.

Do not infer their meaning solely from their names.

Until their semantics are confirmed from the legacy application or other
business documentation:

Do not use them to determine order status.
Do not use them to infer whether an order is completed.
Do not use them to infer whether an order is cancelled.
Do not use them as substitutes for PEDRES.sitres.
Do not create dashboard business rules based on them.

PEDRES.sitres is currently the only confirmed order-status field for the
dashboard.

7. Dashboard-Relevant Navigation

The core entities should support dashboard queries following these paths.

Customer-centric
FINCLI
  → PEDRES
      → PEDRE2
          → ESTPRO

Useful for questions such as:

What orders did a customer place?
How much has a customer ordered?
Which products has a customer purchased?
Which products are most frequently purchased by a customer?
What is the customer's order history?
Order-centric
PEDRES
  → FINCLI
  → PEDRE2
      → ESTPRO

Useful for:

Order details
Customer information
Order totals
Number of items
Products in an order
Product descriptions
Product size and colour
Order status
Product-centric
ESTPRO
  → PEDRE2
      → PEDRES
          → FINCLI

Useful for:

Product sales
Product popularity
Product sales by customer
Product sales by period
Product sales by order status
8. Important Rules for Dashboard Development

When generating queries, APIs, services, components, or visualizations based on
these entities:

Treat PEDRES as the order header/entity.
Treat PEDRE2 as the order-item/detail entity.
Treat FINCLI as the customer entity.
Treat ESTPRO as the product entity.
Use (codemp, dteres, numres) to identify an order.
Use seqre2 to identify an item within an order.
Use (codclp, codgru, codsub, codpro) to identify a product.
Use PEDRES.codcli = FINCLI.codcli to associate orders with customers.
Use PEDRES.(codemp, dteres, numres) = PEDRE2.(codemp, dteres, numres) to
associate orders with their items.
Use PEDRE2.(codclp, codgru, codsub, codpro) = ESTPRO.(codclp, codgru, codsub, codpro) to associate items with products.
Use PEDRE2.codtam for the ordered product size.
Use PEDRE2.codcor for the ordered product colour.
Use PEDRES.sitres as the currently confirmed order-status field.
Do not invent meanings for undocumented legacy flags.
Do not assume that codpro, numres, or codcli alone are globally unique
identifiers unless explicitly documented elsewhere.
Current Confidence Level

The relationships described in this document are based on confirmed knowledge
of the legacy application/business model.

The following are considered confirmed:

Customer → Order relationship
Order → Order Item relationship
Order Item → Product relationship
Composite order key
Composite product key
Item sequence
Product size/colour semantics
SITRES as the relevant order-status field

The exact meanings of individual SITRES values and the other legacy flags
remain to be documented.