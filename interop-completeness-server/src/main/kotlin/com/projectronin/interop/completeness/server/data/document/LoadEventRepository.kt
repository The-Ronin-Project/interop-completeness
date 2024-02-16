package com.projectronin.interop.completeness.server.data.document

import com.projectronin.interop.completeness.server.data.document.model.LoadEventDO
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

/**
 * MongoDB repository for [LoadEventDO]s.
 */
interface LoadEventRepository : MongoRepository<LoadEventDO, String> {
    @Query("{'event.runId':'?0'}")
    fun findByRunId(runId: String): List<LoadEventDO>
}
