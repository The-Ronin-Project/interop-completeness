package com.projectronin.interop.completeness.server.model

data class RunQuery(
    val runId: String?,
    val startDate: String?,
    val endDate: String?,
    val tenantMnemonic: String?,
    val backfillId: String?,
)
