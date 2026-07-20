# Emerion Load Service - Agents Guide

## Overview

Copilot agents are specialized assistants for specific tasks. Each agent has deep knowledge of its domain and can help you work more effectively.

## Available Agents

### 1. **database-specialist** 🗄️
**For:** Writing Firebird queries, projections, and JdbcTemplate repositories

Use when:
- Writing native SQL SELECT queries
- Creating projection interfaces for results
- Implementing RowMapper for result mapping
- Debugging SQL issues
- Optimizing query performance
- Handling NULL values in queries

**Example:**
```
/agent database-specialist
"Write a native query to find customers with their total order amount and order count"
```

---

### 2. **migration-architect** 🏗️
**For:** Designing end-to-end migration workflows

Use when:
- Planning a new migration for a table
- Designing batch processing
- Implementing idempotency
- Adding error handling and retry logic
- Planning migration state tracking
- Designing resilience patterns

**Example:**
```
/agent migration-architect
"Design a migration for the ORDERS table with 500k records"
```

---

### 3. **data-mapper** 🔄
**For:** Creating mappers from projections to models

Use when:
- Creating mapper classes (manual or MapStruct)
- Handling NULL → default values
- Implementing data validation
- Normalizing/cleaning data
- Type conversions
- Data enrichment

**Example:**
```
/agent data-mapper
"Create a mapper from OrderProjection to Order model with NULL handling"
```

---

### 4. **api-integrator** 🌐
**For:** Building REST clients to send data to new API

Use when:
- Creating REST client for the new API service
- Implementing error handling and retries
- Batch sending patterns
- Health checks and monitoring
- Testing API integration
- Response validation

**Example:**
```
/agent api-integrator
"Create a REST client to send customers to the new API with retry logic"
```

---

### 5. **testing-expert** ✅
**For:** Creating tests for all components

Use when:
- Writing unit tests
- Creating integration tests with TestContainers
- Generating test data
- Testing error scenarios
- Mocking dependencies
- Improving test coverage

**Example:**
```
/agent testing-expert
"Create integration tests for the customer migration with TestContainers"
```

---

### 6. **dev-assistant** 💡
**For:** General architecture and development questions

Use when:
- General architecture guidance
- Code review suggestions
- Best practices questions
- Troubleshooting issues
- Performance optimization
- Security considerations

**Example:**
```
/agent dev-assistant
"Should I use JPA for Firebird queries? Why or why not?"
```

---

## Agent Workflow

For a typical feature/task:

```
START
  ↓
Ask dev-assistant: "How should I build this?"
  ↓
Decide architecture
  ↓
Get detailed help from specialists:
  ├─→ database-specialist (for queries)
  ├─→ data-mapper (for transformations)
  ├─→ api-integrator (for API calls)
  └─→ migration-architect (for workflow)
  ↓
Ask testing-expert: "How do I test this?"
  ↓
Ask dev-assistant: "Review my approach"
  ↓
END
```

## Example: Adding a New Migration (Order Table)

### Step 1: Plan with dev-assistant
```bash
/agent dev-assistant
"I need to migrate the ORDERS table (100k records). What's the best approach?"
```

Response includes:
- Architecture recommendations
- Task breakdown
- Technology choices

### Step 2: Design with migration-architect
```bash
/agent migration-architect
"Design migration for ORDERS table: 100k records, needs idempotency"
```

Response includes:
- Full workflow design
- Batch strategy
- Error handling
- State tracking

### Step 3: Create queries with database-specialist
```bash
/agent database-specialist
"Write a query for ORDERS with customer name and order total"
```

Response includes:
- SQL query
- Projection interface
- RowMapper implementation

### Step 4: Create mappers with data-mapper
```bash
/agent data-mapper
"Create mapper from OrderProjection to Order model"
```

Response includes:
- Mapper class
- NULL handling
- Validation logic

### Step 5: Build API integration with api-integrator
```bash
/agent api-integrator
"Create REST client to send orders to new API"
```

Response includes:
- REST client
- Error handling
- Retry logic

### Step 6: Test with testing-expert
```bash
/agent testing-expert
"Create integration tests for order migration"
```

Response includes:
- Test classes
- Test data
- Mock API setup

### Step 7: Review with dev-assistant
```bash
/agent dev-assistant
"Review my order migration implementation for best practices"
```

Response includes:
- Improvement suggestions
- Best practices
- Performance tips

---

## Agent Capabilities Matrix

| Task | database-specialist | migration-architect | data-mapper | api-integrator | testing-expert | dev-assistant |
|------|:---:|:---:|:---:|:---:|:---:|:---:|
| SQL queries | ✅ | ⚠️ | ❌ | ❌ | ❌ | ⚠️ |
| Query optimization | ✅ | ❌ | ❌ | ❌ | ❌ | ⚠️ |
| Batch processing | ⚠️ | ✅ | ❌ | ⚠️ | ❌ | ⚠️ |
| Error handling | ❌ | ✅ | ⚠️ | ✅ | ❌ | ✅ |
| Mappers | ❌ | ❌ | ✅ | ❌ | ⚠️ | ⚠️ |
| Data validation | ⚠️ | ❌ | ✅ | ❌ | ⚠️ | ⚠️ |
| REST clients | ❌ | ⚠️ | ❌ | ✅ | ❌ | ⚠️ |
| Retry logic | ❌ | ⚠️ | ❌ | ✅ | ❌ | ⚠️ |
| Unit testing | ❌ | ❌ | ⚠️ | ⚠️ | ✅ | ⚠️ |
| Integration testing | ❌ | ❌ | ❌ | ⚠️ | ✅ | ⚠️ |
| Test data | ❌ | ❌ | ❌ | ⚠️ | ✅ | ❌ |
| Architecture | ⚠️ | ✅ | ⚠️ | ⚠️ | ❌ | ✅ |
| Code review | ❌ | ❌ | ❌ | ❌ | ⚠️ | ✅ |
| Troubleshooting | ⚠️ | ⚠️ | ⚠️ | ⚠️ | ⚠️ | ✅ |

Legend: ✅ Primary expert | ⚠️ Can help | ❌ Not applicable

---

## Tips for Working with Agents

### 1. Be Specific
❌ "Generate code"  
✅ "Create a native query for CUSTOMERS table with LEFT JOIN on ORDERS"

### 2. Provide Context
❌ "Create a mapper"  
✅ "Create a mapper from FirebirdCustomerProjection to Customer model, with email defaulting to 'unknown@example.com' if NULL"

### 3. Ask for Explanations
❌ "Show me the code"  
✅ "Show me the code and explain why this approach is better than JPA"

### 4. Request Multiple Scenarios
❌ "Handle NULL values"  
✅ "Handle NULL in these 5 fields with different default strategies for each"

### 5. Ask for Tests
❌ "Create the code"  
✅ "Create the code and include unit tests for error scenarios"

### 6. Iterate
❌ Accept first answer  
✅ Ask follow-up: "Now add monitoring" → "Add retry logic" → "Test it"

---

## Agent Interactions

Some agents work well together:

**database-specialist + data-mapper**
- Query returns projection
- Mapper transforms to model
- "Show me both query and mapper"

**migration-architect + api-integrator**
- Migration design
- API integration for sending
- "Design flow including API calls"

**migration-architect + testing-expert**
- Migration design
- Test strategy
- "Design and show how to test it"

**api-integrator + testing-expert**
- API client
- Mocking for tests
- "Create client and mock setup"

---

## Real-World Examples

### Example 1: Adding Customer Migration
```
1. dev-assistant: "Architecture for customer migration?"
2. migration-architect: "Design batch migration for 50k customers"
3. database-specialist: "Query for active customers with order count"
4. data-mapper: "Map CustomerProjection to Customer model"
5. api-integrator: "Client to send customers to new API"
6. testing-expert: "Integration tests with TestContainers"
7. dev-assistant: "Review for best practices"
```

### Example 2: Optimizing Slow Migration
```
1. dev-assistant: "Why is migration slow?"
2. database-specialist: "Optimize the query"
3. migration-architect: "Adjust batch size and processing"
4. dev-assistant: "Monitor and verify improvements"
```

### Example 3: Handling New Error Scenario
```
1. dev-assistant: "How to handle API timeout?"
2. api-integrator: "Add retry with exponential backoff"
3. migration-architect: "Update workflow to handle retries"
4. testing-expert: "Test timeout scenario"
```

---

## Quick Reference

```bash
# See all agents
/agent

# Use specific agent
/agent database-specialist

# After starting an agent, just ask questions naturally:
"Write a query to find customers created in the last 30 days"
"Add NULL handling for missing email"
"Why would this fail?"
```

---

## Troubleshooting Agents

### Agent doesn't understand my question
→ Try being more specific, provide code samples, give context

### Agent provides code that doesn't compile
→ Show the error, ask agent to fix it, provide more context about your setup

### Not getting the answer you expect
→ Try a different agent, ask dev-assistant for guidance, break into smaller questions

### Want to keep conversation context
→ Use the same agent (don't switch) until you're done with that task

---

## Next Steps

1. Pick a task (e.g., "Add customer migration")
2. Start with `/agent dev-assistant` for architecture
3. Use specialists as needed
4. Ask testing-expert for test strategy
5. Review with dev-assistant before implementing

Happy coding! 🚀
