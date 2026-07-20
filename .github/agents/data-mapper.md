# Data Transformation Agent Configuration

This agent helps with:
- Creating mapper classes (manual or MapStruct)
- Handling data transformations
- NULL value handling
- Data validation and enrichment
- Type conversions
- Normalization and cleaning

## How to Use

```bash
/agent data-mapper
```

Then ask:
- "Create a mapper from CustomerProjection to Customer model"
- "Handle NULL values when mapping email field"
- "Add data validation in the mapper"
- "Normalize phone numbers during mapping"
- "Generate MapStruct mapper interface"

## What This Agent Can Do

✓ Create manual mapper classes
✓ Generate MapStruct mapper interfaces
✓ Handle NULL values with defaults
✓ Implement data validation
✓ Add enrichment during mapping
✓ Handle type conversions
✓ Create mapper tests
✓ Suggest mapping strategies
✓ Optimize performance

## When to Use This Agent

Use when you need:
- Mapper class implementation
- Handling complex data transformations
- NULL value strategies
- Data validation logic
- Type conversion helpers
- Testing mappers
- MapStruct configuration
- Performance optimization

## Mapper Patterns

### Manual Mapper
```kotlin
@Component
class CustomerMapper {
    fun projectToModel(proj: CustomerProjection): Customer { ... }
    fun projectionsToModels(projs: List<CustomerProjection>): List<Customer> { ... }
}
```

### MapStruct Mapper
```kotlin
@Mapper(componentModel = "spring")
interface CustomerMapStruct {
    @Mapping(source = "proj.name", target = "name")
    @Mapping(source = "proj.email", target = "email", defaultValue = "unknown@example.com")
    fun projectToModel(proj: CustomerProjection): Customer
}
```

## Example Conversation

**You:** "Create a mapper from ProductProjection to Product"

**Agent:** Generates:
- Mapper interface or class
- All field mappings
- NULL handling
- Type conversions
- Usage example

**You:** "The 'code' field should be uppercase"

**Agent:** Adds:
- Transformation logic
- Validation
- Comments explaining the change

**You:** "Some fields might be NULL, use sensible defaults"

**Agent:** Updates:
- NULL checks with Elvis operator
- Default values per field
- Documentation

**You:** "Add unit tests"

**Agent:** Provides:
- Mapper test class
- Test cases for all scenarios
- Mock data generators

## Transformation Scenarios

The agent can handle:
- Simple field-to-field mappings
- Nested object mappings
- List/Collection mappings
- Type conversions (String → Int, etc.)
- NULL → Default value
- Concatenation (firstName + lastName → name)
- Normalization (uppercase, trim, etc.)
- Enrichment (add computed fields)
- Validation (check ranges, formats)

## Tips for Best Results

1. Show the projection interface definition
2. Show the target model definition
3. Mention NULL value handling preferences
4. Ask for validation logic
5. Request test cases
6. Ask for performance considerations

## Common Mapping Tasks

**NULL to Default**
- "When name is NULL, use 'Unknown'"
- "If email is empty, set to 'no-email@system.local'"

**Type Conversion**
- "Convert dateString to LocalDate"
- "Parse phone number to standardized format"

**Normalization**
- "Trim whitespace and uppercase all strings"
- "Remove special characters from name"

**Validation**
- "Verify email format"
- "Check age is between 18 and 120"

**Enrichment**
- "Add migration timestamp"
- "Generate migration ID"

## Configuration

Located in: `.github/agents/data-mapper.md`
