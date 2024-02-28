package com.projectronin.interop.completeness.server.event

import com.projectronin.interop.completeness.server.BaseCompletenessIT
import com.projectronin.interop.completeness.topics.CompletenessKafkaTopicConfig
import com.projectronin.interop.completeness.topics.InteropCompletenessKafkaTopic
import com.projectronin.interop.kafka.model.KafkaEvent
import com.projectronin.interop.kafka.model.PushResponse
import com.projectronin.interop.kafka.spring.KafkaBootstrapConfig
import com.projectronin.interop.kafka.spring.KafkaCloudConfig
import com.projectronin.interop.kafka.spring.KafkaConfig
import com.projectronin.interop.kafka.spring.KafkaPropertiesConfig
import com.projectronin.interop.kafka.spring.KafkaPublishConfig
import com.projectronin.interop.kafka.spring.KafkaRetrieveConfig
import com.projectronin.interop.kafka.spring.KafkaSaslConfig
import com.projectronin.interop.kafka.spring.KafkaSaslJaasConfig
import com.projectronin.interop.kafka.spring.KafkaSecurityConfig
import com.projectronin.interop.kafka.testing.client.KafkaTestingClient

abstract class BaseKafkaIT : BaseCompletenessIT() {
    private val kafkaConfig =
        KafkaConfig(
            cloud =
                KafkaCloudConfig(
                    vendor = "oci",
                    region = "us-phoenix-1",
                ),
            bootstrap = KafkaBootstrapConfig("localhost:9092"),
            publish = KafkaPublishConfig("interop-kafka-testing-client"),
            retrieve = KafkaRetrieveConfig("interop-kafka-testing-client"),
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

    val kafkaTestingClient = KafkaTestingClient("localhost:9092", kafkaConfig)
    val kafkaTopicConfig = CompletenessKafkaTopicConfig(kafkaConfig)

    fun <D> publishEvents(
        topic: InteropCompletenessKafkaTopic,
        events: List<KafkaEvent<D>>,
    ): PushResponse<KafkaEvent<D>> {
        return kafkaTestingClient.client.publishEvents(topic, events)
    }
}
