package io.github.coronado.baseobjects

import com.squareup.moshi.Json
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Currency

/**
 * Example data classes used for prototyping kotlin representations of Triple/Coronado API data objects
 *
 * @author Coronado Project Team
 */

// define objects with fields, camelcase the properties, assign datatypes, assign nullability

data class Address(
    @Json(name = "complete_address") val completeAddress: String, // The complete address, as would be written out for mail delivery or route navigation. ex: "7370 BAKER ST STE 100\nPITTSBURGH, PA 15206"
    @Json(name = "line_1") val line1: String?, // ex: "7370 BAKER ST STE 100"
    @Json(name = "line_2") val line2: String?,
    val locality: String?, // City or locality name, ex: "PITTSBURGH"
    val province: String, // State abbreviation or province name, ex: "PA"
    @Json(name = "postal_code") val postalCode: String?, // ZIP Code™, ZIP+4, or postal code, ex: "15206"
    @Json(name = "country_code") val countryCode: String?, // 2-letter ISO-3166 country code, ex: "US"
    val latitude: Latitude,
    val longitude: Longitude
)

typealias Latitude = BigDecimal
typealias Longitude = BigDecimal

data class CardAccount(
    val id: String,
    val cardProgramId: String,
    @Json(name = "externalId") val cardAccountExternalId: String,
    val status: CardAccountStatus,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
)

enum class CardAccountStatus { ENROLLED, NOT_ENROLLED, CLOSED }

data class CardAccountIdentifier(
    val publisherExternalId: String,
    val cardProgramExternalId: String,
    @Json(name = "externalId") val cardAccountExternalId: String,
    val status: CardAccountStatus
)

data class CardProgram(
    val id: String,
    val publisherId: String,
    val externalId: String,
    val name: String,
    val programCurrency: Currency,
    val cardBins: Set<String>,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
)

data class MerchantCategoryCode(
    val code: String,
    val description: String
)

data class MerchantLocation(
    val id: String,
    val locationName: String,
    val online: Boolean,
    @Json(name = "email") val emailAddress: String,
    val phoneNumber: String,
    val address: Address
)

data class Offer(
    val id: String,
    val activationRequired: Boolean, // is this really true/false ?
    val activationDurationInDays: Int, // is this integer or decimal ?
    val currencyCode: Currency,
    val category: String, // define this as an enum?  sample value: "AUTOMOTIVE"
    val categoryTags: String, // should this be a collection ?
    val categoryMccs: Set<String>, // should this be a collection of an embedded object type?
    val description: String,
    val effectiveDate: String, // should this be a tz-aware instant type ?
    val excludedDates: Set<String>, // should this be a collection of tz-aware instant type?
    val expirationDate: String, // should this be a tz-aware instant type?
    val activated: Boolean,
    val headline: String,
    val maxRedemptions: String, // yuck  ex: "1/3M"
    val maximumRewardPerTransaction: Int,
    val maximumRewardCumulative: Int,
    val merchantCategoryCode: MerchantCategoryCode,
    val merchantName: String,
    val merchantLogoUrl: String,
    val minimumSpend: BigDecimal,
    val mode: String, // is there an enum for this?   ex: "ONLINE"
    val rewardRate: Int,
    val rewardValue: BigDecimal,
    val rewardType: String, // ex: "CARD_LINKED"
    val type: String, // vague property name!     ex: "CARD_LINKED"
    val validDayParts: String, // this is some kind of embedded object type {},
    val termsAndConditions: String,
    val merchantWebsite: String
)

data class OfferActivation(
    val id: String,
    val cardAccountId: String,
    val activatedAt: String, // should this be a tz-aware instant type? ex: "2019-08-24"
    val activationExpiresOn: String, // should this be a tz-aware instant type? ex: "2019-08-24"
    val offer: Offer,
    val merchant: String // this should be a type ? {"name": "string","logo_url": "string" }
)

data class DisplayRules(
    val id: String,
    val description: String,
    val enabled: Boolean,
    val scope: DisplayRuleScope, // this should be some embedded type? { "level": "PORTFOLIO_MANAGER", "id": "triple-abc-123", "name": "string"  },
    val type: String, // some enum?  better property name? ex: "MERCHANT_NAME_EQUAL_TO"
    val value: String, // this is supposed to be some numeric value?
    val action: String // some enum?  "EXCLUDE"
)

data class DisplayRuleScope(
    val id: String,
    val level: String,
    val name: String
)

data class Publisher(
    val id: String,
    val portfolioManagerId: String,
    @Json(name = "externalId") val publisherExternalId: String,
    val assumedName: String,
    val address: Address,
    val revenueShare: BigDecimal,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
)

data class Reward(
    val transactionId: String,
    val offerId: String, // maybe this should be an Offer type
    val offerExternalId: String,
    val transactionDate: ZonedDateTime,
    val cardBin: String,
    val cardLast4: String,
    val transactionAmount: BigDecimal,
    val transactionCurrencyCode: Currency,
    val rewardAmount: BigDecimal,
    val rewardCurrencyCode: String, //  there's dedicated type for this?
    val offerHeadline: String,
    val merchantName: String,
    val merchantCompleteAddress: String,
    val status: String // should this be an enum?  "REJECTED"
)

data class RewardDetail(
    val offerId: String,
    val amount: BigDecimal,
    val currencyCode: String,
    val status: String, // should this be an enum?  "REJECTED"
    val rejection: String, // should this be an enum? ex:"PURCHASE_AMOUNT_TOO_LOW"
    val notes: String
)

data class Transaction(
    val id: String,
    val cardAccountId: String,
    @Json(name = "externalId") val transactionExternalId: String,
    val localDate: LocalDate,
    val localTime: LocalTime,
    val debit: Boolean,
    val amount: BigDecimal,
    val currencyCode: String,
    val transactionType: TransactionType,
    val description: String,
    val merchantCategoryCode: MerchantCategoryCode,
    val merchantAddress: Address,
    val processorMid: String,
    val processorMidType: ProcessorMIDType,
    val matchingStatus: String, // should be an enum?    // ex:"HISTORIC_TRANSACTION"
    val rewardDetails: List<RewardDetail>,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime
)

enum class TransactionType { CHECK, DEPOSIT, FEE, PAYMENT, PURCHASE, REFUND, TRANSFER, WITHDRAWAL }

enum class ProcessorMIDType {
    AMEX_SE_NUMBER, DISCOVER_MID, MC_AUTH_LOC_ID, MC_AUTH_ACQ_ID, MC_AUTH_ICA, MC_CLEARING_LOC_ID, MC_CLEARING_ACQ_ID,
    MC_CLEARING_ICA, MERCHANT_PROCESSOR, NCR, VISA_VMID, VISA_VSID
}
