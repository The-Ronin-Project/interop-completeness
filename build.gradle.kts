plugins {
    alias(libs.plugins.interop.gradle.docker.integration) apply false
    alias(libs.plugins.interop.gradle.junit) apply false
    alias(libs.plugins.interop.gradle.spring.boot) apply false
    alias(libs.plugins.interop.gradle.spring.framework) apply false
    alias(libs.plugins.interop.gradle.server.version)
    alias(libs.plugins.interop.gradle.version.catalog)
    alias(libs.plugins.interop.sonarqube)
}

subprojects {
    apply(plugin = "com.projectronin.interop.gradle.base")
    if (project.name != "interop-completeness-server") {
        apply(plugin = "com.projectronin.interop.gradle.server-publish")
    }
}
