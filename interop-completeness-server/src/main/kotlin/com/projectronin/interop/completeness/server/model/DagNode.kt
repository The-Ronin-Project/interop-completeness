package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("A node representing a resource type and the resources that consume it")
data class DagNode(
    @GraphQLDescription("The resource type")
    val resource: String,
    @GraphQLDescription("The resources that consume this resource")
    val consumedResources: List<String>,
)
