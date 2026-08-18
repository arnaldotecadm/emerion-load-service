# PEDRES — Pedido / Order Header

## Business Meaning

`PEDRES` represents the main entity of a customer order (`pedido`).

Each record represents the **header/general information of one order**. It contains:

- order identification;
- customer information;
- salesperson and commercial information;
- general order dates and lifecycle information;
- payment/payment-condition information;
- order-level quantities;
- order-level commercial values;
- discounts and additions;
- taxation information;
- freight information;
- order processing/status information;
- references to users responsible for different stages of the order;
- references to related entities.

`PEDRES` should be considered the **authoritative source for order-level information and totals**.

The individual products/items belonging to an order are stored in `PEDRE2`.

### Main relationship

Conceptually:

```text
PEDRES (order header)
    |
    +-- PEDRE2 (order items)

PEDRES answers questions such as:

How many orders exist?
What is the total value of an order?
Which customer placed the order?
Which salesperson is responsible?
When was the order created?
What is the order status?
What are the order-level taxes?
What discounts/freight/additions were applied?
How much has been invoiced, returned, delivered, etc.?

PEDRE2 should be used when the dashboard needs product/item-level analysis.

Order Identity
Column	Meaning
codemp	Company identifier associated with the order.
dteres	Order date.
numres	Order number.
hreres	Order time.
seqres	Internal/alternate order sequence identifier.
id_pedres	Internal identifier for the PEDRES record.

The logical business identity of an order appears to be associated with:

codemp + dteres + numres

This should be preferred over assuming that id_pedres is the business-visible order number.

Customer

PEDRES contains both a reference to the customer entity and a snapshot of customer/address information associated with the order.

Customer reference
Column	Meaning
codcli	Customer identifier.
cgccli	Customer tax identification/document.
inscli	Customer state/company registration.
inecli	Customer additional registration identifier.
cgecli	Customer additional tax/registration identifier.

codcli relates the order to FINCLI.

PEDRES.codcli -> FINCLI.codcli
Customer address snapshot
Column	Meaning
cepcli	Customer postal code.
endcli	Customer address.
refcli	Customer address reference/complement.
numcli	Customer address number.
baicli	Customer neighborhood/district.
cidcli	Customer city.
ufecli	Customer state/UF.
uferes	State/UF associated with the order.
tencli	Customer address/type-related information; exact meaning should be confirmed.

The address fields in PEDRES should be treated as order-time/snapshot information, rather than automatically replacing the current customer information from FINCLI.

This distinction is important because the customer's current address may differ from the address stored when an historical order was created.

Sales and Commercial Information
Column	Meaning
codven	Salesperson/vendor identifier.
codpfa	Payment condition/form identifier.
tippfa	Payment condition/form type.
codtip	Order/type classification.
codatd	Related attendant/representative identifier.
codfil	Branch/filial identifier.
codtra	Carrier/transport-related identifier.
codtcl	Customer type/classification.
codgcl	Customer group classification.
codcom	Commercial/commission-related code.
tipcom	Commission/commercial type.
tipcpa	Purchase/payment type.
modpfa	Payment condition/form modality.
clitab	Customer/table-related commercial classification.

Known relationships include:

PEDRES.codcli  -> FINCLI.codcli
PEDRES.codven  -> FINVEN.codven
PEDRES.codtra  -> FINTRA.codtra
PEDRES.codtcl  -> FINTCL.codtcl
PEDRES.codgcl  -> FINGCL.codgcl
PEDRES.codcom  -> FINCOM.codcom
Order Quantities
Column	Meaning
qtires	Quantity of items associated with the order; exact distinction from other quantity fields should be confirmed.
seqite	Item sequence/reference.
qtpres	Order quantity/count; exact business meaning should be confirmed.
seqpar	Related sequence/parent sequence.
linres	Order line-related value.
qtlres	Quantity-related value.
qtfres	Quantity-related value.
qtdimp	Imported/printed/processed quantity; exact meaning should be confirmed.
qtire2	Number of PEDRE2 records/items.
qtire3	Number of related PEDRE3 records/items.
qtire4	Number of related PEDRE4 records/items.
qtilib	Quantity released/liberated; exact business meaning should be confirmed.

For dashboard item counts, qtire2 and/or the actual number of PEDRE2 rows should be validated against the legacy application before using either as the authoritative metric.

Financial and Commercial Values

These are order-level financial fields.

Main totals
Column	Meaning
totven	Total sales value.
totcst	Total cost value.
totres	Total order/reservation value.
totren	Total net/revenue-related value.
totger	General/overall total.
totliq	Net/liquid total.
totbrt	Gross total.
totcli	Total customer value.
totacp	Total related to accounts/payments; exact meaning should be confirmed.
totfrt	Total freight value.
Important

The exact semantic distinction between:

totres
totren
totger
totliq
totbrt
totven
totcli
totacp

should not be inferred purely from the column names.

For dashboard KPIs, the legacy Delphi application's calculation/display rules should be considered authoritative.

Discounts and Additions
Column	Meaning
dscreg	Regular/general discount.
dsccom	Commercial discount.
totdco	Total discount/commercial adjustment.
meddco	Discount-related average/metric.
medacr	Addition/accrual-related average/metric.
medcom	Commercial average/metric.
medprm	Promotional average/metric.
totdsc	Total discount.
totdsr	Total discount-related value.
totdsp	Total promotional/discount value.
totacr	Total additions/accruals.
dscres	Order-level discount.
pcores	Order-level percentage/value related to discount or commercial calculation.
pcoatd	Percentage associated with attendant/commercial operation.
dtedsc	Not present in current migrated schema.
renmin	Minimum revenue/value threshold.
limcli	Customer limit.
acrcnd	Condition-related addition.
dsccnd	Condition-related discount.

Some of these fields require confirmation from the Delphi business rules before being used for dashboard calculations.

Taxation

PEDRES contains order-level tax information.

IPI
Column	Meaning
codipi	IPI classification/code.
tipipi	IPI type.
trbipi	IPI taxation indicator/type.
redipi	IPI reduction.
bscipi	IPI calculation base.
basipi	IPI base.
totipi	Total IPI.
flgipi	IPI-related flag/status.
txfipi	IPI tax-rule/calculation information.
ICMS
Column	Meaning
codicm	ICMS classification/code.
tipicm	ICMS type.
trbicm	ICMS taxation indicator/type.
redicm	ICMS reduction.
bscicm	ICMS calculation base.
basicm	ICMS base.
toticm	Total ICMS.
txficm	ICMS tax-rule/calculation information.
Substitution tax
Column	Meaning
bassub	Substitution-tax calculation base.
totsub	Total substitution tax.
ISS
Column	Meaning
basiss	ISS calculation base.
totiss	Total ISS.
flgser	Service-related flag.
Other taxation
Column	Meaning
bascat	CAT-related calculation base.
totcat	CAT-related total.
medcat	CAT-related metric.
totipc	Total IPC-related value.

Tax-related fields should be interpreted together with the applicable tax classification and order/item rules.

Freight
Column	Meaning
frtres	Freight type/responsibility for the order.
totfrt	Total freight value.
Order Lifecycle

PEDRES contains timestamps, users and observations associated with several stages of the order lifecycle.

Commercial processing
Column	Meaning
flgcom	Commercial processing status/flag.
dtecom	Commercial processing date.
hrecom	Commercial processing time.
obscom	Commercial processing observation.
usucom	User responsible for commercial processing.
Financial processing
Column	Meaning
flgfin	Financial processing status/flag.
dtefin	Financial processing date.
hrefin	Financial processing time.
obsfin	Financial processing observation.
usufin	User responsible for financial processing.
Rejection
Column	Meaning
dterej	Rejection date.
hrerej	Rejection time.
obsrej	Rejection observation.
usurej	User responsible for rejection.
Delivery
Column	Meaning
dtedel	Delivery date.
hredel	Delivery time.
obsdel	Delivery observation.
usudel	User responsible for delivery processing.
Finalization
Column	Meaning
dtefpe	Finalization date.
hrefpe	Finalization time.
obsfpe	Finalization observation.
usufpe	User responsible for finalization.
Processing
Column	Meaning
flgpro	Processing flag.
pedpro	Processing status.
dtepro	Processing date.
hrepro	Processing time.
obspro	Processing observation.
usupro	User responsible for processing.
Confirmation
Column	Meaning
flgcon	Confirmation flag.
dtecon	Confirmation date.
hrecon	Confirmation time.
obscon	Confirmation observation.
usucon	User responsible for confirmation.

The exact allowed values for these flags/statuses have not yet been documented and should not be guessed.

Other Status and Flags

The table contains numerous legacy flags.

Known examples include:

flgpsq
flgctb
flgavi
flgoco
atuest
intfin
consum
flgcom
pedant
flgfin
flgsld
flgimp
flgger
libsld
flgreq
lanest
flgres
flgpro
flgcon
flgser
flgfec
flgdif
flgcmp
flgipi
libcli
flgope

These should be treated as business status indicators, but their exact values and semantics should not be inferred from the names alone.

For example, flgfin = 'S' should not be assumed to mean "finished" until confirmed from the legacy application's behavior/data.

A future version of this document should contain a status dictionary such as:

flgfin
  'S' = ...
  'N' = ...

when this information is recovered from the Delphi application/database.

Users and Responsibilities

Several users are associated with different order-processing stages.

Column	Responsibility
codusu	User associated with the order.
usucom	User responsible for commercial processing.
usufin	User responsible for financial processing.
usurej	User responsible for rejection.
usudel	User responsible for delivery.
usufpe	User responsible for finalization.
usupro	User responsible for processing.
usucon	User responsible for confirmation.
usucmp	User responsible for the CMP stage.

These users generally relate to GERUSU.codusu.

Related Entities

Known relationships relevant to the application:

PEDRES.codcli  -> FINCLI.codcli
PEDRES.codven  -> FINVEN.codven
PEDRES.codtra  -> FINTRA.codtra
PEDRES.codusu  -> GERUSU.codusu
PEDRES.usudel  -> GERUSU.codusu
PEDRES.usufin  -> GERUSU.codusu
PEDRES.usufpe  -> GERUSU.codusu
PEDRES.usurej  -> GERUSU.codusu
PEDRES. uferes -> GERUFE.sigufe
PEDRES.codtip  -> PEDTIP.codtip
PEDRES.codipi  -> ESTIPI.codipi / ESTIPI.tipipi
PEDRES.codpfa  -> ESTPFA.codpfa / ESTPFA.tippfa

The principal relationship for dashboard/customer analysis is:

FINCLI
   |
   | codcli
   v
PEDRES
   |
   | order
   v
PEDRE2
   |
   | product key
   v
ESTPRO

This represents the main business path:

Customer
   ↓
Order
   ↓
Order Items
   ↓
Product
Dashboard-Relevant Fields

For the dashboard, PEDRES is particularly important for order-level KPIs.

Potential dashboard dimensions include:

Order volume
COUNT(PEDRES)
COUNT(DISTINCT customer)
COUNT(DISTINCT salesperson)
Sales

Potential measures include:

totven
totres
totren
totger
totliq
totbrt
totcli

However, the exact authoritative sales KPI must be established from the Delphi application's business rules.

Discounts

Potential measures:

totdsc
totdsr
totdsp
totacr
dscres
dscreg
dsccom
Taxes

Potential measures:

totipi
toticm
totsub
totiss
totcat
Customer analysis

Use:

codcli

to join orders to FINCLI.

Example conceptual relationship:

FINCLI.codcli = PEDRES.codcli
Product analysis

Do not use PEDRES alone for product-level analysis.

For product-level analysis:

PEDRES
   ↓
PEDRE2
   ↓
ESTPRO

PEDRES provides the order context, while PEDRE2 provides the individual products/items.

Important Dashboard Modeling Rules
1. PEDRES is an order-level table

Do not treat each PEDRES row as a product.

One PEDRES row represents an order/header.

2. PEDRE2 is the item-level table

When calculating:

products sold;
quantities sold;
sales by product;
sales by product category;
product profitability;
product mix;

use PEDRE2.

3. Avoid multiplying order totals through joins

If an order has 10 PEDRE2 rows, joining PEDRES to PEDRE2 produces 10 rows for that order.

Therefore, fields such as:

PEDRES.totven
PEDRES.totres
PEDRES.totger
PEDRES.totliq
PEDRES.totbrt
PEDRES.totipi
PEDRES.toticm

must not simply be summed after joining to PEDRE2.

This would duplicate order-level totals.

For example, this pattern is potentially incorrect:

SELECT SUM(p.totven)
FROM pedres p
JOIN pedre2 i ON ...

unless the query first guarantees one row per order.

4. Use PEDRE2 for item-level aggregation

For example:

Customer
  → Orders from PEDRES
  → Items from PEDRE2
  → Products from ESTPRO

This is the preferred path for product-oriented dashboard analysis.

Known Data Modeling Pattern

The current core dashboard domain can be represented as:

                    FINCLI
                      |
                      | codcli
                      |
                    PEDRES
                 /     |      \
                /      |       \
               /       |        \
          PEDRE2     FINVEN    FINTRA
             |
             |
           ESTPRO

Where:

FINCLI = customer master;
PEDRES = order header;
PEDRE2 = order items;
ESTPRO = product master;
FINVEN = salesperson/vendor;
FINTRA = carrier/transport.
Fields Requiring Further Investigation

The following areas should not yet be treated as definitively understood:

exact meaning of all flg* values;
exact distinction between the multiple order total fields;
exact semantics of med* fields;
exact meaning of seq* fields;
exact meaning of qti* / qtp* / qtl* / qtf* fields;
exact lifecycle represented by each processing flag;
exact meaning of flgpro, flgres, lanest, atuest, etc.;
exact calculation rules for discounts;
exact calculation rules for the various tax fields;
whether id_pedres is used as a technical key by the legacy application or only by newer functionality.

These should be populated only when confirmed from the legacy database, Delphi source code, Delphi screens/reports, or representative data.

Complete Schema Reference

The migrated PostgreSQL table currently contains 200 columns.

The complete column list should be maintained as a technical reference, but the business documentation above should be considered more important for AI-assisted development than a simple alphabetical/ordinal column dump.

When creating dashboard functionality, Copilot should prefer the documented business semantics and relationship rules over assumptions based solely on column names.