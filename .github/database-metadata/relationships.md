# Database Relationships

This document describes the relationships between tables in the Emerion
database.

The relationship information comes primarily from the migrated PostgreSQL
schema. Some logical business relationships may exist even when they are not
represented by an explicit PostgreSQL foreign key constraint.

## Relationship Notation

Single-column relationship:

```text
TABLE.COLUMN -> OTHER_TABLE.COLUMN

Composite relationship:

TABLE.(COLUMN1, COLUMN2) -> OTHER_TABLE.(COLUMN1, COLUMN2)

A relationship identified from PostgreSQL foreign-key metadata is a
database relationship.

A relationship identified from the structure and business meaning of the
legacy application, but not represented by a PostgreSQL foreign key, is a
logical business relationship.

Do not assume that every logical business relationship has a corresponding
PostgreSQL foreign-key constraint.

Primary Domain Relationships

The current dashboard domain is primarily centered around:

PEDRES — order
PEDRE2 — order detail/item
FINCLI — customer
ESTPRO — product
PEDRES -> PEDRE2

PEDRE2 contains the individual items belonging to an order represented by
PEDRES.

The order is identified by the composite key:

PEDRES.(CODEMP, DTERES, NUMRES)

and the corresponding fields exist in PEDRE2:

PEDRE2.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)

Multiple PEDRE2 records can belong to a single PEDRES record.

Therefore:

PEDRES represents order-level information.
PEDRE2 represents item-level information.
An order can contain multiple items.
Order-level metrics must not be calculated by simply counting PEDRE2
rows.
PEDRE2 -> ESTPRO

Each PEDRE2 record contains the following product-identification fields:

CODCLP
CODGRU
CODSUB
CODPRO

These correspond exactly to the composite product key of ESTPRO:

ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)

Therefore there is a logical business relationship:

PEDRE2.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)

This relationship may not be represented as an explicit PostgreSQL foreign key
in the migrated schema.

The four columns must be treated together when identifying the product.

Do not join PEDRE2 to ESTPRO using only one of these columns unless the
business rule explicitly requires it.

PEDRES -> FINCLI

PEDRES contains customer information and is related to the customer entity
through:

PEDRES.CODCLI -> FINCLI.CODCLI

FINCLI.CODCLI identifies the customer.

PEDRE2 -> FINCLI

The order detail also contains customer information:

PEDRE2.CODCLI -> FINCLI.CODCLI

When analyzing an order, PEDRES.CODCLI should normally be considered the
order-level customer association, while PEDRE2.CODCLI is item/detail-level
data.

The application should not automatically assume that these fields can be
treated interchangeably without considering the business context.

Important Composite Keys
PEDRES

The order is identified by:

(CODEMP, DTERES, NUMRES)
PEDRE2

An individual order-detail record additionally contains:

SEQRE2

The order association is:

(CODEMP, DTERES, NUMRES)

with SEQRE2 distinguishing individual detail records within the order.

ESTPRO

The product is identified by:

(CODCLP, CODGRU, CODSUB, CODPRO)
FINCLI

The customer is identified by:

CODCLI
Product Relationships

ESTPRO has relationships with several supporting product classification
tables.

ESTPRO.CODCAT -> ESTCAT.CODCAT
ESTPRO.CODCLP -> ESTCLP.CODCLP
ESTPRO.CODCOM -> FINCOM.CODCOM
ESTPRO.CODMRC -> ESTMRC.CODMRC
ESTPRO.CODST1 -> ESTST1.CODST1
ESTPRO.CODST2 -> ESTST2.CODST2
ESTPRO.CODTIP -> ESTTIP.CODTIP
ESTPRO.CODUNE -> ESTUND.CODUND
ESTPRO.CODUNS -> ESTUND.CODUND

The product also has composite relationships with other classification and
taxation structures:

ESTPRO.(CODGRU, CODSUB) -> ESTSUB.(CODGRU, CODSUB)


ESTPRO.(CODSTE, TIPSTE) -> ESTSTR.(CODSTR, TIPSTR)


ESTPRO.(CODSTS, TIPSTS) -> ESTSTR.(CODSTR, TIPSTR)


ESTPRO.(IPIENT, IPITEN) -> ESTIPI.(CODIPI, TIPIPI)


ESTPRO.(IPISAI, IPITSD) -> ESTIPI.(CODIPI, TIPIPI)
Other Product-to-ESTPRO Relationships

The following tables reference the composite product key:

CMPPFO.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)


ESTBAR.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)


GERPRO.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)


PEDPR2.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)
Customer Relationships

FINCLI has relationships with several supporting entities.

FINCLI.CODBAN -> FINBAN.CODBAN
FINCLI.CODCCL -> FINCCL.CODCCL
FINCLI.CODCOM -> FINCOM.CODCOM
FINCLI.CODGCL -> FINGCL.CODGCL
FINCLI.CODTCL -> FINTCL.CODTCL
FINCLI.CODTRA -> FINTRA.CODTRA
FINCLI.CODUSU -> GERUSU.CODUSU
FINCLI.USUATU -> GERUSU.CODUSU
FINCLI.CODVEN -> FINVEN.CODVEN
FINCLI.CODATD -> FINVEN.CODVEN

Customer geographic/state relationships:

FINCLI.UFACLI -> GERUFE.SIGUFE
FINCLI.UFCCLI -> GERUFE.SIGUFE
FINCLI.UFECLI -> GERUFE.SIGUFE
FINCLI.UFFCLI -> GERUFE.SIGUFE

Customer financial/category relationships:

FINCLI.(CODMCR, CODMRG, CODSET, CODMST)
    -> FINMST.(CODMCR, CODMRG, CODSET, CODMST)


FINCLI.(CODPFA, TIPPFA)
    -> ESTPFA.(CODPFA, TIPPFA)

Customer-related entities:

FINCLI.ID_FINCIE -> FINCIE.ID_FINCIE
FINCLI.ID_FINCIF -> FINCIE.ID_FINCIE
FINCLI.ID_FINCT2 -> FINCT2.ID_FINCT2
FINCLI.ID_FINCT3 -> FINCT3.ID_FINCT3
FINCLI.ID_FINCT4 -> FINCT4.ID_FINCT4
FINCLI.ID_FINCT5 -> FINCT5.ID_FINCT5
FINCLI.ID_FINCT6 -> FINCT6.ID_FINCT6
FINCLI.ID_FINCTB -> FINCTB.ID_FINCTB
FINCLI.ID_FINHOL -> FINHOL.ID_FINHOL
FINCLI.ID_FINPAI -> FINPAI.ID_FINPAI
FINCLI.ID_FINUFE -> FINUFE.ID_FINUFE
FINCLI.ID_FINUFF -> FINUFE.ID_FINUFE
FINCLI -> Related Tables
FINCAL.ID_FINCLI -> FINCLI.CODCLI
FINCBL.ID_FINCLI -> FINCLI.CODCLI
FINCDE.CODCLI -> FINCLI.CODCLI
FINCHC.CODCLI -> FINCLI.CODCLI
FINCPR.CODCLI -> FINCLI.CODCLI
FINCRE.CODCLI -> FINCLI.CODCLI
FINRC4.CODCLI -> FINCLI.CODCLI
FINRMC.CODCLI -> FINCLI.CODCLI
FINRMO.CODCLI -> FINCLI.CODCLI


EXPETI.CODCLI -> FINCLI.CODCLI
FATCRE.CODCLI -> FINCLI.CODCLI
FATDCL.CODCLI -> FINCLI.CODCLI
FATGER.CODCLI -> FINCLI.CODCLI


LOJDEV.CODCLI -> FINCLI.CODCLI
LOJGER.CODCLI -> FINCLI.CODCLI
LOJOE3.CODCLI -> FINCLI.CODCLI
LOJPED.CODCLI -> FINCLI.CODCLI
LOJRP3.CODCLI -> FINCLI.CODCLI


PEDCTA.CODCLI -> FINCLI.CODCLI
PEDLIB.CODCLI -> FINCLI.CODCLI
PEDRES.CODCLI -> FINCLI.CODCLI
PEDRE2.CODCLI -> FINCLI.CODCLI
FINVEN Relationships
FINVEN.CODCOM -> FINCOM.CODCOM
FINVEN.CODCVE -> FINCVE.CODCVE
FINVEN.CODGVE -> FINGVE.CODGVE
FINVEN.CODTVE -> FINTVE.CODTVE
FINVEN.ID_FINCIE -> FINCIE.ID_FINCIE
FINVEN.ID_FINPAI -> FINPAI.ID_FINPAI
FINVEN.ID_FINUFE -> FINUFE.ID_FINUFE
FINVEN.SIGUFE -> GERUFE.SIGUFE

Other tables referencing FINVEN include:

EXPETI.CODVEN -> FINVEN.CODVEN
FATCRE.CODVEN -> FINVEN.CODVEN
FINCLI.CODATD -> FINVEN.CODVEN
FINCLI.CODVEN -> FINVEN.CODVEN
FINCRE.CODVEN -> FINVEN.CODVEN
GERUSU.CODVEN -> FINVEN.CODVEN
LOJDEV.CODVEN -> FINVEN.CODVEN
LOJGER.CODVEN -> FINVEN.CODVEN
LOJPED.CODVEN -> FINVEN.CODVEN
PEDCTA.CODVEN -> FINVEN.CODVEN
PEDLIB.CODVEN -> FINVEN.CODVEN
PEDRES.CODVEN -> FINVEN.CODVEN
PEDRES Relationships

PEDRES has relationships with the following entities:

PEDRES.CODCLI -> FINCLI.CODCLI
PEDRES.CODIPI -> ESTIPI.CODIPI
PEDRES.TIPIPI -> ESTIPI.TIPIPI
PEDRES.CODPFA -> ESTPFA.CODPFA
PEDRES.TIPPFA -> ESTPFA.TIPPFA
PEDRES.CODTIP -> PEDTIP.CODTIP
PEDRES.CODTRA -> FINTRA.CODTRA
PEDRES.CODUSU -> GERUSU.CODUSU
PEDRES.CODVEN -> FINVEN.CODVEN
PEDRES.UFERES -> GERUFE.SIGUFE
PEDRES.USUDEL -> GERUSU.CODUSU
PEDRES.USUFIN -> GERUSU.CODUSU
PEDRES.USUFPE -> GERUSU.CODUSU
PEDRES.USUREJ -> GERUSU.CODUSU

Composite taxation/product-classification relationships:

PEDRES.(CODIPI, TIPIPI)
    -> ESTIPI.(CODIPI, TIPIPI)


PEDRES.(CODPFA, TIPPFA)
    -> ESTPFA.(CODPFA, TIPPFA)
PEDRE2 Relationships

PEDRE2 belongs to PEDRES through the order composite key:

PEDRE2.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)

PEDRE2 also has the following logical product relationship:

PEDRE2.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)

This product relationship is based on the structure of PEDRE2 and ESTPRO
and may not be represented as an explicit PostgreSQL foreign key.

Other PEDRE2 relationships:

PEDRE2.CODCLI -> FINCLI.CODCLI
PEDRE2.CODIPI -> ESTIPI.CODIPI
PEDRE2.TIPIPI -> ESTIPI.TIPIPI
PEDRE2.CODPFA -> ESTPFA.CODPFA
PEDRE2.TIPPFA -> ESTPFA.TIPPFA
PEDRE2.CODTRA -> FINTRA.CODTRA
PEDRE2.CODVEN -> FINVEN.CODVEN
PEDRE2.CODUSU -> GERUSU.CODUSU
PEDRE2.UFERES -> GERUFE.SIGUFE
PEDRE2.USUDEL -> GERUSU.CODUSU
PEDRE2.USUFIN -> GERUSU.CODUSU
PEDRE2.USUFPE -> GERUSU.CODUSU
PEDRE2.USUREJ -> GERUSU.CODUSU
PEDRE2.CODTIP -> PEDTIP.CODTIP
PEDRES-Related Composite Relationships

Several tables reference PEDRES using its composite order key:

FATOBS.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDCOM.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDFIN.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDLOG.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDOCO.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDPRO.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDRE2.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDRE3.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDREJ.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)
PEDRE2-Related Tables

FATDE2 references PEDRE2 using the detail identifier:

FATDE2.(CODEMP, DTERES, NUMRES, SEQRE2)
    -> PEDRE2.(CODEMP, DTERES, NUMRES, SEQRE2)
Complete Relationship Map

The following relationships were identified from the migrated schema and
cleaned to remove duplicate column occurrences produced by the original
relationship extraction.

CMPPFO.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDLIB.CODCLI -> FINCLI.CODCLI
PEDLIB.CODVEN -> FINVEN.CODVEN


PEDLOG.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDOCO.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDPR2.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)


PEDPRO.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDRE2.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDRE2.(CODCLP, CODGRU, CODSUB, CODPRO)
    -> ESTPRO.(CODCLP, CODGRU, CODSUB, CODPRO)


PEDRE3.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDREJ.(CODEMP, DTERES, NUMRES)
    -> PEDRES.(CODEMP, DTERES, NUMRES)


PEDRES.(CODIPI, TIPIPI)
    -> ESTIPI.(CODIPI, TIPIPI)


PEDRES.(CODPFA, TIPPFA)
    -> ESTPFA.(CODPFA, TIPPFA)


PEDRES.CODCLI -> FINCLI.CODCLI
PEDRES.CODTIP -> PEDTIP.CODTIP
PEDRES.CODTRA -> FINTRA.CODTRA
PEDRES.CODUSU -> GERUSU.CODUSU
PEDRES.CODVEN -> FINVEN.CODVEN
PEDRES.UFERES -> GERUFE.SIGUFE
PEDRES.USUDEL -> GERUSU.CODUSU
PEDRES.USUFIN -> GERUSU.CODUSU
PEDRES.USUFPE -> GERUSU.CODUSU
PEDRES.USUREJ -> GERUSU.CODUSU


PEDRE2.(CODIPI, TIPIPI)
    -> ESTIPI.(CODIPI, TIPIPI)


PEDRE2.(CODPFA, TIPPFA)
    -> ESTPFA.(CODPFA, TIPPFA)


PEDRE2.CODCLI -> FINCLI.CODCLI
PEDRE2.CODTIP -> PEDTIP.CODTIP
PEDRE2.CODTRA -> FINTRA.CODTRA
PEDRE2.CODUSU -> GERUSU.CODUSU
PEDRE2.CODVEN -> FINVEN.CODVEN
PEDRE2.UFERES -> GERUFE.SIGUFE
PEDRE2.USUDEL -> GERUSU.CODUSU
PEDRE2.USUFIN -> GERUSU.CODUSU
PEDRE2.USUFPE -> GERUSU.CODUSU
PEDRE2.USUREJ -> GERUSU.CODUSU

Relationship Interpretation Rules

When using this relationship information for dashboard development:

Prefer composite keys when identifying PEDRES, PEDRE2, or ESTPRO
records.
Do not assume that a single column uniquely identifies a product when the
product is identified by (CODCLP, CODGRU, CODSUB, CODPRO).
Do not count PEDRE2 rows as orders.
Use PEDRES for order-level metrics.
Use PEDRE2 for item-level metrics.
Use FINCLI for customer-level metrics.
Use ESTPRO for product-level metrics.
Do not assume that absence of a PostgreSQL foreign key means absence of a
logical relationship.
Do not invent business meanings for columns based only on their names.
When a relationship is marked as logical rather than database-enforced,
treat it as a business relationship and verify its semantics before
introducing new business rules.