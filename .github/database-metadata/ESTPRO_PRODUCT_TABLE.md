# ESTPRO — Product/Item Table Reference

Field mappings derived from `ManPro2.dfm` (the main product/item management form in the legacy Delphi estoque application).

> **DataSources in use**:
> - `DsPro` — product master record (main dataset, maps to `estpro`)
> - `DsIte` — company-specific costs, prices and stock per item (`estite` or similar per-company table)
> - `DsQte` — stock quantities per colour/size combination
> - `dsRegNcmEnt` / `dsRegNcmSaida` — NCM fiscal rules (entry/exit)

---

## Core Identity

| Column    | Display Label          | Notes                                              |
|-----------|------------------------|----------------------------------------------------|
| `CODPRO`  | Item                   | Product PK → `externalId` in ingestion DTOs         |
| `DSCPRO`  | Descrição Principal    | Main product description; TRIM before use           |
| `DSRPRO`  | Descrição Reduzida     | Short/reduced description                           |
| `REFPRO`  | Referência Interna     | Internal reference code                             |
| `SIMPRO`  | Similar                | Similar/substitute product code                     |
| `NUMPRO`  | Part number            | Manufacturer part number                            |
| `CODANT`  | Código antigo          | Legacy product code                                 |
| `ID2PRO`  | ID novo                | New system ID (migration target)                    |
| `FLBPRO`  | Descontinuado (D)      | Discontinued flag; `'D'` = discontinued             |
| `DTCPRO`  | Data de cadastro       |                                                     |
| `CODUSU`  | Usuário                | Registering user FK                                 |
| `WEBPRO`  | Web-Site               |                                                     |

---

## Classification Hierarchy

| Column    | Display Label   | Name column  | Notes                         |
|-----------|-----------------|--------------|-------------------------------|
| `CODCLP`  | Classificação   | `NOMCLP`     | Top-level classification FK   |
| `CODGRU`  | Grupo           | `NOMGRU`     | Product group FK              |
| `CODSUB`  | Sub-Grupo       | `NOMSUB`     | Sub-group FK                  |
| `CODCAT`  | Categoria       | `NOMCAT`     | Category FK                   |
| `CODTIP`  | Tipo            | `NOMTIP`     | Product type FK               |
| `CODMRC`  | Marca           | `NOMMRC`     | Brand FK                      |
| `CODUNE`  | Unidade de Saída| —            | Output unit of measure FK     |
| `CODUNS`  | (Unit, entry?)  | —            | Entry unit of measure FK      |
| `LOCPRO`  | Localização     | —            | Storage location              |
| `OBSPRO`  | Observações     | —            | Product notes                 |
| `ISSPRO`  | (%) ISS         | —            | ISS tax rate                  |
| `GARPRO`  | Prazo de garantia | —          | Warranty period (days)        |
| `PERCOM`  | (%) Comissão    | —            | Commission percentage         |
| `IDEPRO`  | % Desc          | —            | Default discount %            |
| `IMGPRO`  | —               | —            | Product image (blob)          |

---

## Physical Dimensions / Packaging

| Column    | Display Label           | Notes                        |
|-----------|-------------------------|------------------------------|
| `PESLIQ`  | Peso Líquido            | Net weight                   |
| `PESBRT`  | Peso Bruto              | Gross weight                 |
| `LIQEMB`  | Peso Líquido/Caixa      | Net weight per box           |
| `BRTEMB`  | Peso Bruto/Caixa        | Gross weight per box         |
| `CUBPRO`  | Cubagem                 | Volume (m³)                  |
| `ALTPRO`  | Altura                  | Height                       |
| `LARPRO`  | Largura                 | Width                        |
| `COMPRO`  | Comprimento             | Length                       |
| `QTDEMB`  | Qtd. Emb.               | Qty per packaging unit       |
| `QTDVOL`  | Qtd. volumes            | Number of volumes/boxes      |

---

## Barcodes

| Column    | Display Label           | Notes                                     |
|-----------|-------------------------|-------------------------------------------|
| `CODBAR`  | Cód. de barras          | Primary barcode (EAN)                     |
| `CBAPRO`  | Cod. Barras Próprio     | Own/internal barcode                      |
| `CBAEMB`  | Cod. Barras/Embalagem   | Packaging barcode                         |
| `CBAEM2`  | Cod. Barras Emb. 2      |                                           |
| `CBAEM3`  | Cod. Barras Emb. 3      |                                           |
| `TIPEAN`  | Tipo EAN                | EAN type (EAN-8, EAN-13, etc.)            |
| `INIEAN`  | —                       | EAN sequence start                        |
| `SEQBAR`  | —                       | Barcode sequence number                   |
| `QTDBAR`  | —                       | Number of barcodes                        |
| `CBAQTE`  | —                       | Qty associated with barcode               |

---

## Fiscal — NCM / CEST / Origin

| Column          | Display Label         | Notes                                   |
|-----------------|-----------------------|-----------------------------------------|
| `CODNCM`        | NCM do Produto        | Nomenclatura Comum do Mercosul          |
| `CEST`          | CEST                  | Código Especificador da Subst. Tributária |
| `CODST1`        | Origem do Produto     | Product origin code (CST/CSOSN origin)  |
| `INI_REG_ST`    | Início de regime ST   | ST regime start date                    |
| `CODIF`         | CODIF                 | CODIF (fuel-related)                    |
| `FCI`           | FCI                   | Ficha de Conteúdo de Importação         |
| `CODANP`        | Código ANP            | ANP code (fuel products)                |
| `DESCANP`       | Descrição DI          | ANP/fuel description                    |

---

## Fiscal — ICMS

| Column           | Display Label           | Notes                                   |
|------------------|-------------------------|-----------------------------------------|
| `CODICM`         | —                       | ICMS code FK                            |
| `CODSTS`         | Saída (CST)             | ICMS CST/CSOSN for exit                 |
| `CODSTE`         | Entrada (CST)           | ICMS CST/CSOSN for entry                |
| `SAIICM`         | (%) ICMS saída          | ICMS rate (exit)                        |
| `ENTICM`         | (%) ICMS entrada        | ICMS rate (entry)                       |
| `ICMSAI`         | Saída                   | ICMS amount (exit)                      |
| `ICMENT`         | Entrada                 | ICMS amount (entry)                     |
| `PERICM`         | Alíquota                | ICMS percentage                         |
| `ICMTEN`         | —                       | ICMS tenths?                            |
| `ICMTSD`         | —                       | ICMS tax source/destination             |
| `ID_ESTICM_ENTRADA` | —                    | FK → ICMS rule (entry)                  |
| `ID_ESTICM_SAIDA`   | —                    | FK → ICMS rule (exit)                   |
| `COD_FCP_ENTRADA`   | Último Custo (FOB)   | FK → FCP rule (entry)                   |
| `COD_FCP_SAIDA`     | FCP Saida            | FK → FCP rule (exit)                    |

---

## Fiscal — IPI

| Column    | Display Label           | Notes                       |
|-----------|-------------------------|-----------------------------|
| `CODIPI`  | —                       | IPI code FK                 |
| `CSTIPI`  | Situação tributária     | IPI tax situation code      |
| `CLFENT`  | Classificação do IPI    | IPI classification (entry)  |
| `CLFSAI`  | Classificação do IPI    | IPI classification (exit)   |
| `CLSIPI`  | —                       | IPI classification (general)|
| `PERIPI`  | Alíquota                | IPI rate %                  |
| `IPISAI`  | Saída                   | IPI amount (exit)           |
| `IPIENT`  | Entrada                 | IPI amount (entry)          |
| `ENTIPI`  | —                       | IPI entry reference         |
| `SAIIPI`  | —                       | IPI exit reference          |
| `QTEPRO`  | Relação entrada         | Entry conversion ratio      |
| `QTSPRO`  | Relação saída           | Exit conversion ratio       |

---

## Fiscal — PIS / COFINS

| Column                     | Display Label          | Notes                          |
|----------------------------|------------------------|--------------------------------|
| `ALIQ_PIS`                 | PIS                    | PIS aliquot %                  |
| `ALIQ_COF`                 | COFINS                 | COFINS aliquot %               |
| `ID_REGRA_PIS`             | Regra Pis/Saída        | PIS rule FK (exit)             |
| `ID_REGRA_PIS_ENTRADA`     | Regra Pis/Entrada      | PIS rule FK (entry)            |
| `ID_REGRA_COFINS`          | Regra Cofins/Saída     | COFINS rule FK (exit)          |
| `ID_REGRA_COFINS_ENTRADA`  | Regra Cofins/Entrada   | COFINS rule FK (entry)         |
| `ID_REGRA_FCP`             | —                      | FCP rule FK                    |
| `IBSCBS_CST`               | IBSCBS CST             | IBSCBS CST code                |
| `IBSCBS_C_CLASS_TRIB`      | IBSCBS Class Trib      | IBSCBS tax classification      |

---

## Fiscal — Import

| Column    | Display Label           | Notes                             |
|-----------|-------------------------|-----------------------------------|
| `DESIMP`  | Descrição embalagem compra | Import description               |
| `DSCIMP`  | Descrição de catálogo   | Catalogue description             |
| `DSRIMP`  | Descrição em inglês     | English description               |
| `PERIMP`  | —                       | Import duty rate %                |
| `VALIMP`  | Qtd. Última Compra      | Import value                      |
| `QTDIMP`  | —                       | Import quantity                   |
| `DULIMP`  | —                       | Import last date                  |
| `ENTIMP`  | —                       | Entry import reference            |

---

## NCM Fiscal Rules (dsRegNcmEnt / dsRegNcmSaida)

| Column    | Display Label           |
|-----------|-------------------------|
| `OBSPRO`  | Regra de NCM/Entrada    |
| `CODITE`  | Regra de NCM/Saída      |
| `CODSTR`  | —                       |
| `CODST2`  | CODIF                   |
| `TIPSTE`  | —                       |
| `TIPSTS`  | —                       |

---

## Costs and Prices — per Company (`DsIte`)

These fields live in a per-company/per-item table (one row per `CODPRO` + `CODEMP`).

| Column    | Display Label           | Notes                                 |
|-----------|-------------------------|---------------------------------------|
| `CODITE`  | —                       | PK of company-item record             |
| `CODEMP`  | Empresa                 | Company FK                            |
| `CSTITE`  | Último Preço            | Last sale price                       |
| `VCHITE`  | Custo Histórico         | Historical cost                       |
| `VCRITE`  | Custo Reposição         | Replacement cost                      |
| `VCPITE`  | Custo Ponderado         | Weighted average cost                 |
| `CUBPRO`  | Custo da Última Compra  | Last purchase cost                    |
| `CUSTOFOB`| Último Custo (FOB)      | Last FOB cost                         |
| `CUSTOCIF`| Último Custo (CIF)      | Last CIF cost                         |
| `DTEREG2` | Dt. Últ. (FOB)          | Date of last FOB purchase             |
| `QTDCMP`  | Qtd. Última Compra      | Qty on last purchase                  |

### Base Sale Prices (per company)

| Column    | Display Label  |
|-----------|----------------|
| `VB1ITE`  | Unitário 1     |
| `VB2ITE`  | Unitário 2     |
| `VB3ITE`  | Unitário 3     |
| `VB4ITE`  | Unitário 4     |
| `VB5ITE`  | Unitário 5     |

### Promotional Prices (per company)

| Column    | Display Label   |
|-----------|-----------------|
| `VP1ITE`  | Promocional 1   |
| `VP2ITE`  | Promocional 2   |
| `VP3ITE`  | Promocional 3   |
| `VP4ITE`  | Promocional 4   |
| `VP5ITE`  | Promocional 5   |

### Discounts per price table (per company)

| Column    | Notes                     |
|-----------|---------------------------|
| `DS1ITE`  | Desconto tabela 1         |
| `DS2ITE`  | Desconto tabela 2         |
| `DS3ITE`  | Desconto tabela 3         |
| `DS4ITE`  | Desconto tabela 4         |
| `DS5ITE`  | Desconto tabela 5         |

### Markup (per company)

| Column    | Notes                  |
|-----------|------------------------|
| `MK1ITE`..`MK5ITE` | Markup T 1–5 |
| `MS1ITE`..`MS5ITE` | Markup S 1–5 |
| `VPRITE`  | Custo Ponderado (G)    |
| `VMEITE`  | Média Ponderada (E)    |
| `CODCM1`..`CODCM5` | Company price table codes 1–5 |

### Stock per Company (`DsIte`)

| Column    | Display Label  |
|-----------|----------------|
| `QTAITE`  | Adquirido      |
| `QTRITE`  | Reservado      |
| `QTSITE`  | Disponível     |
| `QTMITE`  | Mínimo         |
| `QMAITE`  | Máximo         |
| `QTDITE`  | Atual          |

---

## Stock per Colour/Size (`DsQte`)

| Column    | Display Label  | Notes                          |
|-----------|----------------|--------------------------------|
| `CODCOR`  | Cor            | Colour FK → `NOMCOR`           |
| `CODTAM`  | Tamanhos       | Size FK                        |
| `TAMCOR`  | —              | Combined size-colour key       |
| `QTAQTE`  | Adquirido      |                                |
| `QTRQTE`  | Reservado      |                                |
| `QTSQTE`  | Disponível     |                                |
| `QTMQTE`  | Mínimo         |                                |
| `QMAQTE`  | Máximo         |                                |
| `QTDRMA`  | RMA            | Return Merchandise quantity    |
| `QTDQTE`  | Atual          | Current stock                  |

---

## Flags

| Column    | Meaning                        |
|-----------|--------------------------------|
| `FLBPRO`  | Descontinuado/Bloqueado (`'D'`) |
| `FLGKIT`  | É kit/combo                    |
| `FLGTAM`  | Usa tamanhos                   |
| `FLGANT`  | Tem código antigo              |
| `FLGATU`  | Pendente de atualização        |
| `FLGFIL`  | Restrito a filial              |
| `FLGLIS`  | Em lista                       |
| `FLGPRO`  | Flag promoção                  |
| `FLGTAB`  | Usa tabela de preços           |
| `FLGTRG`  | Flag trigger/reprocessamento   |

---

## Audit

| Column    | Display Label     |
|-----------|-------------------|
| `DTCPRO`  | Data de cadastro  |
| `CODUSU`  | Usuário           |
| `LOGUSU`  | Login             |
| `ATUMSU`  | Atualização sub   |
| `ATUVCR`  | Atualização custo |

---

## Key Relationships

| FK column  | Points to        | Name column | Notes                          |
|------------|------------------|-------------|--------------------------------|
| `CODGRU`   | product group    | `NOMGRU`    |                                |
| `CODSUB`   | product sub-group| `NOMSUB`    |                                |
| `CODCAT`   | category         | `NOMCAT`    |                                |
| `CODCLP`   | classification   | `NOMCLP`    |                                |
| `CODTIP`   | type             | `NOMTIP`    |                                |
| `CODMRC`   | brand            | `NOMMRC`    |                                |
| `CODEMP`   | company          | `NOMEMP`    |                                |
| `CODCOR`   | colour           | `NOMCOR`    |                                |
| `CODICM`   | ICMS table       | `NOMICM`    | join for `PERICM`              |
| `CODIPI`   | IPI table        | `NOMIPI`    | join for `PERIPI`              |
| `CODST1`   | origin/CST table | `NOMST1`    |                                |

---

## Complete Sorted Field List

```
ALIQ_COF  ALIQ_PIS  ALTPRO    ATUMSU    ATUVCR    BRTEMB    CATPRO
CBAEM2    CBAEM3    CBAEMB    CBAPRO    CBAQTE    CEST      CLFENT    CLFSAI
CLSIPI    CODANP    CODANT    CODBAR    CODCAT    CODCLP    CODCM1    CODCM2
CODCM3    CODCM4    CODCM5    CODCOM    CODCOR    CODEMP    CODGRU    CODICM
CODIF     CODIPI    CODITE    CODMRC    CODNCM    CODNOM    CODPRO    CODST1
CODST2    CODSTE    CODSTR    CODSTS    CODSUB    CODTAM    CODTIP    CODUNE
CODUNS    CODUSU    COD_FCP_ENTRADA     COD_FCP_SAIDA       COMPRO    CSTIPI
CSTITE    CSTOUT    CUBPRO    CUSTOCIF  CUSTOFOB  CXAPRO    DESCANP   DESIM2
DESIMP    DESNC1..8 DSCIMP    DSCPRO    DSRIMP    DSRPRO    DTCPRO    DTEREG2
DS1ITE..DS5ITE      DULCMP    DULIMP    ENTICM    ENTIMP    ENTIPI    FCI
FLBPRO    FLGANT    FLGATU    FLGFIL    FLGKIT    FLGLIS    FLGPRO    FLGTAB
FLGTAM    FLGTRG    GARPRO    IBSCBS_CST          IBSCBS_C_CLASS_TRIB
ICM       ICMENT    ICMSAI    ICMTEN    ICMTSD    ID2PRO    IDEPRO
ID_ESTICM_ENTRADA   ID_ESTICM_SAIDA     ID_REGRA_COFINS     ID_REGRA_COFINS_ENTRADA
ID_REGRA_FCP        ID_REGRA_PIS        ID_REGRA_PIS_ENTRADA
IMGPAF    IMGPRO    IMPPRD    INIEAN    INI_REG_ST          IPIENT    IPISAI
IPITEN    IPITSD    ISSPRO    LANCBA    LANMIN    LARPRO    LIQEMB    LOCPRO
LOGUSU    MK1ITE..MK5ITE      MS1ITE..MS5ITE      NOMCAT    NOMCLP    NOMCOR
NOMEMP    NOMGRU    NOMICM    NOMIPI    NOMMRC    NOMST1    NOMSTR    NOMSUB
NOMTIP    NROPRO    NUMPRO    OBSPRO    PERCOM    PERICM    PERIMP    PERIPI
PESBRT    PESCUB    PESLIQ    PRODEP    QMAITE    QMAQTE    QTAITE    QTAQTE
QTDBAR    QTDCMP    QTDEMB    QTDIMP    QTDITE    QTDQTE    QTDREG2   QTDRMA
QTDVOL    QTEPRO    QTMITE    QTMQTE    QTRITE    QTRQTE    QTSITE    QTSPRO
QTSQTE    REFPRO    SAIICM    SAIIPI    SEQBAR    SIMPRO    TAMCOR    TIPEAN
TIPSTE    TIPSTS    VALIMP    VB1ITE..VB5ITE      VCHITE    VCHOUT    VCPITE
VCRITE    VMEITE    VP1ITE..VP5ITE      VPFITE    VPFOUT    VPRITE    VREITE
VREOUT    WEBPRO
```

---

*Source: reverse-engineered from `ManPro2.dfm` (text-format Delphi form — product/item management screen) in the legacy emerion-estoque Delphi project.*
