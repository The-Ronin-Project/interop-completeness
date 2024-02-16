package com.projectronin.interop.completeness.server.data.document

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@DataMongoTest
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class])
abstract class BaseMongoRepositoryTest

@Configuration
@EnableMongoRepositories
class MongoDBTestContainerConfig {
    companion object {
        val container = MongoDBContainer("mongo:latest").withExposedPorts(27017)

        init {
            container.start()

            System.setProperty("mongodb.container.port", container.getMappedPort(27017).toString())
        }
    }
}
