# Emerion Load Service - Copilot Resources Index

Quick links to everything you need for your project.

## 📖 Start Here

1. **First Time?** → Read [`README.md`](README.md)
2. **Want Overview?** → Read [`SETUP_SUMMARY.md`](SETUP_SUMMARY.md)
3. **Using Agents?** → Check [`AGENTS_GUIDE.md`](AGENTS_GUIDE.md)
4. **Need Templates?** → See [`COPILOT_EXAMPLES.md`](COPILOT_EXAMPLES.md)

## 📚 Complete File Directory

### Main Documentation
| File | Purpose | Time |
|------|---------|------|
| [`README.md`](README.md) | Setup overview, what Copilot understands | 5 min |
| [`COPILOT_SETUP.md`](COPILOT_SETUP.md) | How to use instruction files | 5 min |
| [`AGENTS_GUIDE.md`](AGENTS_GUIDE.md) | 6 agents, workflows, examples | 10 min |
| [`COPILOT_EXAMPLES.md`](COPILOT_EXAMPLES.md) | 9 copy-paste request templates | 5 min |
| [`SETUP_SUMMARY.md`](SETUP_SUMMARY.md) | Comprehensive setup details | 10 min |
| [`INDEX.md`](INDEX.md) | This file | 2 min |

### Core Instructions (Auto-Loaded by Copilot)
| File | When to Reference | Focus |
|------|-------------------|-------|
| [`instructions/firebird-native-queries.instructions.md`](instructions/firebird-native-queries.instructions.md) | Writing SQL queries | Native SQL, JdbcTemplate, RowMapper, Projections |
| [`instructions/api-integration.instructions.md`](instructions/api-integration.instructions.md) | Sending to new API | REST client, retry logic, batching, error handling |
| [`instructions/firebird-postgres.instructions.md`](instructions/firebird-postgres.instructions.md) | Data mapping | Type conversions, NULL handling, migrations |
| [`instructions/api-structure.instructions.md`](instructions/api-structure.instructions.md) | API design | REST endpoints, pagination, responses |
| [`instructions/spring-configuration.instructions.md`](instructions/spring-configuration.instructions.md) | Database config | Datasources, entities, repositories, Gradle |

### Agent Configurations
| Agent | File | When to Use |
|-------|------|-------------|
| database-specialist | [`agents/database-specialist.md`](agents/database-specialist.md) | Writing Firebird queries |
| migration-architect | [`agents/migration-architect.md`](agents/migration-architect.md) | Designing migrations |
| data-mapper | [`agents/data-mapper.md`](agents/data-mapper.md) | Creating mappers |
| api-integrator | [`agents/api-integrator.md`](agents/api-integrator.md) | Building API clients |
| testing-expert | [`agents/testing-expert.md`](agents/testing-expert.md) | Writing tests |
| dev-assistant | [`agents/dev-assistant.md`](agents/dev-assistant.md) | General help |

### Skills (Reusable Code Patterns & Templates)
| Skill | File | Purpose |
|-------|------|---------|
| Firebird Query Skill | [`skills/firebird-query-skill.md`](skills/firebird-query-skill.md) | Native queries, projections, JdbcTemplate, RowMapper patterns |
| Data Mapper Skill | [`skills/data-mapper-skill.md`](skills/data-mapper-skill.md) | Data transformation, mapping, MapStruct, batch processing |
| Skills Guide | [`skills/README.md`](skills/README.md) | How to use skills, creating new skills |

### Database Metadata (Schema Reference)
| Reference | File | Contains |
|-----------|------|----------|
| Firebird Schema | [`database-metadata/firebird-schema-portal_2024_01.md`](database-metadata/firebird-schema-portal_2024_01.md) | Complete DB schema, tables, domains, data type mapping |
| Metadata Guide | [`database-metadata/README.md`](database-metadata/README.md) | How to use metadata, quick reference, gotchas |

## 🏗️ Architectural Quick Reference

```
FIREBIRD
   ↓
QUERY (Native SQL)
   ↓
PROJECTION (DTO/Interface)
   ↓
MAPPER (Transform + Validate)
   ↓
MODEL (Kotlin data class)
   ↓
SEND (REST to new API)
   ↓
RETRY/LOG (Error handling)
```

## 🚀 Quick Commands

### In Copilot CLI
```bash
# See all loaded instructions
/env

# List available agents
/agent

# Use specific agent
/agent database-specialist
/agent migration-architect
/agent data-mapper
/agent api-integrator
/agent testing-expert
/agent dev-assistant
```

### In Terminal
```bash
# Commit setup
git add .github/
git commit -m "chore: Add Copilot CLI context and agents"

# Open Copilot
copilot
```

## 💡 Common Tasks

### Task: "I need to query the ORDERS table from Firebird"
1. Read: [`database-metadata/firebird-schema-portal_2024_01.md`](database-metadata/firebird-schema-portal_2024_01.md) (find table structure)
2. Reference: [`skills/firebird-query-skill.md`](skills/firebird-query-skill.md) (query patterns)
3. Use: `/agent database-specialist`
4. Ask: "Write a query for orders with customer info. Follow patterns from .github/skills/firebird-query-skill.md"

### Task: "Design the migration flow"
1. Read: [`AGENTS_GUIDE.md`](AGENTS_GUIDE.md) (workflow section)
2. Use: `/agent migration-architect`
3. Ask: "Design migration for [TABLE] with [ROW_COUNT] records. Reference .github/database-metadata/"

### Task: "Create mapper from projection to model"
1. Reference: [`skills/data-mapper-skill.md`](skills/data-mapper-skill.md) (mapper patterns)
2. Use: `/agent data-mapper`
3. Ask: "Create mapper from [Projection] to [Model]. Follow patterns from .github/skills/data-mapper-skill.md"

### Task: "Send data to new API"
1. Read: [`api-integration.instructions.md`](instructions/api-integration.instructions.md)
2. Use: `/agent api-integrator`
3. Ask: "Create REST client to send [DATA] to new API"

### Task: "Write tests"
1. Reference: [`skills/data-mapper-skill.md`](skills/data-mapper-skill.md) (test patterns)
2. Use: `/agent testing-expert`
3. Ask: "Create unit tests for [MAPPER]. Follow patterns from .github/skills/data-mapper-skill.md"

### Task: "General architecture question"
1. Use: `/agent dev-assistant`
2. Ask: "Should I use [APPROACH]? Why?"

## 📊 File Statistics

```
Total Files:           23+
Total Lines:           7000+
Instruction Files:     5
Agent Configs:         6
Skills:                2 + README
Database Metadata:     2
Documentation:         5
Examples:              9+
Workflows:             3+
```

## 🎯 Learning Paths

### Path 1: Get Started (30 minutes)
1. Read `README.md` (5 min)
2. Check `skills/README.md` for available patterns (5 min)
3. Read `AGENTS_GUIDE.md` (10 min)
4. Look at `COPILOT_EXAMPLES.md` (5 min)
5. Try first request (5 min)

### Path 2: Deep Dive (2 hours)
1. Read all documentation files (30 min)
2. Review `skills/firebird-query-skill.md` and `skills/data-mapper-skill.md` (30 min)
3. Review `database-metadata/firebird-schema-portal_2024_01.md` (30 min)
4. Review all instruction files (20 min)
5. Practice requests (10 min)

### Path 3: Master (Ongoing)
1. Use agents daily with skills references
2. Read specific `.instructions.md` when needed
3. Build migrations using database metadata
4. Update skills and metadata with lessons learned
5. Help team members with patterns

## 🎓 Skills & Metadata Integration

### Using Skills in Requests
When asking Copilot for code, reference the specific skill file:

```
"Create a Firebird query for CLIENTES table.
Follow patterns from .github/skills/firebird-query-skill.md
Reference .github/database-metadata/firebird-schema-portal_2024_01.md for table structure"
```

### Typical Workflow with Skills
1. **Query** → Use `firebird-query-skill.md` + database metadata
2. **Projection** → Create interface matching query result
3. **Mapper** → Use `data-mapper-skill.md` for transformation
4. **Test** → Reference mapper test patterns in `data-mapper-skill.md`
5. **Integrate** → Use `api-integration.instructions.md` to send data

## 🔧 Customization

### Adding New Instructions
1. Create `.github/instructions/[name].instructions.md`
2. Copilot auto-loads it
3. Use: "Follow [name].instructions.md patterns"

### Adding New Agents
1. Create `.github/agents/[name].md`
2. Document capabilities
3. Use: `/agent name`

### Updating Files
- Edit any file directly
- Copilot auto-loads changes
- Commit to Git for team sharing

## ❓ FAQ

**Q: Where do I start?**
A: Read `README.md` then `AGENTS_GUIDE.md`

**Q: How do I use agents?**
A: See `AGENTS_GUIDE.md` for workflows

**Q: What request templates exist?**
A: Check `COPILOT_EXAMPLES.md`

**Q: How do I write Firebird queries?**
A: Read `instructions/firebird-native-queries.instructions.md`

**Q: How do I send data to new API?**
A: Read `instructions/api-integration.instructions.md`

**Q: Should I use MapStruct or manual mappers?**
A: Ask `/agent dev-assistant`

**Q: I want to add a new pattern**
A: Update `.instructions.md` files and commit

## 📞 Support

### For Queries
→ See [`firebird-native-queries.instructions.md`](instructions/firebird-native-queries.instructions.md)  
→ Use `/agent database-specialist`

### For API Integration
→ See [`api-integration.instructions.md`](instructions/api-integration.instructions.md)  
→ Use `/agent api-integrator`

### For General Help
→ Use `/agent dev-assistant`

### For Architecture Decisions
→ See [`AGENTS_GUIDE.md`](AGENTS_GUIDE.md)  
→ Use `/agent dev-assistant` or `/agent migration-architect`

## 🎉 Ready to Build

You have everything you need:
- ✅ Complete architectural context
- ✅ 6 specialized agents
- ✅ Detailed instruction files
- ✅ Copy-paste templates
- ✅ Best practices embedded

**Start using Copilot now!** 🚀

---

**Navigation:** [`README.md`](README.md) • [`AGENTS_GUIDE.md`](AGENTS_GUIDE.md) • [`COPILOT_EXAMPLES.md`](COPILOT_EXAMPLES.md) • [`SETUP_SUMMARY.md`](SETUP_SUMMARY.md)
