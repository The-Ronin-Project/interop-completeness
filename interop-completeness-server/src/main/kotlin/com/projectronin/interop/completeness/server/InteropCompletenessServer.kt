package com.projectronin.interop.completeness.server

import com.projectronin.interop.common.http.spring.HttpSpringConfig
import org.ktorm.database.Database
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

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
@EnableMongoRepositories
@EnableTransactionManagement
class InteropCompletenessServer {
    @Bean
    fun database(dataSource: DataSource): Database = Database.connectWithSpringSupport(dataSource)

    @Bean
    fun txManager(datasource: DataSource): PlatformTransactionManager = DataSourceTransactionManager(datasource)
}

fun main(args: Array<String>) {
    runApplication<InteropCompletenessServer>(*args)
}
