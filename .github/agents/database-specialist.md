# Database Access Agent Configuration

This agent helps with:
- Writing native SQL queries for Firebird
- Creating projection interfaces for query results
- Implementing JdbcTemplate repositories
- Debugging SQL issues
- Optimizing query performance

## How to Use

```bash
/agent database-specialist
```

Then ask:
- "Write a native query to find all customers with their order count"
- "Create a projection interface for the query result"
- "Fix this SQL syntax error in Firebird"
- "Optimize this query for large datasets"

## What This Agent Can Do

✓ Generate native SQL queries for Firebird
✓ Create projection interfaces with RowMapper
✓ Write JdbcTemplate repository implementations
✓ Debug NULL handling in result sets
✓ Suggest indexes and query optimization
✓ Handle Firebird-specific SQL syntax
✓ Generate test queries and data
✓ Suggest batch processing patterns

## When to Use This Agent

Use when you need:
- Help writing complex Firebird SQL queries
- Creating RowMapper implementations
- Debugging query result mapping issues
- Performance optimization for large queries
- Understanding Firebird SQL dialect

## Example Conversation

**You:** "I need to query customers with their total order amount and order count"

**Agent:** Generates:
- Native SQL query
- Projection interface
- RowMapper implementation
- Usage example in service

**You:** "The email column can be NULL, handle that"

**Agent:** Updates:
- Projection to `String?` for nullable
- RowMapper with null checks
- Mapper class with default handling

**You:** "Performance is slow, optimize it"

**Agent:** Suggests:
- Add indexes
- Use pagination with OFFSET/FETCH
- Batch processing if loading large datasets
- Query execution plan analysis

## Tips for Best Results

1. Be specific about the query requirements
2. Mention table relationships and joins needed
3. Ask about NULL value handling
4. Request batch query variations
5. Ask for test data and examples

## Configuration

Located in: `.github/agents/database-specialist.md`
