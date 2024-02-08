package com.projectronin.interop.completeness.server.dataloaders

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.projectronin.interop.completeness.server.model.Run
import com.projectronin.interop.completeness.server.model.RunQuery
import com.projectronin.interop.completeness.server.service.RunService
import graphql.GraphQLContext
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/* TODO: Currently RunService is stubbed, and so this loader will always return the same data until
 * RunService has been implemented
 */
@Component
class RunLoader(private val runService: RunService) : KotlinDataLoader<RunQuery, Run> {
    override val dataLoaderName = NAME

    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<RunQuery, Run> =
        DataLoaderFactory.newDataLoader { queries ->
            CompletableFuture.supplyAsync {
                queries.map { query -> runService.getRun(query) }
            }
        }

    companion object {
        val NAME = RunLoader::class.simpleName!!
    }
}
