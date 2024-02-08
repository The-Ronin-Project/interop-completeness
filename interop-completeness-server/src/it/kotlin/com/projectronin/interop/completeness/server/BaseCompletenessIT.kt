package com.projectronin.interop.completeness.server

import com.projectronin.interop.common.http.spring.HttpSpringConfig
import org.junit.jupiter.api.AfterEach
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

abstract class BaseCompletenessIT {
    companion object {
        val docker =
            DockerComposeContainer(File(BaseCompletenessIT::class.java.getResource("/docker-compose-it.yaml")!!.file))
                .withExposedService("completeness-server", 8080)

        val start =
            docker
                .withLocalCompose(false)
                .waitingFor("completeness-server", Wait.forLogMessage(".*Started InteropCompletenessServerKt in.*", 1))
                .start()
    }

    private val serverPort by lazy {
        docker.getServicePort("completeness-server", 8080)
    }

    protected val serverUrl = "http://localhost:$serverPort"
    protected val httpClient = HttpSpringConfig().getHttpClient()

    @AfterEach
    fun tearDown() {
    }
}
