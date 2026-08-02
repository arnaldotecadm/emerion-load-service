# FINCLI — Customer Table Reference

Field mappings derived from `ManCli.dfm` (the main customer management form in the legacy Delphi application).

> **Naming convention**: columns follow a `<prefix><address-type>CLI` pattern.
> Address type suffixes: **F** = Faturamento (billing), **C** = Cobrança (collection), **A** = Compras (purchase), **E** = Entrega (delivery).

---

## Core Identity

| Column      | Display Label        | Notes                                                       |
|-------------|----------------------|-------------------------------------------------------------|
| `CODCLI`    | Código               | Customer PK; used as `customerExternalId` in ingestion DTOs |
| `NOMCLI`    | Nome/Razão social    | Legal name; TRIM before use                                 |
| `CGCCLI`    | CNPJ/CPF             | Tax document → maps to `cpfCnpj`                            |
| `APECLI`    | Apelido/Fantasia     | Trade name / nickname                                       |
| `DTNCLI`    | Dt.Fundação          | Birth/foundation date                                       |
| `INSCLI`    | I.E / Inscrição Est. | State tax registration (RG for individuals)                 |
| `FLBCLI`    | Bloqueado            | `'*'` = blocked; `' '` / null = active                     |

---

## Commercial Relationships

| Column    | Display Label        | Notes                              |
|-----------|----------------------|------------------------------------|
| `CODATD`  | Atendente            | FK to attendant table              |
| `CODVEN`  | Vendedor             | FK → `NOMVEN` = salesperson name   |
| `CODBAN`  | Banco preferencial   | FK → `NOMBAN` = bank name          |
| `CODTCL`  | Tipo de cliente      | FK → `NOMTCL` = customer type name |
| `CODCCL`  | Categoria            | FK → `NOMCCL` = category name      |
| `CODGCL`  | Grupo                | FK → `NOMGCL` = group name         |
| `CODTRA`  | Transportadora       | FK → `NOMTRA` = carrier name       |
| `CODCOM`  | Comissão             | Commission code                    |
| `CLITAB`  | Tabela aplicada      | Applied price table                |
| `CODPFA`  | Padrão de faturamento| Billing pattern                    |

---

## Billing Address (`*F*CLI` — Faturamento)

| Column      | Display Label      |
|-------------|--------------------|
| `CEFCLI`    | CEP                |
| `ENFCLI`    | Endereço           |
| `RFFCLI`    | Complemento        |
| `PT1CLI`    | Ponto (complemento extra) |
| `NRFCLI`    | No.                |
| `BAFCLI`    | Bairro             |
| `CIFCLI`    | Cidade (lookup key) |
| `UFFCLI`    | UF                 |
| `TEFCLI`    | Telefone           |
| `FO1CLI`    | Fone 1             |
| `FA1CLI`    | Fax 1              |
| `PF1CLI`    | Celular 1          |
| `PC1CLI`    | Prefixo celular 1  |
| `FC1CLI`    | Fax celular 1      |
| `COFCLI`    | Contato            |
| `ID_FINUFF` | ID UF faturamento  (FK to UF lookup) |
| `ID_FINCIF` | ID Cidade faturamento (FK to city lookup) |

---

## Collection Address (`*C*CLI` — Cobrança)

| Column      | Display Label      |
|-------------|--------------------|
| `CECCLI`    | CEP                |
| `ENCCLI`    | Endereço           |
| `RFCCLI`    | Complemento        |
| `PT2CLI`    | Ponto              |
| `NRCCLI`    | No.                |
| `BACCLI`    | Bairro             |
| `CICCLI`    | Cidade (lookup key) |
| `UFCCLI`    | UF                 |
| `TECCLI`    | Telefone           |
| `FO2CLI`    | Fone 2             |
| `FA2CLI`    | Fax 2              |
| `PF2CLI`    | Celular 2          |
| `PC2CLI`    | Prefixo celular 2  |
| `FC2CLI`    | Fax celular 2      |
| `COCCLI`    | Contato            |
| `TENCOB`    | Telefone do contato de cobrança |
| `ID_FINUFC` | ID UF cobrança (FK) |
| `ID_FINCIC` | ID Cidade cobrança (FK) |

---

## Purchase Address (`*A*CLI` — Compras)

| Column      | Display Label      |
|-------------|--------------------|
| `CEACLI`    | CEP                |
| `ENACLI`    | Endereço           |
| `RFACLI`    | Complemento        |
| `PT3CLI`    | Ponto              |
| `NRACLI`    | No.                |
| `BAACLI`    | Bairro             |
| `CIACLI`    | Cidade (lookup key) |
| `UFACLI`    | UF                 |
| `TEACLI`    | Telefone           |
| `FO3CLI`    | Fone 3             |
| `FA3CLI`    | Fax 3              |
| `PF3CLI`    | Celular 3          |
| `PC3CLI`    | Prefixo celular 3  |
| `FC3CLI`    | Fax celular 3      |
| `COMCLI`    | Comprador / Contato compras |
| `TENCOM`    | Telefone contato compras |
| `ID_FINUFA` | ID UF compras (FK) |
| `ID_FINCIA` | ID Cidade compras (FK) |

---

## Delivery Address (`*E*CLI` — Entrega)

| Column      | Display Label      |
|-------------|--------------------|
| `CEECLI`    | CEP                |
| `ENECLI`    | Endereço           |
| `RFECLI`    | Complemento        |
| `PT4CLI`    | Ponto              |
| `NRECLI`    | No.                |
| `BAECLI`    | Bairro             |
| `CIECLI`    | Cidade (lookup key) |
| `UFECLI`    | UF                 |
| `TEECLI`    | Telefone           |
| `FO4CLI`    | Fone 4             |
| `FA4CLI`    | Fax 4              |
| `PF4CLI`    | Celular 4          |
| `PC4CLI`    | Prefixo celular 4  |
| `FC4CLI`    | Fax celular 4      |
| `COECLI`    | Contato entrega    |
| `TENENT`    | Telefone contato entrega |
| `CGECLI`    | CNPJ/CPF entrega   |
| `INECLI`    | Inscrição (RG/IE) entrega |
| `ID_FINUFE` | ID UF entrega (FK) |
| `ID_FINCIE` | ID Cidade entrega (FK) |

---

## Contact / Digital

| Column             | Display Label             |
|--------------------|---------------------------|
| `EM1CLI`           | E-mail 1                  |
| `EM2CLI`           | E-mail 2                  |
| `WEBCLI`           | Web-site                  |
| `EMAIL_INTERNO_XML`| Email de recep. da NFe    |

---

## Fiscal / Tax

| Column               | Display Label       | Notes                             |
|----------------------|---------------------|-----------------------------------|
| `REGTRB`             | Regime Trib.        | FK → `FINREGTRIB.NUMREGTRIB`; join for name |
| `CNAE`               | CNAE                | CNAE code                        |
| `INSC_MUNICIPAL`     | IM                  | Municipal inscription             |
| `INDIC_IE`           | Indicação IE        | IE indicator                      |
| `INDIC_ESTRANGEIRO`  | Estrangeiro         | Foreign customer flag             |
| `ID_FINPAI`          | País                | FK to country lookup              |

---

## Financial / Credit

| Column    | Display Label                  | Notes                              |
|-----------|--------------------------------|------------------------------------|
| `LIMCLI`  | Limite de crédito              | Credit limit amount                |
| `DTLCRE`  | Data limite (crédito)          | Credit expiry date                 |
| `CLIDEV`  | Crédito por devoluções         | Return credit balance              |
| `OBSCLI`  | Observações                    | Customer notes                     |
| `OBSFIN`  | Obs. Financeiro                | Financial notes                    |
| `FLGPSQ`  | Realizar consultas?            | Allow financial queries on orders  |
| `FLGPRO`  | Pode ser protestado?           | Can be legally protested           |
| `FLGINF`  | Enviar informações comerciais? | Send commercial info flag          |

---

## SUFRAMA (Free Trade Zone)

| Column    | Display Label              |
|-----------|----------------------------|
| `NROSUF`  | No. SUFRAMA                |
| `TOTACM`  | Maior acúmulo              |
| `DTEACM`  | Data do maior acúmulo      |
| `MUNSUF`  | No. Município zona franca  |
| `DTVSUF`  | Validade do SUFRAMA        |

---

## Geo Classification

| Column    | Display Label  |
|-----------|----------------|
| `CODMCR`  | Macro região   |
| `CODMRG`  | Micro região   |
| `CODSET`  | Setor          |
| `CODMST`  | Micro setor    |

---

## Holding / Partner

| Column      | Display Label | Notes              |
|-------------|---------------|--------------------|
| `ID_FINHOL` | Holding       | FK to holding entity |

---

## Accounting

| Column      | Display Label      | Notes                          |
|-------------|--------------------|--------------------------------|
| `ID_FINCTB` | Nível 1 (base)     | Accounting level 1 FK          |
| `ID_FINCT2` | Nível 2            |                                |
| `ID_FINCT3` | Nível 3            |                                |
| `ID_FINCT4` | Nível 4            |                                |
| `ID_FINCT5` | Nível 5            |                                |
| `ID_FINCT6` | Nível 6            |                                |
| `NROCTB`    | Número contábil    |                                |

---

## Audit / Timestamps

| Column      | Display Label                  | Notes                               |
|-------------|--------------------------------|-------------------------------------|
| `DCACLI`    | Data do cadastro               | Registration date                   |
| `CODUSU`    | Usuário responsável            | FK to user who registered           |
| `DTEATU`    | Data da última atualização     |                                     |
| `HREATU`    | Horário da última atualização  |                                     |
| `USUATU`    | Usuário da última atualização  |                                     |
| `CRE_DEV_USU` | Usuário crédito/devolução    |                                     |
| `CRE_DEV_DTE` | Data crédito/devolução       |                                     |

---

## Address Type Summary

The four address types share the same column structure, distinguished by a letter in position 3 of the column name:

| Suffix letter | Address type | Alias used in queries |
|---------------|--------------|-----------------------|
| `F`           | Faturamento  | `'FATURAMENTO'`       |
| `C`           | Cobrança     | `'COBRANCA'`          |
| `A`           | Compras      | `'COMPRAS'`           |
| `E`           | Entrega      | `'ENTREGA'`           |

Example mapping pattern already used in `CustomerAddressQueryRepository`:

```sql
-- Billing (F)
cefcli as cep, enfcli as endereco, nrfcli as numero, rffcli as referencia,
bafcli as bairro, cifcli as cidade, uffcli as uf, tefcli as telefone,
pt1cli || '-' || fo1cli as telefoneContato, cofcli as complemento,
pc1cli || '-' || fc1cli as fax

-- Collection (C) — swap F→C in column names
-- Purchase (A) — swap F→A
-- Delivery (E) — swap F→E
```

---

## Related Lookup Tables

| FK column pattern | Lookup table  | Join field          | Label field   |
|-------------------|---------------|---------------------|---------------|
| `ID_FIN*FF`       | city/UF table | `ID_FIN...`         | `NOMCIE`      |
| `CODVEN`          | sales table   | `CODVEN`            | `NOMVEN`      |
| `CODTRA`          | carrier table | `CODTRA`            | `NOMTRA`      |
| `CODGCL`          | group table   | `CODGCL`            | `NOMGCL`      |
| `CODCCL`          | category table| `CODCCL`            | `NOMCCL`      |
| `CODTCL`          | type table    | `CODTCL`            | `NOMTCL`      |
| `CODBAN`          | bank table    | `CODBAN`            | `NOMBAN`      |
| `REGTRB`          | `FINREGTRIB`  | `NUMREGTRIB = REGTRB`| `NOMREGTRIB` |

---

*Source: reverse-engineered from `ManCli.dfm` (Delphi form — mancli customer management screen) in the legacy emerion-financeiro Delphi project.*
