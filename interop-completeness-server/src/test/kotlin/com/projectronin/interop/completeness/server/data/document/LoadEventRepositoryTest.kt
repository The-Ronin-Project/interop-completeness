package com.projectronin.interop.completeness.server.data.document

import com.projectronin.interop.completeness.server.data.document.model.LoadEventDO
import com.projectronin.interop.completeness.server.util.loadEvent
import com.projectronin.interop.completeness.server.util.loadResource
import com.projectronin.interop.completeness.server.util.result
import com.projectronin.interop.completeness.server.util.successfulResource
import com.projectronin.json.eventinteropcompleteness.ResourceType
import com.projectronin.json.eventinteropcompleteness.v1.Result
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull

class LoadEventRepositoryTest : BaseMongoRepositoryTest() {
    @Autowired
    private lateinit var repository: LoadEventRepository

    private val eventForRun1234 =
        loadEvent {
            runId = "1234"
            sourceResource =
                loadResource {
                    resourceType = ResourceType.PATIENT
                    id = "5678"
                }
            result =
                result {
                    status = Result.Status.SUCCESS
                }
            targetResourceType = ResourceType.APPOINTMENT
            successfulResources =
                listOf(
                    successfulResource {
                        id = ""
                    },
                )
        }

    @BeforeEach
    fun purge() {
        repository.deleteAll()
    }

    @Test
    fun `insert works`() {
        val loadEventDO = LoadEventDO(event = eventForRun1234)
        val inserted = repository.insert(loadEventDO)
        assertNotNull(inserted.id)
        assertEquals(loadEventDO.event, inserted.event)

        assertEquals(1, repository.count())
    }

    @Test
    fun `can read by ID`() {
        val loadEventDO = LoadEventDO(event = eventForRun1234)
        val inserted = repository.insert(loadEventDO)

        val lookup = repository.findById(inserted.id.toString()).getOrNull()
        assertEquals(inserted, lookup)
    }

    @Test
    fun `can retrieve by run ID when none exist`() {
        val loadEventDO1 = LoadEventDO(event = eventForRun1234)
        val inserted1 = repository.insert(loadEventDO1)

        val result = repository.findByRunId("unknown-run")
        assertEquals(0, result.size)
    }

    @Test
    fun `can retrieve by run ID when one exists`() {
        val loadEventDO1 = LoadEventDO(event = eventForRun1234)
        val loadEventDO2 =
            LoadEventDO(
                event =
                    loadEvent {
                        runId = "run-1001"
                        sourceResource =
                            loadResource {
                                resourceType = ResourceType.PATIENT
                                id = "5678"
                            }
                        result =
                            result {
                                status = Result.Status.SUCCESS
                            }
                        targetResourceType = ResourceType.APPOINTMENT
                        successfulResources =
                            listOf(
                                successfulResource {
                                    id = ""
                                },
                            )
                    },
            )
        val inserted = repository.insert(listOf(loadEventDO1, loadEventDO2))

        val result = repository.findByRunId("run-1001")
        assertEquals(1, result.size)
        assertTrue(result.contains(inserted[1]))
    }

    @Test
    fun `can retrieve by run ID when multiple exist`() {
        val loadEventDO1 = LoadEventDO(event = eventForRun1234)
        val loadEventDO2 =
            LoadEventDO(
                event =
                    loadEvent {
                        runId = "run-1001"
                        sourceResource =
                            loadResource {
                                resourceType = ResourceType.PATIENT
                                id = "5678"
                            }
                        result =
                            result {
                                status = Result.Status.SUCCESS
                            }
                        targetResourceType = ResourceType.APPOINTMENT
                        successfulResources =
                            listOf(
                                successfulResource {
                                    id = ""
                                },
                            )
                    },
            )
        val loadEventDO3 =
            LoadEventDO(
                event =
                    loadEvent {
                        runId = "run-1001"
                        sourceResource =
                            loadResource {
                                resourceType = ResourceType.PATIENT
                                id = "9012"
                            }
                        result =
                            result {
                                status = Result.Status.SUCCESS
                            }
                        targetResourceType = ResourceType.APPOINTMENT
                        successfulResources =
                            listOf(
                                successfulResource {
                                    id = ""
                                },
                            )
                    },
            )
        val inserted = repository.insert(listOf(loadEventDO1, loadEventDO2, loadEventDO3))

        val result = repository.findByRunId("run-1001")
        assertEquals(2, result.size)
        assertTrue(result.containsAll(listOf(inserted[1], inserted[2])))
    }
}
