# ESTPRO — Product Master

## Purpose

`ESTPRO` is the main product entity.

It stores the master data and business characteristics of products used throughout the commercial/order process, including:

- Product identification
- Product description and references
- Product classification
- Units of measure
- Packaging information
- Physical characteristics
- Tax-related configuration
- Product status
- Brand/category relationships
- Barcodes
- Regulatory classifications
- Product-specific pricing/cost-related information
- Product images and descriptive information

`ESTPRO` is particularly important for the dashboard because `PEDRE2` references it through the composite product key:

`PEDRE2.(codclp, codgru, codsub, codpro) -> ESTPRO.(codclp, codgru, codsub, codpro)`

This relationship allows order-item data to be enriched with the product's description, classification, unit, brand, tax configuration, physical characteristics, and other product metadata.

---

## Primary / Business Key

The product is identified by a composite key:

```text
(codclp, codgru, codsub, codpro)

Field	Meaning
codclp	Product class/category hierarchy level
codgru	Product group
codsub	Product subgroup
codpro	Product code

The exact business terminology of codclp, codgru, codsub, and codpro should be treated as legacy-domain terminology unless confirmed by the application/business documentation.

For dashboard purposes, the complete combination should be treated as the product identifier rather than assuming codpro alone uniquely identifies a product.

Core Product Identification
Field	Type	Description
codclp	character(1)	First component of the product composite key
codgru	character(3)	Second component of the product composite key
codsub	character(4)	Third component of the product composite key
codpro	character(5)	Fourth component of the product composite key
dscpro	varchar(70)	Main product description/name
dsrpro	varchar(40)	Short product description/reference
refpro	varchar(20)	Product reference
idepro	varchar(30)	Product identifier
id2pro	varchar(30)	Secondary product identifier
numpro	varchar(30)	Product number
codant	varchar(30)	Previous/legacy product code or identifier
nropro	integer	Product number/sequence; exact business meaning should be confirmed

dscpro is expected to be one of the most important fields for the dashboard because it provides the human-readable product name.

Product Classification
Field	Description
catpro	Product category/classification stored as text
codcat	Product category identifier
codmrc	Brand/manufacturer identifier
codtip	Product type identifier
codst1	Product status/classification code
codst2	Additional product status/classification code
codcom	Commercial classification/code
prodep	Product department/dependency classification
flgkit	Indicates a kit/product composed of other products
flgpro	Product-related status flag
flbpro	Product flag; exact business meaning should be confirmed
flgtrg	Product flag; exact business meaning should be confirmed
flglis	Product/listing flag; exact business meaning should be confirmed
flgatu	Product update/status flag

The classification fields should be used carefully. The exact interpretation of legacy flg* fields should come from the Delphi application or existing business rules rather than being inferred solely from their names.

Units and Quantities
Field	Description
codune	Main unit of measure code
qtepro	Quantity associated with the main unit
coduns	Secondary unit of measure code
qtspro	Quantity associated with the secondary unit
qtdemb	Quantity per package/embalagem
qtdvol	Number of volumes
qtdbar	Quantity associated with a barcode
seqbar	Barcode sequence
codbar	Barcode identifier

These fields are relevant when displaying product quantities, packaging, stock/order units, and operational information.

Packaging and Physical Characteristics
Field	Description
cbaemb	Packaging barcode/reference
cbaem2	Additional packaging barcode/reference
cbaem3	Additional packaging barcode/reference
liqemb	Liquid/empty packaging-related quantity or weight; exact meaning should be confirmed
brtemb	Gross packaging-related quantity/weight; exact meaning should be confirmed
cubpro	Product cubic volume
cxapro	Product/package box dimension or volume-related measure
pesliq	Net weight
pesbrt	Gross weight
pescub	Cubic/packaging weight or related measure; exact meaning should be confirmed
altpro	Product height
compro	Product length
larpro	Product width
garpro	Product depth/length-related integer measure; exact meaning should be confirmed

The dimensional fields are potentially useful for logistics-oriented dashboard features, such as volume, weight, packaging, and transportation analysis.

Location and Commercial Information
Field	Description
locpro	Product location
webpro	Web-facing product description/information
obspro	General product observations
simpro	Product similarity/reference information
codcom	Commercial classification/code
impprd	Product import/impurity-related code; exact business meaning should be confirmed

locpro may be useful for operational dashboards if the product location represents warehouse/storage information.

Pricing / Cost / Margin-Related Fields
Field	Description
isspro	ISS-related value/rate associated with the product
saiicm	ICMS outgoing configuration/value
enticm	ICMS incoming configuration/value
saiipi	IPI outgoing configuration/value
entipi	IPI incoming configuration/value
entimp	Import-related value/rate
valimp	Import-related value
dscimp	Import-related description
dsrimp	Import-related short description

These fields should not automatically be interpreted as current selling price or product cost. Product pricing/cost in the order context is primarily represented by fields in PEDRE2.

Tax and Fiscal Configuration

ESTPRO contains a substantial amount of fiscal configuration used when products participate in sales and purchases.

ICMS
Field	Description
icmsai	ICMS outgoing fiscal classification
icmtsd	ICMS outgoing tax/type information
icment	ICMS incoming fiscal classification
icmten	ICMS incoming tax/type information
id_esticm_saida	Reference to outgoing ICMS rule/configuration
id_esticm_entrada	Reference to incoming ICMS rule/configuration
IPI
Field	Description
ipisai	IPI outgoing fiscal classification
ipitsd	IPI outgoing tax/type information
ipient	IPI incoming fiscal classification
ipiten	IPI incoming tax/type information

These fields are directly relevant to the relationships:

ESTPRO.(ipient, ipiten) -> ESTIPI.(codipi, tipipi)
ESTPRO.(ipisai, ipitsd) -> ESTIPI.(codipi, tipipi)
PIS / COFINS
Field	Description
id_regra_pis	PIS rule for the product
id_regra_cofins	COFINS rule for the product
id_regra_pis_entrada	PIS incoming rule
id_regra_cofins_entrada	COFINS incoming rule
id_regra_fcp	FCP rule
id_fcp_entrada	FCP incoming configuration
id_fcp_saida	FCP outgoing configuration
cod_fcp_entrada	FCP incoming code
cod_fcp_saida	FCP outgoing code
Fiscal / Regulatory Classification
Field	Description
codncm	NCM code
codanp	ANP code
descanp	ANP description
codif	Fiscal/regulatory identifier
cest	CEST code
fci	FCI information
clfent	Fiscal classification for incoming operations
clfsai	Fiscal classification for outgoing operations
desimp	Fiscal/import-related description
desim2	Additional fiscal/import-related description
IBS / CBS Fiscal Fields

The schema contains fields related to newer IBS/CBS taxation:

Field	Description
ibscbs_cst	IBS/CBS CST classification
ibscbs_c_class_trib	IBS/CBS tax classification

These fields may become relevant to future dashboard or fiscal-analysis functionality.

Images and Presentation
Field	Description
imgpro	Product image/content
imgpaf	Product image/reference
webpro	Web-facing product information
dscpro	Main display description
dsrpro	Short display description

imgpro is a text field and may contain product image/content information. The exact storage format should be confirmed before exposing it directly through the API.

Product Relationships

The main known relationship relevant to the dashboard is:

PEDRE2.(codclp, codgru, codsub, codpro)
    ->
ESTPRO.(codclp, codgru, codsub, codpro)

This is the primary mechanism for enriching an order item with product information.

The broader relationship model also contains:

ESTPRO.(codcat)
    ->
ESTCAT.(codcat)


ESTPRO.(codclp)
    ->
ESTCLP.(codclp)


ESTPRO.(codmrc)
    ->
ESTMRC.(codmrc)


ESTPRO.(codst1)
    ->
ESTST1.(codst1)


ESTPRO.(codst2)
    ->
ESTST2.(codst2)


ESTPRO.(codtip)
    ->
ESTTIP.(codtip)


ESTPRO.(codune)
    ->
ESTUND.(codund)


ESTPRO.(coduns)
    ->
ESTUND.(codund)


ESTPRO.(codcom)
    ->
FINCOM.(codcom)


ESTPRO.(codgru, codsub)
    ->
ESTSUB.(codgru, codsub)


ESTPRO.(codste, tipste)
    ->
ESTSTR.(codstr, tipstr)


ESTPRO.(codsts, tipsts)
    ->
ESTSTR.(codstr, tipstr)


ESTPRO.(ipient, ipiten)
    ->
ESTIPI.(codipi, tipipi)


ESTPRO.(ipisai, ipitsd)
    ->
ESTIPI.(codipi, tipipi)

For the initial dashboard, these secondary relationships should be treated as enrichment relationships rather than primary dashboard entities.

Relationship With PEDRES / PEDRE2

The core dashboard data model can be understood as:

FINCLI
   |
   | codcli
   v
PEDRES
   |
   | (codemp, dteres, numres)
   v
PEDRE2
   |
   | (codclp, codgru, codsub, codpro)
   v
ESTPRO

Conceptually:

Customer
   |
   └── Orders
          |
          └── Order Items
                  |
                  └── Products

This is one of the most important structures for the dashboard.

It allows the application to answer questions such as:

Which customers are buying?
What orders did a customer make?
Which products are present in orders?
Which products generate the most sales?
Which customers buy specific products?
What is the sales volume by product?
What is the sales volume by product group/category?
Which products have the highest quantities sold?
Which products generate the highest revenue?
What is the relationship between product cost and selling value?
Which products have the highest margins?
How do product sales evolve over time?
Dashboard-Relevant Product Fields

For the first dashboard version, the most important ESTPRO fields are likely:

Product identity
codclp
codgru
codsub
codpro
dscpro
dsrpro
refpro
Classification
codcat
codmrc
codtip
codst1
codst2
Units / quantities
codune
qtepro
coduns
qtspro
qtdemb
Physical characteristics
pesliq
pesbrt
cubpro
altpro
compro
larpro
Fiscal/product classification
codncm
cest
codanp
Presentation
imgpro
webpro
obspro

The exact subset exposed by the API should be determined by the actual dashboard requirements rather than exposing the entire legacy table.

Important Interpretation Rule

ESTPRO is a product master, while PEDRE2 is the historical transactional representation of a product within an order.

Therefore:

ESTPRO describes what the product currently is.
PEDRE2 describes how the product was used in a particular order.
Quantities and monetary values associated with a specific sale should generally come from PEDRE2.
Product descriptions, classification, brand, NCM, dimensions, units, and other master attributes can be obtained from ESTPRO.

This distinction is important when designing historical dashboards.

For example, if a product's description or classification changes in ESTPRO, joining historical PEDRE2 records to the current ESTPRO record may display the current product metadata rather than necessarily representing the metadata that existed when the order was created.

Data Quality / Unknown Fields

The following fields have names whose exact business semantics cannot be established reliably from the schema alone:

flbpro
flgtrg
flgpro
flgkit
flglis
flgatu
simpro
nropro
liqemb
brtemb
cxapro
pescub
garpro
impprd

These should not be given definitive interpretations without additional evidence from:

Delphi source code
Delphi screens/reports
Existing SQL queries
Business rules
Sample data
Existing documentation

Copilot should not assume meanings for these fields solely from their names.

Source-System Context

ESTPRO is part of the legacy product master used by the Delphi application.

The database uses abbreviated Portuguese field names. The abbreviations should be preserved in the metadata because they correspond to the actual legacy schema, while descriptions should provide the semantic meaning in clear Portuguese/English as appropriate for the project documentation.

The new dashboard should not reproduce the legacy data model blindly. The purpose of this metadata is to allow the application/AI tooling to understand the legacy domain and determine which information is useful for a modern customer-facing dashboard.