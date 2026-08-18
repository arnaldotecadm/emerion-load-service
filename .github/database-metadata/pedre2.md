# PEDRE2 — Pedido / Item do Pedido

## Business meaning

`PEDRE2` represents the **individual items of a customer order**.

Each record represents one product/item associated with a `PEDRES` order. It contains the product identification, quantities, prices, costs, discounts, taxes, profitability information, stock-related quantities, and other item-level processing information.

The relationship between `PEDRES` and `PEDRE2` is:

```text
PEDRES (order header)
    |
    | codemp + dteres + numres
    |
    +---- PEDRE2 (order item)

The relationship between PEDRE2 and ESTPRO identifies the product:

PEDRE2
    |
    | codclp + codgru + codsub + codpro
    |
    +---- ESTPRO

Therefore, PEDRE2 should generally be treated as the fact/detail table for order items, while:

PEDRES represents the order/header;
FINCLI represents the customer;
ESTPRO represents the product.

For dashboard purposes, PEDRE2 is one of the most important tables because it provides the item-level facts needed for product, sales, quantity, discount, cost, revenue, and profitability analysis.

Primary / identifying fields

The main fields identifying an order item are:

Field	Meaning
codemp	Company identifier
dteres	Order date
numres	Order number
seqre2	Item sequence within the order

The combination:

codemp + dteres + numres

identifies the parent PEDRES order.

seqre2 identifies the individual item within that order.

id_pedre2 is an additional internal identifier for the PEDRE2 record.

id_pedres is an internal identifier associated with the parent order.

Relationship to PEDRES

PEDRE2 belongs to PEDRES through:

PEDRE2.codemp  -> PEDRES.codemp
PEDRE2.dteres  -> PEDRES.dteres
PEDRE2.numres  -> PEDRES.numres

The relationship should be interpreted as:

One PEDRES order
    |
    +-- PEDRE2 item 1
    +-- PEDRE2 item 2
    +-- PEDRE2 item 3
    +-- ...

When aggregating order-level information from PEDRE2, avoid counting each item as a separate order.

Use the order key:

codemp + dteres + numres

to identify distinct orders.

Relationship to ESTPRO

The product represented by an item is identified through:

PEDRE2.codclp -> ESTPRO.codclp
PEDRE2.codgru -> ESTPRO.codgru
PEDRE2.codsub -> ESTPRO.codsub
PEDRE2.codpro -> ESTPRO.codpro

Together these fields form the product relationship.

Conceptually:

PEDRE2
  codclp
  codgru
  codsub
  codpro
      |
      v
ESTPRO
  codclp
  codgru
  codsub
  codpro

Do not assume codpro alone uniquely identifies the product.

Product identification and description
Field	Meaning
codclp	Product classification/grouping code
codgru	Product group code
codsub	Product subgroup code
codpro	Product code
codtam	Product size/variation
codcor	Product color/variation
desre2	Description of the item
obsre2	Item observations
codund	Unit of measurement
refre2	Product/reference code

ESTPRO should normally be used as the authoritative product master when product information is required.

PEDRE2.desre2 may represent the description captured on the order item and may therefore differ from the current description in ESTPRO.

Quantities

Important quantity fields include:

Field	Meaning
qtpre2	Main quantity associated with the item
qtsre2	Quantity related to the item/order process
qtfre2	Quantity associated with a specific fulfillment/process state
qtdre2	Additional item quantity
qtdemb	Quantity per package/embalagem
ultqts	Previous/last quantity
sldre2	Remaining/balance quantity
qtdsep	Separated quantity
sldsep	Remaining quantity to separate
qtdfab	Manufactured quantity
sldfab	Remaining quantity related to manufacturing
qtire4	Quantity associated with another item/process stage

The exact operational meaning of some quantity fields depends on the legacy application's business process.

For dashboard development, do not automatically assume all quantity fields represent sales quantity.

qtpre2 should be investigated as the primary candidate for ordered item quantity, but existing application/business rules should be consulted before using it as a definitive sales KPI.

Pricing

Important pricing/value fields include:

Field	Meaning
vlure2	Unit value/price
vlqre2	Quantity/value-related price
vchre2	Value associated with item pricing
vrere2	Revenue/value component
vcrre2	Cost/value component
vcpre2	Price/cost component
vprre2	Price/value component
totven	Total sales value
totre2	Item total
totge2	Item gross total
totren	Net/revenue-related total
totliq	Net/liquid total
totbrt	Gross total

For dashboard calculations, prefer existing total/value fields from the legacy system when their semantics are confirmed rather than reconstructing business totals from individual components.

Cost and profitability

Important cost/profitability fields include:

Field	Meaning
cstre2	Item cost
totcst	Total cost
cstcst	Cost component
vchcst	Cost-related value
vrecst	Cost-related value
vcrcst	Cost-related value
vcpcst	Cost-related value
vprcst	Cost-related value
vcsre2	Cost-related item value
vmere2	Margin/value component
vpfre2	Profit/value component
vmecst	Cost-related margin
vpfcst	Cost-related profit
marpre	Margin based on price
marped	Margin associated with the order
lucrol	Profit amount/value
lucrop	Profit percentage/value
totper	Percentage-related total
pcoatd	Percentage/value related to attendance/operation

These fields are potentially very valuable for dashboards involving:

gross margin;
profitability by product;
profitability by customer;
profitability by order;
profitability by product group;
revenue vs cost;
low-margin products.

However, the exact formulas used by the legacy Delphi application should be preserved when possible instead of assuming that similarly named fields have conventional accounting meanings.

Discounts

Important discount fields include:

Field	Meaning
dscre2	Item discount percentage
vdsre2	Item discount value
dspre2	Price discount
vdpre2	Price discount value
dsrre2	Related discount percentage
vdrre2	Related discount value
dsccom	Commercial discount
vdscom	Commercial discount value
totdsc	Total discount
totdsr	Related discount total
totdsp	Price discount total
totdco	Commercial discount total
dscper	Discount percentage
difdsc	Difference related to discount
tipdsc	Discount type

For dashboards, distinguish between:

discount percentage;
discount amount;
commercial discount;
promotional/other discounts.

Do not add different discount value columns together unless the legacy business rules confirm that they are independent components.

Additional charges / increases

Important fields include:

Field	Meaning
pacre2	Increase/addition percentage
vacre2	Increase/addition value
totacr	Total additions
tipacr	Addition type
totacp	Additional total
totcli	Customer-related total

These may represent increases, additional charges, or commercial adjustments.

Taxation

PEDRE2 contains extensive item-level tax information.

IPI
Field	Meaning
regipi	IPI tax regime
tipipi	IPI tax type
trbipi	IPI taxation indicator
redipi	IPI reduction
bscipi	IPI tax base
basipi	IPI base
ipire2	IPI rate/value
totipi	IPI total
cstipi	IPI CST
clsipi	IPI classification
ICMS
Field	Meaning
regicm	ICMS tax regime
tipicm	ICMS tax type
trbicm	ICMS taxation indicator
redicm	ICMS reduction
bscicm	ICMS tax base
basicm	ICMS base
icmre2	ICMS rate/value
toticm	ICMS total
Substitution tax
Field	Meaning
regsub	Substitution-tax regime
trbsub	Substitution-tax indicator
basesb	Substitution-tax base
bassub	Substitution-tax base
icmsub	Substitution-tax ICMS value
totsub	Substitution-tax total
mrgsub	Substitution-tax margin
redsub	Substitution-tax reduction
ISS
Field	Meaning
basiss	ISS base
totiss	ISS total
issre2	ISS-related value
PIS / COFINS
Field	Meaning
baspis	PIS base
aliqpis	PIS rate
cstpis	PIS CST
totpis	PIS total
bascof	COFINS base
aliqcof	COFINS rate
cstcof	COFINS CST
totcof	COFINS total
totpiszf	PIS total for ZF
totcofzf	COFINS total for ZF
Other tax fields

Additional taxation-related fields include:

codstr
tipstr
codtxf
codcfo
codtxf2
cstlan
cstlan
totipc
totcat
medcat
bascat
aliqpis_zf
aliqcof_zf
flg_desc_zf_pis
flg_desc_zf_cof
frticm
segicm
desicm
frtipi
segipi
desipi
totitetrb

Tax fields should generally be used for tax-specific reporting rather than general sales KPIs.

Product classification captured on the order item

The item also stores classification information:

Field	Meaning
codtip	Product type
codcat	Product category
codmrc	Product brand
codst1	Product status/classification
codst2	Product secondary status
codstr	Structure/service classification
tipstr	Structure/service type

These fields can be useful for dashboard aggregation, but ESTPRO is the primary product master and should normally be preferred when current product classification is required.

Operational / workflow fields

Important workflow/status fields include:

flaseq
flgres
flgreq
flgdup
flgval
flglib
flgpac
flgfec
flgmar
flgren
flgatu
flgfab
lancst

These indicate various states in the legacy order-processing workflow.

Their exact meanings should not be inferred solely from the field names.

Order / production references

Fields connecting the item to other operational processes include:

Field	Meaning
empger	Related company
dteger	Related date
numger	Related number
seqgr2	Related sequence
seqgr3	Related sequence
empent	Entry company
dteent	Entry date
nument	Entry number
seqen2	Entry sequence
empped	Related order company
dteped	Related order date
numped	Related order number
seqpe2	Related order item sequence
pedcli	Customer/order reference
nrore2	Item/reference number
nroite	Item number

These fields support operational integration with other legacy processes.

Purchase / external reference
numpedcompra
numitemcompra

represent an external purchase-order number and item number.

These can be useful when the dashboard needs to trace a sales item back to a customer's purchase order.

Logistics / stock fields

Relevant fields include:

qtdsep
sldsep
qtdfab
sldfab
qtdemb
totvol
volre2
totfrt
totseg
totoutdesp
totdescinc

These may support dashboards related to:

fulfillment;
separation;
manufacturing;
shipping;
freight;
additional expenses;
volumes.
Dashboard relevance

For the initial dashboard, the most important PEDRE2 fields are likely:

Order identification
codemp
dteres
numres
seqre2
Product identification
codclp
codgru
codsub
codpro
codtam
codcor
Quantity
qtpre2
qtsre2
qtdre2
sldre2
Pricing / revenue
vlure2
totven
totre2
totren
totliq
totbrt
Cost
cstre2
totcst
Discounts
dscre2
vdsre2
totdsc
Profitability
marpre
marped
lucrol
lucrop
Product classification
codcat
codmrc
codtip
codclp
codgru
codsub
codpro

These are particularly useful for dashboards such as:

Top products by revenue;
Top products by quantity;
Revenue by category;
Revenue by brand;
Revenue by product group;
Gross margin by product;
Gross margin by category;
Discount analysis;
Product profitability;
Product mix;
Customer/product analysis;
Order-item analysis.
Important dashboard modeling rule

Do not join PEDRES, PEDRE2, FINCLI, and ESTPRO and then blindly aggregate every numeric column.

PEDRES contains order-level totals while PEDRE2 contains item-level totals.

For example:

PEDRES.totres

is an order-level value.

Whereas:

PEDRE2.totre2
PEDRE2.totven
PEDRE2.totcst

are item-level values.

If an order has five PEDRE2 records, joining PEDRES to those five records repeats the PEDRES values five times.

Therefore:

use PEDRES for order-level KPIs;
use PEDRE2 for item/product-level KPIs;
aggregate PEDRE2 before joining it back to order-level data when necessary.
Recommended dashboard fact model

For analytical purposes, the data can conceptually be viewed as:

                    FINCLI
                      |
                      | customer
                      v
PEDRES ----------> PEDRE2 <---------- ESTPRO
 order              item              product
 header             facts             master

Where:

PEDRES
  = order-level dimension/fact


PEDRE2
  = order-item fact


FINCLI
  = customer dimension


ESTPRO
  = product dimension

This model is likely to be the foundation for the main dashboard analytics.

Current known relationships
PEDRE2.(codemp, dteres, numres) -> PEDRES.(codemp, dteres, numres)


PEDRE2.(codclp, codgru, codsub, codpro) -> ESTPRO.(codclp, codgru, codsub, codpro)


PEDRE2.codtip -> PEDTIP.codtip


PEDRE2.codipi + PEDRE2.tipipi -> ESTIPI.codipi + ESTIPI.tipipi


PEDRE2.codpfa + PEDRE2.tippfa -> ESTPFA.codpfa + ESTPFA.tippfa


PEDRE2.codtra -> FINTRA.codtra


PEDRE2.codven -> FINVEN.codven


PEDRE2.codusu -> GERUSU.codusu


PEDRE2.codstr + PEDRE2.tipstr -> ESTSTR.codstr + ESTSTR.tipstr

The relationship information is based on the migrated schema's detected relationships and should be treated as the current database relationship model.

Notes for Copilot

When implementing dashboard functionality involving orders and products:

Treat PEDRES as the order header.
Treat PEDRE2 as the order-item/detail table.
Treat FINCLI as the customer master.
Treat ESTPRO as the product master.
Do not count PEDRE2 rows as orders.
Count distinct (codemp, dteres, numres) when calculating order counts from PEDRE2.
Use (codemp, dteres, numres, seqre2) when an individual order item needs to be uniquely identified.
Use (codclp, codgru, codsub, codpro) to identify the product relationship.
Do not assume codpro alone uniquely identifies a product.
Avoid summing PEDRES totals after joining to PEDRE2 without first aggregating the item data.
Prefer item-level fields from PEDRE2 for product analytics.
Prefer order-level fields from PEDRES for order-level KPIs.
Prefer ESTPRO for current product master information.
Prefer FINCLI for current customer information.
Preserve the existing legacy business calculations when the meaning of a financial field is uncertain.