package br.com.vercel.emerionloadservice.scheduling

import br.com.vercel.emerionloadservice.service.CustomerOrderService
import br.com.vercel.emerionloadservice.service.CustomerService
import br.com.vercel.emerionloadservice.service.ProductService
import br.com.vercel.emerionloadservice.service.VendedorService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class SchedulerTest {
    private val customerService: CustomerService = mock()
    private val productService: ProductService = mock()
    private val customerOrderService: CustomerOrderService = mock()
    private val vendedorService: VendedorService = mock()
    private val scheduler =
        Scheduler(
            customerService,
            productService,
            customerOrderService,
            vendedorService,
        )

    @Test
    fun `should process every pending stream with the configured batch size`() {
        try {
            scheduler.processCustomers()
            scheduler.processProducts()
            scheduler.processCustomerOrders()
            scheduler.processVendedores()

            verify(customerService).processRecordsToWebservice(any())
            verify(productService).processRecordsToWebservice(any())
            verify(customerOrderService).processRecordsToWebservice(any())
            verify(vendedorService).processRecordsToWebservice(any())
        } finally {
            scheduler.shutdown()
        }
    }
}
