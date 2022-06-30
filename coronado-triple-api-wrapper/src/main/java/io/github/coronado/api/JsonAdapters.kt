package io.github.coronado.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Currency

/**
 * Singleton adapter objects used for JSON serialization
 *
 * @author Coronado Project Team
 */

object BigDecimalAdapter {
    @FromJson
    fun fromJson(string: String) = BigDecimal(string)

    @ToJson
    fun toJson(value: BigDecimal) = value.toString()
}

object CurrencyAdapter {
    @FromJson
    fun fromJson(string: String) = Currency.getInstance(string)

    @ToJson
    fun toJson(value: Currency) = value.toString()
}

object ZonedDateTimeAdapter {
    @FromJson
    fun fromJson(string: String) = ZonedDateTime.parse(string)

    @ToJson
    fun toJson(value: ZonedDateTime) = value.toString()
}

object LocalDateTimeAdapter {
    @FromJson
    fun fromJson(string: String) = LocalDateTime.parse(string)

    @ToJson
    fun toJson(value: LocalDateTime) = value.toString()
}

object LocalDateAdapter {
    @FromJson
    fun fromJson(string: String) = LocalDate.parse(string)

    @ToJson
    fun toJson(value: LocalDate) = value.toString()
}

object LocalTimeAdapter {
    @FromJson
    fun fromJson(string: String) = LocalTime.parse(string)

    @ToJson
    fun toJson(value: LocalTime) = value.toString()
}
