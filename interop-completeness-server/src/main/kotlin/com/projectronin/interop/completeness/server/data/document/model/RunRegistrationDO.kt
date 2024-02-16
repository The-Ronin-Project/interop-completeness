package com.projectronin.interop.completeness.server.data.document.model

import com.projectronin.json.eventinteropcompleteness.v1.RunRegistrationV1Schema
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

internal const val RUN_REGISTRATIONS = "run_registrations"

/**
 * Data object for run registrations as defined by [RunRegistrationDO].
 */
@Document(RUN_REGISTRATIONS)
data class RunRegistrationDO(
    @Id
    val id: ObjectId = ObjectId(),
    val event: RunRegistrationV1Schema,
)
