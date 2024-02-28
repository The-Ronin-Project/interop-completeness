package com.projectronin.interop.completeness.server

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.projectronin.interop.common.http.spring.HttpSpringConfig
import oracle.jdbc.pool.OracleDataSource
import org.junit.jupiter.api.AfterEach

abstract class BaseCompletenessIT {
    init {
        val wallet = BaseCompletenessIT::class.java.getResource("/tls_wallet/")!!

        System.setProperty("javax.net.ssl.keyStore", "${wallet.path}keystore.jks")
        System.setProperty("javax.net.ssl.keyStorePassword", "Longpassword1")
        System.setProperty("javax.net.ssl.trustStore", "${wallet.path}truststore.jks")
        System.setProperty("javax.net.ssl.trustStorePassword", "Longpassword1")
    }

    protected val serverUrl = "http://localhost:8080"
    protected val graphqlEndpoint = "$serverUrl/graphql"
    protected val httpClient = HttpSpringConfig().getHttpClient()

    @Suppress("ktlint:standard:max-line-length")
    protected val dataSource =
        OracleDataSource().apply {
            url =
                "jdbc:oracle:thin:@(description=(retry_count=0)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=localhost))(connect_data=(service_name=my_atp_low.adb.oraclecloud.com))(security=(ssl_server_dn_match=no)))"
            user = "admin"
            setPassword("Longpassword1")
        }

    @Suppress("ktlint:standard:max-line-length")
    private val mongoClient =
        MongoClients.create(
            MongoClientSettings.builder().applyConnectionString(
                ConnectionString(
                    "mongodb://admin:Longpassword1@localhost:27017/admin?authMechanism=PLAIN&authSource=\$external&ssl=true&retryWrites=false&loadBalanced=true",
                ),
            ).build(),
        )

    protected val mongoDatabase = mongoClient.getDatabase("admin")

    @AfterEach
    fun tearDown() {
    }
}
