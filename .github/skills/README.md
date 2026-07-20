# Skills Reference Guide

This folder contains reusable skills and patterns for building the emerion-load-service project.

## What Are Skills?

Skills are documented best practices and templates that Copilot uses to generate code consistently with your project's patterns. They are more focused than instructions—they show exact code patterns you can copy-paste and adapt.

---

## Available Skills

### 1. `firebird-query-skill.md`
**Purpose**: Master the Query → Projection pattern for Firebird

**Contains**:
- Firebird SQL characteristics (FIRST/SKIP, External Functions, NUMERIC precision)
- Query templates (basic, paginated, joins, aggregates)
- String manipulation functions (LTRIM, RTRIM, TRIM, etc.)
- NULL handling in Firebird
- NUMERIC type mapping for BigDecimal
- Performance tips and index-friendly patterns
- Firebird external functions reference
- Common pitfalls and solutions
- Projection interface patterns
- RowMapper implementations
- Testing patterns for Firebird queries

**When to use**:
- Writing native Firebird SQL queries
- Creating projection interfaces
- Implementing JdbcTemplate repositories
- Debugging SQL issues
- Optimizing query performance

**Example**: "Create a pagination query for FINCLI table using the firebird-query-skill pattern"

---

### 2. `data-mapper-skill.md`
**Purpose**: Master the Projection → Model pattern for data transformation

**Contains**:
- Full mapping pattern flow (Query → Projection → Mapper → Model → Send)
- Manual mapper class patterns
- MapStruct mapper interface patterns
- Data validation in mappers
- Batch mapping for large datasets
- Error handling in mappers (MappingResult pattern)
- Mapper testing patterns
- Integration with services
- Type-specific mapping strategies (strings, numbers, dates, NULL handling)
- Idempotency and upsert patterns
- Unit tests for mappers

**When to use**:
- Creating mapper classes for Firebird → PostgreSQL transformation
- Handling data transformations and enrichment
- NULL value strategies
- Data validation logic
- Testing mappers
- Batch processing

**Example**: "Create a mapper from CustomerProjection to Customer model with name normalization"

---

## How Copilot Uses Skills

1. **Code Generation**: When you ask for code, Copilot references these skills for patterns
2. **Consistency**: Ensures generated code follows your established patterns
3. **Copy-Paste Ready**: Skills include actual code you can adapt
4. **Best Practices**: Embedded lessons about what works and what doesn't

---

## Skill-Based Development Workflow

### Step 1: Plan Your Migration
Use `firebird-schema-portal_2024_01.md` to identify tables and fields.

### Step 2: Write Query (Using firebird-query-skill.md)
Ask Copilot:
```
"Create a native Firebird query to fetch all active FINCLI.
Use the firebird-query-skill pattern for pagination with FIRST/SKIP.
Follow the template in .github/skills/firebird-query-skill.md"
```

### Step 3: Create Projection
Copilot will generate a projection interface matching the query result.

### Step 4: Create Mapper (Using data-mapper-skill.md)
Ask Copilot:
```
"Create a mapper from CustomerProjection to Customer model.
Include name normalization, email validation, and phone formatting.
Follow patterns from .github/skills/data-mapper-skill.md"
```

### Step 5: Create Model & Repository
Copilot generates JPA entities and repositories.

### Step 6: Test Everything
Ask Copilot:
```
"Write unit tests for the CustomerMapper using the patterns from data-mapper-skill.md"
```

---

## Template Requests (Copy-Paste)

### Firebird Query Tasks
```
"I need a native Firebird query for [TABLE].
Requirements:
- Fetch [specific fields]
- Filter by [conditions]
- Paginate with [batch size]

Use the firebird-query-skill pattern and reference database-metadata/firebird-schema-portal_2024_01.md"
```

### Mapper Tasks
```
"Create a mapper from [ProjectionClass] to [ModelClass].
Transformations needed:
- [Field1]: [transformation]
- [Field2]: [transformation]

Follow data-mapper-skill.md patterns for error handling and testing."
```

### Batch Migration Tasks
```
"Implement batch migration of [TABLE] from Firebird to PostgreSQL.
Requirements:
- Batch size: [number]
- Idempotent (can re-run safely)
- Error handling and logging

Reference firebird-query-skill.md and data-mapper-skill.md"
```

---

## Skills Integration with Instructions

| Skill | Related Instructions | Use Together For |
|-------|---------------------|------------------|
| firebird-query-skill.md | firebird-native-queries.instructions.md | Native query patterns |
| data-mapper-skill.md | firebird-postgres.instructions.md | Data transformation |
| Both | api-integration.instructions.md | Full migration flow |

---

## Adding New Skills

When you discover a new pattern that works well:

1. Create a new `.md` file in this folder
2. Name it clearly (e.g., `batch-processing-skill.md`, `error-handling-skill.md`)
3. Include:
   - Overview of what it teaches
   - Template code (copy-paste ready)
   - Common patterns and pitfalls
   - Testing examples
   - When to use it
4. Reference it in requests: "Follow patterns from batch-processing-skill.md"

---

## Common Skills You Might Create Later

- `batch-processing-skill.md` - Large dataset handling
- `error-handling-skill.md` - Centralized exception handling
- `rest-integration-skill.md` - Sending data to new API
- `transaction-management-skill.md` - Multi-DB transactions
- `logging-skill.md` - Structured logging patterns

---

## Best Practices for Using Skills

### DO:
✅ Reference skills explicitly in requests  
✅ Use skill code patterns as starting points  
✅ Update skills as you refine patterns  
✅ Create new skills for recurring patterns  
✅ Share skills with your team  

### DON'T:
❌ Ignore skills when asking Copilot for code  
❌ Create duplicate patterns  
❌ Forget to update skills when patterns change  
❌ Leave outdated skills without updating  
❌ Overcomplicate skills—keep them focused  

---

## Example: Using Skills in Action

**Your Request**:
```
I need to migrate the ESTPRO table. Create:
1. Firebird query using firebird-query-skill pattern
2. ProductProjection interface
3. ProductMapper using data-mapper-skill pattern
4. Product entity and repository
5. Tests for the mapper

Reference: .github/skills/firebird-query-skill.md
           .github/skills/data-mapper-skill.md
           .github/database-metadata/firebird-schema-portal_2024_01.md
```

**What Copilot Does**:
1. References firebird-query-skill.md for query patterns
2. References data-mapper-skill.md for mapper patterns
3. Looks up ESTPRO structure in database-metadata
4. Generates consistent, testable code
5. Includes error handling and validation

---

## Quick Checklist Before Asking Copilot

- [ ] Skills folder exists and is referenced
- [ ] Database metadata is available
- [ ] Instructions are loaded
- [ ] You're being specific about patterns to follow
- [ ] You're referencing exact files: `reference .github/skills/firebird-query-skill.md`

---

**Pro Tip**: The more specific you are about which skill to use, the better Copilot's response will be!
