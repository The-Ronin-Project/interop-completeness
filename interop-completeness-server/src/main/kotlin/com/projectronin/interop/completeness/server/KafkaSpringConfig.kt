package com.projectronin.interop.completeness.server

import com.projectronin.interop.completeness.topics.CompletenessKafkaTopicConfig
import com.projectronin.interop.kafka.spring.KafkaBootstrapConfig
import com.projectronin.interop.kafka.spring.KafkaCloudConfig
import com.projectronin.interop.kafka.spring.KafkaConfig
import com.projectronin.interop.kafka.spring.KafkaPropertiesConfig
import com.projectronin.interop.kafka.spring.KafkaPublishConfig
import com.projectronin.interop.kafka.spring.KafkaRetrieveConfig
import com.projectronin.interop.kafka.spring.KafkaSaslConfig
import com.projectronin.interop.kafka.spring.KafkaSaslJaasConfig
import com.projectronin.interop.kafka.spring.KafkaSecurityConfig
import com.projectronin.kafka.config.ClusterProperties
import com.projectronin.kafka.spring.config.KafkaConfiguration
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SaslConfigs
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    KafkaConfiguration::class,
    CompletenessKafkaTopicConfig::class,
)
class KafkaSpringConfig {
    // TODO: Fix needing both of these. This is just a config to prevent us needing to redefine multiple values
    @Bean
    fun kafkaConfig(clusterProperties: ClusterProperties): KafkaConfig =
        KafkaConfig(
            cloud =
                KafkaCloudConfig(
                    vendor = "oci",
                    region = "us-phoenix-1",
                ),
            bootstrap = KafkaBootstrapConfig(clusterProperties.getProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG)),
            publish = KafkaPublishConfig("interop-completeness"),
            retrieve = KafkaRetrieveConfig("interop-completeness"),
            properties =
                KafkaPropertiesConfig(
                    security = KafkaSecurityConfig(protocol = clusterProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG)),
                    sasl =
                        KafkaSaslConfig(
                            mechanism = clusterProperties.getProperty(SaslConfigs.SASL_MECHANISM),
                            jaas = KafkaSaslJaasConfig(clusterProperties.getProperty(SaslConfigs.SASL_JAAS_CONFIG)),
                        ),
                ),
        )
}
