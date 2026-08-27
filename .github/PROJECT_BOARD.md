# Profitability Metrics & BI Dashboard - Project Board

This document is the source of truth referenced by the GitHub issues for the 3-sprint rollout that closes the API data gap for dashboard KPIs.

## Sprint 1 - Quick Wins (+15% coverage)

### [1.1] Extract Delivery Dates (PEDRES + ESTTRS)
- Add `dataProgramada`, `dataEntrega`, `dataFaturamento` to `CustomerOrderDTO`.
- Update projection + mapper to expose `PEDRES.dtprog`, `PEDRES.dtdtec`, `PEDRES.dtfin`.
- Keep business date semantics (`LocalDate` in DTO).
- Acceptance: dates visible in `/orders` payload for sampled closed orders.

### [1.2] Add Order Status Flags to DTO
- Expose lifecycle-related flags currently not mapped (`flgres`, `flgfin`, `flgpro`, `flgfec`).
- Document each flag meaning based on metadata before API exposure.
- Acceptance: all flags returned and non-null where source data is populated.

### [1.3] Expose Customer Aging Data (FINCRP)
- New DTO/projection/mapper for receivables aging fields.
- New endpoint: `GET /customers/{customerId}/receivables`.
- Acceptance: due date, overdue days, paid/open status, and value returned.

### [1.4] Validate PEDRES -> FINCOM Relationship (Commission)
- Identify and document relational path and cardinality for commissions.
- Produce implementation notes for Sprint 2.4.
- Acceptance: relationship documented with SQL evidence.

## Sprint 2 - Core Analytics Layer (+40% coverage)

### [2.1] Implement Product Cost Extraction (ESTCST -> PEDRE2) **CRITICAL**
- Join order items with cost source using product identity keys.
- Add `costPrice` and `costTotal` to order item read path.
- Acceptance: item cost fields are available and consistent with source values.

### [2.2] Create Sales Funnel Endpoint (Quotation -> Order -> Invoice) **CRITICAL**
- New endpoint: `GET /metrics/sales-funnel`.
- Aggregate per stage and compute conversion rates.
- Dependencies: **2.1**.
- Acceptance: stage totals and conversion rates coherent by date range.

### [2.3] Implement Aging Report (FINCRP Aggregation)
- New endpoint: `GET /metrics/aging-report?asOf=YYYY-MM-DD`.
- Aggregate in bands: 0-30, 31-60, 61-90, 90+.
- Dependencies: **1.3**.
- Acceptance: all bands returned and total equals AR sum.

### [2.4] Add Commission Data (FINCOM -> Seller)
- Implement commission extraction after relationship validation.
- New endpoint: `GET /sellers/{sellerId}/commissions`.
- Dependencies: **1.4**.
- Acceptance: seller commission totals match source records.

## Sprint 3 - Dashboard & Advanced Analytics (+30% coverage)

### [3.1] Implement Profitability Metrics (Markup, Margin, ABC) **CRITICAL**
- New endpoint: `GET /metrics/profitability`.
- Metrics: markup, margin, profit, ABC segmentation.
- Dependencies: **2.1**.
- Acceptance: metric ranges sane and ABC buckets sum to 100%.

### [3.2] Build Predictive Cash Flow (FINCRP + Payment Patterns)
- New endpoint: `GET /metrics/cash-flow-forecast`.
- Scenarios: optimistic, base, pessimistic.
- Dependencies: **2.3**.
- Acceptance: 30/60/90-day forecasts generated with measurable error tracking.

### [3.3] Implement Seller Performance Dashboard (Multi-Metric)
- New endpoint: `GET /metrics/seller-performance`.
- Metrics: sales, order count, average order value, profitability, commissions.
- Dependencies: **2.1**, **2.4**.
- Acceptance: ranked seller view with consistent aggregates.

### [3.4] Create Customer Lifetime Value (CLV) Dashboard
- New endpoint: `GET /metrics/customer-ltv`.
- CLV + churn/at-risk segmentation with trend context.
- Dependencies: **2.1**.
- Acceptance: customer segments and CLV values computed consistently.

## Dependency Map (high level)

- `2.1` blocks `2.2`, `3.1`, `3.3`, `3.4`
- `1.3` blocks `2.3`, then `3.2`
- `1.4` blocks `2.4`, then part of `3.3`

## Notes

- Use `.github/database-metadata/` as authoritative metadata source.
- Do not infer undocumented status/business flags.
- Keep tenant identity and customer identity fields explicit in payloads.
