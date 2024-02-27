package com.projectronin.interop.completeness.server.event

import com.projectronin.interop.completeness.server.data.relational.DagDAO
import com.projectronin.interop.completeness.topics.InteropCompletenessKafkaTopic
import com.projectronin.json.eventinteropcompleteness.ResourceType
import com.projectronin.json.eventinteropcompleteness.v1.DagRegistrationV1Schema
import com.projectronin.kafka.config.ClusterProperties
import com.projectronin.kafka.data.RoninEvent
import com.projectronin.kafka.streams.kafkaStreams
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.apache.kafka.streams.KafkaStreams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class DagStreamTest {
    private val clusterProperties: ClusterProperties = mockk(relaxed = true)
    private val dagDAO: DagDAO = mockk()
    private val dagRegistrationTopic: InteropCompletenessKafkaTopic =
        mockk {
            every { topicName } returns "TopicName"
        }
    private val dagStream = DagStream(clusterProperties, dagDAO, dagRegistrationTopic, "dlq")

    @Test
    fun `initialize starts the stream`() {
        mockkStatic("com.projectronin.kafka.streams.KafkaStreamsKt")
        val kafkaStream =
            mockk<KafkaStreams> {
                every { start() } just Runs
            }
        every { kafkaStreams(any(), any()) } returns kafkaStream

        dagStream.initialize()

        verify(exactly = 1) { kafkaStream.start() }

        unmockkAll()
    }

    @Test
    fun `processing works`() {
        val eventDateTime = OffsetDateTime.of(2024, 2, 26, 17, 7, 0, 0, ZoneOffset.UTC)

        val registration =
            DagRegistrationV1Schema().apply {
                resource = ResourceType.CONDITION
                consumedResources = listOf(ResourceType.PATIENT, ResourceType.OBSERVATION)
            }
        val event =
            mockk<RoninEvent<DagRegistrationV1Schema>> {
                every { data } returns registration
                every { time } returns eventDateTime.toInstant()
            }

        val dbUuids = listOf(UUID.randomUUID(), UUID.randomUUID())
        every { dagDAO.replace("Condition", listOf("Patient", "Observation"), eventDateTime) } returns dbUuids

        val response = dagStream.processDagEvent(event)
        assertEquals(dbUuids, response)
    }
}
