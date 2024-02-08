package com.projectronin.interop.completeness.server.service

import com.projectronin.interop.completeness.server.model.FhirResource
import com.projectronin.interop.completeness.server.model.NodeResultStatus
import com.projectronin.interop.completeness.server.model.ResourceNodeResult
import com.projectronin.interop.completeness.server.model.ResourceResult
import com.projectronin.interop.completeness.server.model.Run
import com.projectronin.interop.completeness.server.model.RunQuery
import com.projectronin.interop.completeness.server.model.RunStatus
import com.projectronin.interop.completeness.server.model.RunType
import com.projectronin.interop.completeness.server.model.Tenant
import org.springframework.stereotype.Component
import java.util.Date

@Component
class RunService {
    // TODO: Awaiting DB Layer to be implemented
    fun getRun(query: RunQuery) =
        Run(
            id = query.runId!!,
            description = "dummy data",
            type = RunType.BACKFILL,
            backfillId = "123",
            runRangeStartTime = Date().time.toString(),
            runRangeEndTime = null,
            result = RunStatus.INCOMPLETE,
            tenant = Tenant("epic", null),
        )

    fun getResourceNode(runId: String): List<ResourceNodeResult> =
        listOf(
            ResourceNodeResult(
                resourceType = "Medication",
                resourceResult = RunStatus.SUCCESS,
                lastUpdated = "Today",
                resourceDetails =
                    listOf(
                        ResourceResult(
                            id = "123",
                            runId = runId,
                            sourceResource =
                                FhirResource(
                                    "Medication",
                                    "epic-123",
                                ),
                            result = NodeResultStatus.SUCCESS,
                            message = "Upsert successful",
                            targetResourceType = "MedicationAdministration",
                            successfulResources = listOf(),
                            failedResources = listOf(),
                            time = "today",
                        ),
                    ),
            ),
        )

    fun getInitialLoadResources(runId: String): List<FhirResource> =
        listOf(
            FhirResource(
                resourceType = "Patient",
                id = "abc",
            ),
        )

    fun getLoadedResources(
        runId: String,
        resourceTypes: List<String>,
    ): List<ResourceNodeResult> =
        listOf(
            ResourceNodeResult(
                resourceType = "Medication",
                resourceResult = RunStatus.SUCCESS,
                lastUpdated = "Today",
                resourceDetails =
                    listOf(
                        ResourceResult(
                            id = "123",
                            runId = runId,
                            sourceResource =
                                FhirResource(
                                    "Medication",
                                    "epic-123",
                                ),
                            result = NodeResultStatus.SUCCESS,
                            message = "Upsert successful",
                            targetResourceType = "MedicationAdministration",
                            successfulResources = listOf(),
                            failedResources = listOf(),
                            time = "today",
                        ),
                    ),
            ),
        )
}
