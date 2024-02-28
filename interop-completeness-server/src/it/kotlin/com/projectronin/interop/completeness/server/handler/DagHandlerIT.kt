package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.projectronin.interop.completeness.client.generated.DAGQuery
import com.projectronin.interop.completeness.server.BaseCompletenessIT
import com.projectronin.interop.completeness.server.data.relational.DagDAO
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.ktorm.database.Database
import java.time.OffsetDateTime

class DagHandlerIT : BaseCompletenessIT() {
    private val database = Database.connectWithSpringSupport(dataSource)
    private val dagDAO = DagDAO(database)

    @Test
    fun `check DAG can be fetched`() {
        // Insert some test data
        val eventTime = OffsetDateTime.now()
        dagDAO.replace("MedicationAdministration", listOf("Patient", "Medication"), eventTime)

        val client = GraphQLWebClient(url = graphqlEndpoint)
        val response =
            runBlocking {
                client.execute(DAGQuery())
            }
        response.errors?.let { println("Found errors querying DAG: ${response.errors}") }
        response.data?.let { println("Found DAG: ${response.data}") }
        val medicationAdministrationNode = response.data!!.dag.nodes.first { it.resource == "MedicationAdministration" }
        assertEquals(2, medicationAdministrationNode.consumedResources.size)
        assertTrue("Patient" in medicationAdministrationNode.consumedResources)
        assertTrue("Medication" in medicationAdministrationNode.consumedResources)
    }
}
