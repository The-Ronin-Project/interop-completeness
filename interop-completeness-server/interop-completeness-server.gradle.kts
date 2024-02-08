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

    implementation(libs.spring.kafka)

    implementation(libs.interop.common)
    implementation(libs.interop.commonHttp)
    implementation(libs.interop.commonJackson)

    implementation(libs.bundles.graphql)
    implementation(libs.dd.trace.api)

    implementation(libs.ronin.kafka)
    implementation(libs.springdoc.openapi.ui)

    // Needed to format logs for DataDog
    runtimeOnly(libs.logstash.logback.encoder)

    testImplementation(libs.mockk)

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
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.projectronin.interop.completeness.server"))
    schemaFile.set(file("${project.projectDir}/completenessSchema.graphql"))
}

// We want to tie the GraphQL schema generation to the kotlin compile step.
tasks.compileKotlin.get().finalizedBy(graphqlGenerateSDL)
