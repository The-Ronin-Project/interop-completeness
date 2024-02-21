package com.projectronin.interop.completeness.server

import com.projectronin.product.common.config.HttpClientConfiguration
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import java.util.concurrent.TimeUnit

private const val DEFAULT_HTTP_CONNECTION_TIMEOUT = 15000L
private const val DEFAULT_HTTP_READ_TIMEOUT = 15000L

@Configuration
@ComponentScan(
    value = ["com.projectronin.product.common.config"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [HttpClientConfiguration::class],
        ),
    ],
)
class ProductSpringBootConfig {
    @Bean
    fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(DEFAULT_HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
}
