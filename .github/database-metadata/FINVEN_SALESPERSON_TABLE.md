# FINVEN — Salesperson (Vendedor) Table Reference

Field mappings derived from `ManVen.dfm` (the main salesperson management form in the legacy Delphi application).

> **DataSource**: `DsVen` (main vendedor dataset).  
> All columns end in `VEN` unless they are FK lookup columns from related tables.

---

## Core Identity

| Column    | Display Label             | Notes                                              |
|-----------|---------------------------|----------------------------------------------------|
| `CODVEN`  | Código                    | Salesperson PK; used as `customerExternalId` reference in `fincli.codven` |
| `NOMVEN`  | Nome/Razão social         | Legal name; TRIM before use                        |
| `CGCVEN`  | CNPJ/CPF                  | Tax document → maps to `cpfCnpj` equivalent        |
| `APEVEN`  | Apelido/Fantasia          | Trade name / nickname                              |
| `INSVEN`  | Inscrição Estadual/RG     | State tax registration or ID                       |
| `FLGATI`  | Ativo?                    | Active flag (`S`/`N` or `1`/`0`)                   |
| `DCAVEN`  | Cadastrado em             | Registration date                                  |

---

## Commercial Classification

| Column    | Display Label  | Related name column | Notes                    |
|-----------|----------------|----------------------|--------------------------|
| `CODGVE`  | Grupo          | `NOMGVE`             | Salesperson group FK     |
| `CODCVE`  | Categoria      | `NOMCVE`             | Salesperson category FK  |
| `CODTVE`  | Tipo           | `NOMTVE`             | Salesperson type FK      |
| `CODCOM`  | Comissão       | `PERCOM`             | Commission code FK; `PERCOM` = commission % |
| `CODBAN`  | Banco          | `NOMBAN`             | Preferred bank FK        |
| `CODAGB`  | Agência        | —                    | Bank agency code         |
| `CODCNB`  | No. da conta   | `NOMCNB`/`NOMTIT`    | Bank account number; `NOMCNB`/`ABRTIT`/`NOMTIT` = account holder |

---

## Address

| Column    | Display Label         | Notes                                     |
|-----------|-----------------------|-------------------------------------------|
| `CEPVEN`  | Cep                   | Postal code                               |
| `ENDVEN`  | Endereço              | Street name                               |
| `TENVEN`  | Tipo de logradouro    | Street type (Rua, Av, etc.)               |
| `NUMVEN`  | No.                   | Street number                             |
| `REFVEN`  | Complemento           | Complement / reference                    |
| `BAIVEN`  | Bairro                | Neighbourhood                             |
| `CIDVEN`  | Cidade                | City name (text; `NOMCIE` for NFe lookup) |
| `SIGUFE`  | UF                    | State abbreviation (shared lookup field)  |

---

## Contact / Digital

| Column    | Display Label | Notes                          |
|-----------|---------------|--------------------------------|
| `PRFVEN`  | Prefixo       | Phone area code / prefix       |
| `FONVEN`  | Telefone      | Phone number                   |
| `PFAVEN`  | Prefixo (fax) | Fax area code / prefix         |
| `FAXVEN`  | Fax           | Fax number                     |
| `PRCVEN`  | Prefixo (cel) | Mobile area code / prefix      |
| `CELVEN`  | Celular       | Mobile phone                   |
| `EMAVEN`  | E-mail        | Email address                  |
| `WEBVEN`  | Web-site      | Website URL                    |

---

## Fiscal / NF-e Lookup

| Column       | Display Label   | Notes                                         |
|--------------|-----------------|-----------------------------------------------|
| `ID_FINUFE`  | UF (NFe)        | FK to UF lookup for tax documents             |
| `NOMUFE`     | UF name         | Resolved from `ID_FINUFE` lookup              |
| `SIGNFE`     | Sigla NFe       | State abbreviation for NF-e                   |
| `ID_FINCIE`  | Município (NFe) | FK to city lookup for tax documents           |
| `NOMCIE`     | Município (NFe) | Resolved name (DataSource: `DsFinCie`)        |
| `ID_FINPAI`  | País            | FK to country lookup                          |
| `NOMPAI`     | País name       | Resolved from `ID_FINPAI` lookup              |

---

## Performance / Goals (auxiliary columns)

| Column    | Meaning                         |
|-----------|---------------------------------|
| `SLDVEN`  | Saldo vendedor (balance)        |
| `METREP`  | Meta de representação (target)  |
| `QTDREP`  | Quantidade de representação     |
| `SEQREP`  | Sequência representação         |
| `NOMRES`  | Nome responsável                |
| `CODCLP`  | Código classe de preço (price class FK) |
| `FLGTRG`  | Flag targeting / trigger        |

---

## Report / Print Aliases

These appear as derived or display fields in report components (`CGCCPF`, `ENDERECO`, `TELEFONE`) — they are **not stored columns** but computed display fields for print:

| Alias      | Resolves from     |
|------------|-------------------|
| `CGCCPF`   | `CGCVEN`          |
| `ENDERECO` | `ENDVEN` + number |
| `TELEFONE` | `FONVEN`          |

---

## FK Lookup Summary

| FK column   | Points to        | Name column  |
|-------------|------------------|--------------|
| `CODGVE`    | salesperson group| `NOMGVE`     |
| `CODCVE`    | salesperson category | `NOMCVE` |
| `CODTVE`    | salesperson type | `NOMTVE`     |
| `CODCOM`    | commission table | `PERCOM` (%)  |
| `CODBAN`    | bank table       | `NOMBAN`     |
| `ID_FINUFE` | UF lookup        | `NOMUFE`     |
| `ID_FINCIE` | city lookup      | `NOMCIE`     |
| `ID_FINPAI` | country lookup   | `NOMPAI`     |

---

## Relationship with fincli

`fincli.CODVEN` is a FK to `finven.CODVEN`.  
When joining for salesperson name: `JOIN finven ON finven.codven = fincli.codven`.

---

## Complete Field List (all columns found)

```
CODVEN  NOMVEN  CGCVEN  APEVEN  INSVEN  FLGATI  DCAVEN
CODGVE  CODCVE  CODTVE  CODCOM  CODBAN  CODAGB  CODCNB  NOMCNB  NOMTIT  ABRTIT
CEPVEN  ENDVEN  TENVEN  NUMVEN  REFVEN  BAIVEN  CIDVEN  SIGUFE
PRFVEN  FONVEN  PFAVEN  FAXVEN  PRCVEN  CELVEN  EMAVEN  WEBVEN
ID_FINUFE  ID_FINCIE  ID_FINPAI  NOMCIE  NOMUFE  NOMPAI  SIGNFE
SLDVEN  METREP  QTDREP  SEQREP  NOMRES  CODCLP  FLGTRG  PERCOM
```

---

*Source: reverse-engineered from `ManVen.dfm` (text-format Delphi form — salesperson management screen) in the legacy emerion-financeiro Delphi project.*
