package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.projectronin.interop.completeness.server.data.relational.DagDAO
import com.projectronin.interop.completeness.server.model.Dag
import com.projectronin.interop.completeness.server.model.DagNode
import graphql.execution.DataFetcherResult
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class DagHandler(val dagDAO: DagDAO) : Query {
    @GraphQLDescription("Get the Resource DAG")
    fun dag(dfe: DataFetchingEnvironment): DataFetcherResult<Dag> {
        val dagNodes =
            dagDAO.findAll()
                .groupBy { it.resource }
                .map { (resource, nodes) -> DagNode(resource, nodes.map { it.subscribesTo }) }
        val dag = Dag(dagNodes)
        return DataFetcherResult.newResult<Dag>()
            .data(dag)
            .build()
    }
}
