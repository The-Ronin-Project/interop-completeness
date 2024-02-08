package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("A collection of all the Resources Types loaded by Mirth as well as the dependencies between them")
data class Dag(
    val nodes: List<DagNode>,
) {
    constructor(vararg nodes: DagNode) : this(nodes.asList())
}
