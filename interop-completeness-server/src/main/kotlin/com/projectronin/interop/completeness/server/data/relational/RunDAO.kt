package com.projectronin.interop.completeness.server.data.relational

import com.projectronin.interop.common.ktorm.dao.BaseInteropDAO
import com.projectronin.interop.common.ktorm.valueLookup
import com.projectronin.interop.completeness.server.data.relational.binding.RunDOs
import com.projectronin.interop.completeness.server.data.relational.model.RunDO
import org.ktorm.database.Database
import org.ktorm.dsl.insert
import org.ktorm.schema.Column
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLIntegrityConstraintViolationException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Repository
class RunDAO(database: Database) : BaseInteropDAO<RunDO, UUID>(database) {
    override val primaryKeyColumn: Column<UUID> = RunDOs.id

    /**
     * Find all [RunDO]s given a tenantId
     */
    fun getByTenantId(tenantId: String): List<RunDO> = database.valueLookup(tenantId, RunDOs.tenantId)

    /**
     * Find all [RunDO]s given a backfillId
     */
    fun getByBackfillId(backfillId: UUID): List<RunDO> = database.valueLookup(backfillId, RunDOs.backfillId)

    /**
     * Insert a [RunDO]. Throws a [SQLIntegrityConstraintViolationException] if the inserted [RunDO] is
     * not unique by the primary key id
     */
    @Transactional
    fun insert(runDO: RunDO): UUID {
        val uuid = UUID.randomUUID()
        logger.debug { "Inserting Run with UUID $uuid" }
        database.insert(RunDOs) {
            set(it.id, uuid)
            set(it.description, runDO.description)
            set(it.type, runDO.type)
            set(it.tenantId, runDO.tenantId)
            set(it.backfillId, runDO.backfillId)
            set(it.runStartTime, runDO.runStartTime)
            set(it.runEndTime, runDO.runEndTime)
            set(it.status, runDO.status)
            set(it.statusMessage, runDO.statusMessage)
            set(it.createDateTime, OffsetDateTime.now(ZoneOffset.UTC))
            set(it.eventDateTime, runDO.eventDateTime)
        }
        return uuid
    }
}
