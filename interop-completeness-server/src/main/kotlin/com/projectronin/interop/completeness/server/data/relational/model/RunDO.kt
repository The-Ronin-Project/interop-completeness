package com.projectronin.interop.completeness.server.data.relational.model

import com.projectronin.interop.completeness.server.model.RunStatus
import com.projectronin.interop.completeness.server.model.RunType
import org.ktorm.entity.Entity
import java.time.OffsetDateTime
import java.util.UUID

interface RunDO : Entity<RunDO> {
    companion object : Entity.Factory<RunDO>()

    var id: UUID
    var description: String?
    var type: RunType
    var tenantId: String
    var backfillId: UUID?
    var runStartTime: OffsetDateTime
    var runEndTime: OffsetDateTime?
    var status: RunStatus
    var statusMessage: String?
    var createDateTime: OffsetDateTime
    var eventDateTime: OffsetDateTime
}
