package com.projectronin.interop.completeness.server.data.document.model

import com.projectronin.json.eventinteropcompleteness.v1.LoadEventV1Schema
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

internal const val LOAD_EVENTS = "load_events"

/**
 * Data object for load events as defined by [LoadEventV1Schema].
 */
@Document(LOAD_EVENTS)
data class LoadEventDO(
    @Id
    val id: ObjectId = ObjectId(),
    val event: LoadEventV1Schema,
)
