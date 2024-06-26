package com.projectronin.interop.completeness.topics

import com.projectronin.interop.kafka.model.KafkaTopic

data class InteropCompletenessKafkaTopic(
    override val systemName: String,
    override val topicName: String,
    override val dataSchema: String,
) : KafkaTopic {
    override val useLatestOffset: Boolean = true
}
