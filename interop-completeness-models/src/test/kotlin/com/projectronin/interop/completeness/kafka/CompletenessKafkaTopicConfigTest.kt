package com.projectronin.interop.completeness.kafka

import com.projectronin.interop.completeness.models.kafka.CompletenessKafkaTopicConfig
import com.projectronin.interop.kafka.spring.KafkaConfig
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CompletenessKafkaTopicConfigTest {
    private val vendor = "oci"
    private val region = "us-phoenix-1"
    private val system = "ehr-data-authority"

    private val kafkaConfig =
        mockk<KafkaConfig> {
            every { cloud.vendor } returns vendor
            every { cloud.region } returns region
            every { retrieve.serviceId } returns system
        }

    private val kafkaTopicConfig = CompletenessKafkaTopicConfig(kafkaConfig)

    @Test
    fun `creates dag topic`() {
        val topic = kafkaTopicConfig.dagRegistration()

        assertEquals(system, topic.systemName)
        assertEquals("oci.us-phoenix-1.interop-completeness.dag-registration.v1", topic.topicName)
        assertEquals(
            "https://github.com/projectronin/contract-event-interop-completeness" +
                "/blob/main/src/main/resources/schemas/dag-registration-v1.schema.json",
            topic.dataSchema,
        )
        assertTrue(topic.useLatestOffset)
    }

    @Test
    fun `creates load topic`() {
        val topic = kafkaTopicConfig.loadEvent()

        assertEquals(system, topic.systemName)
        assertEquals("oci.us-phoenix-1.interop-completeness.load.v1", topic.topicName)
        assertEquals(
            "https://github.com/projectronin/contract-event-interop-completeness" +
                "/blob/main/src/main/resources/schemas/load-event-v1.schema.json",
            topic.dataSchema,
        )
        assertTrue(topic.useLatestOffset)
    }

    @Test
    fun `creates runRegistration topic`() {
        val topic = kafkaTopicConfig.runRegistration()

        assertEquals(system, topic.systemName)
        assertEquals("oci.us-phoenix-1.interop-completeness.run-registration.v1", topic.topicName)
        assertEquals(
            "https://github.com/projectronin/contract-event-interop-completeness" +
                "/blob/main/src/main/resources/schemas/run-registration-v1.schema.json",
            topic.dataSchema,
        )
        assertTrue(topic.useLatestOffset)
    }
}
