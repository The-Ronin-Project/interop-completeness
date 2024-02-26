package com.projectronin.interop.completeness.server.data.relational

import com.github.database.rider.core.api.connection.ConnectionHolder
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.junit5.api.DBRider
import com.projectronin.interop.common.test.database.dbrider.DBRiderConnection
import com.projectronin.interop.common.test.database.ktorm.KtormHelper
import com.projectronin.interop.common.test.database.liquibase.LiquibaseTest
import com.projectronin.interop.completeness.server.data.relational.model.RunDO
import com.projectronin.interop.completeness.server.model.RunStatus
import com.projectronin.interop.completeness.server.model.RunType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.UUID

@DBRider
@LiquibaseTest(
    changeLog = "completeness/db/changelog/completeness.db.changelog-master.yaml",
)
class RunDAOTest {
    @DBRiderConnection
    lateinit var connectionHolder: ConnectionHolder

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `getByID - can find value`() {
        val dao = RunDAO(KtormHelper.database())

        val found = dao.getByID(UUID.fromString("d0e8e80a-297a-4b19-bd59-4b8072db9cc4"))
        assertNotNull(found)

        // Check the values match what's in the YAML
        assertNotNull(found!!.id)
        assertEquals("run with backfill, has non-null description, has non-null run_end_time", found.description)
        assertEquals(RunType.BACKFILL, found.type)
        assertEquals("tenant1", found.tenantId)
        assertEquals(UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4"), found.backfillId)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:00:00+00:00"), found.runStartTime)
        assertEquals(OffsetDateTime.parse("2024-02-21T03:00:00+00:00"), found.runEndTime)
        assertEquals(RunStatus.SUCCESS, found.status)
        assertEquals("Successfully loaded", found.statusMessage)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:01:00+00:00"), found.createDateTime)
        assertEquals(OffsetDateTime.parse("2024-02-20T23:59:00+00:00"), found.eventDateTime)
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `getByID - cannot find value`() {
        val dao = RunDAO(KtormHelper.database())

        val found = dao.getByID(UUID.fromString("00000000-0000-0000-0000-000000000000"))
        assertNull(found)
    }

    @Test
    @DataSet(value = ["/dbunit/run/RunWithNullFields.yaml"], cleanAfter = true)
    fun `getByID - can deserialize nullable fields with nulls`() {
        val dao = RunDAO(KtormHelper.database())

        val found = dao.getByID(UUID.fromString("d0e8e80a-297a-4b19-bd59-4b8072db9cc4"))
        assertNotNull(found)

        // Check the values match what's in the YAML
        assertNotNull(found!!.id)
        assertNull(found.description)
        assertEquals(RunType.AD_HOC, found.type)
        assertEquals("tenant1", found.tenantId)
        assertNull(found.backfillId)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:00:00+00:00"), found.runStartTime)
        assertNull(found.runEndTime)
        assertEquals(RunStatus.INCOMPLETE, found.status)
        assertNull(found.statusMessage)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:01:00+00:00"), found.createDateTime)
        assertEquals(OffsetDateTime.parse("2024-02-20T23:59:00+00:00"), found.eventDateTime)
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `getByBackfillId - can find value`() {
        val dao = RunDAO(KtormHelper.database())

        val results = dao.getByBackfillId(UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4"))
        assertEquals(1, results.size)
        val found = results[0]

        // Check the values match what's in the YAML
        assertNotNull(found.id)
        assertEquals("run with backfill, has non-null description, has non-null run_end_time", found.description)
        assertEquals(RunType.BACKFILL, found.type)
        assertEquals("tenant1", found.tenantId)
        assertEquals(UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4"), found.backfillId)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:00:00+00:00"), found.runStartTime)
        assertEquals(OffsetDateTime.parse("2024-02-21T03:00:00+00:00"), found.runEndTime)
        assertEquals(RunStatus.SUCCESS, found.status)
        assertEquals("Successfully loaded", found.statusMessage)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:01:00+00:00"), found.createDateTime)
        assertEquals(OffsetDateTime.parse("2024-02-20T23:59:00+00:00"), found.eventDateTime)
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `getByBackfillId - cannot find value`() {
        val dao = RunDAO(KtormHelper.database())

        val results = dao.getByBackfillId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
        assertEquals(0, results.size)
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `getByTenantId - can find value`() {
        val dao = RunDAO(KtormHelper.database())

        val results = dao.getByTenantId("tenant1")
        assertEquals(1, results.size)
        val found = results[0]

        // Check the values match what's in the YAML
        assertNotNull(found.id)
        assertEquals("run with backfill, has non-null description, has non-null run_end_time", found.description)
        assertEquals(RunType.BACKFILL, found.type)
        assertEquals("tenant1", found.tenantId)
        assertEquals(UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4"), found.backfillId)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:00:00+00:00"), found.runStartTime)
        assertEquals(OffsetDateTime.parse("2024-02-21T03:00:00+00:00"), found.runEndTime)
        assertEquals(RunStatus.SUCCESS, found.status)
        assertEquals("Successfully loaded", found.statusMessage)
        assertEquals(OffsetDateTime.parse("2024-02-21T00:01:00+00:00"), found.createDateTime)
        assertEquals(OffsetDateTime.parse("2024-02-20T23:59:00+00:00"), found.eventDateTime)
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `getByTenantId - cannot find value`() {
        val dao = RunDAO(KtormHelper.database())

        val results = dao.getByTenantId("tenant2")
        assertEquals(0, results.size)
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `insert - can insert and find value`() {
        val dao = RunDAO(KtormHelper.database())

        val insertedUuid =
            dao.insert(
                RunDO {
                    description = "new run"
                    type = RunType.BACKFILL
                    tenantId = "tenant1"
                    backfillId = UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4")
                    runStartTime = OffsetDateTime.parse("2024-02-22T00:00:00+00:00")
                    runEndTime = OffsetDateTime.parse("2024-02-22T03:00:00+00:00")
                    status = RunStatus.SUCCESS
                    statusMessage = "Successful backfill"
                    eventDateTime = OffsetDateTime.parse("2024-02-21T23:59:00+00:00")
                },
            )

        // Check that we can find the new run regardless of which find method we use
        val foundById = dao.getByID(insertedUuid)
        val foundByBackfillId =
            dao.getByBackfillId(UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4"))
                .firstOrNull { it.id == insertedUuid }
        val foundByTenantId =
            dao.getByTenantId("tenant1")
                .firstOrNull { it.id == insertedUuid }

        for (found in listOf(foundById, foundByBackfillId, foundByTenantId)) {
            // Check the values match what we just inserted
            assertNotNull(found)
            assertEquals(insertedUuid, found!!.id)
            assertEquals("new run", found.description)
            assertEquals(RunType.BACKFILL, found.type)
            assertEquals("tenant1", found.tenantId)
            assertEquals(UUID.fromString("b4e8e80a-297a-4b19-bd59-4b8072db9cc4"), found.backfillId)
            assertEquals(OffsetDateTime.parse("2024-02-22T00:00:00+00:00"), found.runStartTime)
            assertEquals(OffsetDateTime.parse("2024-02-22T03:00:00+00:00"), found.runEndTime)
            assertEquals(RunStatus.SUCCESS, found.status)
            assertEquals("Successful backfill", found.statusMessage)
            assertWasRecent(found.createDateTime)
            assertEquals(OffsetDateTime.parse("2024-02-21T23:59:00+00:00"), found.eventDateTime)
        }
    }

    @Test
    @DataSet(value = ["/dbunit/run/SimpleRun.yaml"], cleanAfter = true)
    fun `insert - can insert and find value with nullable fields`() {
        val dao = RunDAO(KtormHelper.database())

        /*
         * We are not setting values for
         * [description, backfillId, runEndTime, statusMessage]
         * as those are nullable
         *
         * We are not setting values for
         * [id, createDateTime]
         * as those are generated by the DAO layer
         */
        val insertedUuid =
            dao.insert(
                RunDO {
                    type = RunType.NIGHTLY
                    tenantId = "tenant1"
                    runStartTime = OffsetDateTime.parse("2024-02-22T00:00:00+00:00")
                    status = RunStatus.SUCCESS
                    eventDateTime = OffsetDateTime.parse("2024-02-21T23:59:00+00:00")
                },
            )

        // Check that we can find the new run regardless of which find method we use
        val foundById = dao.getByID(insertedUuid)
        val foundByTenantId =
            dao.getByTenantId("tenant1")
                .firstOrNull { it.id == insertedUuid }

        for (found in listOf(foundById, foundByTenantId)) {
            // Check the values match what we just inserted
            assertNotNull(found)
            assertEquals(insertedUuid, found!!.id)
            assertNull(found.description)
            assertEquals(RunType.NIGHTLY, found.type)
            assertEquals("tenant1", found.tenantId)
            assertNull(found.backfillId)
            assertEquals(OffsetDateTime.parse("2024-02-22T00:00:00+00:00"), found.runStartTime)
            assertNull(found.runEndTime)
            assertEquals(RunStatus.SUCCESS, found.status)
            assertNull(found.statusMessage)
            assertWasRecent(found.createDateTime)
            assertEquals(OffsetDateTime.parse("2024-02-21T23:59:00+00:00"), found.eventDateTime)
        }
    }
}
