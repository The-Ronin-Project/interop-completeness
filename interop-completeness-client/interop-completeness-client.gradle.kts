plugins {
    alias(libs.plugins.graphql)
}

dependencies {
    api(libs.graphql.kotlin.client.spring)
}

graphql {
    client {
        val queryDirectory = "${project.projectDir}/src/main/resources"
        val schemaPath = "${project.projectDir}/../interop-completeness-server/completenessSchema.graphql"

        packageName = "com.projectronin.interop.completeness.client.generated"
        schemaFile = file(schemaPath)
        queryFiles =
            listOf(
                file("$queryDirectory/DAGQuery.graphql"),
            )
    }
}

ktlint {
    filter {
        exclude {
            it.file.path.contains("/generated/")
        }
    }
}

sonar {
    isSkipProject = true
}
