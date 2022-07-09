package io.github.coronado.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ClientIT {

    val jwtAccessTokenPropertyName = "access_token"
    val jwtTokenTypePropertyName = "token_type"
    val cfgNameTripleApiServiceDomain = "TRIPLE_API_SERVICE_DOMAIN"
    val cfgNameTripleApiClientId = "TRIPLE_API_CLIENT_ID"
    val cfgNameTripleApiClientSecret = "TRIPLE_API_CLIENT_SECRET"
    val config = mapOf(
        cfgNameTripleApiServiceDomain to System.getenv()[cfgNameTripleApiServiceDomain]!!,
        cfgNameTripleApiClientId to System.getenv()[cfgNameTripleApiClientId]!!,
        cfgNameTripleApiClientSecret to System.getenv()[cfgNameTripleApiClientSecret]!!
    )

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test auth with varied scopes`() {
        Client.OAuth2Scope.values().forEach {
            val client = Client.Builder()
                .serviceDomain(config[cfgNameTripleApiServiceDomain]!!)
                .clientId(config[cfgNameTripleApiClientId]!!)
                .clientSecret(config[cfgNameTripleApiClientSecret]!!)
                .scope(it)
                .build()
            assertEquals(client.serviceDomain, config[cfgNameTripleApiServiceDomain]!!)
            assertEquals(client.clientId, config[cfgNameTripleApiClientId]!!)
            assertEquals(client.clientSecret, config[cfgNameTripleApiClientSecret]!!)
            assertEquals(it, client.scope)
        }
    }

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test auth expired tokens`() {
        val clientA = Client.Builder()
            .serviceDomain(config[cfgNameTripleApiServiceDomain]!!)
            .clientId(config[cfgNameTripleApiClientId]!!)
            .clientSecret(config[cfgNameTripleApiClientSecret]!!)
            .scope(Client.OAuth2Scope.PARTNER_CONTENT_PROVIDERS)
            .build()
        val oldTokenA = clientA.token()
        Thread.sleep(5000)
        val newTokenA = clientA.token()
        assertEquals(oldTokenA, newTokenA)

        val clientB = Client.Builder()
            .serviceDomain(config[cfgNameTripleApiServiceDomain]!!)
            .clientId(config[cfgNameTripleApiClientId]!!)
            .clientSecret(config[cfgNameTripleApiClientSecret]!!)
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
            .serviceDomain(config[cfgNameTripleApiServiceDomain]!!)
            .clientId(config[cfgNameTripleApiClientId]!!)
            .clientSecret(config[cfgNameTripleApiClientSecret]!!)
            .scope(Client.OAuth2Scope.PARTNER_PUBLISHERS)
            .build()
        client.tokenPayload()
        val control = client.jwt[jwtAccessTokenPropertyName]!!
        assertEquals(client.accessToken, control)
    }

    @Test // @Ignore("This test needs real credentials and makes live connections to an endpoint")
    fun `test token type`() {
        val client = Client.Builder()
            .serviceDomain(config[cfgNameTripleApiServiceDomain]!!)
            .clientId(config[cfgNameTripleApiClientId]!!)
            .clientSecret(config[cfgNameTripleApiClientSecret]!!)
            .scope(Client.OAuth2Scope.PARTNER_PUBLISHERS)
            .build()
        client.tokenPayload()
        val control = client.jwt[jwtTokenTypePropertyName]!!
        assertEquals(client.tokenType(), control)
    }
}
