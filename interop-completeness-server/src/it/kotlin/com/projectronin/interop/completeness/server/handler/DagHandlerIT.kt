package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.projectronin.interop.common.http.auth.AuthMethod
import com.projectronin.interop.common.http.auth.AuthenticationConfig
import com.projectronin.interop.common.http.auth.Client
import com.projectronin.interop.common.http.auth.InteropAuthenticationService
import com.projectronin.interop.common.http.auth.Token
import com.projectronin.interop.completeness.client.generated.DAGQuery
import com.projectronin.interop.completeness.server.BaseCompletenessIT
import com.projectronin.interop.completeness.server.data.relational.DagDAO
import io.ktor.client.request.bearerAuth
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import java.net.URL
import java.time.OffsetDateTime

class DagHandlerIT : BaseCompletenessIT() {
    private val database = Database.connectWithSpringSupport(dataSource)
    private val dagDAO = DagDAO(database)
    private val authenticationService = InteropAuthenticationService(
        httpClient,
        AuthenticationConfig(
            token = Token(url = oath2Endpoint),
            audience = "https://interop-completeness.local.projectronin.io",
            client = Client("client-id", "client-secret"),
            method = AuthMethod.STANDARD,
        )
    )
    private val graphQlClient = GraphQLKtorClient(URL(graphqlEndpoint), httpClient)

    @Test
    fun `check DAG can be fetched`() {
        // Insert some test data
        val eventTime = OffsetDateTime.now()
        dagDAO.replace("MedicationAdministration", listOf("Patient", "Medication"), eventTime)

        val token = authenticationService.getAuthentication()
        val response =
            runBlocking {
                graphQlClient.execute(DAGQuery()) {
                    bearerAuth(token.accessToken)
                }
            }
        response.errors?.let { println("Found errors querying DAG: ${response.errors}") }
        response.data?.let { println("Found DAG: ${response.data}") }
        val medicationAdministrationNode = response.data!!.dag.nodes.first { it.resource == "MedicationAdministration" }
        assertEquals(2, medicationAdministrationNode.consumedResources.size)
        assertTrue("Patient" in medicationAdministrationNode.consumedResources)
        assertTrue("Medication" in medicationAdministrationNode.consumedResources)
    }
}
