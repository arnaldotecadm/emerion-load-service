package br.com.vercel.emerionloadservice.scheduling

import br.com.vercel.emerionloadservice.service.CustomerOrderService
import br.com.vercel.emerionloadservice.service.CustomerService
import br.com.vercel.emerionloadservice.service.ProductService
import br.com.vercel.emerionloadservice.service.VendedorService
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.stereotype.Component

@Component
class Scheduler(
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val customerOrderService: CustomerOrderService,
    private val vendedorService: VendedorService,
) : SchedulingConfigurer {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val taskScheduler =
        ThreadPoolTaskScheduler().apply {
            poolSize = PROCESSING_STREAMS
            setThreadNamePrefix("ingestion-scheduler-")
        }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskScheduler.initialize()
        taskRegistrar.setTaskScheduler(taskScheduler)
    }

    @Scheduled(fixedDelayString = "\${scheduling.customer.fixed-delay-ms:10000}")
    fun processCustomers() {
        processStream("customers") {
            customerService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    @Scheduled(fixedDelayString = "\${scheduling.product.fixed-delay-ms:10000}")
    fun processProducts() {
        processStream("products") {
            productService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    @Scheduled(fixedDelayString = "\${scheduling.customer-order.fixed-delay-ms:10000}")
    fun processCustomerOrders() {
        processStream("customer orders") {
            customerOrderService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    @Scheduled(fixedDelayString = "\${scheduling.vendedor.fixed-delay-ms:10000}")
    fun processVendedores() {
        processStream("vendedores") {
            vendedorService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    @PreDestroy
    fun shutdown() {
        taskScheduler.shutdown()
    }

    private fun processStream(
        name: String,
        processRecords: () -> Unit,
    ) {
        try {
            logger.info("Processing pending {}", name)
            processRecords()
        } catch (e: Exception) {
            logger.error("Failed to process pending {}", name, e)
        }
    }

    companion object {
        private const val BATCH_SIZE = 500
        private const val PROCESSING_STREAMS = 4
    }
}
