package com.projectronin.interop.completeness.server.util

import com.projectronin.json.eventinteropcompleteness.v1.FailedResource
import com.projectronin.json.eventinteropcompleteness.v1.LoadEventV1Schema
import com.projectronin.json.eventinteropcompleteness.v1.LoadResourceV1Schema
import com.projectronin.json.eventinteropcompleteness.v1.Result
import com.projectronin.json.eventinteropcompleteness.v1.RunRegistrationV1Schema
import com.projectronin.json.eventinteropcompleteness.v1.SuccessfulResource

fun loadEvent(block: LoadEventV1Schema.() -> Unit) = LoadEventV1Schema().apply(block)

fun loadResource(block: LoadResourceV1Schema.() -> Unit) = LoadResourceV1Schema().apply(block)

fun result(block: Result.() -> Unit) = Result().apply(block)

fun successfulResource(block: SuccessfulResource.() -> Unit) = SuccessfulResource().apply(block)

fun failedResource(block: FailedResource.() -> Unit) = FailedResource().apply(block)

fun runRegistration(block: RunRegistrationV1Schema.() -> Unit) = RunRegistrationV1Schema().apply(block)
