# FINCLI — Cliente

## Business meaning

`FINCLI` represents the **main customer entity**.

Each record represents a customer and contains the information necessary to identify the customer, classify the customer, locate the customer geographically, associate the customer with commercial representatives and pricing rules, and maintain credit and financial information.

For the dashboard, `FINCLI` should be treated as the **customer master/dimension**.

The main relationship with orders is:

```text
FINCLI
   |
   | codcli
   |
   +---- PEDRES
            |
            +---- PEDRE2

Therefore:

FINCLI = customer;
PEDRES = order;
PEDRE2 = order item;
ESTPRO = product.
Primary customer identifier

The primary customer identifier is:

codcli

codcli should be treated as the canonical customer ID throughout the application.

Relationships from other tables such as PEDRES.codcli and other financial/order tables normally point to this identifier.

Do not use the customer's name, tax ID, email, or address as the primary application identifier.

Relationship to PEDRES

The main customer/order relationship is:

FINCLI.codcli -> PEDRES.codcli

Conceptually:

FINCLI
  |
  +-- Customer A
       |
       +-- PEDRES Order 1
       +-- PEDRES Order 2
       +-- PEDRES Order 3

This relationship is fundamental for customer analytics.

For example:

Customer
    ↓
Orders
    ↓
Order Items
    ↓
Products

This allows dashboards to answer questions such as:

How many customers exist?
How many active customers exist?
Which customers generate the most revenue?
Which customers buy the most products?
Which customers have the highest average order value?
Which customers have declining activity?
Which customers have the highest discounts?
Which customers generate the highest margin?
Customer identification

Important identity fields:

Field	Meaning
codcli	Customer identifier
nomcli	Customer name
apecli	Customer short name / nickname
inscli	Customer state registration / tax registration
cgccli	Customer CNPJ/CPF or tax identification
tipo_pessoa	Type of person/entity
indic_estrangeiro	Foreign customer indicator
indic_ie	State-registration indicator
insc_municipal	Municipal registration
cnae	Economic activity classification
cest	CEST classification

nomcli is the main human-readable customer name.

codcli is the identifier that should be used internally.

Customer type
tipo_pessoa

identifies the type of customer/person.

The actual code values should be obtained from the legacy application's business rules before presenting them as user-facing labels.

Do not assume the values without validating them against the legacy system.

Customer addresses

FINCLI contains several sets of address information.

Fiscal/main address
cefcli
tefcli
enfcli
rffcli
nrfcli
bafcli
cifcli
uffcli

These represent:

postal code;
street/type;
address;
reference;
number;
neighborhood;
city;
state.
Additional address
ceacli
teacli
enacli
rfacli
nracli
baacli
ciacli
ufacli
Delivery/other address
ceecli
teecli
enecli
rfecli
nrecli
baecli
ciecli
ufecli
Commercial address
ceccli
teccli
enccli
rfccli
nrccli
baccli
ciccli
ufccli

The exact business purpose of each address group should be preserved from the legacy application's terminology rather than inferred solely from the field names.

For dashboards, state/region information such as:

uffcli
ufacli
ufecli
ufccli

can be useful for geographic analysis.

Geographic analysis

Customer geographic information can support dashboard views such as:

customers by state;
revenue by state;
orders by state;
customer concentration by region;
regional growth;
regional product mix.

The relevant state fields include:

uffcli
ufacli
ufecli
ufccli

Do not assume that all four fields represent the same type of address.

The application should determine which address represents the desired business definition of "customer location".

Contact information

Important contact fields include:

tefcli
teacli
teecli
teccli


em1cli
em2cli
em3cli


webcli

These represent telephone/contact and email/web information.

em1cli, em2cli, and em3cli are customer email addresses.

webcli contains the customer's website/web information.

These fields are primarily useful for customer detail views rather than aggregate KPIs.

Customer notes
obscli
obsfin

contain free-form customer/financial observations.

They may contain useful business information, but should not normally be used as structured analytical fields.

Commercial relationships

Important commercial association fields include:

Field	Meaning
codven	Sales representative
codatd	Attendant/service representative
codgcl	Customer group
codtcl	Customer type/classification
codcom	Commercial classification/code
codmcr	Market classification
codmrg	Margin classification
codset	Sector/segment classification
codmst	Market/status classification
codrep	Representative
codpal	Related classification/relationship
codpfa	Payment/pricing condition
tippfa	Payment/pricing condition type

These fields can support customer segmentation and sales analysis.

For example:

Customer
   |
   +-- Sales representative
   +-- Customer group
   +-- Customer type
   +-- Market segment
   +-- Pricing/payment condition

The exact descriptions of these codes should be obtained from their related master tables.

Important customer relationships

Known relationships include:

FINCLI.codven -> FINVEN.codven


FINCLI.codatd -> FINVEN.codven


FINCLI.codgcl -> FINGCL.codgcl


FINCLI.codban -> FINBAN.codban


FINCLI.codccl -> FINCCL.codccl


FINCLI.codcom -> FINCOM.codcom


FINCLI.codtcl -> FINTCL.codtcl


FINCLI.codtra -> FINTRA.codtra


FINCLI.codusu -> GERUSU.codusu


FINCLI.usuatu -> GERUSU.codusu


FINCLI.codpfa + FINCLI.tippfa
    -> ESTPFA.codpfa + ESTPFA.tippfa


FINCLI.codmcr + FINCLI.codmrg + FINCLI.codset + FINCLI.codmst
    -> FINMST.codmcr + FINMST.codmrg + FINMST.codset + FINMST.codmst

These relationships should be used when the dashboard needs descriptive information for the corresponding codes.

Credit information

Important credit-related fields include:

Field	Meaning
limcli	Customer credit limit
dtlcre	Credit-related date
incpag	Payment-related indicator
clidev	Customer debt/debit-related value
dcalim	Credit-limit-related date
datlim	Credit-limit-related date
hrelim	Credit-limit-related time
usulim	User responsible for limit operation
qtdicl	Quantity/count related to credit/customer information

The exact semantics of some date fields should be confirmed against the Delphi application.

limcli is particularly important for a potential credit utilization dashboard.

A potential analytical concept is:

credit utilization =
customer outstanding exposure / credit limit

However, the outstanding exposure should not be inferred from FINCLI.clidev without confirming its exact meaning.

Customer financial information

Relevant fields include:

clidev
totacm
incpag
dtlcre
cre_dev_usu
cre_dev_obs
cre_dev_dte

These fields relate to credit/debit, financial status, and credit-related operations.

The fields:

cre_dev_usu
cre_dev_obs
cre_dev_dte

appear to represent information about a credit/debit operation or review.

Their exact business semantics should be confirmed from the legacy application before exposing them as dashboard KPIs.

Customer lifecycle / maintenance

Important lifecycle fields include:

dtncli
dcacli
dteacm
dteatu
hreatu
usuatu
dtvsuf

These represent dates associated with customer creation, activity, updates, or related lifecycle events.

dteatu should be considered the primary candidate for last-update information, but the exact business meaning should be confirmed against the legacy application.

usuatu identifies the user associated with the update.

Customer status / flags

There are many legacy flags in FINCLI:

tipcli
flbcli
flgtrg
flgpsq
flgpro
flginf
flgca1
flgca2
flgca3
flgint
flgpal
incpal

and other related indicators.

These should not be interpreted automatically from their names.

The legacy Delphi application's usage should be consulted before turning these into dashboard statuses.

For example, a field beginning with flg should not automatically be presented as a boolean value without understanding its possible values.

Customer pricing/payment configuration

Important fields include:

codpfa
tippfa
codmcr
codmrg
codset
codmst

These can affect how the customer is commercially treated.

They may be useful for segmentation such as:

customer pricing profile;
market segment;
margin group;
payment condition;
customer commercial classification.
Customer-specific payment/contact fields

The table contains several groups of fields such as:

pt1cli / fo1cli
pt2cli / fo2cli
pt3cli / fo3cli
pt4cli / fo4cli


pf1cli / fa1cli
pf2cli / fa2cli
pf3cli / fa3cli
pf4cli / fa4cli


pc1cli / fc1cli
pc2cli / fc2cli
pc3cli / fc3cli
pc4cli / fc4cli

These appear to represent repeated contact/payment/reference structures.

Their exact business semantics should not be inferred solely from the abbreviated field names.

They are not currently considered primary dashboard fields.

Customer financial/operational messages
menbl1
menbl2
menbl3
menbl4
menbl5


mencr1
mencr2
mencr3
mencr4
mencr5

These are message/text fields associated with customer financial or operational processing.

They are not primary analytical fields.

Internal/system identifiers

The following fields identify related records in other financial structures:

id_finhol
id_finuff
id_fincif
id_finufa
id_fincia
id_finufe
id_fincie
id_finufc
id_fincic
id_finpai
id_finctb
id_finct2
id_finct3
id_finct4
id_finct5
id_finct6

These should generally be treated as internal relationship identifiers.

They should not be exposed as primary customer identifiers.

Dashboard relevance

For the initial dashboard, the most important FINCLI fields are likely:

Customer identity
codcli
nomcli
apecli
cgccli
inscli
tipo_pessoa
Geography
cifcli
uffcli
ciacli
ufacli
ciecli
ufecli
ciccli
ufccli
Commercial segmentation
codven
codgcl
codtcl
codcom
codmcr
codmrg
codset
codmst
codrep
codpfa
tippfa
Credit
limcli
clidev
dtlcre
dcalim
datlim
Lifecycle
dtncli
dcacli
dteacm
dteatu

These fields can support dashboard features such as:

total customers;
new customers over time;
customers by state;
customers by segment;
customers by sales representative;
top customers by revenue;
top customers by order count;
average revenue per customer;
average order value per customer;
customer growth;
customer activity;
credit limit;
credit utilization;
customer segmentation.
Customer analytics

When calculating customer-level metrics, FINCLI should normally be joined to order data through:

FINCLI.codcli = PEDRES.codcli

A typical analytical path is:

FINCLI
    |
    | codcli
    v
PEDRES
    |
    | codemp + dteres + numres
    v
PEDRE2
    |
    | codclp + codgru + codsub + codpro
    v
ESTPRO

This allows the application to answer questions such as:

Customer
   ↓
Orders
   ↓
Items
   ↓
Products

and calculate:

revenue by customer;
quantity purchased by customer;
orders per customer;
average order value;
products purchased;
categories purchased;
brands purchased;
margin by customer;
discount by customer.
Important aggregation rule

FINCLI is a customer master table.

It should not itself be used as the source for sales totals.

For sales metrics, join:

FINCLI -> PEDRES

or:

FINCLI -> PEDRES -> PEDRE2

depending on whether the metric is order-level or item-level.

For example:

Order-level revenue

Use order-level totals from PEDRES.

Product-level revenue

Use item-level totals from PEDRE2.

Customer revenue

Aggregate the appropriate order-level or item-level measure by:

FINCLI.codcli

Do not mix order-level and item-level totals in the same aggregation without explicitly controlling the grain.

Recommended customer dimension model

For analytical purposes:

                 FINCLI
               CUSTOMER
                   |
                   | codcli
                   |
                 PEDRES
              ORDER HEADER
                   |
                   | order key
                   |
                 PEDRE2
              ORDER ITEM
                   |
                   | product key
                   |
                 ESTPRO
                PRODUCT

This should be considered the core business model for the initial dashboard.

Notes for Copilot

When implementing customer-related dashboard functionality:

Treat FINCLI as the authoritative customer master.
Use codcli as the customer identifier.
Do not identify customers by name or tax ID.
Join orders to customers using PEDRES.codcli = FINCLI.codcli.
Use PEDRES for order-level metrics.
Use PEDRE2 for item/product-level metrics.
Use ESTPRO for current product master information.
Use the related master tables to translate customer classification codes into meaningful labels.
Do not assume the meaning of legacy flg* fields without verifying their values and usage.
Do not assume that similarly abbreviated fields have conventional meanings.
Preserve the legacy application's business rules when calculating financial or credit metrics.
Avoid exposing internal id_fin* fields as business-facing customer identifiers.
Geographic analysis should explicitly choose which customer address represents the desired business definition of customer location.
Customer-level KPIs should be aggregated at FINCLI.codcli.
When calculating revenue, orders, products, or margin, explicitly define the grain of the underlying data before aggregating.
Core business model

The four primary dashboard entities currently identified are:

FINCLI
Customer


PEDRES
Order


PEDRE2
Order Item


ESTPRO
Product

Their conceptual relationship is:

FINCLI
   |
   | Customer
   v
PEDRES
   |
   | Order
   v
PEDRE2
   |
   | Product
   v
ESTPRO

This model should be considered the primary business context when Copilot proposes new dashboard components, APIs, queries, aggregations, or visualizations.