package com.projectronin.interop.completeness.server

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ProductSpringBootConfigTest {
    @Test
    fun httpClient() {
        assertNotNull(ProductSpringBootConfig().okHttpClient())
    }
}
