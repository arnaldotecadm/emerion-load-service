# React App → emerion-load-service API Contracts

This document shows the **exact JSON shapes** the React app expects from each API endpoint, derived from the actual TypeScript types in `my-react-app/src`.

---

## 1. Customers API (Already Implemented ✓)

**Endpoint**: `GET /customer/all?page=0&size=10`  
**Response Type**: `CustomerPage`

```typescript
// Response
{
  "content": [
    {
      "id": 1,
      "nomeFantasia": "Alimentos S/A",
      "razaoSocial": "Alimentos Comercial Ltda.",
      "cpfCnpj": "12345678000190",
      "inscricaoEstadual": "123.456.789.012",
      "regimeTributario": "SIMPLES",
      "bloqueado": false
    },
    // ... more customers
  ],
  "totalElements": 1284,
  "totalPages": 129,
  "number": 0,
  "size": 10,
  "numberOfElements": 10,
  "first": true,
  "last": false,
  "empty": false
}
```

**Source**: `src/features/dashboard/types/customer.ts`  
**Firebird Query**: See `CustomerRepository.getAllCustomers()`

---

## 2. Customer Detail API (Already Implemented ✓)

**Endpoint**: `GET /customer/{id}`  
**Response Type**: `Customer`

```typescript
{
  "id": 1,
  "nomeFantasia": "Alimentos S/A",
  "razaoSocial": "Alimentos Comercial Ltda.",
  "cpfCnpj": "12345678000190",
  "inscricaoEstadual": "123.456.789.012",
  "regimeTributario": "SIMPLES",
  "bloqueado": false
}
```

**Firebird Query**: See `CustomerRepository.getCustomerByCodCli()`

---

## 3. Orders API (NEEDED - currently mocked with 1.2k orders)

**Endpoint**: `GET /order/all?page=0&size=10`  
**Response Type**: `OrderPage`

```typescript
{
  "content": [
    {
      "id": 12345,
      "codCli": 1,                          // Customer ID
      "nomeCliente": "Alimentos S/A",       // Computed: join FINCLI
      "dataOrdem": "2023-11-01T10:30:00",   // DTOPED
      "dataEntrega": "2023-11-15T23:59:59", // DTEENT
      "valorTotal": 18200.50,               // TOTPED
      "status": "Faturado",                 // Computed: if exists in FATPED
      "diasAtrasado": 5,                    // Computed: DATEDIFF(DTEENT, TODAY)
      "itens": [
        {
          "sequencia": 1,
          "codProduto": "PR001",
          "quantidade": 10.0000,
          "precoUnitario": 1500.00,
          "valorTotal": 15000.00
        },
        {
          "sequencia": 2,
          "codProduto": "PR002",
          "quantidade": 2.0000,
          "precoUnitario": 1100.25,
          "valorTotal": 2200.50
        }
      ]
    },
    // ... more orders
  ],
  "totalElements": 1200,
  "totalPages": 120,
  "number": 0,
  "size": 10,
  "numberOfElements": 10,
  "first": true,
  "last": false,
  "empty": false
}
```

**Used by dashboard for**:
- "VENDAS TOTAIS" KPI → sum `valorTotal`
- "TOTAL DE ORÇAMENTOS" KPI → count of orders
- "TICKET MÉDIO" KPI → avg `valorTotal`
- "PEDIDOS ATRASADOS" KPI → count where `diasAtrasado > 0`
- "Tendência de Vendas" chart → group by date, sum `valorTotal`
- "Detalhamento por Período" table → all fields

---

## 4. Order Detail API (NEEDED)

**Endpoint**: `GET /order/{id}`  
**Response Type**: `Order` (same as above, just one record)

```typescript
{
  "id": 12345,
  "codCli": 1,
  "nomeCliente": "Alimentos S/A",
  "dataOrdem": "2023-11-01T10:30:00",
  "dataEntrega": "2023-11-15T23:59:59",
  "valorTotal": 18200.50,
  "status": "Faturado",
  "diasAtrasado": 5,
  "itens": [
    {
      "sequencia": 1,
      "codProduto": "PR001",
      "quantidade": 10.0000,
      "precoUnitario": 1500.00,
      "valorTotal": 15000.00
    },
    // ... more items
  ]
}
```

---

## 5. Customer Order History (NEEDED - shown in customer detail mock)

**Endpoint**: `GET /customer/{id}/orders`  
**Response Type**: `OrderHistory[]`

```typescript
[
  {
    "dataOrdem": "2023-11-01",
    "valorTotal": 18200.50,
    "status": "Faturado"
  },
  {
    "dataOrdem": "2023-10-12",
    "valorTotal": 22450.75,
    "status": "Faturado"
  },
  {
    "dataOrdem": "2023-09-28",
    "valorTotal": 9800.00,
    "status": "Faturado"
  }
]
```

**Used by**: Customer detail page → "Pedidos Recentes" (recent orders section)

---

## 6. Top Customers by Revenue (NEEDED - shown in KpiRow/TopClientsTable)

**Endpoint**: `GET /customer/ranking/revenue?limit=5`  
**Response Type**: `CustomerRanking[]`

```typescript
[
  {
    "rank": 1,
    "id": 1,
    "nome": "Alimentos S/A",
    "receitaMensal": 142500.00,
    "tendenciaPct": 12.0,    // % change vs prev month
    "vip": true
  },
  {
    "rank": 2,
    "id": 5,
    "nome": "Logística Luz",
    "receitaMensal": 118900.00,
    "tendenciaPct": 8.0,
    "vip": true
  },
  // ... more
]
```

**Used by**: 
- "Top Clientes por Receita" table (KPI drill-down)
- Dashboard KPI card "LIMITE DE CRÉDITO" (top 5)

---

## 7. Sales by Period (NEEDED - shown in VendasTotaisPage chart/table)

**Endpoint**: `GET /sales/summary?granularity=mes&year=2023`  
**Response Type**: `SalesSummary[]`

```typescript
[
  {
    "periodo": "Jan",
    "valor": 312400.00,
    "qtdePedidos": 1120,
    "valorAnterior": 298100.00,     // Previous month
    "variacao": 4.8                 // % change
  },
  {
    "periodo": "Fev",
    "valor": 328900.00,
    "qtdePedidos": 1168,
    "valorAnterior": 315600.00,
    "variacao": 4.2
  },
  // ... Jan-Dec
]
```

**Alternative (granularity=dia)**: Same structure but for each day over 7 days  
**Alternative (granularity=semana)**: Same structure but for each week over 8 weeks

**Used by**: 
- "Vendas Totais" detail page → chart, stats table
- Dashboard KPI "VENDAS TOTAIS"

---

## 8. Products by Category Mix (NEEDED - shown in ProductMixPanel donut)

**Endpoint**: `GET /product/mix`  
**Response Type**: `ProductMix[]`

```typescript
[
  {
    "categoria": "Software",
    "percentual": 45,
    "valor": 1931000.00  // Total revenue for this category
  },
  {
    "categoria": "Serviços",
    "percentual": 25,
    "valor": 1069000.00
  },
  {
    "categoria": "Hardware",
    "percentual": 20,
    "valor": 855000.00
  },
  {
    "categoria": "Outros",
    "percentual": 10,
    "valor": 427500.00
  }
]
```

**Used by**: 
- "Mix de Produtos" donut chart (dashboard overview)
- Sum of percentuals must = 100

---

## 9. Sales Channel Breakdown (NEEDED - shown in SalesChannelPanel)

**Endpoint**: `GET /sales/channel`  
**Response Type**: `SalesChannel[]`

```typescript
[
  {
    "canal": "E-commerce",
    "percentual": 42,
    "valor": 1797000.00
  },
  {
    "canal": "Vendedor Externo",
    "percentual": 28,
    "valor": 1197000.00
  },
  {
    "canal": "Televendas",
    "percentual": 18,
    "valor": 769500.00
  },
  {
    "canal": "Parceiros",
    "percentual": 12,
    "valor": 513000.00
  }
]
```

**Used by**: "Vendas por Canal" bar chart (Vendas Totais detail page)

---

## 10. At-Risk Customers (NEEDED - shown in DelinquencyRiskPanel)

**Endpoint**: `GET /customer/risk?limit=4`  
**Response Type**: `RiskCustomer[]`

```typescript
[
  {
    "id": 14,
    "nome": "Indústrias Têxtil SA",
    "diasAtraso": 33,         // Days overdue
    "nivelRisco": "Alto"      // "Alto" (30+) or "Médio" (<30)
  },
  {
    "id": 10,
    "nome": "Papelaria Central",
    "diasAtraso": 58,
    "nivelRisco": "Alto"
  },
  {
    "id": 12,
    "nome": "Construtora Norte",
    "diasAtraso": 15,
    "nivelRisco": "Médio"
  },
  {
    "id": 8,
    "nome": "Mercado do Povo",
    "diasAtraso": 25,
    "nivelRisco": "Médio"
  }
]
```

**Used by**: "Risco de Inadimplência" panel (customer overview)  
**Computed from**: FINCONTA or similar accounts-receivable aged balance

---

## 11. Credit Utilization (NEEDED - shown in CreditUtilizationPanel)

**Endpoint**: `GET /customer/credit-usage?limit=5`  
**Response Type**: `CreditUsage[]`

```typescript
[
  {
    "id": 12,
    "nome": "Indústrias Têxtil SA",
    "limiteCreditoAtual": 400000.00,
    "utilizadoPct": 98,       // % of limit in use
    "limiteFinal": 1800000.00 // Total authorized limit
  },
  {
    "id": 13,
    "nome": "Papelaria Central",
    "limiteCreditoAtual": 70000.00,
    "utilizadoPct": 99,
    "limiteFinal": 300000.00
  },
  // ... more
]
```

**Used by**: "Limite de Crédito por Cliente" panel (customer overview)  
**Computed from**: Customer credit limit + current outstanding balance

---

## Implementation Roadmap

| Phase | Endpoint | Status | Tables Needed |
|-------|----------|--------|---------------|
| **Live** | `GET /customer/all` | ✅ Done | FINCLI, FINREGTRIB |
| **Live** | `GET /customer/{id}` | ✅ Done | FINCLI, FINREGTRIB |
| **P1** | `GET /order/all` | 🔴 Needed | PEDRES, PEDRE2, FINCLI |
| **P1** | `GET /order/{id}` | 🔴 Needed | PEDRES, PEDRE2 |
| **P2** | `GET /customer/{id}/orders` | 🔴 Needed | PEDRES, FATPED |
| **P2** | `GET /customer/ranking/revenue` | 🔴 Needed | PEDRES, FINCLI |
| **P2** | `GET /sales/summary` | 🔴 Needed | PEDRES, PEDRE2 |
| **P3** | `GET /product/mix` | 🔴 Needed | PEDRE2, ESTPRO |
| **P3** | `GET /sales/channel` | 🔴 Needed | PEDRES (+ channel field if exists) |
| **P3** | `GET /customer/risk` | 🔴 Needed | FINCONTA (accounts receivable) |
| **P3** | `GET /customer/credit-usage` | 🔴 Needed | FINCLI (credit fields) + FINCONTA |

---

## Summary

**Minimum implementation** (P1) requires:
- 2 endpoints
- 13 Firebird fields (PEDRES: 6, PEDRE2: 7)
- 2 tables
- ~3 days with Copilot

**Full dashboard** (P1+P2+P3) requires:
- 11 endpoints
- ~40 Firebird fields
- 5-6 tables
- ~2-3 weeks incrementally

Start with **P1 (orders)** → it powers 70% of the dashboard KPIs.
