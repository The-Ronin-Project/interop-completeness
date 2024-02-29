package com.projectronin.interop.completeness.server.data.relational.binding

import com.projectronin.interop.common.ktorm.binding.binaryUuid
import com.projectronin.interop.completeness.server.data.relational.model.DagDO
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object DagDOs : Table<DagDO>("dag") {
    var id = binaryUuid("id").primaryKey().bindTo { it.id }
    var resource = varchar("resource_type").bindTo { it.resource }
    var subscribesTo = varchar("subscribes_to").bindTo { it.subscribesTo }
    var createDateTime = oracleUtcDateTime("create_dt_tm").bindTo { it.createDateTime }
    var eventDateTime = oracleUtcDateTime("event_dt_tm").bindTo { it.eventDateTime }
}
