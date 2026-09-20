package br.com.vercel.emerionloadservice.scheduling

import br.com.vercel.emerionloadservice.service.CustomerOrderService
import br.com.vercel.emerionloadservice.service.CustomerService
import br.com.vercel.emerionloadservice.service.ProductService
import br.com.vercel.emerionloadservice.service.VendedorService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Scheduler(
    private val customerService: CustomerService,
    private val productService: ProductService,
    private val customerOrderService: CustomerOrderService,
    private val vendedorService: VendedorService // Replace with actual VendedorService type
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedDelay = 10000)
    fun scheduleTask() {
        logger.info("Scheduling customers")
        customerService.processRecordsToWebservice(Pageable.ofSize(200))
    }

    @Scheduled(fixedDelay = 10000)
    fun scheduleTaskForProducts() {
        logger.info("Scheduling products")
        productService.processRecordsToWebservice(Pageable.ofSize(200))
    }

    @Scheduled(fixedDelay = 10000)
    fun scheduleTaskForCustomerOrders() {
        logger.info("Scheduling customer orders")
        customerOrderService.processRecordsToWebservice(Pageable.ofSize(200))
    }

    @Scheduled(fixedDelay = 10000)
    fun scheduleTaskForVendedors() {
        logger.info("Scheduling vendedores")
        vendedorService.processRecordsToWebservice(Pageable.ofSize(200))
    }
}
