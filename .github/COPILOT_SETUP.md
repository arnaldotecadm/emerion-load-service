# Copilot Context Setup - Emerion Load Service

## Overview
This directory contains comprehensive Copilot CLI instructions to help with code generation, architecture decisions, and best practices throughout the development of the Emerion Load Service.

## File Structure

```
.github/
├── copilot-instructions.md                           # Main instructions (loaded automatically)
└── instructions/
    ├── firebird-postgres.instructions.md            # Firebird→PostgreSQL migration patterns
    ├── api-structure.instructions.md                # REST API design & conventions
    └── spring-configuration.instructions.md         # Spring Boot config for dual databases
```

## How Copilot Uses These Files

When you use the GitHub Copilot CLI in this directory, it automatically loads:
1. **`.github/copilot-instructions.md`** - Main context file (loaded by default)
2. **`.github/instructions/*.instructions.md`** - All instruction files in this folder

These files are loaded in the context of every Copilot conversation, providing:
- Project architecture and structure
- Code patterns and conventions
- Best practices for Kotlin/Spring Boot
- Database configuration patterns
- REST API design guidelines

## Key Instruction Files Explained

### 1. `copilot-instructions.md` (Main File)
**What it covers:**
- Project overview and goals
- Tech stack details (Kotlin, Spring Boot, JPA, PostgreSQL, Firebird)
- Project folder structure and conventions
- Code patterns (entities, repositories, services, controllers)
- Data migration strategy (Extract → Transform → Validate → Load)
- REST API conventions
- Logging and error handling guidelines
- Configuration management
- Testing approach
- Important guidelines for Copilot

**When to reference:** Use this when asking Copilot for:
- New features or endpoints
- Architecture decisions
- Code structure questions
- General best practices

### 2. `firebird-postgres.instructions.md`
**What it covers:**
- Data type mapping between Firebird and PostgreSQL
- NULL value handling
- ID/Primary key strategies
- Foreign key constraints
- Sequence/Generator handling
- Transaction management across two databases
- Character encoding considerations
- Numeric precision issues
- Date/Time handling
- Large data migration patterns
- Duplicate detection & de-duplication
- Data validation rules
- Idempotency patterns (for re-runnable migrations)
- Error handling during migration
- Testing strategies for migration

**When to reference:** Use this when asking Copilot for:
- Data migration logic
- Entity mapping between databases
- Batch processing implementations
- Error handling in migration flows
- Data validation queries

### 3. `api-structure.instructions.md`
**What it covers:**
- API versioning strategies
- Endpoint naming conventions
- HTTP status codes
- Request/Response format standards
- Pagination implementation
- Filtering and search patterns
- Input validation
- POST/PUT/PATCH distinctions
- DELETE response handling
- Exception handling with `@ControllerAdvice`
- Swagger/OpenAPI documentation
- Authentication & Authorization
- CORS configuration
- Caching patterns
- Unit and integration testing patterns

**When to reference:** Use this when asking Copilot for:
- New REST endpoints
- Error response formatting
- Pagination implementation
- Input validation
- API documentation
- Controller testing

### 4. `spring-configuration.instructions.md`
**What it covers:**
- Build configuration (build.gradle.kts dependencies)
- Application configuration (application.yml)
- Firebird DataSource configuration
- PostgreSQL DataSource configuration
- JPA configuration for dual databases
- Entity package organization
- Firebird entity example (read-only)
- PostgreSQL entity example
- Repository examples
- Migration service example
- Integration testing with TestContainers
- Key configuration points

**When to reference:** Use this when asking Copilot for:
- Database configuration changes
- New entity definitions
- Repository implementations
- Migration service development
- Integration test setup

## How to Work with Copilot

### 1. Ask Copilot to Generate Code
Provide context and let Copilot use the instructions:

```bash
# Example in Copilot CLI
# "Create a new service to migrate products from Firebird to PostgreSQL with batch processing"
# Or: "Add a REST endpoint to get customers with pagination and filtering"
```

### 2. Reference Specific Instructions
If Copilot's response doesn't align with your needs, you can remind it:

```bash
# "Remember the firebird-postgres.instructions.md guidelines on handling NULL values"
# "Follow the patterns in api-structure.instructions.md for error responses"
```

### 3. Ask for Architecture Help
Copilot has context of your project structure:

```bash
# "Where should I put the new OrderMigration service?"
# "How should I organize the entity classes for Orders?"
# "What's the right way to handle foreign keys from Firebird?"
```

## Adding More Instructions (Later)

When you expand to the React frontend or new API service, create:
- `.github/instructions/react-patterns.instructions.md` - React/TypeScript conventions
- `.github/instructions/api-server.instructions.md` - Spring Boot API server patterns

Copilot will automatically load all `*.instructions.md` files.

## Tips for Best Results

### 1. Be Specific in Your Requests
Instead of:
- ❌ "Generate code"

Use:
- ✅ "Create a Kotlin service that reads customers from Firebird and migrates them to PostgreSQL using batch processing (1000 records per batch)"

### 2. Ask for Validation
```bash
# "Does this follow the Spring Boot configuration patterns in spring-configuration.instructions.md?"
# "Is this REST endpoint design consistent with api-structure.instructions.md?"
```

### 3. Request Improvements
```bash
# "This migration logic needs error handling. Add try-catch following the patterns in firebird-postgres.instructions.md"
# "Add pagination to this endpoint using the patterns from api-structure.instructions.md"
```

### 4. Ask About Patterns
```bash
# "What's the recommended way to handle NULL values when migrating from Firebird?"
# "Show me the transaction management pattern for separate Firebird and PostgreSQL operations"
```

## Common Use Cases

### Use Case 1: Create a New Migration Service
```bash
"I need to migrate the ORDERS table from Firebird to PostgreSQL. 
Create:
1. Firebird entity (read-only) in model/firebird/
2. PostgreSQL entity in model/
3. Firebird repository in repository/firebird/
4. PostgreSQL repository in repository/
5. Service in service/ with batch migration logic (handle NULL values, validate data)
6. Controller endpoint to trigger migration

Follow patterns from firebird-postgres.instructions.md"
```

### Use Case 2: Add a List Endpoint with Pagination
```bash
"Add a REST endpoint to list customers with:
- Pagination (page, size, sort parameters)
- Filtering by status and type
- Proper error responses
- Swagger documentation

Follow the patterns from api-structure.instructions.md"
```

### Use Case 3: Set Up New Database Connection
```bash
"I need to add support for a third database (MySQL) alongside Firebird and PostgreSQL.
Show me the configuration pattern for:
1. DataSource configuration
2. Entity package organization
3. Repository setup
4. Transaction management

Reference spring-configuration.instructions.md"
```

## Checking Copilot's Context

To verify Copilot has loaded your instructions:

```bash
/env
```

This shows all loaded instructions, MCP servers, agents, and skills.

## Keeping Instructions Updated

As the project evolves:
1. Update the relevant `.instructions.md` file
2. Add new patterns and conventions
3. Document new frameworks or dependencies
4. Copilot will automatically use the updated version

## Next Steps

1. **Commit these files** to your repository
2. **Start using Copilot** in the CLI with `/init` if needed
3. **Test Copilot's understanding** by asking for code generation
4. **Refine the instructions** based on what Copilot generates
5. **Add more instructions** as you add more services/features

## Questions or Improvements?

If you need to adjust any instructions:
- Edit the `.instructions.md` files directly
- Add more specific patterns
- Document edge cases you encounter
- Share examples of what works well

---

**Created for:** Emerion Load Service (Kotlin/Spring Boot)
**Purpose:** Provide Copilot with comprehensive project context
**Status:** Ready to use!
