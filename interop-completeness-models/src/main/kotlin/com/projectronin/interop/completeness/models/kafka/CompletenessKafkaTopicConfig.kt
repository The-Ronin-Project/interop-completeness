package com.projectronin.interop.completeness.models.kafka

import com.projectronin.interop.kafka.spring.KafkaConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CompletenessKafkaTopicConfig(kafkaConfig: KafkaConfig) {
    private val vendor = kafkaConfig.cloud.vendor
    private val region = kafkaConfig.cloud.region
    private val systemName = kafkaConfig.retrieve.serviceId
    val schemaBase = "https://github.com/projectronin/contract-event-interop-completeness/blob/main/src/main/resources/schemas"

    @Bean
    fun dagRegistration(): InteropCompletenessKafkaTopic = createTopic(topicName = "dag-registration", schemaName = "dag-registration-v1")

    @Bean
    fun loadEvent(): InteropCompletenessKafkaTopic = createTopic(topicName = "load", schemaName = "load-event-v1")

    @Bean
    fun runRegistration(): InteropCompletenessKafkaTopic = createTopic(topicName = "run-registration", schemaName = "run-registration-v1")

    private fun createTopic(
        topicName: String,
        schemaName: String,
    ): InteropCompletenessKafkaTopic {
        return InteropCompletenessKafkaTopic(
            systemName = systemName,
            topicName = createTopicName(topicName),
            dataSchema = "$schemaBase/$schemaName.schema.json",
        )
    }

    private fun createTopicName(topicString: String): String {
        val topicParameters =
            listOf(
                vendor,
                region,
                "interop-completeness",
                topicString,
                "v1",
            )
        return topicParameters.joinToString(".")
    }
}
