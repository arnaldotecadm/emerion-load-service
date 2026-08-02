# Emerion Load Service — Token-Optimized Copilot Context

## Purpose
Kotlin/Spring Boot loader that reads legacy Firebird data and sends normalized payloads to the ingestion API.

Core flow: **Query → Projection → Mapper → Model → Ingestion DTO → Send**.

## High-Value Rules (Repository Contract)
1. **Multi-tenant segregation is mandatory.**
   - Retailer identifier: `geremp.cgcemp` → `cnpjEmpresa`.
   - Resolve once via `CompanyProvider`, configured by `company.codemp` (default `1`).
   - Stamp `cnpjEmpresa` on every ingestion DTO.

2. **Do not conflate identifiers.**
   - Retailer: `cnpjEmpresa` (from `geremp`).
   - Customer identity: `cpfCnpj` (from `fincli.cgccli`).
   - Customer external key in relationships: `customerExternalId` (legacy `codCli`).

3. **Customer order payload must carry both retailer and customer identifiers.**
   - `CustomerOrderIngestionDto` includes:
     - `cnpjEmpresa`
     - `cpfCnpj`
     - `customerExternalId`
     - order key `externalId` (`numres`)

4. **`pedres.dteres` is DATE semantics.**
   - In projection: use `LocalDateTime` for JDBC/projection compatibility.
   - In model/DTO: map to `LocalDate`.

5. **Firebird/Jaybird compatibility constraints.**
   - Avoid direct projection `TIMESTAMP -> LocalDate` in Spring Data native interface projections.
   - On old Jaybird drivers, avoid `getObject(column, Class)`; prefer `getInt + wasNull` or `getBigDecimal`.

## Endpoint Intent
- This service is ingestion-oriented.
- Current GET endpoints are for testing/inspection and may return ingestion-shaped DTOs so `cnpjEmpresa` is visible.

## Implementation Anchors
- `service/CompanyProvider.kt`
- `client/IngestionServiceClient.kt`
- `client/dto/*IngestionDto.kt`
- `repository/*QueryRepository.kt` + `repository/mapper/*Mapper.kt`

## Preferred Working Style for Changes
- Keep SQL native for Firebird extraction.
- Keep transformations in mappers.
- Reuse existing DTO/mapper patterns before adding new abstractions.
- Make field semantics explicit in names (`customerExternalId`, `cpfCnpj`, `cnpjEmpresa`).

## fincli Key Fields (Customer Table)
Full reference: `.github/database-metadata/FINCLI_CUSTOMER_TABLE.md`

| Column    | Meaning                       |
|-----------|-------------------------------|
| `CODCLI`  | Customer PK → `customerExternalId` |
| `NOMCLI`  | Nome/Razão social             |
| `CGCCLI`  | CNPJ/CPF → `cpfCnpj`         |
| `APECLI`  | Apelido/Fantasia (trade name) |
| `DTNCLI`  | Data de nascimento/fundação   |
| `INSCLI`  | Inscrição Estadual/RG         |
| `FLBCLI`  | Bloqueado (`'*'` = blocked)   |
| `CODVEN`  | Vendedor FK                   |
| `CODATD`  | Atendente FK                  |
| `REGTRB`  | Regime tributário → join `FINREGTRIB` on `NUMREGTRIB` |
| `LIMCLI`  | Limite de crédito             |
| `OBSCLI`  | Observações do cliente        |
| `DCACLI`  | Data do cadastro              |

### fincli Address Columns Convention
Four address types share the same structure; column name position 3 = address type:
- **F** → Faturamento (billing)
- **C** → Cobrança (collection)
- **A** → Compras (purchase)
- **E** → Entrega (delivery)

Pattern: `CE<x>CLI`=CEP, `EN<x>CLI`=Endereço, `NR<x>CLI`=Número, `BA<x>CLI`=Bairro,
`CI<x>CLI`=Cidade key, `UF<x>CLI`=UF, `TE<x>CLI`=Telefone, `CO<x>CLI`=Contato,
`RF<x>CLI`=Complemento/Ref, `PT<x>CLI`=Ponto, `FO<x>CLI`=Fone, `FA<x>CLI`=Fax,
`PF<x>CLI`=Celular.

## finven Key Fields (Salesperson Table)
Full reference: `.github/database-metadata/FINVEN_SALESPERSON_TABLE.md`

| Column      | Display Label             | Notes                                  |
|-------------|---------------------------|----------------------------------------|
| `CODVEN`    | Código                    | PK; FK from `fincli.CODVEN`            |
| `NOMVEN`    | Nome/Razão social         | Salesperson name                       |
| `CGCVEN`    | CNPJ/CPF                  | Tax document                           |
| `APEVEN`    | Apelido/Fantasia          | Trade name                             |
| `INSVEN`    | Inscrição Estadual/RG     |                                        |
| `FLGATI`    | Ativo?                    | Active flag                            |
| `DCAVEN`    | Cadastrado em             | Registration date                      |
| `CODGVE`    | Grupo                     | FK → `NOMGVE`                          |
| `CODCVE`    | Categoria                 | FK → `NOMCVE`                          |
| `CODTVE`    | Tipo                      | FK → `NOMTVE`                          |
| `CODCOM`    | Comissão                  | FK → `PERCOM` (commission %)           |
| `CODBAN`    | Banco                     | FK → `NOMBAN`                          |
| `CODAGB`    | Agência                   | Bank agency code                       |
| `CODCNB`    | No. da conta              | Bank account number                    |
| `NOMCNB`    | Titular                   | Account holder name                    |
| `CEPVEN`    | Cep                       | Postal code                            |
| `ENDVEN`    | Endereço                  |                                        |
| `NUMVEN`    | No.                       | Street number                          |
| `BAIVEN`    | Bairro                    |                                        |
| `CIDVEN`    | Cidade                    | City name (text)                       |
| `SIGUFE`    | UF                        | State abbreviation                     |
| `FONVEN`    | Telefone                  |                                        |
| `PRFVEN`    | Prefixo (tel)             | Phone area code                        |
| `FAXVEN`    | Fax                       |                                        |
| `CELVEN`    | Celular                   |                                        |
| `EMAVEN`    | E-mail                    |                                        |
| `WEBVEN`    | Web-site                  |                                        |
| `ID_FINUFE` | UF (NFe)                  | FK to UF lookup                        |
| `ID_FINCIE` | Município (NFe)           | FK to city lookup                      |
| `ID_FINPAI` | País                      | FK to country lookup                   |

Join pattern: `JOIN finven ven ON ven.codven = cli.codven`

## estpro Key Fields (Product/Item Table)
Full reference: `.github/database-metadata/ESTPRO_PRODUCT_TABLE.md`

> DataSources: `DsPro` = product master; `DsIte` = per-company costs/prices/stock; `DsQte` = stock by colour/size.

| Column      | Display Label          | Notes                                         |
|-------------|------------------------|-----------------------------------------------|
| `CODPRO`    | Item                   | Product PK → `externalId`                     |
| `DSCPRO`    | Descrição Principal    | Main description                              |
| `DSRPRO`    | Descrição Reduzida     | Short description                             |
| `REFPRO`    | Referência Interna     | Internal reference                            |
| `SIMPRO`    | Similar                | Substitute product                            |
| `NUMPRO`    | Part number            |                                               |
| `FLBPRO`    | Descontinuado          | `'D'` = discontinued                          |
| `DTCPRO`    | Data de cadastro       |                                               |
| `CODGRU`    | Grupo                  | FK → `NOMGRU`                                 |
| `CODSUB`    | Sub-Grupo              | FK → `NOMSUB`                                 |
| `CODCAT`    | Categoria              | FK → `NOMCAT`                                 |
| `CODCLP`    | Classificação          | FK → `NOMCLP`                                 |
| `CODTIP`    | Tipo                   | FK → `NOMTIP`                                 |
| `CODMRC`    | Marca                  | FK → `NOMMRC`                                 |
| `CODUNE`    | Unidade de Saída       | Unit of measure FK                            |
| `CODNCM`    | NCM                    | Nomenclatura Comum Mercosul                   |
| `CEST`      | CEST                   | Subst. tributária code                        |
| `CODST1`    | Origem do Produto      | Origin/CST code FK                            |
| `PESLIQ`    | Peso Líquido           |                                               |
| `PESBRT`    | Peso Bruto             |                                               |
| `CODBAR`    | Cód. de barras         | Primary EAN barcode                           |
| `CBAPRO`    | Cod. Barras Próprio    | Own barcode                                   |
| `LOCPRO`    | Localização            | Storage location                              |
| `WEBPRO`    | Web-Site               |                                               |

### estpro Costs & Prices (DsIte — per company)
| Column        | Display Label          |
|---------------|------------------------|
| `CSTITE`      | Último Preço           |
| `VCHITE`      | Custo Histórico        |
| `VCPITE`      | Custo Ponderado        |
| `VCRITE`      | Custo Reposição        |
| `VB1ITE`–`VB5ITE` | Unitário 1–5      |
| `VP1ITE`–`VP5ITE` | Promocional 1–5   |
| `CUSTOFOB`    | Último Custo (FOB)     |
| `CUSTOCIF`    | Último Custo (CIF)     |
| `QTDCMP`      | Qtd. Última Compra     |

### estpro Stock (DsQte — per colour/size)
`QTAQTE`=Adquirido, `QTRQTE`=Reservado, `QTSQTE`=Disponível, `QTMQTE`=Mínimo, `QMAQTE`=Máximo, `QTDRMA`=RMA

## PEDRES / PEDRE2 Key Fields (Order Header & Items)
Full reference: `.github/database-metadata/PEDRES_PEDRE2_ORDER_TABLES.md`

> **DATE rule**: `PEDRES.DTERES` is DATE semantics — read as `LocalDateTime` in JDBC projections, map to `LocalDate` in DTOs.  
> DataSources: `DsRes` = PEDRES (header); `DsRe2` = PEDRE2 (items, FK: `NUMRES`+`SEQRE2`).

### PEDRES — Order Header

| Column        | Display Label       | Notes                                  |
|---------------|---------------------|----------------------------------------|
| `NUMRES`      | Nro. do Pedido      | Order PK → `externalId`                |
| `DTERES`      | Emissão             | Order date (DATE semantics)            |
| `DTFRES`      | Entregar Em         | Requested delivery date                |
| `CODEMP`      | Empresa             | Company FK                             |
| `CODCLI`      | Cliente             | Customer FK → `fincli.CODCLI`          |
| `CGCCLI`      | CPF/CNPJ            | Denorm. customer tax doc               |
| `INSCLI`      | IE                  | Denorm. customer state registration    |
| `CODVEN`      | Vendedor            | Salesperson FK → `finven.CODVEN`       |
| `CODATD`      | Atendente           | Attendant FK                           |
| `CODPFA`      | Padrao Fat.         | Billing pattern FK                     |
| `CODTCL`      | Tipo Cliente        | Customer type FK                       |
| `CODTRA`      | —                   | Carrier FK                             |
| `ID_FRETE`    | Frete               | Freight FK / amount                    |
| `DSCCOM`      | (%) Desc. Comercial | Commercial discount %                  |
| `DSCREG`      | (%) Desc. ICMS Reg. | Regional ICMS discount %               |
| `TOTRES`      | Total do Pedido     | Order net total (no taxes)             |
| `TOTGER`      | Total Geral         | Grand total (all taxes)                |
| `TOTICM`      | Total ICMS          |                                        |
| `TOTIPI`      | IPI                 |                                        |
| `TOTSUB`      | ICMS Subs.          |                                        |
| `TOTPIS`      | —                   |                                        |
| `TOTCOF`      | —                   |                                        |
| `TOTFRT`      | Frete               |                                        |
| `TOTSEG`      | Seguro              |                                        |
| `TOTOUTDESP`  | Outras Desp.        |                                        |
| `REGTRB`      | —                   | Tax regime on order                    |
| `SITRES`      | —                   | Order status code                      |
| `PEDANT`      | Pedido Anterior     | Previous/linked order                  |

### PEDRE2 — Order Items (FK: NUMRES + SEQRE2)

| Column    | Display Label    | Notes                                      |
|-----------|------------------|--------------------------------------------|
| `SEQRE2`  | —                | Item sequence PK (1, 2, 3…)                |
| `NUMRES`  | —                | FK to `PEDRES.NUMRES`                      |
| `CODPRO`  | Item             | Product FK → `estpro.CODPRO`               |
| `DSCRE2`  | Descrição        | Item description on order                  |
| `CODUND`  | Unidade          | Unit of measure                            |
| `CODCOR`  | Cor              | Colour FK                                  |
| `CODTAM`  | Tamanho          | Size FK                                    |
| `QTPRE2`  | Qtd.             | Ordered quantity                           |
| `VLURE2`  | Valor Unitário   | Unit price applied                         |
| `TOTRE2`  | Valor Produtos   | Item line total                            |
| `DESRE2`  | (%) Desc.        | Item discount %                            |
| `VDSRE2`  | —                | Item discount value                        |
| `BASICM`  | Base ICMS        | ICMS base on item                          |
| `TOTICM`  | Total ICMS       |                                            |
| `ICMRE2`  | (%) ICMS         |                                            |
| `BASIPI`  | B.Calc.          | IPI base                                   |
| `TOTIPI`  | IPI              |                                            |
| `BASSUB`  | Base St.         | ICMS-ST base                               |
| `TOTSUB`  | ICMS Subs.       |                                            |
| `BASPIS`  | —                | PIS base                                   |
| `TOTPIS`  | Valor PIS        |                                            |
| `BASCOF`  | —                | COFINS base                                |
| `TOTCOF`  | Valor COFINS     |                                            |
| `NUMPEDCOMPRA` | Pedido Compra | Linked purchase order number             |
| `OBSRE2`  | Observação       | Item-level notes                           |
