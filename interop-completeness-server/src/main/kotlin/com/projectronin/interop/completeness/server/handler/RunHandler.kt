package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.projectronin.interop.completeness.server.dataloaders.RunLoader
import com.projectronin.interop.completeness.server.model.Run
import com.projectronin.interop.completeness.server.model.RunQuery
import datadog.trace.api.Trace
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class RunHandler : Query {
    @GraphQLDescription("Search for runs")
    @Trace
    fun runs(
        runId: String?,
        startDate: String?,
        endDate: String?,
        tenantMnemonic: String?,
        backfillId: String?,
        dfe: DataFetchingEnvironment,
    ): CompletableFuture<Run> =
        dfe.getDataLoader<RunQuery, Run>(RunLoader.NAME)
            .load(RunQuery(runId, startDate, endDate, tenantMnemonic, backfillId), dfe)
}
