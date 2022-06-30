package io.github.coronado.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ClientIT {

    val cfgNameTripleApiEndpoint = "TRIPLE_API_ENDPOINT"
    val cfgNameTripleApiClientId = "TRIPLE_API_CLIENT_ID"
    val cfgNameTripleApiClientSecret = "TRIPLE_API_CLIENT_SECRET"
    val config = mapOf(
        "endpoint" to System.getenv()[cfgNameTripleApiEndpoint]!!,
        "clientId" to System.getenv()[cfgNameTripleApiClientId]!!,
        "clientSecret" to System.getenv()[cfgNameTripleApiClientSecret]!!
    )

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test auth with varied scopes`() {
        Client.OAuth2Scope.values().forEach {
            val client = Client.Builder()
                .endpoint(config["endpoint"]!!)
                .clientId(config["clientId"]!!)
                .clientSecret(config["clientSecret"]!!)
                .scope(it)
                .build()
            assertEquals(client.endpoint, config["endpoint"]!!)
            assertEquals(client.clientId, config["clientId"]!!)
            assertEquals(client.clientSecret, config["clientSecret"]!!)
            assertEquals(it, client.scope)
        }
    }

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test auth expired tokens`() {
        val clientA = Client.Builder()
            .endpoint(config["endpoint"]!!)
            .clientId(config["clientId"]!!)
            .clientSecret(config["clientSecret"]!!)
            .scope(Client.OAuth2Scope.PARTNER_CONTENT_PROVIDERS)
            .build()
        val oldTokenA = clientA.token()
        Thread.sleep(5000)
        val newTokenA = clientA.token()
        assertEquals(oldTokenA, newTokenA)

        val clientB = Client.Builder()
            .endpoint(config["endpoint"]!!)
            .clientId(config["clientId"]!!)
            .clientSecret(config["clientSecret"]!!)
            .scope(Client.OAuth2Scope.PARTNER_CONTENT_PROVIDERS)
            .ttl(2)
            .build()
        val oldTokenB = clientB.token()
        Thread.sleep(5000)
        val newTokenB = clientB.token()
        assertNotEquals(oldTokenB, newTokenB)
    }

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test access tokens`() {
        val client = Client.Builder()
            .endpoint(config["endpoint"]!!)
            .clientId(config["clientId"]!!)
            .clientSecret(config["clientSecret"]!!)
            .scope(Client.OAuth2Scope.PARTNER_PUBLISHERS)
            .build()
        client.tokenPayload()
        val control = client.jwt["access_token"]!!
        assertEquals(client.accessToken, control)
    }

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test token type`() {
        val client = Client.Builder()
            .endpoint(config["endpoint"]!!)
            .clientId(config["clientId"]!!)
            .clientSecret(config["clientSecret"]!!)
            .scope(Client.OAuth2Scope.PARTNER_PUBLISHERS)
            .build()
        client.tokenPayload()
        val control = client.jwt["token_type"]!!
        assertEquals(client.tokenType(), control)
    }
}
