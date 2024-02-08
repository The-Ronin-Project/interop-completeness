package com.projectronin.interop.completeness.client.spring

import com.projectronin.interop.common.http.spring.HttpSpringConfig
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan("com.projectronin.interop.completeness.client")
@Import(HttpSpringConfig::class)
class CompletenessClientSpringConfig
