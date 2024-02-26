package com.projectronin.interop.completeness.server.data.relational

import com.projectronin.interop.common.ktorm.dao.BaseInteropDAO
import com.projectronin.interop.common.ktorm.valueLookup
import com.projectronin.interop.completeness.server.data.relational.binding.DagDOs
import com.projectronin.interop.completeness.server.data.relational.model.DagDO
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.schema.Column
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLIntegrityConstraintViolationException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

/**
 * Relational DB DAO for [DagDO]s.
 */
@Repository
class DagDAO(database: Database) : BaseInteropDAO<DagDO, UUID>(database) {
    override val primaryKeyColumn: Column<UUID> = DagDOs.id

    /**
     * Find [DagDO]s by the subscribesTo field
     */
    fun findResourcesThatSubscribeTo(subscribesTo: String): List<DagDO> = database.valueLookup(subscribesTo, DagDOs.subscribesTo)

    /**
     * Find [DagDO]s by the resource field
     */
    fun findResourcesThatTrigger(resource: String): List<DagDO> = database.valueLookup(resource, DagDOs.resource)

    /**
     * Insert a [DagDO]. Throws a [SQLIntegrityConstraintViolationException] if the inserted [DagDO] is
     * not Unique by (resource, subscribesTo) or not unique by the primary key id
     */
    @Transactional
    private fun insert(dagDO: DagDO): UUID {
        val uuid = UUID.randomUUID()
        logger.debug { "Inserting DAG edge for resource ${dagDO.resource} subscribes_to ${dagDO.subscribesTo} with UUID $uuid" }
        database.insert(DagDOs) {
            set(it.id, uuid)
            set(it.resource, dagDO.resource)
            set(it.subscribesTo, dagDO.subscribesTo)
            set(it.createDateTime, OffsetDateTime.now(ZoneOffset.UTC))
            set(it.eventDateTime, dagDO.eventDateTime)
        }
        return uuid
    }

    /**
     * Delete all [DagDO] by equality on the resource field
     * Returns the number of [DagDO]s deleted from the DB
     */
    @Transactional
    private fun delete(resource: String) =
        database.delete(DagDOs) {
            (it.resource eq resource)
        }

    /**
     * Replace all existing [DagDO]s for a resource with new ones
     */
    @Transactional
    fun replace(
        resource: String,
        consumedResources: List<String>,
        eventDateTime: OffsetDateTime,
    ): List<UUID> {
        val deleted = delete(resource)
        logger.debug { "Deleted $deleted DagDOs with resource $resource" }

        val created =
            consumedResources.map {
                val dagDO =
                    DagDO {
                        this.resource = resource
                        this.subscribesTo = it
                        this.createDateTime = OffsetDateTime.now(ZoneOffset.UTC)
                        this.eventDateTime = eventDateTime
                    }
                insert(dagDO)
            }

        return created
    }
}
