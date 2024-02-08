package com.projectronin.interop.completeness.server.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("A FHIR Resource")
data class FhirResource(
    @GraphQLDescription("The FHIR resource type")
    val resourceType: String,
    @GraphQLDescription("The identifier of the resource")
    val id: String,
)
