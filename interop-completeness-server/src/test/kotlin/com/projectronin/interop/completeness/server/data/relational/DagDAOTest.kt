package com.projectronin.interop.completeness.server.data.relational

import com.github.database.rider.core.api.connection.ConnectionHolder
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.junit5.api.DBRider
import com.projectronin.interop.common.test.database.dbrider.DBRiderConnection
import com.projectronin.interop.common.test.database.ktorm.KtormHelper
import com.projectronin.interop.common.test.database.liquibase.LiquibaseTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAmount

@DBRider
@LiquibaseTest(
    changeLog = "completeness/db/changelog/completeness.db.changelog-master.yaml",
)
class DagDAOTest {
    @DBRiderConnection
    lateinit var connectionHolder: ConnectionHolder

    @Test
    @DataSet(value = ["/dbunit/dag/SimpleDag.yaml"], cleanAfter = true)
    fun `findResourcesThatSubscribeTo - can find multiple`() {
        val dao = DagDAO(KtormHelper.database())
        val found = dao.findResourcesThatSubscribeTo("Patient")
        assertEquals(3, found.size)
        val resourceSet = found.map { it.resource }.toSet()
        assertTrue(resourceSet.contains("Medication"))
        assertTrue(resourceSet.contains("Condition"))
        assertTrue(resourceSet.contains("Appointment"))
    }

    @Test
    @DataSet(value = ["/dbunit/dag/SimpleDag.yaml"], cleanAfter = true)
    fun `findResourcesThatSubscribeTo - can find none`() {
        val dao = DagDAO(KtormHelper.database())
        val found = dao.findResourcesThatSubscribeTo("Condition")
        assertEquals(0, found.size)
    }

    @Test
    @DataSet(value = ["/dbunit/dag/SimpleDag.yaml"], cleanAfter = true)
    fun `findResourcesThatTrigger - can find value`() {
        val dao = DagDAO(KtormHelper.database())
        val found = dao.findResourcesThatTrigger("Medication")
        assertEquals(1, found.size)
        assertEquals("Patient", found[0].subscribesTo)
    }

    @Test
    @DataSet(value = ["/dbunit/dag/SimpleDag.yaml"], cleanAfter = true)
    fun `findResourcesThatTrigger - cannot find value`() {
        val dao = DagDAO(KtormHelper.database())
        val found = dao.findResourcesThatTrigger("Patient")
        assertEquals(0, found.size)
    }

    @Test
    @DataSet(value = ["/dbunit/dag/SimpleDag.yaml"], cleanAfter = true)
    fun `replace - cannot find value after insertion`() {
        val dao = DagDAO(KtormHelper.database())

        val eventTime = OffsetDateTime.parse("2024-02-21T00:00:00+00:00")
        val originalId =
            dao.findResourcesThatTrigger("MedicationAdministration")
                .first { it.subscribesTo == "Medication" }.id
        val newUuids = dao.replace("MedicationAdministration", listOf("Patient"), eventTime)
        assertEquals(1, newUuids.size)

        // Check value cannot be found by original id
        assertNull(dao.getByID(originalId))

        // Check that findResourcesThatSubscribeTo does not return old node
        val found = dao.findResourcesThatSubscribeTo("Medication")
        val resourceSet = found.map { it.resource }.toSet()
        assertFalse(resourceSet.contains("MedicationAdministration"))

        // Check that the new node was inserted
        val newNode =
            dao.findResourcesThatTrigger("MedicationAdministration")
                .first { it.subscribesTo == "Patient" }
        assertNotNull(newNode)
        assertEquals(newUuids.first(), newNode!!.id)
        assertEquals("MedicationAdministration", newNode.resource)
        assertEquals("Patient", newNode.subscribesTo)
        assertWasRecent(newNode.createDateTime)
        assertEquals(eventTime, newNode.eventDateTime)

        // Check that the new node can be found findResourcesThatSubscribeTo
        val foundByPatient = dao.findResourcesThatSubscribeTo("Patient")
        val resourcesFoundByPatient = foundByPatient.map { it.resource }.toSet()
        assertTrue(resourcesFoundByPatient.contains("MedicationAdministration"))
    }
}

fun assertWasRecent(
    dateTime: OffsetDateTime,
    temporalAmount: TemporalAmount = Duration.ofSeconds(20),
) = assertTrue(dateTime.wasRecent(temporalAmount)) { "$dateTime was not recent" }

fun OffsetDateTime.wasRecent(temporalAmount: TemporalAmount): Boolean {
    val now = OffsetDateTime.now(ZoneOffset.UTC)
    return now > this && now.minus(temporalAmount) < this
}
