# PEDRES / PEDRE2 — Order Header & Order Items Reference

Field mappings derived from `ManPed.dfm` (the main order management form in the legacy Delphi pedido application).

> **DataSources**:
> - `DsRes` → `PEDRES` (order header — one row per order)
> - `DsRe2` → `PEDRE2` (order items — one row per item line; FK: `NUMRES`)
>
> **Important**: `PEDRES.DTERES` is **DATE semantics** — read as `LocalDateTime` in JDBC projections, convert to `LocalDate` in mappers/DTOs.

---

## PEDRES — Order Header

### Identity & Key Fields

| Column      | Display Label        | Notes                                         |
|-------------|----------------------|-----------------------------------------------|
| `NUMRES`    | Nro. do Pedido       | Order PK → `externalId` in ingestion DTOs     |
| `DTERES`    | Emissão              | Order date — DATE semantics (see note above)  |
| `DTFRES`    | Entregar Em          | Requested delivery date                       |
| `ANORES`    | —                    | Year (extracted from `DTERES`)                |
| `MESRES`    | —                    | Month (extracted from `DTERES`)               |
| `CODEMP`    | Empresa              | Company FK                                    |
| `CODFIL`    | —                    | Branch FK                                     |
| `LINRES`    | —                    | Order line / series                           |
| `SEQRES`    | —                    | Sequence within line                          |
| `SITRES`    | —                    | Order status code                             |
| `PEDANT`    | Pedido Anterior      | Previous/linked order reference               |
| `PRFRES`    | —                    | Prefix/series                                 |

### Customer on the Order

| Column      | Display Label  | Notes                                    |
|-------------|----------------|------------------------------------------|
| `CODCLI`    | Cliente        | Customer FK → `fincli.CODCLI`            |
| `CGCCLI`    | CPF/CNPJ       | Denormalized customer tax doc            |
| `INSCLI`    | IE             | Denormalized customer state registration |
| `UFERES`    | UF             | Delivery UF                              |
| `CEPCLI`    | —              | Denormalized customer CEP                |
| `ENDCLI`    | —              | Denormalized delivery address            |
| `NUMCLI`    | —              | Denormalized address number              |
| `BAICLI`    | —              | Denormalized neighbourhood               |
| `CIDCLI`    | —              | Denormalized city name                   |
| `UFECLI`    | —              | Denormalized delivery UF                 |
| `CGECLI`    | —              | CNPJ/CPF delivery address                |
| `INECLI`    | —              | IE delivery address                      |
| `TENCLI`    | —              | Customer phone                           |
| `COMCLI`    | —              | Customer buyer/contact                   |
| `REFCLI`    | —              | Customer's purchase order reference      |
| `NUMCLI`    | —              | Customer address number                  |
| `LIBCLI`    | —              | Customer credit release flag             |
| `LIMCLI`    | Limite         | Customer credit limit (denormalized)     |
| `CODTCL`    | Tipo Cliente   | Customer type FK                         |
| `CODGCL`    | —              | Customer group FK                        |
| `CODGUS`    | —              | Customer user group FK                   |
| `CLITAB`    | —              | Customer applied price table             |

### Sales & Commercial

| Column      | Display Label        | Notes                                   |
|-------------|----------------------|-----------------------------------------|
| `CODVEN`    | Vendedor             | Salesperson FK → `finven.CODVEN`        |
| `CODATD`    | Atendente            | Attendant FK                            |
| `PCOATD`    | —                    | Attendant % commission                  |
| `PCORES`    | —                    | Order % commission                      |
| `OBRGVE`    | —                    | Obligatory vendor flag                  |
| `CODPFA`    | Padrao Fat.          | Billing pattern FK                      |
| `TIPPFA`    | —                    | Billing pattern type                    |
| `MODPFA`    | —                    | Billing pattern mode                    |
| `CODTRA`    | —                    | Carrier FK                              |
| `TIPFRE`    | —                    | Freight type                            |
| `TIPFRT`    | —                    | Freight type (variation)                |
| `NOMFRT`    | —                    | Freight description                     |
| `ID_FRETE`  | Frete                | Freight FK / amount                     |
| `TABPRC`    | —                    | Price table used                        |
| `CODPRM`    | —                    | Promotion code FK                       |
| `CODTXF`    | —                    | Financial rate FK                       |
| `CODTXF2`   | —                    | Secondary financial rate FK             |
| `DSCCOM`    | (%) Desc. Comercial  | Commercial discount %                   |
| `DSCREG`    | (%) Desc. ICMS Reg.  | ICMS regional discount %                |
| `DIFDSC`    | —                    | Discount difference                     |
| `APLCFO`    | —                    | Apply CFO flag                          |
| `CODCFO`    | —                    | CFO code (Código Fiscal de Operação)    |
| `INDIC_CF`  | Indic Consumidor     | Consumer indicator (NF-e)               |
| `INDIC_PRESENCA` | —               | Buyer presence indicator (NF-e)         |
| `REGTRB`    | —                    | Customer tax regime on order            |
| `NUMREGTRIB`| —                    | Tax regime number                       |
| `NOMREGTRIB`| —                    | Tax regime description                  |
| `REGTRBEMP` | —                    | Company tax regime                      |
| `NROSUF`    | —                    | SUFRAMA number                          |
| `DTVSUF`    | —                    | SUFRAMA validity date                   |

### Observations (up to 8 lines each)

| Pattern        | Purpose                         |
|----------------|---------------------------------|
| `OB1RES`..`OB8RES` | Order observations 1–8    |
| `OB1FAT`..`OB8FAT` | Invoice observations 1–8  |
| `OB1CAN`..`OB5CAN` | Cancellation obs. 1–5     |
| `OBSRES`       | General order notes             |
| `OBSFIN`       | Financial notes                 |
| `OBSPRO`       | Production notes                |
| `OBSANT`       | Previous order notes            |
| `OBSCOM`       | Purchase notes                  |
| `OBSCMP`       | Print notes                     |
| `OBSCON`       | Conference notes                |
| `OBSDEL`       | Delivery notes                  |
| `OBSREJ`       | Rejection notes                 |
| `OBSFPE`       | FPE notes                       |

### Order Totals (`DsRes`)

| Column        | Display Label       | Notes                                  |
|---------------|---------------------|----------------------------------------|
| `TOTRES`      | Total do Pedido     | Order total (net, no taxes)            |
| `TOTGER`      | Total Geral         | Grand total (including all taxes)      |
| `TOTLIQ`      | —                   | Net total                              |
| `TOTBRT`      | —                   | Gross total                            |
| `TOTREN`      | —                   | Total with rebate/negotiation          |
| `TOTICM`      | Total ICMS          | ICMS total                             |
| `TOTIPI`      | IPI                 | IPI total                              |
| `TOTSUB`      | ICMS Subs.          | ICMS substitution total                |
| `TOTPIS`      | —                   | PIS total                              |
| `TOTCOF`      | —                   | COFINS total                           |
| `TOTFRT`      | Frete               | Freight total                          |
| `TOTSEG`      | Seguro              | Insurance total                        |
| `TOTOUTDESP`  | Outras Desp.        | Other expenses total                   |
| `TOTDESCINC`  | Desc.Inc.           | Included discount total                |
| `BASICM`      | Base ICMS           | ICMS calculation base (header)         |
| `BASSUB`      | Base St.            | ST calculation base (header)           |

### Status Timestamps (workflow trail)

Each workflow step has `DTE*` (date) + `HRE*` (time) + `USU*` (user):

| Step    | Date col  | Time col  | User col   | Meaning            |
|---------|-----------|-----------|------------|--------------------|
| Pedido  | `DTEPED`  | —         | `CODUSU`   | Order created      |
| Compra  | `DTECOM`  | `HRECOM`  | `USUCOM`   | Purchase confirmed |
| Impres. | `DTECMP`  | `HRECMP`  | `USUCMP`   | Printed            |
| Conf.   | `DTECON`  | `HRECON`  | `USUCON`   | Confirmed          |
| Entrega | `DTEDEL`  | `HREDEL`  | `USUDEL`   | Delivered          |
| Financ. | `DTEFIN`  | `HREFIN`  | `USUFIN`   | Financially closed |
| Progr.  | `DTEPRO`  | `HREPRO`  | `USUPRO`   | Programmed         |
| Rejeição| `DTEREJ`  | `HREREJ`  | `USUREJ`   | Rejected           |
| FPE     | `DTEFPE`  | `HREFPE`  | `USUFPE`   | FPE processed      |
| Contab. | `DTECTA`  | —         | —          | Accounted          |
| Gerenc. | `DTEGER`  | —         | —          | Managed            |

### Status Flags

| Column    | Meaning                              |
|-----------|--------------------------------------|
| `FLGRES`  | Order status (general)               |
| `FLGCOM`  | Commercial confirmed                 |
| `FLGCON`  | Conference done                      |
| `FLGFIN`  | Financially closed                   |
| `FLGGER`  | Managed/released                     |
| `FLGFEC`  | Closed/completed                     |
| `FLGIMP`  | Printed                              |
| `FLGPAC`  | Packed                               |
| `FLGFAB`  | In production                        |
| `FLGEST`  | Stock updated                        |
| `FLGLIB`  | Credit released                      |
| `FLGSLD`  | Balance/saldo applied                |
| `FLGVAL`  | Validated                            |
| `FLGREJ`  | Rejected                             |
| `FLGDUP`  | Duplicate                            |
| `FLGDIF`  | Differential applied                 |
| `FLGMAR`  | Markup applied                       |
| `FLGCTB`  | Accounted                            |
| `FLGSER`  | Service order                        |
| `FLGREQ`  | Required/mandatory                   |
| `FLGREN`  | Negotiated/renegotiated              |
| `FLGAVI`  | Aviso (notice sent)                  |
| `FLGOCO`  | Occurrence registered                |
| `FLGCLI`  | Customer flag (on order)             |
| `FLGPSQ`  | Allows financial query               |
| `FLGPRO`  | Promotional order                    |
| `FLGTAB`  | Uses price table                     |
| `FLGOPE`  | Operation flag                       |
| `FLGCMP`  | Purchase confirmed                   |
| `FLGFIL`  | Branch-specific                      |
| `FLGANT`  | Has previous order reference         |
| `FLGATU`  | Pending update                       |
| `FLGMOBILE`| Created on mobile                   |
| `FLG_SINEIF20` | SINEI F20 flag                  |
| `FLGNCALC_DIF_MVA` | No MVA diff calc flag         |
| `FLG_DESC_ZF_PIS` / `FLG_DESC_ZF_COF` | Free trade zone discounts |

### FK Internal IDs (surrogate keys)

| Column          | Purpose                           |
|-----------------|-----------------------------------|
| `ID_PEDRES`     | Surrogate PK of order             |
| `ID_PEDGER`     | Linked managed/released record    |
| `ID_PEDCTA`     | Linked accounting record          |
| `ID_PEDCND`     | Linked payment condition          |
| `ID_PEDCT2`     | Secondary payment condition       |
| `ID_PEDFE2`     | Related billing order             |
| `ID_SEPPED`     | Separation/picking record         |
| `ID_PEDICL`     | Customer item record              |
| `ID_PEDRES_ORIG`| Original order (for return/devol) |
| `ID_PEDGR3`     | Grouped order 3                   |
| `ID_FINCIE`     | City FK (NF-e)                    |
| `ID_FINUFE`     | UF FK (NF-e)                      |
| `ID_REGRA_FCP`  | FCP rule FK                       |

---

## PEDRE2 — Order Items

> PK: composite (`NUMRES` + `SEQRE2`).

### Item Identity

| Column      | Display Label     | Notes                                          |
|-------------|-------------------|------------------------------------------------|
| `SEQRE2`    | —                 | Item sequence within order (1, 2, 3…)          |
| `NRORE2`    | Itens             | Item number display                            |
| `NUMRES`    | Nro. do Pedido    | FK to `PEDRES.NUMRES`                          |
| `CODPRO`    | Item              | Product FK → `estpro.CODPRO`                   |
| `CODGRU`    | Grupo             | Product group FK (denormalized)                |
| `CODSUB`    | Sub-Grupo         | Product sub-group FK (denormalized)            |
| `CODCLP`    | Classificação     | Product classification FK                     |
| `CBARE2`    | Cód. Barras       | Barcode used on item line                      |
| `CODUND`    | Unidade           | Unit of measure FK                             |
| `CODCOR`    | Cor               | Colour FK                                      |
| `CODTAM`    | Tamanho           | Size FK                                        |
| `CODEAN`    | —                 | EAN code                                       |
| `LINRE2`    | —                 | Line number                                    |
| `CODEIT`    | —                 | Item code variant                              |
| `ID_PEDRE2` | —                 | Surrogate PK of item                           |
| `ID_LOJPE2` | —                 | Store FK on item                               |

### Descriptions

| Column    | Display Label          | Notes                                  |
|-----------|------------------------|----------------------------------------|
| `DSCRE2`  | Descrição              | Item description on order              |
| `DSRRE2`  | —                      | Reduced description                    |
| `DSPRE2`  | —                      | Description for invoice (NF-e)         |
| `REFRE2`  | Referência             | Product internal reference             |
| `OBSRE2`  | Observação             | Item-level notes                       |

### Quantities

| Column    | Display Label         | Notes                                  |
|-----------|-----------------------|----------------------------------------|
| `QTPRE2`  | Qtd.                  | Ordered quantity                       |
| `QTDRE2`  | —                     | Quantity variant                       |
| `QTFRE2`  | —                     | Invoiced quantity                      |
| `QTSRE2`  | —                     | Separated/picked quantity              |
| `QTIRE2`  | —                     | Printed quantity                       |
| `QTIRE3`  | —                     | Printed qty 3 (copy)                   |
| `QTIRE4`  | —                     | Printed qty 4 (copy)                   |
| `QTPRES`  | —                     | Ordered qty (on header aggregate)      |
| `QTFRES`  | —                     | Invoiced qty (header aggregate)        |
| `QTSRE2`  | —                     | Separated qty                          |
| `SLDRE2`  | —                     | Balance qty on item                    |
| `SLDRES`  | —                     | Balance qty on order                   |
| `QTDEMB`  | Embalagem             | Packaging quantity                     |

### Prices & Values

| Column    | Display Label    | Notes                                       |
|-----------|------------------|---------------------------------------------|
| `VLURE2`  | Valor Unitário   | Unit price (applied)                        |
| `PCORE2`  | —                | Original price (before discount)            |
| `VPRRE2`  | —                | Reference price                             |
| `VRERE2`  | —                | Replacement price                           |
| `VPFRE2`  | —                | Final price                                 |
| `VCHRE2`  | —                | Historical cost                             |
| `VCPRE2`  | —                | Weighted average cost                       |
| `VCRRE2`  | —                | Replacement cost                            |
| `VCSRE2`  | —                | Cost of sale                                |
| `VMERE2`  | —                | Average price                               |
| `VACRE2`  | —                | Surcharge value                             |
| `VAPRE2`  | —                | Previous price value                        |
| `PDPRE2`  | —                | Discount price                              |
| `VDSRE2`  | —                | Discount value                              |
| `VDRRE2`  | —                | Rebate discount value                       |
| `DESRE2`  | (%) Desc.        | Discount % on item                          |
| `DSCRE2`  | Margem           | Margin / commercial discount indicator      |
| `VLQRE2`  | —                | Net value (qty × unit)                      |
| `TOTRE2`  | Valor Produtos   | Total item line value                       |
| `TOTLIQ`  | —                | Net total (item)                            |
| `TOTBRT`  | —                | Gross total (item)                          |
| `TOTDSR`  | —                | Total after discount                        |
| `TOTFRT`  | Frete            | Freight apportioned to item                 |
| `LUCROL`  | —                | Profit % on item                            |
| `LUCROP`  | —                | Profit R$ on item                           |
| `VALLIQTRB` | IE             | Liquid tributary value                      |

### Fiscal — ICMS (per item)

| Column    | Display Label    | Notes                               |
|-----------|------------------|-------------------------------------|
| `CODST1`  | CST              | ICMS CST/origin code                |
| `CODST2`  | —                | ICMS secondary CST                  |
| `CODICM`  | —                | ICMS rate table FK                  |
| `BASICM`  | Base ICMS        | ICMS calculation base               |
| `TOTICM`  | Total ICMS       | ICMS total on item                  |
| `ICMRE2`  | (%) ICMS         | ICMS rate applied                   |
| `REDICM`  | Red.%            | ICMS reduction %                    |
| `ICMSUB`  | —                | ICMS substitution value             |
| `BASSUB`  | Base St.         | ICMS-ST calculation base            |
| `TOTSUB`  | ICMS Subs.       | ICMS-ST total                       |
| `MRGSUB`  | MVA              | MVA (margin) for ST                 |
| `REDSUB`  | —                | ST reduction %                      |
| `DSCICM`  | —                | ICMS discount                       |
| `DESICM`  | —                | ICMS exoneration                    |
| `TXFICM`  | —                | ICMS fiscal rate                    |
| `REGICM`  | —                | ICMS regime                         |
| `FRTICM`  | Frete rateado    | ICMS on freight                     |
| `SEGICM`  | —                | ICMS on insurance                   |
| `TRBICM`  | —                | ICMS tributado                      |
| `BSCICM`  | —                | ICMS base complement                |
| `DESREG`  | Desc. ICMS Região| Regional ICMS discount description  |
| `CODDESONERACAO` | Motivo Desoneracao | Exoneration reason code     |
| `SUFR_ICMS` | —              | SUFRAMA ICMS reduction flag         |
| `ID_ESTICM` | —              | ICMS rule FK                        |

### Fiscal — IPI (per item)

| Column    | Display Label  | Notes                     |
|-----------|----------------|---------------------------|
| `CODIPI`  | —              | IPI rate table FK         |
| `CSTIPI`  | CST.           | IPI CST code              |
| `CLSIPI`  | —              | IPI classification        |
| `CLSIPI_1`| —              | IPI classification (alt)  |
| `BASIPI`  | B.Calc.        | IPI calculation base      |
| `TOTIPI`  | IPI            | IPI total on item         |
| `IPIRE2`  | Aliq.          | IPI rate                  |
| `FRTIPI`  | —              | IPI on freight            |
| `SEGIPI`  | —              | IPI on insurance          |
| `TRBIPI`  | —              | IPI tributado             |
| `BSCIPI`  | —              | IPI base complement       |
| `DSCIPI`  | —              | IPI discount              |
| `DESIPI`  | —              | IPI exoneration           |
| `TXFIPI`  | —              | IPI fiscal rate           |
| `REGIPI`  | —              | IPI regime                |
| `SUFR_IPI`| —              | SUFRAMA IPI reduction     |
| `ID_ESTIPI`| —             | IPI rule FK               |
| `COD_ENQ` | —              | IPI enquadramento code    |

### Fiscal — PIS / COFINS (per item)

| Column         | Display Label  | Notes                     |
|----------------|----------------|---------------------------|
| `BASPIS`       | B.Calc.        | PIS calculation base      |
| `TOTPIS`       | Valor PIS      | PIS total on item         |
| `ALIQPIS`      | Aliq.          | PIS rate                  |
| `CSTPIS`       | CST.           | PIS CST code              |
| `PERCOF`       | —              | COFINS %                  |
| `PERPIS`       | —              | PIS %                     |
| `BASCOF`       | B.Calc.        | COFINS calculation base   |
| `TOTCOF`       | Valor COFINS   | COFINS total on item      |
| `ALIQCOF`      | Aliq.          | COFINS rate               |
| `CSTCOF`       | CST.           | COFINS CST code           |
| `SUFR_PIS`     | —              | SUFRAMA PIS reduction     |
| `SUFR_COFINS`  | —              | SUFRAMA COFINS reduction  |
| `ALIQPIS_ZF`   | —              | PIS rate free trade zone  |
| `ALIQCOF_ZF`   | —              | COFINS rate free trade zone |
| `FLG_DESC_ZF_PIS` | —           | ZF PIS discount flag      |
| `FLG_DESC_ZF_COF` | —           | ZF COFINS discount flag   |
| `ALIQ_CRED_SN` | —              | Simples Nacional credit rate |
| `ID_REGRA_FCP` | —              | FCP rule FK               |
| `ALIQ_FCPUFDEST` | —            | FCP rate destination UF   |
| `ALIQ_ICMSINTER` | —            | Inter-state ICMS rate     |
| `ALIQ_ICMSINTERPART` | —        | ICMS difal partition rate |
| `ALIQ_ICMSUFDEST` | —           | ICMS rate destination UF  |
| `ID_ESTSIP`    | —              | SIP rule FK               |

### Other Fiscal / IBPT

| Column              | Display Label  | Notes                          |
|---------------------|----------------|--------------------------------|
| `ISSRE2`            | —              | ISS on item                    |
| `BASISS`            | —              | ISS base                       |
| `CSTLAN`            | —              | CST launch                     |
| `CSTCST`            | —              | CST code                       |
| `CSTOUT`            | —              | CST output                     |
| `CODCFO`            | —              | CFO (op. fiscal code)          |
| `DESCFO`            | —              | CFO description                |
| `IBSCBS_CST`        | IBSCBS CST     | IBSCBS CST code                |
| `IBSCBS_C_CLASS_TRIB` | IBSCBS Class Trib | IBSCBS tax classification |
| `ALIQ_IBPT`         | —              | IBPT rate                      |
| `TOTIBPT`           | —              | IBPT total                     |
| `TOTITETRB`         | —              | Item total (tributary)         |
| `CODIGO_IBPT`       | —              | IBPT code                      |
| `EX_IBPT`           | —              | IBPT exception                 |
| `CEST`              | —              | CEST code                      |

### Physical / Packing

| Column    | Display Label  | Notes                  |
|-----------|----------------|------------------------|
| `LIQRE2`  | Peso Liquido   | Net weight on item     |
| `BRTRE2`  | Peso Bruto     | Gross weight on item   |
| `CUBRE2`  | —              | Volume/cubagem on item |
| `VOLRE2`  | —              | Volume                 |
| `PACRE2`  | CPF/CNPJ       | Package code (misnamed label in DFM) |
| `PAPRE2`  | —              | Paper/packaging ref    |
| `QTDEMB`  | Embalagem      | Embalagem qty          |

### Purchase Order Link

| Column           | Display Label    | Notes                            |
|------------------|------------------|----------------------------------|
| `NUMPEDCOMPRA`   | Pedido de Compra | Linked purchase order number     |
| `NUMITEMCOMPRA`  | Item de Compra   | Linked purchase order item       |
| `PEDCLI`         | —                | Customer's PO reference          |
| `PEDPRO`         | —                | Supplier's PO reference          |

---

## Key Join Pattern

```sql
-- Header + items
SELECT
    r.numres, r.dteres, r.codcli, r.cgccli, r.codven, r.codemp,
    r.totres, r.totger, r.toticm, r.totipi, r.totsub,
    i.seqre2, i.codpro, i.qtpre2, i.vlure2, i.totre2,
    i.dscre2, i.desre2, i.vdsre2
FROM pedres r
JOIN pedre2 i ON i.numres = r.numres
WHERE r.flgfec = 0          -- not closed/cancelled (verify flag semantics)
ORDER BY r.numres, i.seqre2
```

---

## Totals Cheat Sheet

| Column    | Table    | Meaning                             |
|-----------|----------|-------------------------------------|
| `TOTRES`  | PEDRES   | Order net total (no taxes)          |
| `TOTGER`  | PEDRES   | Grand total (all taxes included)    |
| `TOTICM`  | both     | ICMS total                          |
| `TOTIPI`  | both     | IPI total                           |
| `TOTSUB`  | both     | ICMS-ST total                       |
| `TOTPIS`  | both     | PIS total                           |
| `TOTCOF`  | both     | COFINS total                        |
| `TOTFRT`  | both     | Freight total                       |
| `TOTSEG`  | PEDRES   | Insurance total                     |
| `TOTOUTDESP` | PEDRES| Other expenses total                |
| `TOTRE2`  | PEDRE2   | Single item line total              |
| `TOTLIQ`  | both     | Net total                           |
| `TOTBRT`  | both     | Gross total                         |

---

## Complete Sorted Field List

```
ACRCND  ALIQCOF  ALIQCOF_ZF  ALIQPIS  ALIQPIS_ZF  ALIQ_CRED_SN
ALIQ_FCPUFDEST  ALIQ_IBPT  ALIQ_ICMSINTER  ALIQ_ICMSINTERPART  ALIQ_ICMSUFDEST
ANORES  APLCFO  APP_VER  ATUEST  BAICLI  BASCAT  BASCOF  BASCOM  BASESB  BASICM
BASIPI  BASISS  BASPIS  BASSUB  BRTRE2  BSCICM  BSCIPI  CBARE2  CEPCLI  CEST
CGCCLI  CGECLI  CIDCLI  CLFRE2  CLITAB  CLSIPI  CLSIPI_1  CODATD  CODCAT  CODCFO
CODCLI  CODCLP  CODCOM  CODCOR  CODDESONERACAO  CODEAN  CODEIT  CODEMP  CODFIL
CODGCL  CODGRU  CODGUS  CODICL  CODICM  CODIGO_IBPT  CODIPI  CODMRC  CODPFA
CODPRM  CODPRO  CODST1  CODST2  CODSTR  CODSUB  CODTAM  CODTCL  CODTIP  CODTRA
CODTXF  CODTXF2  CODUND  CODUSU  CODVEN  CODVWA  COD_ENQ  COMATD  COMCLI
COMICLI  CONSUM  CSTCOF  CSTCST  CSTIPI  CSTLAN  CSTPIS  CSTRE2  CUBRE2  DESCFO
DESICM  DESIPI  DESRE2  DESREG  DESSUB  DEVGER  DEVRES  DIFDSC  DSCCND  DSCCOM
DSCICM  DSCIPI  DSCORGPUBLIC  DSCPER  DSCRE2  DSCREG  DSCRES  DSCSUB  DSPRE2
DSRRE2  DTECMP  DTECOM  DTECON  DTECTA  DTEDEL  DTEFIN  DTEFPE  DTEGER  DTEPED
DTEPRO  DTEREJ  DTERES  DTFRES  DTLPSQ  DTVSUF  ECFEMI  EMPCTA  EMPENT  EMPGER
EMPPED  ENDCLI  EX_IBPT  FATGER  FATRES  FLASEQ  FLGANT  FLGATU  FLGAVI  FLGCLI
FLGCMP  FLGCOM  FLGCON  FLGCTB  FLGDIF  FLGDUP  FLGEST  FLGFAB  FLGFEC  FLGFIN
FLGGER  FLGIMP  FLGIPI  FLGLIB  FLGMAR  FLGMOBILE  FLGNCALC_DIF_MVA  FLGOCO
FLGOPE  FLGPAC  FLGPRO  FLGPSQ  FLGREJ  FLGREN  FLGREQ  FLGRES  FLGSER  FLGSLD
FLGTAB  FLGVAL  FLG_DESC_ZF_COF  FLG_DESC_ZF_PIS  FLG_SINEIF20  FRTICM  FRTIPI
FRTRES  FRTSUB  HRECMP  HRECOM  HRECON  HREDEL  HREFIN  HREFPE  HREPRO  HREREJ
HRERES  IBSCBS_CST  IBSCBS_C_CLASS_TRIB  ICMRE2  ICMRE2SN  ICMSUB
ID  ID_ESTICM  ID_ESTIPI  ID_ESTSIP  ID_FINCIE  ID_FINUFE  ID_FRETE
ID_LOJPE2  ID_PEDCND  ID_PEDCT2  ID_PEDCTA  ID_PEDFE2  ID_PEDGER  ID_PEDGR3
ID_PEDICL  ID_PEDRE2  ID_PEDRES  ID_PEDRES_ORIG  ID_REGRA_FCP  ID_SEPPED
INCFIN  INCREV  INDIC_CF  INDIC_PRESENCA  INECLI  INSCLI  INTFIN  IPIRE2
ISSRE2  IS_CTA  LANCST  LANEST  LIBCLI  LIBSLD  LIMCLI  LINRE2  LINRES  LIQRE2
LUCROL  LUCROP  MARPED  MARPRE  MEDACR  MEDCAT  MEDCOM  MEDDCO  MEDDSC  MEDPRM
MESRES  MODPFA  MOTIVO  MRGSUB  NOMENT  NOMFRT  NOMREGTRIB  NOMTCL  NROITE
NRORE2  NROSUF  NUMCLI  NUMCTA  NUMENT  NUMGER  NUMITEMCOMPRA  NUMPED  NUMPEDCOMPRA
NUMREGTRIB  NUMRES  OB1CAN..OB5CAN  OB1FAT..OB8FAT  OB1RES..OB8RES
OBRGVE  OBSANT  OBSCMP  OBSCOM  OBSCON  OBSDEL  OBSFIN  OBSFPE  OBSPRO
OBSRE2  OBSREJ  OBSRES  OUTCST  PACRE2  PAPRE2  PCOATD  PCORE2  PCORES  PDPRE2
PEDANT  PEDCLI  PEDPRO  PERCOF  PERPIS  PRCCTA  PRFRES  PRODEP  QTDEMB  QTDFAB
QTDIMP  QTDRE2  QTDSEP  QTFRE2  QTFRES  QTILIB  QTIRE2  QTIRE3  QTIRE4  QTIRES
QTLRES  QTPRE2  QTPRES  QTSRE2  REDICM  REDIPI  REDSUB  REFCLI  REFRE2
REGICM  REGIPI  REGSUB  REGTRB  REGTRBEMP  RENMIN  SEGICM  SEGIPI  SEGSUB
SEQEN2  SEQGR2  SEQGR3  SEQITE  SEQLIB  SEQPAR  SEQPE2  SEQPR2  SEQRE2  SEQRE3
SEQRES  SITRES  SLDFAB  SLDGER  SLDRE2  SLDRES  SLDSEP  SUFR_COFINS  SUFR_ICMS
SUFR_IPI  SUFR_PIS  TABPRC  TENCLI  TIPACR  TIPCOM  TIPCPA  TIPDSC  TIPFRE
TIPFRT  TIPICM  TIPIPI  TIPPFA  TIPSTR  TOTACP  TOTACR  TOTBRT  TOTCAT  TOTCLI
TOTCOF  TOTCOFZF  TOTCOM  TOTCST  TOTCUB  TOTDCO  TOTDESCINC  TOTDSC  TOTDSP
TOTDSR  TOTFRT  TOTGE2  TOTGER  TOTGERAL  TOTIBPT  TOTICM  TOTICMSN  TOTIPC
TOTIPI  TOTISS  TOTITETRB  TOTLIQ  TOTOUTDESP  TOTPER  TOTPIS  TOTPISZF
TOTRE2  TOTREN  TOTRES  TOTSEG  TOTSUB  TOTVEN  TOTVOL  TRBCOF  TRBICM  TRBIPI
TRBPIS  TRBSUB  TXFICM  TXFIPI  UFECLI  UFERES  ULTQTS  UPDPROC  USUANT
USUCMP  USUCOM  USUCON  USUDEL  USUFIN  USUFPE  USUPRO  USUREJ  VACRE2  VALCLI
VALLIQTRB  VAPRE2  VCHCST  VCHRE2  VCPCST  VCPRE2  VCRCST  VCRRE2  VCSRE2
VDPRE2  VDRRE2  VDSCOM  VDSRE2  VLDORGPUBLIC  VLQRE2  VLURE2  VMECST  VMERE2
VOLRE2  VPFCST  VPFOUT  VPFRE2  VPRCST  VPRRE2  VRECST  VRERE2
```

---

*Source: reverse-engineered from `ManPed.dfm` (text-format Delphi form — order management screen) in the legacy emerion-pedido Delphi project.*
