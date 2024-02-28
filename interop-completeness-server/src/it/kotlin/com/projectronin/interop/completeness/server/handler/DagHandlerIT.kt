package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
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

    @Test
    fun `check DAG can be fetched`() {
        // Insert some test data
        val eventTime = OffsetDateTime.now()
        dagDAO.replace("MedicationAdministration", listOf("Patient", "Medication"), eventTime)

        val client = GraphQLKtorClient(URL(graphqlEndpoint))
        val token = "test"
        val response =
            runBlocking {
                client.execute(DAGQuery()) {
                    bearerAuth(token)
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
