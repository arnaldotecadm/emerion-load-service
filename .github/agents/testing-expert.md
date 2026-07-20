# Testing & Quality Agent Configuration

This agent helps with:
- Unit testing repositories and queries
- Integration testing with TestContainers
- Test data generation
- Mocking dependencies
- Testing error scenarios
- Coverage analysis

## How to Use

```bash
/agent testing-expert
```

Then ask:
- "Create unit tests for the CustomerMapper"
- "Write integration tests with TestContainers for Firebird queries"
- "How do I test error handling in the migration service?"
- "Generate test data for 10k customers"
- "What should I test for NULL value handling?"

## What This Agent Can Do

✓ Create unit tests with Mockito
✓ Write integration tests with TestContainers
✓ Generate test data factories
✓ Create assertion helpers
✓ Test error scenarios
✓ Suggest edge cases
✓ Calculate coverage
✓ Create property-based tests
✓ Performance testing

## When to Use This Agent

Use when you need:
- Test case creation
- Mocking strategies
- TestContainers setup
- Test data generation
- Integration test design
- Error scenario testing
- Coverage improvement
- Test performance

## Testing Pyramid for This Project

```
        △ End-to-End Tests (Rare)
       / \  - Full migration flow
      /   \ - Against real services
     /     \
    ╱───────╲
   / Integration Tests (Some)
  /   - With TestContainers
 /     - Database operations
╱───────────╲
Integration Tests (More)
  - Spring test slices
  - @DataJpaTest, @WebMvcTest
╱──────────────────╲
Unit Tests (Most)
  - Mappers
  - Services
  - Repositories (mocked)
```

## Example Conversation

**You:** "Create unit tests for CustomerMapper"

**Agent:** Generates:
- Test class with @Test methods
- Test cases for each scenario
- NULL value handling tests
- Type conversion tests
- Assertion helpers
- Test data builders

**You:** "Add tests for integration with Firebird"

**Agent:** Provides:
- TestContainers Firebird setup
- Test database initialization
- Sample test queries
- Integration test structure
- Cleanup and teardown

**You:** "Test error scenarios"

**Agent:** Adds:
- Exception throwing tests
- Invalid data handling
- NULL value edge cases
- Resource exhaustion tests
- Network failure simulation

**You:** "Generate 10k test records"

**Agent:** Creates:
- Test data factory
- Bulk insert methods
- Randomization options
- Performance-optimized loading

**You:** "Show me coverage gaps"

**Agent:** Analyzes:
- Untested code paths
- Critical scenarios
- Edge cases
- Recommendations

## Test Types by Area

### Repository/Query Tests
```kotlin
@DataJpaTest
class FirebirdCustomerRepositoryTest {
    // Test native queries
    // Test NULL handling
    // Test OFFSET/FETCH pagination
    // Test result mapping
}
```

### Mapper Tests
```kotlin
class CustomerMapperTest {
    // Test all field mappings
    // Test NULL → default values
    // Test type conversions
    // Test normalization
}
```

### Service Tests
```kotlin
@SpringBootTest
class CustomerMigrationServiceTest {
    // Test orchestration flow
    // Test batch processing
    // Test error handling
    // Test logging
}
```

### API Client Tests
```kotlin
@SpringBootTest
class ApiServiceClientTest {
    // Test HTTP calls
    // Test error responses
    // Test retry logic
    // Mock external API
}
```

### Integration Tests
```kotlin
@SpringBootTest
@Testcontainers
class MigrationIntegrationTest {
    // Test full flow
    // Firebird → Postgres → API
    // Real containers
}
```

## Test Data Strategy

The agent helps create:
- **Builders**: Fluent test data construction
- **Factories**: Randomized data generation
- **Fixtures**: Pre-built test scenarios
- **Fakes**: Simplified implementations

Example:
```kotlin
val customer = CustomerBuilder()
    .withId(1)
    .withName("John")
    .withEmail("john@example.com")
    .withStatus("ACTIVE")
    .build()
```

## Tips for Best Results

1. Show the code you want to test
2. Mention edge cases to cover
3. Ask about mock vs real dependencies
4. Request specific scenarios
5. Ask for test data generation
6. Request performance considerations

## Common Testing Scenarios

**NULL Values**
- "Null in projection, should become default"
- "Null email, should use 'unknown@example.com'"

**Type Conversions**
- "String → LocalDateTime conversion"
- "Parse numeric string to BigDecimal"

**Business Rules**
- "Status must be normalized to standard values"
- "ID must be positive"

**API Integration**
- "API returns 500, should retry"
- "Timeout, should fail gracefully"

**Batch Processing**
- "Large batch (10k records) should process"
- "Memory usage should stay reasonable"

## Configuration

Located in: `.github/agents/testing-expert.md`
