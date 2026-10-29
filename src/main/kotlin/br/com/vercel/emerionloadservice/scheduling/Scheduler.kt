package br.com.vercel.emerionloadservice.scheduling

import br.com.vercel.emerionloadservice.service.CustomerOrderService
import br.com.vercel.emerionloadservice.service.CustomerQuotationService
import br.com.vercel.emerionloadservice.service.CustomerService
import br.com.vercel.emerionloadservice.service.FinCreService
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
import java.util.concurrent.atomic.AtomicInteger

@Component
class Scheduler(
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val customerOrderService: CustomerOrderService,
    private val customerQuotationService: CustomerQuotationService,
    private val vendedorService: VendedorService,
    private val finCreService: FinCreService,
) : SchedulingConfigurer {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val taskScheduler =
        ThreadPoolTaskScheduler().apply {
            poolSize = 1
            setThreadNamePrefix("ingestion-scheduler-")
        }
    private val nextStreamIndex = AtomicInteger()
    private val streams =
        listOf(
            ::processCustomers,
            ::processProducts,
            ::processCustomerOrders,
            ::processCustomerQuotations,
            ::processVendedores,
            ::processFinCres,
        )

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskScheduler.initialize()
        taskRegistrar.setTaskScheduler(taskScheduler)
    }

    @Scheduled(fixedDelayString = "\${scheduling.fixed-delay-ms:5000}")
    fun processNextStream() {
        streams[nextStreamIndex.getAndUpdate { (it + 1) % streams.size }].invoke()
    }

    fun processCustomers() {
        processStream("customers") {
            customerService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    fun processProducts() {
        processStream("products") {
            productService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    fun processCustomerOrders() {
        processStream("customer orders") {
            customerOrderService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    fun processCustomerQuotations() {
        processStream("customer quotations") {
            customerQuotationService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    fun processVendedores() {
        processStream("vendedores") {
            vendedorService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
        }
    }

    fun processFinCres() {
        processStream("fincre") {
            finCreService.processRecordsToWebservice(Pageable.ofSize(BATCH_SIZE))
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
    }
}
