plugins {
    alias(libs.plugins.interop.gradle.junit)
    alias(libs.plugins.interop.gradle.spring.framework)
}

dependencies {
    implementation(platform(libs.spring.boot.parent))

    implementation(libs.interop.common)
    implementation(libs.interop.commonHttp)
    implementation(libs.interop.commonJackson)
    implementation(libs.jakarta.validation.api)

    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
}
