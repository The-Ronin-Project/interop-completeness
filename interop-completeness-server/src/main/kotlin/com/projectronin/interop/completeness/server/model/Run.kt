package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.extensions.getValueFromDataLoader
import com.projectronin.interop.completeness.server.dataloaders.InitialLoadResourcesLoader
import com.projectronin.interop.completeness.server.dataloaders.LoadedResourcesLoader
import com.projectronin.interop.completeness.server.dataloaders.LoadedResourcesLoaderInput
import com.projectronin.interop.completeness.server.dataloaders.ResourceNodeResultLoader
import graphql.schema.DataFetchingEnvironment
import java.util.concurrent.CompletableFuture

@GraphQLDescription("A data load run")
data class Run(
    @GraphQLDescription("The ID of the run. A UUID")
    val id: String,
    @GraphQLDescription("Optional description for the Run")
    val description: String?,
    @GraphQLDescription("The way the run was triggered")
    val type: RunType,
    @GraphQLDescription("The corresponding backfill ID if the run was triggered by a Backfill. A UUID")
    val backfillId: String?,
    @GraphQLDescription("The time the run had started. Should be of the format YYYY-MM-DDThh:mm:ss+zz:zz")
    var runRangeStartTime: String,
    @GraphQLDescription("The time the run had ended if applicable. Should be of the format YYYY-MM-DDThh:mm:ss+zz:zz")
    var runRangeEndTime: String?,
    @GraphQLDescription("Status of the run")
    val result: RunStatus,
    @GraphQLDescription("Tenant from which the data was loaded")
    val tenant: Tenant,
) {
    @GraphQLDescription("The results of all the data loads for the run")
    fun resourceResults(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<List<ResourceNodeResult>> =
        dataFetchingEnvironment.getValueFromDataLoader(ResourceNodeResultLoader.NAME, id)

    @GraphQLDescription("The list of initial resources in this run that trigger the loading of other resources")
    fun initialLoadResources(dataFetchingEnvironment: DataFetchingEnvironment): CompletableFuture<List<FhirResource>> =
        dataFetchingEnvironment.getValueFromDataLoader(InitialLoadResourcesLoader.NAME, id)

    @GraphQLDescription(
        "The resources in this run that that had loading triggered by one of the initial resources grouped by resource type",
    )
    fun loadedResources(
        resourceTypes: List<String>,
        dataFetchingEnvironment: DataFetchingEnvironment,
    ): CompletableFuture<List<ResourceNodeResult>> =
        dataFetchingEnvironment.getValueFromDataLoader(
            LoadedResourcesLoader.NAME,
            LoadedResourcesLoaderInput(id, resourceTypes),
        )
}
