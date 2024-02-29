package com.projectronin.interop.completeness.server.data.relational.binding

import com.projectronin.interop.common.ktorm.binding.binaryUuid
import com.projectronin.interop.completeness.server.data.relational.model.RunDO
import com.projectronin.interop.completeness.server.model.RunStatus
import com.projectronin.interop.completeness.server.model.RunType
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.varchar

object RunDOs : Table<RunDO>("run") {
    var id = binaryUuid("id").primaryKey().bindTo { it.id }
    val description = varchar("description").bindTo { it.description }
    val type = enum<RunType>("type").bindTo { it.type }
    val tenantId = varchar("tenant_id").bindTo { it.tenantId }
    val backfillId = binaryUuid("backfill_id").bindTo { it.backfillId }
    val runStartTime = oracleUtcDateTime("run_start_time").bindTo { it.runStartTime }
    val runEndTime = oracleUtcDateTime("run_end_time").bindTo { it.runEndTime }
    val status = enum<RunStatus>("status").bindTo { it.status }
    val statusMessage = varchar("status_message").bindTo { it.statusMessage }
    val createDateTime = oracleUtcDateTime("create_dt_tm").bindTo { it.createDateTime }
    val eventDateTime = oracleUtcDateTime("event_dt_tm").bindTo { it.eventDateTime }
}
