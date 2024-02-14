package com.projectronin.interop.completeness.server

import org.junit.jupiter.api.Test

class RunHandlerIT : BaseCompletenessIT() {
    @Test
    fun `temporary test to check docker starts up`() {
        println("Successfully started up Completeness Server on $serverUrl")
        println()

        println("Found the following tables in the DB:")
        dataSource.connection.prepareStatement("SELECT table_name FROM user_tables").executeQuery().use {
            while (it.next()) {
                println(it.getString(1))
            }
        }
        println()

        val count = mongoDatabase.getCollection("test").countDocuments()
        println("test collection in MongoDB currently has $count documents")
    }
}
