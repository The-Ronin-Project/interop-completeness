package com.projectronin.interop.completeness.server.handler

import com.projectronin.interop.completeness.server.data.relational.DagDAO
import com.projectronin.interop.completeness.server.data.relational.model.DagDO
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.UUID

class DagHandlerTest {
    private lateinit var dagDAO: DagDAO
    private lateinit var dagHandler: DagHandler

    @BeforeEach
    fun setup() {
        dagDAO = mockk()
        dagHandler = DagHandler(dagDAO)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Can fetch DAG`() {
        every { dagDAO.findAll() } returns
            listOf(
                createTestDagDO("Medication", "Patient"),
                createTestDagDO("MedicationAdministration", "Medication"),
                createTestDagDO("MedicationAdministration", "Patient"),
                createTestDagDO("Condition", "Patient"),
            )

        val response = dagHandler.dag(mockk())

        val medicationNode = response.data.nodes.first { it.resource == "Medication" }
        assertEquals(1, medicationNode.consumedResources.size)
        assertTrue("Patient" in medicationNode.consumedResources)

        val medicationAdministrationNode = response.data.nodes.first { it.resource == "MedicationAdministration" }
        assertEquals(2, medicationAdministrationNode.consumedResources.size)
        assertTrue("Patient" in medicationAdministrationNode.consumedResources)
        assertTrue("Medication" in medicationAdministrationNode.consumedResources)

        val conditionNode = response.data.nodes.first { it.resource == "Condition" }
        assertEquals(1, conditionNode.consumedResources.size)
        assertTrue("Patient" in conditionNode.consumedResources)
    }

    companion object {
        fun createTestDagDO(
            resource: String,
            subscribesTo: String,
        ): DagDO {
            return DagDO {
                this.resource = resource
                this.subscribesTo = subscribesTo
                this.createDateTime = OffsetDateTime.now()
                this.eventDateTime = OffsetDateTime.now().minusMinutes(1)
                this.id = UUID.randomUUID()
            }
        }
    }
}
