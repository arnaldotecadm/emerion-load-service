package br.com.vercel.emerionloadservice.scheduling

import br.com.vercel.emerionloadservice.service.CustomerOrderService
import br.com.vercel.emerionloadservice.service.CustomerQuotationService
import br.com.vercel.emerionloadservice.service.CustomerService
import br.com.vercel.emerionloadservice.service.FinCreService
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
    private val customerQuotationService: CustomerQuotationService = mock()
    private val vendedorService: VendedorService = mock()
    private val finCreService: FinCreService = mock()
    private val scheduler =
        Scheduler(
            customerService,
            productService,
            customerOrderService,
            customerQuotationService,
            vendedorService,
            finCreService,
        )

    @Test
    fun `should process every pending stream sequentially`() {
        try {
            repeat(6) {
                scheduler.processNextStream()
            }

            verify(customerService).processRecordsToWebservice(any())
            verify(productService).processRecordsToWebservice(any())
            verify(customerOrderService).processRecordsToWebservice(any())
            verify(customerQuotationService).processRecordsToWebservice(any())
            verify(vendedorService).processRecordsToWebservice(any())
            verify(finCreService).processRecordsToWebservice(any())
        } finally {
            scheduler.shutdown()
        }
    }
}
