package com.projectronin.interop.completeness.server.data.relational.binding

import com.projectronin.interop.common.ktorm.binding.binaryUuid
import com.projectronin.interop.common.ktorm.binding.utcDateTime
import com.projectronin.interop.completeness.server.data.relational.model.DagDO
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object DagDOs : Table<DagDO>("dag") {
    var id = binaryUuid("id").primaryKey().bindTo { it.id }
    var resource = varchar("resource").bindTo { it.resource }
    var subscribesTo = varchar("subscribes_to").bindTo { it.subscribesTo }
    var createDateTime = utcDateTime("create_dt_tm").bindTo { it.createDateTime }
    var eventDateTime = utcDateTime("event_dt_tm").bindTo { it.eventDateTime }
}
