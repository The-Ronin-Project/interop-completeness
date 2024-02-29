import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask

plugins {
    java
    alias(libs.plugins.interop.sonarqube)
    alias(libs.plugins.interop.gradle.spring.boot)
    alias(libs.plugins.interop.gradle.docker.integration)
    alias(libs.plugins.graphql)
}

dependencies {
    implementation(project(":interop-completeness-topics"))
    api(enforcedPlatform(libs.kotlin.bom))
    implementation(platform(libs.spring.boot.parent))
    implementation(libs.product.spring.webflux.starter)
    implementation(libs.spring.boot.starter.data.mongodb)
    implementation(libs.spring.boot.starter.jdbc)

    implementation(libs.interop.event.completeness)

    implementation(libs.interop.common)
    implementation(libs.interop.commonHttp)
    implementation(libs.interop.commonJackson)
    implementation(libs.interop.commonKtorm)

    implementation(libs.bundles.graphql)
    implementation(libs.dd.trace.api)

    implementation(libs.ronin.common.kafka)
    implementation(libs.interop.kafka) {
        exclude(module = "ronin-kafka")
    }
    implementation(libs.springdoc.openapi.ui)

    implementation(libs.bundles.ojdbc)
    implementation(libs.ktorm.core)

    runtimeOnly(libs.liquibase.core)
    runtimeOnly(libs.ktorm.support.oracle)
    // Needed to format logs for DataDog
    runtimeOnly(libs.logstash.logback.encoder)

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.mockk)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.interop.commonTestDb)
    testRuntimeOnly(libs.mysql.connector.java)
    testImplementation(libs.rider.core)
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:mysql")

    itImplementation(project(":interop-completeness-topics"))
    itImplementation(project)
    itImplementation(platform(libs.testcontainers.bom))
    itImplementation("org.testcontainers:testcontainers")
    itImplementation(libs.bundles.ktor)
    // these dependencies need to be available at compile time for graphql to generate them for ITs
    compileOnly(libs.graphql.kotlin.client.ktor)
    compileOnly(libs.graphql.kotlin.client.serialization)
    itImplementation(libs.graphql.kotlin.client.ktor)
    itImplementation(libs.graphql.kotlin.client.serialization)
    itImplementation(libs.kotlinx.coroutines.core)
    itImplementation(libs.ktorm.core)
    itImplementation(libs.interop.commonHttp)
    itImplementation(libs.interop.commonJackson)
    itImplementation(libs.interop.commonKtorm)
    itImplementation(libs.interop.kafka.events.internal)
    itImplementation(libs.interop.event.completeness)
    itImplementation(libs.interop.fhir)
    itImplementation(libs.interop.fhir.generators)
    itImplementation(libs.interop.kafka)
    itImplementation(libs.ronin.kafka)
    itImplementation(libs.interop.kafka.testing.client)
    itImplementation(libs.ronin.test.data.generator)
    itImplementation(libs.bundles.ojdbc)
    itImplementation(libs.mongo)
    itImplementation(libs.ktorm.core)
    itImplementation(libs.interop.commonKtorm)
    itRuntimeOnly(libs.ktorm.support.oracle)
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.projectronin.interop.completeness.server"))
    schemaFile.set(file("${project.projectDir}/completenessSchema.graphql"))
}

// We want to tie the GraphQL schema generation to the kotlin compile step.
tasks.compileKotlin.get().finalizedBy(graphqlGenerateSDL)

// Configures the GraphQL Client that we're using in integration tests
graphql {
    client {
        val queryDirectory = "${project.projectDir}/src/it/resources/graphql"
        val schemaPath = "${project.projectDir}/completenessSchema.graphql"

        packageName = "com.projectronin.interop.completeness.client.generated"
        schemaFile = file(schemaPath)
        queryFiles =
            listOf(
                file("$queryDirectory/DAGQuery.graphql"),
            )
        serializer = GraphQLSerializer.KOTLINX
    }
}
