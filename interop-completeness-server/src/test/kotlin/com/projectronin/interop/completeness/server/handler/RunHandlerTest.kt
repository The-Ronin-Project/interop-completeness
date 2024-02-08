package com.projectronin.interop.completeness.server.handler

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.projectronin.interop.completeness.server.dataloaders.InitialLoadResourcesLoader
import com.projectronin.interop.completeness.server.dataloaders.LoadedResourcesLoader
import com.projectronin.interop.completeness.server.dataloaders.ResourceNodeResultLoader
import com.projectronin.interop.completeness.server.dataloaders.RunLoader
import com.projectronin.interop.completeness.server.service.RunService
import graphql.GraphQLContext
import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class RunHandlerTest {
    private lateinit var runService: RunService
    private lateinit var dataLoaderRegistry: KotlinDataLoaderRegistry
    private lateinit var dataFetchingEnvironment: DataFetchingEnvironment
    private val runHandler = RunHandler()

    @BeforeEach
    fun setup() {
        // To be mocked in the future
        runService = RunService()

        // Set up the DataFetchingEnvironment
        val graphQLContext = GraphQLContext.newContext().build()
        dataLoaderRegistry = KotlinDataLoaderRegistry()
        dataLoaderRegistry.register(RunLoader.NAME, RunLoader(runService).getDataLoader(graphQLContext))
        dataLoaderRegistry.register(
            InitialLoadResourcesLoader.NAME,
            InitialLoadResourcesLoader(runService).getDataLoader(graphQLContext),
        )
        dataLoaderRegistry.register(
            ResourceNodeResultLoader.NAME,
            ResourceNodeResultLoader(runService).getDataLoader(graphQLContext),
        )
        dataLoaderRegistry.register(
            LoadedResourcesLoader.NAME,
            LoadedResourcesLoader(runService).getDataLoader(graphQLContext),
        )
        dataFetchingEnvironment =
            DataFetchingEnvironmentImpl.newDataFetchingEnvironment()
                .dataLoaderRegistry(dataLoaderRegistry)
                .graphQLContext(graphQLContext)
                .build()
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `Run Handler can find a run given a run ID`() {
        val runId = "123"
        val runFuture =
            runHandler.runs(
                runId,
                startDate = null,
                endDate = null,
                tenantMnemonic = null,
                backfillId = null,
                dataFetchingEnvironment,
            )
        dataLoaderRegistry.dispatchAll()
        val run = runFuture.get(10, TimeUnit.SECONDS)

        assertNotNull(run)
        assertEquals(runId, run.id)

        val loadedResourcesFuture = run.loadedResources(listOf("Patient"), dataFetchingEnvironment)
        dataLoaderRegistry.dispatchAll()
        val loadedResources = loadedResourcesFuture.get(10, TimeUnit.SECONDS)
        assertNotNull(loadedResources)
        assertEquals(1, loadedResources.size)

        val initialLoadResourcesFuture = run.initialLoadResources(dataFetchingEnvironment)
        dataLoaderRegistry.dispatchAll()
        val initialLoadResources = initialLoadResourcesFuture.get(10, TimeUnit.SECONDS)
        assertNotNull(initialLoadResources)
        assertEquals(1, initialLoadResources.size)

        val resourceResultsFuture = run.resourceResults(dataFetchingEnvironment)
        dataLoaderRegistry.dispatchAll()
        val resourceResults = resourceResultsFuture.get(10, TimeUnit.SECONDS)
        assertNotNull(resourceResults)
        assertEquals(1, resourceResults.size)
    }
}
