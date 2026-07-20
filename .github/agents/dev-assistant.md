# General Development Assistant Agent Configuration

This agent provides:
- Architecture and design guidance
- Code review and suggestions
- Best practices and patterns
- Problem-solving help
- Project structure advice
- Dependency management

## How to Use

```bash
/agent dev-assistant
```

Then ask:
- "Should I use MapStruct or manual mappers?"
- "How should I structure the error handling?"
- "What's the best way to handle configuration?"
- "Review this code for best practices"
- "Help me troubleshoot this issue"

## What This Agent Can Do

✓ Suggest architecture patterns
✓ Review code for improvements
✓ Recommend dependencies
✓ Explain design decisions
✓ Troubleshoot issues
✓ Suggest refactorings
✓ Performance recommendations
✓ Security guidance
✓ Best practices advice

## When to Use This Agent

Use when you need:
- Architecture guidance
- Design pattern help
- General coding questions
- Project structure advice
- Dependency recommendations
- Troubleshooting
- Performance optimization
- Security review

## Key Architectural Decisions

The agent can help with:

**1. Query Layer**
- Native queries vs JPA
- Query caching strategy
- Connection pooling
- Batch sizes

**2. Mapping Strategy**
- Manual vs MapStruct
- Transformation points
- Validation placement
- Error handling

**3. API Integration**
- Synchronous vs async
- Retry strategy
- Batching approach
- Error handling

**4. Error Handling**
- Custom exceptions
- Recovery strategies
- Logging approach
- Alerting

## Example Conversations

**You:** "Should I use JPA for Firebird queries?"

**Agent:** Recommends:
- Native queries instead (legacy schema complexity)
- Why JPA won't work well
- JdbcTemplate + projections approach
- Pros and cons

**You:** "I need to handle 100k+ customer records"

**Agent:** Suggests:
- Batch processing with offset/limit
- Memory-efficient streaming
- Monitoring and logging
- Progress tracking
- Resource management

**You:** "API is sometimes slow, what should I do?"

**Agent:** Recommends:
- Timeout configuration
- Retry with backoff
- Circuit breaker pattern
- Monitoring metrics
- Load testing approach

**You:** "How should I test this migration?"

**Agent:** Provides:
- Testing pyramid
- Unit vs integration tests
- Mock vs real dependencies
- Test data strategy
- Coverage targets

## Performance Optimization

The agent helps with:
- Query optimization
- Connection pooling
- Batch sizes
- Memory usage
- Caching strategies
- Monitoring

**Example:**
```
Query is slow?
├─ Check OFFSET/FETCH is optimized
├─ Consider batch size (1000-5000?)
├─ Monitor memory usage
├─ Add indexes to Firebird
└─ Consider parallel processing (carefully!)
```

## Security Considerations

The agent considers:
- SQL injection prevention (use parameterized queries)
- Sensitive data in logs
- Connection credential management
- API authentication
- Error messages (don't leak internals)

## Dependencies to Consider

The agent recommends:
- RestTemplate/WebClient for HTTP
- MapStruct for complex mappings
- Resilience4j for retry/circuit breaker
- Micrometer for monitoring
- Testcontainers for integration tests
- Spring Cloud Config for configuration

## Tips for Best Results

1. Provide context about your constraints
2. Mention performance requirements
3. Ask about trade-offs
4. Request multiple options
5. Ask for explanations, not just answers
6. Request testing strategy

## Common Questions

**"What if migration fails?"**
- Retry with backoff
- Log detailed errors
- Track failed batches
- Consider resume capability

**"How do I know it's working?"**
- Progress logging
- Metrics/monitoring
- Health checks
- Result validation

**"What about data consistency?"**
- Transaction management
- Batch boundaries
- Rollback capability
- Validation checks

**"Can I run it in parallel?"**
- Yes, but carefully
- Use separate batches
- Monitor resource usage
- Consider transaction isolation

**"How do I debug this?"**
- Detailed logging
- Test with small datasets first
- Use breakpoints
- Check SQL directly
- Monitor API calls

## Troubleshooting Workflow

The agent helps debug:
1. Identify the problem
2. Check logs and metrics
3. Test in isolation
4. Create minimal reproduction
5. Implement fix
6. Add tests
7. Monitor results

## Configuration

Located in: `.github/agents/dev-assistant.md`

## When to Escalate

Use specialized agents for:
- Complex SQL: **database-specialist**
- Data mapping: **data-mapper**
- API integration: **api-integrator**
- Migration design: **migration-architect**
- Testing: **testing-expert**
