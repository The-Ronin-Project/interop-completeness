package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("The resources loaded for a given Resource Type")
data class ResourceNodeResult(
    @GraphQLDescription("The type of the resources being loaded")
    val resourceType: String,
    @GraphQLDescription("The aggregate status of the resources' statuses")
    val resourceResult: RunStatus,
    @GraphQLDescription("The time this aggregation was last updated. Should be of the format YYYY-MM-DDThh:mm:ss+zz:zz")
    val lastUpdated: String,
    @GraphQLDescription("The results of this load grouped by the source resource that triggered them")
    val resourceDetails: List<ResourceResult>,
)
