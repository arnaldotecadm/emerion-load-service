# REST API Integration Skill

## Description
Guidance for sending migrated data from Emerion Load Service to the new Spring Boot API service via REST calls.

## Purpose
The Emerion Load Service extracts data from Firebird and **sends it** to the new API. This skill covers:
- Configuring REST client (RestTemplate or WebClient)
- Building HTTP requests to new service
- Handling responses and errors
- Batch sending patterns
- Retry and resilience logic

## Configuration

### RestTemplate Bean
```kotlin
package br.com.vercel.emerionloadservice.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestClientConfig {
    
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .setConnectTimeout(java.time.Duration.ofSeconds(5))
            .setReadTimeout(java.time.Duration.ofSeconds(10))
            .requestFactory(this::clientHttpRequestFactory)
            .interceptors { request, body, execution ->
                // Log requests if needed
                execution.execute(request, body)
            }
            .build()
    }
    
    private fun clientHttpRequestFactory(): ClientHttpRequestFactory {
        val factory = SimpleClientHttpRequestFactory()
        factory.setConnectTimeout(5000)
        factory.setReadTimeout(10000)
        return BufferingClientHttpRequestFactory(factory)
    }
}
```

### API Service Configuration
```yaml
# application.yml
api-service:
  base-url: http://localhost:8081  # New API service
  endpoints:
    customers: /api/v1/customers
    orders: /api/v1/orders
    products: /api/v1/products
  batch-size: 100
  timeout-seconds: 10
```

## REST Client Implementation

### 1. API Response DTO
```kotlin
package br.com.vercel.emerionloadservice.dto

data class ApiResponse<T>(
    val data: T?,
    val status: String,
    val message: String? = null,
    val timestamp: String
)

data class ApiErrorResponse(
    val error: String,
    val status: Int,
    val timestamp: String
)
```

### 2. API Service Client
```kotlin
package br.com.vercel.emerionloadservice.client

import br.com.vercel.emerionloadservice.model.Customer
import br.com.vercel.emerionloadservice.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@Component
class ApiServiceClient(
    val restTemplate: RestTemplate,
    @Value("\${api-service.base-url}") val baseUrl: String,
    @Value("\${api-service.endpoints.customers}") val customersEndpoint: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    fun sendCustomers(customers: List<Customer>): ApiResponse<*> {
        logger.info("Sending ${customers.size} customers to API service")
        
        val url = "$baseUrl$customersEndpoint"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
        
        val request = HttpEntity(customers, headers)
        
        return try {
            val response = restTemplate.postForObject(
                url,
                request,
                ApiResponse::class.java
            )
            logger.info("Successfully sent customers to API service")
            response as ApiResponse<*>
        } catch (e: HttpStatusCodeException) {
            logger.error("Failed to send customers: ${e.statusCode} - ${e.responseBodyAsString}")
            throw ApiServiceException("Failed to send customers: ${e.message}", e)
        } catch (e: Exception) {
            logger.error("Error sending customers to API service", e)
            throw ApiServiceException("Error sending customers", e)
        }
    }
    
    fun sendCustomersBatch(customers: List<Customer>, batchId: String): ApiResponse<*> {
        logger.info("Sending batch '$batchId' with ${customers.size} customers")
        
        val url = "$baseUrl$customersEndpoint/batch"
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("X-Batch-ID", batchId)
        }
        
        val request = HttpEntity(mapOf("batchId" to batchId, "data" to customers), headers)
        
        return try {
            val response = restTemplate.postForObject(
                url,
                request,
                ApiResponse::class.java
            )
            logger.info("Batch '$batchId' sent successfully")
            response as ApiResponse<*>
        } catch (e: HttpStatusCodeException) {
            logger.error("Failed to send batch '$batchId': ${e.statusCode}")
            throw BatchSendException("Failed to send batch: ${e.message}", batchId, e)
        }
    }
}

class ApiServiceException(message: String, cause: Throwable) : RuntimeException(message, cause)
class BatchSendException(message: String, val batchId: String, cause: Throwable) : RuntimeException(message, cause)
```

### 3. WebClient Alternative (Reactive)
```kotlin
package br.com.vercel.emerionloadservice.client

import br.com.vercel.emerionloadservice.model.Customer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import org.slf4j.LoggerFactory

@Component
class ApiServiceWebClient(
    val webClient: WebClient,
    @Value("\${api-service.base-url}") val baseUrl: String
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    fun sendCustomersAsync(customers: List<Customer>): Mono<String> {
        logger.info("Sending ${customers.size} customers asynchronously")
        
        return webClient
            .post()
            .uri("/api/v1/customers")
            .bodyValue(customers)
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnSuccess { logger.info("Customers sent successfully") }
            .doOnError { e -> logger.error("Error sending customers", e) }
    }
}
```

## Testing

### Unit Test
```kotlin
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.RestTemplate

@SpringBootTest
class ApiServiceClientTest {
    @Mock
    val restTemplate: RestTemplate
    
    val client = ApiServiceClient(restTemplate, "http://localhost:8081", "/api/v1/customers")
    
    @Test
    fun testSendCustomers() {
        val customers = listOf(
            Customer(1, "John", "john@example.com", LocalDateTime.now(), "ACTIVE"),
            Customer(2, "Jane", "jane@example.com", LocalDateTime.now(), "ACTIVE")
        )
        
        Mockito.`when`(restTemplate.postForObject(
            Mockito.any(),
            Mockito.any(),
            Mockito.any()
        )).thenReturn(ApiResponse(data = "Ok", status = "success"))
        
        val response = client.sendCustomers(customers)
        assert(response.status == "success")
    }
}
```

## Batch Sending Pattern

### Orchestration Service
```kotlin
@Service
class MigrationOrchestrationService(
    val firebirdsqlRepository: FirebirdCustomerRepository,
    val mapper: CustomerMapper,
    val apiClient: ApiServiceClient
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val batchSize = 100
    
    fun migrateAllCustomers() {
        var offset = 0
        var batchNumber = 1
        var totalSent = 0
        
        while (true) {
            // 1. Extract batch
            val projections = firebirdsqlRepository.findCustomersInBatch(offset, batchSize)
            if (projections.isEmpty()) break
            
            // 2. Map batch
            val models = mapper.projectionsToModels(projections)
            
            // 3. Send batch
            try {
                val batchId = "CUSTOMERS_${System.currentTimeMillis()}_$batchNumber"
                apiClient.sendCustomersBatch(models, batchId)
                totalSent += models.size
                logger.info("Batch $batchNumber sent: $batchId (${models.size} records, total: $totalSent)")
            } catch (e: BatchSendException) {
                logger.error("Batch $batchNumber failed: ${e.message}")
                // Retry logic here if needed
                retryBatch(models, batchNumber)
            }
            
            offset += batchSize
            batchNumber++
        }
        
        logger.info("Migration complete: $totalSent total customers sent")
    }
    
    private fun retryBatch(models: List<Customer>, batchNumber: Int, maxRetries: Int = 3) {
        repeat(maxRetries) { attempt ->
            try {
                val batchId = "CUSTOMERS_RETRY_${System.currentTimeMillis()}_${batchNumber}_$attempt"
                apiClient.sendCustomersBatch(models, batchId)
                logger.info("Retry attempt $attempt succeeded for batch $batchNumber")
                return
            } catch (e: Exception) {
                logger.warn("Retry attempt $attempt failed for batch $batchNumber")
                if (attempt == maxRetries - 1) {
                    throw e
                }
            }
        }
    }
}
```

## Error Handling

### Retry with Backoff
```kotlin
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.core.registry.EntryAddedEvent
import java.time.Duration

@Configuration
class ResilienceConfig {
    
    @Bean
    fun retryRegistry(): RetryRegistry {
        val retryConfig = RetryConfig.custom<Any>()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .intervalFunction(
                io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff(1000, 2.0)
            )
            .retryOnException { it !is ApiServiceException }
            .build()
        
        return RetryRegistry.of(retryConfig)
    }
}

@Service
class ResilientApiClient(val apiClient: ApiServiceClient) {
    
    fun sendCustomersWithRetry(customers: List<Customer>): ApiResponse<*> {
        val retry = Retry.ofDefaults("api-call")
        return Retry.decorateSupplier(retry) { apiClient.sendCustomers(customers) }.get()
    }
}
```

## Testing Endpoints (Controller)

### Migration Trigger Endpoints
```kotlin
@RestController
@RequestMapping("/api/v1/migrations")
class MigrationController(val orchestrationService: MigrationOrchestrationService) {
    
    @PostMapping("/customers/trigger")
    fun triggerCustomerMigration(): ResponseEntity<Map<String, Any>> {
        val startTime = System.currentTimeMillis()
        orchestrationService.migrateAllCustomers()
        val duration = (System.currentTimeMillis() - startTime) / 1000.0
        
        return ResponseEntity.ok(mapOf(
            "status" to "started",
            "message" to "Customer migration triggered",
            "durationSeconds" to duration
        ))
    }
    
    @PostMapping("/customers/batch")
    fun triggerCustomerBatchMigration(
        @RequestParam(defaultValue = "1000") batchSize: Int
    ): ResponseEntity<Map<String, Any>> {
        val startTime = System.currentTimeMillis()
        orchestrationService.migrateCustomersInBatches(batchSize)
        val duration = (System.currentTimeMillis() - startTime) / 1000.0
        
        return ResponseEntity.ok(mapOf(
            "status" to "completed",
            "batchSize" to batchSize,
            "durationSeconds" to duration
        ))
    }
}
```

## Best Practices

✓ Use RestTemplate for simple synchronous calls  
✓ Use WebClient for reactive/async scenarios  
✓ Always handle HTTP errors explicitly  
✓ Log request/response (but not sensitive data)  
✓ Set reasonable timeouts  
✓ Implement retry logic with backoff  
✓ Use batch IDs for tracking  
✓ Monitor API response times  
✓ Test against mock API service  

## Common Errors & Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| Connection timeout | API service slow/down | Check service health, increase timeout |
| 400 Bad Request | Invalid JSON/data format | Verify DTOs match API expectations |
| 409 Conflict | Duplicate data sent | Use idempotency keys or batch IDs |
| 500 Server Error | API service error | Check API logs, retry with backoff |
