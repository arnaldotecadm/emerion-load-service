PEDRES represents information at the order level and should not be treated
as an individual order item.

PEDRE2 — Order Details

PEDRE2 contains the details of an order.

Each record represents an item associated with an order and contains information
such as:

item quantities
item values
product information
item characteristics
other item-level information

PEDRE2 is associated with PEDRES through the order's composite business
key:

(CODEMP, DTERES, NUMRES)

Because multiple PEDRE2 records can belong to the same PEDRES record,
order-level metrics and item-level metrics must be treated separately.

FINCLI — Customer

FINCLI is the main customer entity.

It stores the information required to identify and describe a customer,
including the customer's identifying and registration information.

The primary customer identifier is:

CODCLI
ESTPRO — Product

ESTPRO contains the information related to products.

A product is identified by the composite business key:

(CODCLP, CODGRU, CODSUB, CODPRO)

The product entity is associated with product classifications, groups,
subgroups, categories, brands, types, and units through relationships with
other EST* tables.

Documentation Files
relationships.md

Contains the foreign-key relationships extracted from the migrated PostgreSQL
database.

This describes how tables and columns are technically related.

pedres.md

Documents the business meaning, important fields, relationships, and
dashboard relevance of PEDRES.

pedre2.md

Documents the business meaning, important fields, relationships, and
dashboard relevance of PEDRE2.

fincli.md

Documents the customer entity represented by FINCLI.

estpro.md

Documents the product entity represented by ESTPRO.

business-concepts.md

Describes higher-level business concepts that span multiple database tables.

business-rules.md

Contains business rules discovered from the legacy Delphi application,
database structure, reports, and other verified sources.

Only verified business rules should be documented here. Assumptions must be
clearly identified as assumptions.

dashboard-opportunities.md

Contains potential dashboard insights and features derived from the documented
data model and business concepts.

These are suggestions, not necessarily requirements.

How to Use This Documentation

When working on the Emerion Dashboard:

Use the entity-specific documentation to understand the meaning of the
relevant tables and columns.
Use relationships.md to understand how entities are connected.
Use business-rules.md when implementing calculations, filters, statuses,
or other business logic.
Use business-concepts.md to understand how multiple tables represent a
business concept.
Use dashboard-opportunities.md for potential analytical and UI ideas.

Database structure alone should not be interpreted as business logic.

When the business meaning of a field or relationship is unknown, do not invent
an interpretation. Mark it as unknown or ask for clarification.

Source of Information

The information in this directory is derived from:

the migrated PostgreSQL schema
the legacy Firebird database structure
the legacy Delphi application
Delphi screens and reports inspected by the developer
verified observations about existing application behavior

The legacy Delphi application is an important source of business semantics
because the database schema alone does not fully describe the meaning of the
data.

Important Conventions
Composite Business Keys

Some important entities use composite keys rather than a single identifier.

PEDRES:

(CODEMP, DTERES, NUMRES)

ESTPRO:

(CODCLP, CODGRU, CODSUB, CODPRO)

These keys must be treated as a unit when joining or identifying records.

Relationship Notation

Single-column relationships:

TABLE.COLUMN -> OTHER_TABLE.COLUMN

Composite relationships:

TABLE.(COLUMN1, COLUMN2) -> OTHER_TABLE.(COLUMN1, COLUMN2)
Order-Level vs Item-Level Data

PEDRES contains order-level information.

PEDRE2 contains item-level information.

Multiple PEDRE2 records may belong to a single PEDRES record.

When calculating metrics, aggregations, totals, counts, or KPIs, this distinction
must be preserved.

For example, the number of PEDRE2 records must not automatically be
interpreted as the number of orders.

Information Certainty

Information should be distinguished between:

Verified — directly confirmed from the application, database, or code.
Inferred — strongly suggested by the available evidence but not directly
confirmed.
Unknown — the meaning has not yet been established.

Do not treat inferred or unknown information as verified business logic.