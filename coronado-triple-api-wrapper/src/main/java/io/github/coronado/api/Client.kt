package io.github.coronado.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.Base64

class Client private constructor(
    val serviceDomain: String?,
    val clientId: String?,
    val clientSecret: String?,
    val scope: OAuth2Scope?,
    val expireTimeOverride: Long?
) {
    // Validated state in initializer
    init {
        if (serviceDomain == null) { throw IllegalArgumentException("endpoint must not be null") }
        if (clientId == null) { throw IllegalArgumentException("clientId must not be null") }
        if (clientSecret == null) { throw IllegalArgumentException("clientSecret must not be null") }
        if (scope == null) { throw IllegalArgumentException("scope must not be null") }
    }

    val tokenUrl = "https://auth.$serviceDomain/oauth2/token"
    val apiUrl = "https://api.$serviceDomain/"
    val httpClient = HttpClient.newBuilder().build()

    var jwt = emptyMap<String, String>()
    var accessToken = ""
    var expirationTime = LocalDateTime.now()
    private var tokenType = ""

    data class Builder(
        var serviceDomain: String? = null,
        var clientId: String? = null,
        var clientSecret: String? = null,
        var scope: OAuth2Scope? = null,
        var ttl: Long? = null
    ) {
        fun serviceDomain(serviceDomain: String) = apply { this.serviceDomain = serviceDomain }
        fun clientId(clientId: String) = apply { this.clientId = clientId }
        fun clientSecret(clientSecret: String) = apply { this.clientSecret = clientSecret }
        fun scope(scope: OAuth2Scope) = apply { this.scope = scope }
        fun ttl(ttl: Long) = apply { this.ttl = ttl }
        fun build() = Client(serviceDomain, clientId, clientSecret, scope, ttl)
    }

    enum class OAuth2Scope {
        PARTNER_PORTFOLIOS, // Manage Portfolio Manager details and portfolios of Publishers
        PARTNER_PUBLISHERS, // Manage Publisher details, Card Programs, Consumers, Card Accounts, and Transactions
        PARTNER_VIEW_OFFERS, // View recommended Offers, search Offers, and view Offer details
        PARTNER_CONTENT_PROVIDERS // Manage Content Provider details, Merchants, Merchant Locations, and Offers
    }

    private val scopes = mapOf(
        OAuth2Scope.PARTNER_PORTFOLIOS to "api.tripleup.com/partner.portfolios",
        OAuth2Scope.PARTNER_PUBLISHERS to "api.tripleup.com/partner.publishers",
        OAuth2Scope.PARTNER_VIEW_OFFERS to "api.tripleup.com/partner.view_offers",
        OAuth2Scope.PARTNER_CONTENT_PROVIDERS to "api.tripleup.com/partner.content_providers"
    )

    fun tokenPayload(): String {
        if (LocalDateTime.now().isAfter(expirationTime)) {
            getTokenPayload()
            setState()
        }
        return jwt.toString()
    }

    fun token(): String {
        tokenPayload()
        return accessToken
    }

    fun tokenType(): String {
        tokenPayload()
        return tokenType
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun getTokenPayload() {
        val response = httpClient.send(
            HttpRequest.newBuilder()
                .uri(URI(tokenUrl))
                .header("Authorization", buildBasicAuthHeaderValue(clientId!!, clientSecret!!))
                .header("content-type", "application/x-www-form-urlencoded")
                .POST(
                    HttpRequest.BodyPublishers.ofString(
                        "grant_type=client_credentials&scope=${scopes[OAuth2Scope.PARTNER_PUBLISHERS] ?: "none"}"
                    )
                )
                .build(),
            HttpResponse.BodyHandlers.ofString()
        )
        val responseMap = Moshi.Builder().build().adapter<Map<String, String>>().fromJson(response.body())!!
        if (response.statusCode() == 200) {
            jwt = responseMap
            setState()
        } else throw RuntimeException("Unable to acquire token: ${responseMap["error"]}")
    }

    private fun setState() {
        expirationTime = LocalDateTime.now().plusSeconds(expireTimeOverride ?: jwt["expires_in"]?.toLong() ?: 0L)
        accessToken = jwt["access_token"]!!
        tokenType = jwt["token_type"]!!
    }

    private fun buildBasicAuthHeaderValue(id: String, secret: String): String {
        return "Basic ${String(Base64.getEncoder().encode("$id:$secret".toByteArray(StandardCharsets.UTF_8)))}"
    }

    private fun ofFormData(data: Map<Any, Any>): HttpRequest.BodyPublisher? {
        val result = StringBuilder()
        for ((k, v) in data) {
            if (result.isNotEmpty()) result.append("&")
            val encodedName = URLEncoder.encode(k.toString(), StandardCharsets.UTF_8)
            val encodedValue = URLEncoder.encode(v.toString(), StandardCharsets.UTF_8)
            result.append(encodedName)
            if (encodedValue != null) result.append("=$encodedValue")
        }
        return HttpRequest.BodyPublishers.ofString(result.toString())
    }
}
