package com.projectronin.interop.completeness.server

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
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SaslConfigs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KafkaSpringConfigTest {
    @Test
    fun `builds kafka config from cluster properties`() {
        val clusterProperties = mockk<ClusterProperties>()
        every { clusterProperties.getProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG) } returns "localhost:9092"
        every { clusterProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG) } returns "PLAINTEXT"
        every { clusterProperties.getProperty(SaslConfigs.SASL_MECHANISM) } returns "GSSAPI"
        every { clusterProperties.getProperty(SaslConfigs.SASL_JAAS_CONFIG) } returns "nothing"

        val kafkaConfig = KafkaSpringConfig().kafkaConfig(clusterProperties)

        val expected =
            KafkaConfig(
                cloud =
                    KafkaCloudConfig(
                        vendor = "oci",
                        region = "us-phoenix-1",
                    ),
                bootstrap = KafkaBootstrapConfig("localhost:9092"),
                publish = KafkaPublishConfig("interop-completeness"),
                retrieve = KafkaRetrieveConfig("interop-completeness"),
                properties =
                    KafkaPropertiesConfig(
                        security = KafkaSecurityConfig(protocol = "PLAINTEXT"),
                        sasl =
                            KafkaSaslConfig(
                                mechanism = "GSSAPI",
                                jaas = KafkaSaslJaasConfig("nothing"),
                            ),
                    ),
            )
        assertEquals(expected, kafkaConfig)
    }
}
