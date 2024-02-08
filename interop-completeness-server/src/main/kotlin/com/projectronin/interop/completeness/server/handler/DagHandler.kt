package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.projectronin.interop.completeness.server.model.Dag
import com.projectronin.interop.completeness.server.model.DagNode
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class DagHandler : Query {
    @GraphQLDescription("Get the Resource DAG")
    fun dag(dfe: DataFetchingEnvironment): DataFetcherResult<Dag> {
        return DataFetcherResult.newResult<Dag>()
            .data(dummyData())
            .build()
    }

    companion object {
        fun dummyData() =
            Dag(
                DagNode("Patient", emptyList()),
                DagNode("Medication", listOf("Patient")),
                DagNode("Appointment", listOf("Medication", "Patient")),
                DagNode("MedicationAdministration", listOf("Medication")),
            )
    }
}
