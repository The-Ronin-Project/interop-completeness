package com.projectronin.interop.completeness.server.dataloaders

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.projectronin.interop.completeness.server.model.ResourceNodeResult
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
class LoadedResourcesLoader(private val runService: RunService) :
    KotlinDataLoader<LoadedResourcesLoaderInput, List<ResourceNodeResult>> {
    override val dataLoaderName = NAME

    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<LoadedResourcesLoaderInput, List<ResourceNodeResult>> =
        DataLoaderFactory.newDataLoader { inputs ->
            CompletableFuture.supplyAsync {
                inputs.map { input -> runService.getLoadedResources(input.runId, input.resourceTypes) }
            }
        }

    companion object {
        val NAME = LoadedResourcesLoader::class.simpleName!!
    }
}

data class LoadedResourcesLoaderInput(val runId: String, val resourceTypes: List<String>)
