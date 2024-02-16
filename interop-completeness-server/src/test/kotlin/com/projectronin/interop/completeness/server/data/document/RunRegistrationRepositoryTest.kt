package com.projectronin.interop.completeness.server.data.document

import com.projectronin.interop.completeness.server.data.document.model.RunRegistrationDO
import com.projectronin.interop.completeness.server.util.loadResource
import com.projectronin.interop.completeness.server.util.runRegistration
import com.projectronin.json.eventinteropcompleteness.ResourceType
import com.projectronin.json.eventinteropcompleteness.v1.RunRegistrationV1Schema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull

class RunRegistrationRepositoryTest : BaseMongoRepositoryTest() {
    @Autowired
    private lateinit var repository: RunRegistrationRepository

    @BeforeEach
    fun purge() {
        repository.deleteAll()
    }

    @Test
    fun `insert works`() {
        val runRegistrationDO =
            RunRegistrationDO(
                event =
                    runRegistration {
                        id = "1234"
                        description = "Nightly run for tenant"
                        type = RunRegistrationV1Schema.Type.NIGHTLY
                        tenant = "tenant"
                        initialLoadElements =
                            listOf(
                                loadResource {
                                    resourceType = ResourceType.PATIENT
                                    id = "5678"
                                },
                            )
                        targetedResources = listOf("Patient", "Observation")
                    },
            )

        val inserted = repository.insert(runRegistrationDO)
        Assertions.assertNotNull(inserted.id)
        assertEquals(runRegistrationDO.event, inserted.event)

        assertEquals(1, repository.count())
    }

    @Test
    fun `can read by ID`() {
        val runRegistrationDO =
            RunRegistrationDO(
                event =
                    runRegistration {
                        id = "1234"
                        description = "Nightly run for tenant"
                        type = RunRegistrationV1Schema.Type.NIGHTLY
                        tenant = "tenant"
                        initialLoadElements =
                            listOf(
                                loadResource {
                                    resourceType = ResourceType.PATIENT
                                    id = "5678"
                                },
                            )
                        targetedResources = listOf("Patient", "Observation")
                    },
            )

        val inserted = repository.insert(runRegistrationDO)

        val lookup = repository.findById(inserted.id.toString()).getOrNull()
        assertEquals(inserted, lookup)
    }

    @Test
    fun `retrieve by run ID returns null when none exists`() {
        val runRegistrationDO =
            RunRegistrationDO(
                event =
                    runRegistration {
                        id = "1234"
                        description = "Nightly run for tenant"
                        type = RunRegistrationV1Schema.Type.NIGHTLY
                        tenant = "tenant"
                        initialLoadElements =
                            listOf(
                                loadResource {
                                    resourceType = ResourceType.PATIENT
                                    id = "5678"
                                },
                            )
                        targetedResources = listOf("Patient", "Observation")
                    },
            )

        repository.insert(runRegistrationDO)

        val lookup = repository.findByRunId("unknown-run")
        assertNull(lookup)
    }

    @Test
    fun `can retrieve by run ID`() {
        val runRegistrationDO =
            RunRegistrationDO(
                event =
                    runRegistration {
                        id = "1234"
                        description = "Nightly run for tenant"
                        type = RunRegistrationV1Schema.Type.NIGHTLY
                        tenant = "tenant"
                        initialLoadElements =
                            listOf(
                                loadResource {
                                    resourceType = ResourceType.PATIENT
                                    id = "5678"
                                },
                            )
                        targetedResources = listOf("Patient", "Observation")
                    },
            )

        val inserted = repository.insert(runRegistrationDO)

        val lookup = repository.findByRunId("1234")
        assertEquals(inserted, lookup)
    }
}
