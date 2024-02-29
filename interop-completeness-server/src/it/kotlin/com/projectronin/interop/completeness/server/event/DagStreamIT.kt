package com.projectronin.interop.completeness.server.event

import com.projectronin.interop.completeness.server.data.relational.DagDAO
import com.projectronin.interop.kafka.model.KafkaAction
import com.projectronin.interop.kafka.model.KafkaEvent
import com.projectronin.json.eventinteropcompleteness.ResourceType
import com.projectronin.json.eventinteropcompleteness.v1.DagRegistrationV1Schema
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class DagStreamIT : BaseKafkaIT() {
    private val dagDAO = DagDAO(ktormDatabase)

    @AfterEach
    fun purgeDag() {
        dataSource.connection.use { connection ->
            connection.prepareStatement("TRUNCATE TABLE dag").use { it.execute() }
        }
    }

    @Test
    fun `adds DAG for new resource`() {
        val preEvent = dagDAO.findResourcesThatTrigger("Condition")
        assertTrue(preEvent.isEmpty())

        val registration =
            DagRegistrationV1Schema().apply {
                resource = ResourceType.CONDITION
                consumedResources = listOf(ResourceType.OBSERVATION, ResourceType.PATIENT)
            }
        val event = createEvent(registration)
        publishEvents(kafkaTopicConfig.dagRegistration(), listOf(event))

        // delay to give a chance to pick it up
        runBlocking { delay(1000) }

        val postEvent = dagDAO.findResourcesThatTrigger("Condition")
        assertEquals(2, postEvent.size)

        assertEquals("Condition", postEvent[0].resource)
        assertEquals("Condition", postEvent[1].resource)

        val consumedResources = postEvent.map { it.subscribesTo }
        assertTrue("Observation" in consumedResources)
        assertTrue("Patient" in consumedResources)
    }

    @Test
    fun `updates DAG for existing resource`() {
        dagDAO.replace("Condition", listOf("Observation", "Patient"), OffsetDateTime.now(ZoneOffset.UTC))

        val preEvent = dagDAO.findResourcesThatTrigger("Condition")
        assertEquals(2, preEvent.size)

        val registration =
            DagRegistrationV1Schema().apply {
                resource = ResourceType.CONDITION
                consumedResources = listOf(ResourceType.APPOINTMENT)
            }
        val event = createEvent(registration)
        publishEvents(kafkaTopicConfig.dagRegistration(), listOf(event))

        // delay to give a chance to pick it up
        runBlocking { delay(1000) }

        val postEvent = dagDAO.findResourcesThatTrigger("Condition")
        assertEquals(1, postEvent.size)

        assertEquals("Condition", postEvent[0].resource)
        assertEquals("Appointment", postEvent[0].subscribesTo)
    }

    private fun createEvent(dagRegistration: DagRegistrationV1Schema): KafkaEvent<DagRegistrationV1Schema> =
        KafkaEvent(
            domain = "interop-completeness",
            resource = "dag",
            action = KafkaAction.PUBLISH,
            resourceId = dagRegistration.resource.value().lowercase(),
            data = dagRegistration,
        )
}
