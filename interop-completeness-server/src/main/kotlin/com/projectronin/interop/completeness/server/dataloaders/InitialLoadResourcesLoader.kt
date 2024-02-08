package com.projectronin.interop.completeness.server.dataloaders

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.projectronin.interop.completeness.server.model.FhirResource
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
class InitialLoadResourcesLoader(private val runService: RunService) : KotlinDataLoader<String, List<FhirResource>> {
    override val dataLoaderName = NAME

    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<String, List<FhirResource>> =
        DataLoaderFactory.newDataLoader { ids ->
            CompletableFuture.supplyAsync {
                ids.map { id -> runService.getInitialLoadResources(id) }
            }
        }

    companion object {
        val NAME = InitialLoadResourcesLoader::class.simpleName!!
    }
}
