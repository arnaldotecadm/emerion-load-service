# API Integration Agent Configuration

This agent helps with:
- Building REST clients to call the new API service
- Handling HTTP responses and errors
- Implementing retry logic
- Batch sending patterns
- Health checks and monitoring
- Testing API integrations

## How to Use

```bash
/agent api-integrator
```

Then ask:
- "Create a REST client to send customers to the new API"
- "Add retry logic with exponential backoff"
- "How do I handle API timeout?"
- "Create a health check endpoint"
- "Generate tests for the API client"

## What This Agent Can Do

✓ Build RestTemplate clients
✓ Create WebClient reactive clients
✓ Implement error handling
✓ Add retry/resilience logic
✓ Batch sending patterns
✓ Health checks and monitoring
✓ Mock API testing
✓ Integration testing
✓ Response parsing and validation

## When to Use This Agent

Use when you need:
- REST client implementation
- Error handling and retries
- Batch API calls
- Health monitoring
- Integration testing
- API documentation
- Response validation
- Performance testing

## REST Client Pattern

```
Build Request
    ↓
Set Headers/Auth
    ↓
Send HTTP Call
    ↓
Handle Response (200, 201, 4xx, 5xx)
    ↓
Log Result
    ↓
Return Data or Error
```

## Example Conversation

**You:** "Create a client to send customers to the new API"

**Agent:** Generates:
- RestTemplate or WebClient bean
- ApiServiceClient class
- Request/Response DTOs
- Error handling
- Usage in service layer

**You:** "API sometimes times out, add retry logic"

**Agent:** Implements:
- Retry configuration
- Exponential backoff
- Max retry attempts
- Circuit breaker pattern
- Fallback mechanism

**You:** "Send in batches of 100"

**Agent:** Adds:
- Batch sending method
- Batch ID tracking
- Progress logging
- Partial failure handling

**You:** "How do I know if the API is healthy?"

**Agent:** Creates:
- Health check endpoint
- Connection test method
- Status monitoring
- Alerting strategy

**You:** "Create tests for this"

**Agent:** Provides:
- Unit tests with mocks
- Integration tests
- Test data builders
- Error scenario tests

## Configuration for New API

You'll need:
```yaml
api-service:
  base-url: http://new-api-service:8081
  endpoints:
    customers: /api/v1/customers
    orders: /api/v1/orders
  timeout: 10s
  retries: 3
  batch-size: 100
```

## Common API Patterns

**Send Single Record**
```kotlin
POST /api/v1/customers
Content-Type: application/json

{ "id": 1, "name": "John" }
```

**Send Batch**
```kotlin
POST /api/v1/customers/batch
X-Batch-ID: BATCH_20260718_001

{ "batchId": "...", "data": [...] }
```

**Check Health**
```kotlin
GET /health
GET /actuator/health
```

## Tips for Best Results

1. Provide the new API endpoint documentation
2. Show expected request/response formats
3. Mention timeout requirements
4. Ask about retry strategy preferences
5. Request monitoring and logging
6. Ask for test coverage details

## Error Handling Strategies

**Retry Transient Errors**
- Timeouts
- 503 Service Unavailable
- 429 Too Many Requests

**Don't Retry**
- 400 Bad Request
- 401 Unauthorized
- 404 Not Found

**Handle Gracefully**
- Log the error
- Track failure metrics
- Notify operations
- Consider dead letter queue

## Integration Testing

The agent can help create:
- Mock API server
- Test containers for integration tests
- Test data generators
- Scenario builders
- Assertion helpers

## Configuration

Located in: `.github/agents/api-integrator.md`
