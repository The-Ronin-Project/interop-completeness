import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask

plugins {
    java
    alias(libs.plugins.interop.sonarqube)
    alias(libs.plugins.interop.gradle.spring.boot)
    alias(libs.plugins.interop.gradle.docker.integration)
    alias(libs.plugins.graphql)
}

dependencies {
    api(enforcedPlatform(libs.kotlin.bom))
    implementation(platform(libs.spring.boot.parent))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.mongodb)

    implementation(libs.interop.event.completeness)

    implementation(libs.spring.kafka)

    implementation(libs.interop.common)
    implementation(libs.interop.commonHttp)
    implementation(libs.interop.commonJackson)

    implementation(libs.bundles.graphql)
    implementation(libs.dd.trace.api)

    implementation(libs.ronin.kafka)
    implementation(libs.springdoc.openapi.ui)

    implementation(libs.bundles.ojdbc)

    runtimeOnly(libs.liquibase.core)
    // Needed to format logs for DataDog
    runtimeOnly(libs.logstash.logback.encoder)

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.mockk)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")

    itImplementation(project(":interop-completeness-client"))
    itImplementation(project)
    itImplementation(platform(libs.testcontainers.bom))
    itImplementation("org.testcontainers:testcontainers")
    itImplementation(libs.bundles.ktor)
    itImplementation(libs.kotlinx.coroutines.core)
    itImplementation(libs.interop.commonHttp)
    itImplementation(libs.interop.commonJackson)
    itImplementation(libs.interop.kafka.events.internal)
    itImplementation(libs.interop.fhir)
    itImplementation(libs.interop.fhir.generators)
    itImplementation(libs.interop.kafka)
    itImplementation(libs.interop.kafka.testing.client)
    itImplementation(libs.ronin.test.data.generator)
    itImplementation(libs.bundles.ojdbc)
    itImplementation(libs.mongo)
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.projectronin.interop.completeness.server"))
    schemaFile.set(file("${project.projectDir}/completenessSchema.graphql"))
}

// We want to tie the GraphQL schema generation to the kotlin compile step.
tasks.compileKotlin.get().finalizedBy(graphqlGenerateSDL)

// Colima and Testcontainers appear to have issues on M1/M2, but we saw success with the actual Docker commands, so we're rewiring this project to not use Testcontainers
val runDocker =
    tasks.create("runDocker") {
        doLast {
            exec {
                workingDir = file("./src/it/resources")
                commandLine("docker compose -f docker-compose-it.yaml up -d --wait --wait-timeout 600".split(" "))
            }
        }
    }

tasks.named("itSetup").get().finalizedBy(runDocker)
val itTask = tasks.named("it").get()
itTask.dependsOn(runDocker)

val stopDocker =
    tasks.create("stopDocker") {
        doLast {
            exec {
                workingDir = file("./src/it/resources")
                commandLine("docker compose -f docker-compose-it.yaml down".split(" "))
            }
        }
    }

itTask.finalizedBy(stopDocker)
