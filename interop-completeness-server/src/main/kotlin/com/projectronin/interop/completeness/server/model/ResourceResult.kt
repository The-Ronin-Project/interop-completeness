package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("The result of a group of resource being loaded for one source")
data class ResourceResult(
    @GraphQLDescription("The ID of this record. A UUID")
    val id: String,
    @GraphQLDescription("The ID of the run this resource was loaded through. A UUID")
    val runId: String,
    @GraphQLDescription("The FHIR resource that triggered the load for these resources")
    val sourceResource: FhirResource,
    @GraphQLDescription("The Result Status of this group of resources")
    val result: NodeResultStatus?,
    @GraphQLDescription("Optional message")
    val message: String?,
    @GraphQLDescription("Type of the resource that has been loaded")
    val targetResourceType: String?,
    @GraphQLDescription("Resources that were successfully loaded off of the source resource")
    val successfulResources: List<String>?,
    @GraphQLDescription("Resources that were failed to load off of the source resource")
    val failedResources: List<FailedResource>?,
    @GraphQLDescription("Time that the resources were loaded in. Should be of the format YYYY-MM-DDThh:mm:ss+zz:zz")
    val time: String,
)
