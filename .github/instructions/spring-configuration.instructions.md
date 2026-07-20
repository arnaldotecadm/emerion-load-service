# Spring Boot Configuration for Firebird & PostgreSQL

## Build Configuration (build.gradle.kts)

### Required Dependencies
```kotlin
dependencies {
    // Spring Boot Core
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Databases
    runtimeOnly("org.firebirdsql.jdbc:jaybird:4.0.8.java11")  // Firebird JDBC
    runtimeOnly("org.postgresql:postgresql:42.7.1")            // PostgreSQL JDBC
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Logging
    implementation("org.springframework.boot:spring-boot-starter-logging")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:postgresql:1.19.0")
}
```

## Application Configuration (application.yml)

### Default Profile (PostgreSQL)
```yaml
spring:
  application:
    name: emerion-load-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/emerion
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate  # or create, update (use validate in production)
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
          fetch_size: 50
        order_inserts: true
        order_updates: true
    show-sql: false
    open-in-view: false
  
  mvc:
    throw-exception-if-no-handler-found: true

server:
  servlet:
    context-path: /
  port: 8080
  error:
    include-message: always
    include-stacktrace: on_param

logging:
  level:
    root: INFO
    br.com.vercel.emerionloadservice: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Firebird DataSource Configuration
```yaml
firebird:
  datasource:
    url: jdbc:firebirdsql://localhost:3050/path/to/firebird.fdb
    username: SYSDBA
    password: password
    driver-class-name: org.firebirdsql.jdbc.FBDriver
```

## Java Configuration Classes

### Firebird DataSource Configuration
```kotlin
package br.com.vercel.emerionloadservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class FirebirdDataSourceConfig {
    
    @Bean("firebird")
    @ConfigurationProperties(prefix = "firebird.datasource")
    fun firebirdsqlDataSource(): DataSource {
        return DataSourceBuilder.create()
            .driverClassName("org.firebirdsql.jdbc.FBDriver")
            .build()
    }

    @Bean
    fun firebirdsqlEntityManagerFactory(
        @Qualifier("firebird") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            setPackagesToScan("br.com.vercel.emerionloadservice.model.firebird")
            jpaVendorAdapter = HibernateJpaVendorAdapter().apply {
                setDatabasePlatform("org.hibernate.dialect.FirebirdDialect")
                setGenerateDdl(false)
                setShowSql(false)
            }
            setJpaPropertyMap(mapOf(
                "hibernate.jdbc.batch_size" to "20",
                "hibernate.order_inserts" to "true"
            ))
        }
    }

    @Bean
    fun firebirdsqlTransactionManager(
        @Qualifier("firebird") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }
}
```

### PostgreSQL DataSource Configuration (Default)
```kotlin
package br.com.vercel.emerionloadservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["br.com.vercel.emerionloadservice.repository"],
    entityManagerFactoryRef = "postgresEntityManagerFactory",
    transactionManagerRef = "postgresTransactionManager"
)
class PostgresDataSourceConfig {
    
    @Primary
    @Bean("postgres")
    @ConfigurationProperties(prefix = "spring.datasource")
    fun postgresDataSource(): DataSource {
        return DataSourceBuilder.create()
            .driverClassName("org.postgresql.Driver")
            .build()
    }

    @Primary
    @Bean
    fun postgresEntityManagerFactory(
        @Qualifier("postgres") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            setPackagesToScan("br.com.vercel.emerionloadservice.model")
            jpaVendorAdapter = HibernateJpaVendorAdapter().apply {
                setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect")
                setGenerateDdl(false)
                setShowSql(false)
            }
            setJpaPropertyMap(mapOf(
                "hibernate.jdbc.batch_size" to "20",
                "hibernate.order_inserts" to "true"
            ))
        }
    }

    @Primary
    @Bean
    fun postgresTransactionManager(
        @Qualifier("postgres") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }
}
```

## Entity Package Organization

```
src/main/kotlin/br/com/vercel/emerionloadservice/
├── model/
│   ├── Customer.kt           # PostgreSQL entity
│   ├── Product.kt            # PostgreSQL entity
│   └── firebird/
│       ├── FirebirdCustomer.kt  # Firebird entity (read-only)
│       └── FirebirdProduct.kt   # Firebird entity (read-only)
├── repository/
│   ├── CustomerRepository.kt  # PostgreSQL repo
│   ├── ProductRepository.kt   # PostgreSQL repo
│   ├── firebird/
│   │   ├── FirebirdCustomerRepository.kt  # Firebird repo
│   │   └── FirebirdProductRepository.kt   # Firebird repo
│   ├── mapper/
│   │   └── CustomerMapper.kt
│   └── projection/
│       ├── CustomerDTO.kt
│       └── CustomerProjection.kt
```

## Firebird Entity Example (Read-Only)
```kotlin
package br.com.vercel.emerionloadservice.model.firebird

import jakarta.persistence.*

@Entity
@Table(name = "CUSTOMER", schema = "")  // Firebird doesn't use schemas
data class FirebirdCustomer(
    @Id
    @Column(name = "ID")
    val id: Long,
    
    @Column(name = "NAME")
    val name: String,
    
    @Column(name = "EMAIL")
    val email: String?,
    
    @Column(name = "CREATED_AT")
    val createdAt: java.time.LocalDateTime?,
    
    @Column(name = "IS_ACTIVE")
    val isActive: Boolean = true
)
```

## PostgreSQL Entity Example
```kotlin
package br.com.vercel.emerionloadservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "customer")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,
    
    @Column(name = "name", nullable = false)
    val name: String,
    
    @Column(name = "email")
    val email: String?,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true
)
```

## Repository Examples

### Firebird Repository (Read-Only)
```kotlin
package br.com.vercel.emerionloadservice.repository.firebird

import br.com.vercel.emerionloadservice.model.firebird.FirebirdCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(value = "firebirdsqlTransactionManager", readOnly = true)
interface FirebirdCustomerRepository : JpaRepository<FirebirdCustomer, Long> {
    fun findByName(name: String): List<FirebirdCustomer>
    fun findByIsActive(isActive: Boolean): List<FirebirdCustomer>
}
```

### PostgreSQL Repository
```kotlin
package br.com.vercel.emerionloadservice.repository

import br.com.vercel.emerionloadservice.model.Customer
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByName(name: String): List<Customer>
    
    @Query("SELECT c FROM Customer c WHERE c.isActive = :isActive")
    fun findActive(@Param("isActive") isActive: Boolean, pageable: Pageable): Page<Customer>
}
```

## Migration Service Example
```kotlin
package br.com.vercel.emerionloadservice.service

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.repository.CustomerRepository
import br.com.vercel.emerionloadservice.repository.firebird.FirebirdCustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerMigrationService(
    val firebirdsqlRepository: FirebirdCustomerRepository,
    val postgresRepository: CustomerRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun migrateAllCustomers() {
        logger.info("Starting customer migration from Firebird to PostgreSQL")
        
        // Read from Firebird
        val firebirdsqlCustomers = firebirdsqlRepository.findAll()
        logger.info("Read ${firebirdsqlCustomers.size} customers from Firebird")
        
        // Transform
        val pgCustomers = firebirdsqlCustomers.map { fb ->
            Customer(
                name = fb.name,
                email = fb.email,
                isActive = fb.isActive
            )
        }
        
        // Write to PostgreSQL
        postgresRepository.saveAll(pgCustomers)
        logger.info("Migrated ${pgCustomers.size} customers to PostgreSQL")
    }
}
```

## Testing with Test Containers
```kotlin
package br.com.vercel.emerionloadservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class MigrationIntegrationTest {
    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15")
    }

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Test
    fun testMigration() {
        val saved = customerRepository.save(Customer(name = "Test", email = "test@example.com"))
        assertEquals("Test", saved.name)
    }
}
```

## Key Points for Copilot
- When creating entities, specify correct database dialect and table schema
- Firebird entities go in `model.firebird` package
- PostgreSQL entities go in `model` package (default)
- Use `@Transactional("firebirdsqlTransactionManager")` explicitly for Firebird operations
- Default `@Transactional` uses PostgreSQL transaction manager
- Mark Firebird repositories as `readOnly = true`
- Use data classes for entities (Kotlin idiomatic)
- Handle NULL values explicitly with nullable types
