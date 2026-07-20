# Migration Orchestration Agent Configuration

This agent helps with:
- Designing migration workflows
- Handling error scenarios
- Implementing idempotency
- Batch processing patterns
- Sending data to the new API service
- Monitoring migration progress

## How to Use

```bash
/agent migration-architect
```

Then ask:
- "Design a migration flow for the products table with batch processing"
- "How should I handle failed batches?"
- "Make this migration idempotent"
- "Add retry logic with exponential backoff"
- "Create a migration status dashboard"

## What This Agent Can Do

✓ Design end-to-end migration workflows
✓ Implement batch processing with offset/limit
✓ Create idempotent operations (re-runable migrations)
✓ Add error handling and retry logic
✓ Implement progress tracking and logging
✓ Design migration state persistence
✓ Create health checks and validation
✓ Implement rollback strategies

## When to Use This Agent

Use when you need:
- Architecture for a new migration service
- Error handling and recovery patterns
- Idempotency implementation
- Batch processing strategy
- Migration status tracking
- API integration patterns
- Testing strategy for migrations

## Example Conversation

**You:** "Design a migration for the Orders table with 50k records"

**Agent:** Provides:
- Full service architecture
- Query → Project → Map → Send flow
- Batch processing with configurable size
- Error handling and retries
- Progress logging
- Testing approach

**You:** "Make it restartable without duplicates"

**Agent:** Adds:
- Migration state tracking
- Batch ID generation
- Idempotency checks
- Resume capability
- Duplicate detection

**You:** "Add monitoring and alerts"

**Agent:** Implements:
- Progress logging with percentages
- Duration tracking
- Error metrics
- Success/failure ratios
- Custom metric exporters

**You:** "What if the API is down during migration?"

**Agent:** Explains:
- Retry with exponential backoff
- Circuit breaker pattern
- Fallback mechanisms
- State rollback options
- Dead letter queue pattern

## Tips for Best Results

1. Describe the data volume (rows, bytes)
2. Mention performance requirements
3. Ask about error recovery
4. Request testing strategies
5. Ask for monitoring/observability
6. Request configuration options

## Architecture Pattern

```
QUERY (Firebird)
    ↓
PROJECTION (Java ResultSet → DTO)
    ↓
MAPPER (Projection → Model)
    ↓
VALIDATE (Domain rules)
    ↓
BATCH (Group records)
    ↓
SEND (HTTP to new API)
    ↓
TRACK (Log progress, handle errors)
    ↓
RETRY (If failed, with backoff)
```

## Configuration

Located in: `.github/agents/migration-architect.md`
