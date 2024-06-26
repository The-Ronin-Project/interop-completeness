package com.projectronin.interop.completeness.server.event

import com.projectronin.interop.completeness.server.data.relational.DagDAO
import com.projectronin.interop.completeness.topics.InteropCompletenessKafkaTopic
import com.projectronin.json.eventinteropcompleteness.v1.DagRegistrationV1Schema
import com.projectronin.kafka.config.ClusterProperties
import com.projectronin.kafka.config.StreamProperties
import com.projectronin.kafka.data.RoninEvent
import com.projectronin.kafka.streams.kafkaStreams
import com.projectronin.kafka.streams.stream
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

/**
 * Manages a Kafka Stream for reading in DAG registration events and persisting them to our DB.
 */
@Service
class DagStream(
    private val clusterProperties: ClusterProperties,
    private val dagDAO: DagDAO,
    @Qualifier("dagRegistration")
    private val dagRegistrationTopic: InteropCompletenessKafkaTopic,
) {
    private val logger = KotlinLogging.logger { }

    @PostConstruct
    fun initialize() {
        val topology =
            stream<String, RoninEvent<DagRegistrationV1Schema>>(dagRegistrationTopic.topicName) { kafkaStream ->
                kafkaStream.mapValues { event ->
                    logger.info { "Processing ${event.type} with id ${event.id}" }

                    processDagEvent(event)
                }
            }
        val config =
            StreamProperties(clusterProperties, "interop-completeness.dag-events.v1") {
                addDeserializationType<DagRegistrationV1Schema>("ronin.interop-completeness.dag.publish")
                put(
                    StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                    LogAndContinueExceptionHandler::class.java.name,
                )
            }

        kafkaStreams(topology, config).start()
    }

    fun processDagEvent(event: RoninEvent<DagRegistrationV1Schema>): List<UUID> {
        val dagRegistration = event.data

        val resource = dagRegistration.resource.value()
        val consumedResources = dagRegistration.consumedResources.map { it.value() }
        val eventDateTime = OffsetDateTime.ofInstant(event.time, ZoneOffset.UTC)

        return dagDAO.replace(resource, consumedResources, eventDateTime)
    }
}
