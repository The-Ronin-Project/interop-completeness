plugins {
    alias(libs.plugins.interop.gradle.junit)
    alias(libs.plugins.interop.gradle.spring.framework)
}

dependencies {
    implementation(libs.interop.kafka)
    testImplementation(libs.mockk)
    testImplementation(libs.interop.commonJackson)
}
