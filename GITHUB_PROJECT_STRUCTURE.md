# GitHub Project Structure - Profitability Metrics

## 📊 Visual Overview

```
┌─────────────────────────────────────────────────────────────────┐
│  Profitability Metrics & BI Dashboard                           │
│  Technical Gap Analysis & Implementation                         │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┬──────────────────┬──────────────────────────┐
│                  │                  │                          │
│  Sprint 1        │  Sprint 2        │  Sprint 3                │
│  Quick Wins      │  Core Analytics  │  Dashboard               │
│  +15% Coverage   │  +40% Coverage   │  +30% Coverage           │
│  2 hours         │  9.5 hours       │  11.5 hours              │
│  4 issues        │  4 issues        │  4 issues                │
│                  │                  │                          │
└──────────────────┴──────────────────┴──────────────────────────┘
```

---

## 🎯 Sprint 1: Quick Wins (+15% Coverage)

**Objetivo**: Extrair dados rapidamente com mínimo esforço, ganhar 15% de cobertura

**Status**: 4/4 Planned

### Issues

#### [1.1] Extract Delivery Dates (PEDRES + ESTTRS)
- **Effort**: 30 min ⏱️
- **Priority**: 🔴 HIGH
- **Labels**: `data-extraction`, `sprint-1`, `quick-win`
- **Blocker**: None
- **Depends on**: None
- **What**: Add delivery dates (dataProgramada, dataEntrega, dataFaturamento) to API
- **Where**: CustomerOrderDTO, CustomerOrderProjection, CustomerOrderMapper

#### [1.2] Add Order Status Flags to DTO
- **Effort**: 20 min ⏱️
- **Priority**: 🟡 MEDIUM
- **Labels**: `data-extraction`, `sprint-1`, `quick-win`
- **Blocker**: None
- **Depends on**: None
- **What**: Map order status flags (flgres, flgfin, flgpro, flgfec)
- **Where**: CustomerOrderDTO + metadata docs

#### [1.3] Expose Customer Aging Data (FINCRP)
- **Effort**: 45 min ⏱️
- **Priority**: 🔴 HIGH
- **Labels**: `data-extraction`, `sprint-1`, `new-endpoint`
- **Blocker**: None
- **Depends on**: None
- **What**: Create new endpoint for receivables aging analysis
- **Where**: New DTO + new endpoint: GET /customers/{id}/receivables

#### [1.4] Validate Relationship: PEDRES → FINCOM (Commission)
- **Effort**: 30 min ⏱️
- **Priority**: 🟡 MEDIUM
- **Labels**: `investigation`, `sprint-1`, `blocker-research`
- **Blocker**: None
- **Depends on**: None
- **What**: Identify FK path for commissions, document for Sprint 2.4
- **Output**: Design document + SQL query example

**Total Sprint 1**: ~2 hours | **Total Issues**: 4 | **Coverage**: 65% → 80% (+15%)

---

## 🔥 Sprint 2: Core Analytics Layer (+40% Coverage)

**Objetivo**: Implementar camada de analytics com dados críticos de custo e agregações

**Status**: 4/4 Planned

### Dependency Graph
```
[2.1] Implement Product Costs (CRITICAL)
  ├→ [2.2] Create Sales Funnel (blocks 3.1, 3.3)
  ├→ [3.1] Profitability Metrics
  └→ [3.3] Seller Performance

[1.3] Expose Aging Data
  └→ [2.3] Aging Report (blocks 3.2)
      └→ [3.2] Cash Flow Forecast

[1.4] Validate FINCOM
  └→ [2.4] Add Commission Data (blocks 3.3)
```

### Issues

#### [2.1] Implement Product Cost Extraction (ESTCST → PEDRE2)
- **Effort**: 2h ⏱️
- **Priority**: 🔴 CRITICAL
- **Labels**: `analytics`, `sprint-2`, `critical`
- **Blocker**: BLOCKS [2.2], [3.1], [3.3]
- **Depends on**: None
- **What**: Extract costs from ESTCST and join with order items
- **Acceptance**: 
  - GET /orders/{id}/items returns costPrice + costTotal
  - Cost values match ESTCST (manual validation)
  - No negative costs
  - Markup calculation works

#### [2.2] Create Sales Funnel Endpoint (Quotation → Order → Invoice)
- **Effort**: 3h ⏱️
- **Priority**: 🔴 CRITICAL
- **Labels**: `analytics`, `sprint-2`, `critical`
- **Blocker**: None
- **Depends on**: [2.1]
- **What**: Aggregated funnel view with conversion rates
- **Endpoint**: GET /metrics/sales-funnel?dateStart=X&dateEnd=Y&groupBy=week|month
- **Metrics**: stage count, totalValue, conversionRate %

#### [2.3] Implement Aging Report (FINCRP Aggregation)
- **Effort**: 2h ⏱️
- **Priority**: 🟠 HIGH
- **Labels**: `analytics`, `sprint-2`, `high`
- **Blocker**: None
- **Depends on**: [1.3]
- **What**: Aggregate receivables into aging bands
- **Endpoint**: GET /metrics/aging-report?asOf=YYYY-MM-DD
- **Bands**: 0-30d, 31-60d, 61-90d, 90+d

#### [2.4] Add Commission Data (FINCOM → Seller)
- **Effort**: 2.5h ⏱️
- **Priority**: 🟠 HIGH
- **Labels**: `analytics`, `sprint-2`, `high`
- **Blocker**: None
- **Depends on**: [1.4]
- **What**: Implement commission DTO and endpoint
- **Endpoint**: GET /sellers/{id}/commissions?dateRange=X
- **Validation**: Sum commissions match FINCOM

**Total Sprint 2**: ~9.5 hours | **Total Issues**: 4 | **Coverage**: 80% → 95% (+40%)

---

## 🚀 Sprint 3: Dashboard & Advanced Analytics (+30% Coverage)

**Objetivo**: Implementar métricas derivadas e dashboards finais para React

**Status**: 4/4 Planned

### Dependency Graph
```
[2.1] Product Costs
  ├→ [3.1] Profitability Metrics
  ├→ [3.3] Seller Performance
  └→ [3.4] Customer Lifetime Value

[2.3] Aging Report
  └→ [3.2] Cash Flow Forecast

[2.4] Commission Data
  └→ [3.3] Seller Performance
```

### Issues

#### [3.1] Implement Profitability Metrics (Markup, Margin, ABC)
- **Effort**: 3h ⏱️
- **Priority**: 🔴 CRITICAL
- **Labels**: `analytics`, `sprint-3`, `critical`
- **Blocker**: None
- **Depends on**: [2.1]
- **What**: Derive Markup Real, Margin %, ABC Curve
- **Endpoint**: GET /metrics/profitability?groupBy=product|customer|seller
- **Formulas**:
  - Markup = (salesPrice - cost) / cost
  - Margin = (salesPrice - cost) / salesPrice
  - Profit = totalSales - totalCost
- **ABC**: Top 20%, Middle 30%, Bottom 50%

#### [3.2] Build Predictive Cash Flow (FINCRP + Payment Patterns)
- **Effort**: 3.5h ⏱️
- **Priority**: 🟠 HIGH
- **Labels**: `analytics`, `sprint-3`, `high`
- **Blocker**: None
- **Depends on**: [2.3]
- **What**: Forecast cash inflows based on historical patterns
- **Endpoint**: GET /metrics/cash-flow-forecast?horizon=days
- **Scenarios**: Optimistic, Base, Pessimistic
- **Validation**: Historical accuracy < 20% error

#### [3.3] Implement Seller Performance Dashboard (Multi-Metric)
- **Effort**: 2.5h ⏱️
- **Priority**: 🟠 HIGH
- **Labels**: `analytics`, `sprint-3`, `high`
- **Blocker**: None
- **Depends on**: [2.1], [2.4]
- **What**: Aggregated seller KPIs with rankings
- **Endpoint**: GET /metrics/seller-performance?period=month&sortBy=sales
- **Metrics**:
  - totalSales = SUM(PEDRES.vlrtot)
  - orderCount = COUNT(PEDRES)
  - profitabilityIndex = SUM(profit) / SUM(sales)
  - commissionTotal from [2.4]
- **Rankings**: Sales, Profitability, Volume
- **Trends**: YoY, Last 3 months

#### [3.4] Create Customer Lifetime Value (CLV) Dashboard
- **Effort**: 2.5h ⏱️
- **Priority**: 🟡 MEDIUM
- **Labels**: `analytics`, `sprint-3`, `medium`
- **Blocker**: None
- **Depends on**: [2.1]
- **What**: Customer segmentation and lifetime value analysis
- **Endpoint**: GET /metrics/customer-ltv?segment=all|active|churn|atrisk&sortBy=value
- **Segments**: High-value, Medium, Low, Churned
- **Flags**:
  - Churn: No orders in 90 days
  - AtRisk: Last order > 45 days, high historical usage
- **Trends**: Revenue/count per quarter

**Total Sprint 3**: ~11.5 hours | **Total Issues**: 4 | **Coverage**: 95% → 100% (+30%)

---

## 📈 Overall Roadmap

```
Week 1        Week 2        Week 3        Week 4
├─ Sprint 1 ──┤
    2h
                ├─ Sprint 2 ──────────┤
                    9.5h
                                       ├─ Sprint 3 ─────┤
                                           11.5h

Coverage:
65% ─[+15%]→ 80% ─[+40%]→ 95% ─[+30%]→ 100%
```

---

## 🏷️ Labels Distribution

| Label | Count | Purpose |
|-------|-------|---------|
| `sprint-1` | 4 | Sprint 1 issues |
| `sprint-2` | 4 | Sprint 2 issues |
| `sprint-3` | 4 | Sprint 3 issues |
| `data-extraction` | 3 | Firebird data extraction tasks |
| `new-endpoint` | 1 | New REST API endpoint required |
| `analytics` | 8 | Analytics layer tasks |
| `investigation` | 1 | Research/discovery task |
| `quick-win` | 3 | High ROI, low effort |
| `critical` | 4 | Blocking other tasks |
| `high` | 4 | Important, high priority |
| `medium` | 0 | Standard priority |
| `blocker-research` | 1 | Blocks other tasks until resolved |

---

## 🔗 Milestones

1. **Sprint 1: Quick Wins (+15% Coverage)**
   - Start: Immediately
   - Duration: ~1 day
   - Goal: Gain quick momentum, +15% coverage
   - Effort: 2 hours total

2. **Sprint 2: Core Analytics Layer (+40% Coverage)**
   - Start: After Sprint 1
   - Duration: ~1-2 weeks
   - Goal: Implement critical analytics foundation
   - Effort: 9.5 hours total
   - Dependencies: All Sprint 1 issues must be DONE

3. **Sprint 3: Dashboard & Advanced Analytics (+30% Coverage)**
   - Start: After Sprint 2 (can overlap partially)
   - Duration: ~2-3 weeks
   - Goal: Complete analytics layer and dashboards
   - Effort: 11.5 hours total
   - Dependencies: All Sprint 2 issues must be DONE

---

## ✅ Completion Criteria

**Project is 100% complete when:**
- ✅ All 12 issues are CLOSED
- ✅ All 4 new endpoints are tested and responding
- ✅ 100% of required data gap is closed
- ✅ React dashboard is consuming new metrics
- ✅ All acceptance criteria are met

---

## 📞 Getting Help

**Issue has a blocker?** 
- Comment with details
- Tag code review team
- Reference related issues using `#issue-number`

**Need clarification on requirements?**
- Check `.github/PROJECT_BOARD.md` for full specs
- See `.github/database-metadata/` for Firebird structure
- Run SQL validation scripts from `.github/PROFITABILITY_PROJECT_INIT.md`

**Want to track progress?**
- Use GitHub Projects board with custom fields
- Filter by Sprint label
- Use Milestones for timeline tracking

---

**Project Created**: 2024
**Total Issues**: 12
**Total Effort**: ~23 hours
**Expected Coverage Gain**: 35% (65% → 100%)
