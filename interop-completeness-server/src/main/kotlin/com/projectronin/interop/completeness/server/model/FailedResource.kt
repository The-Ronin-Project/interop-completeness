package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("A resource that failed to load")
data class FailedResource(
    @GraphQLDescription("The identifier of the failed resource")
    val id: String,
    @GraphQLDescription("Optional error message")
    val message: String?,
)
