package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Tenant")
data class Tenant(
    @GraphQLDescription("Tenant Mnemonic")
    val mnemonic: String,
    @GraphQLDescription("Optional tenant name")
    val name: String?,
)
