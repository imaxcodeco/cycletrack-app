package com.seuapp.cycletrack.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import import com.seuapp.cycletrack.util.toEpochMilliseconds

/**
 * Conversoes de data/hora para epoch millis.
 *
 * O projeto usa kotlinx.datetime; a API `java.time` nao existe para LocalDate.
 * Estas extensoes centralizam a conversao usada pelos mappers e pela camada remota.
 */

/** Epoch millis (UTC) de um [LocalDate]. */
fun LocalDate.toEpochMilliseconds(): Long = toEpochDays() * 86_400_000L

/** Epoch millis (UTC) de um [LocalDateTime]. */
fun LocalDateTime.toEpochMilliseconds(): Long = toEpochDays() * 86_400_000L

/** Converte epoch millis em [LocalDate] (UTC). */
fun Long.toLocalDateUtc(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

/** Converte epoch millis em [LocalDateTime] (UTC). */
fun Long.toLocalDateTimeUtc(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC)
