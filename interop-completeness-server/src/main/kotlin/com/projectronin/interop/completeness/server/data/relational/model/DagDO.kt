package com.projectronin.interop.completeness.server.data.relational.model

import org.ktorm.entity.Entity
import java.time.OffsetDateTime
import java.util.UUID

interface DagDO : Entity<DagDO> {
    companion object : Entity.Factory<DagDO>()

    var id: UUID
    var resource: String
    var subscribesTo: String
    var createDateTime: OffsetDateTime
    var eventDateTime: OffsetDateTime
}
