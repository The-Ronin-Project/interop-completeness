package com.projectronin.interop.completeness.server

import com.projectronin.interop.common.http.spring.HttpSpringConfig
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

/**
 * Main Spring Boot application for the Completeness server.
 */
@Import(
    HttpSpringConfig::class,
)
@SpringBootApplication(
    exclude = [
        SecurityAutoConfiguration::class,
        ManagementWebSecurityAutoConfiguration::class,
    ],
)
class InteropCompletenessServer

fun main(args: Array<String>) {
    runApplication<InteropCompletenessServer>(*args)
}
