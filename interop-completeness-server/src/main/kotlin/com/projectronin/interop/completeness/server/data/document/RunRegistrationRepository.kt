package com.projectronin.interop.completeness.server.data.document

import com.projectronin.interop.completeness.server.data.document.model.RunRegistrationDO
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

/**
 * MongoDB repository for [RunRegistrationDO]s.
 */
interface RunRegistrationRepository : MongoRepository<RunRegistrationDO, String> {
    @Query("{'event.id':'?0'}")
    fun findByRunId(runId: String): RunRegistrationDO?
}
