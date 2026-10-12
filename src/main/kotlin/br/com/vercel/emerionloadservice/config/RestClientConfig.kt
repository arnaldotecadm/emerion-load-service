package br.com.vercel.emerionloadservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.time.Duration

@Configuration
class RestClientConfig(
    @Value($$"${ingestion-service.api-key}") private val ingestionApiKey: String,
    @Value($$"${ingestion-service.read-timeout}") private val readTimeout: Duration,
) {
    @Bean
    fun ingestionRestClient(): RestClient {
        val requestFactory =
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(5000)
                setReadTimeout(readTimeout)
            }
        return RestClient
            .builder()
            .requestFactory(requestFactory)
            .defaultHeader("X-API-Key", ingestionApiKey)
            .build()
    }
}
