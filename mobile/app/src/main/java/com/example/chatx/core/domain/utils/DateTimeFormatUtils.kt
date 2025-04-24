package com.example.chatx.core.domain.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.byUnicodePattern

object DateTimeFormatUtils {

    val dateTimeUntilMinuteFormat = LocalDateTime.Format {
        dayOfMonth()
        chars(" ")
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        chars(" ")
        year()
        chars(" ")

        hour()
        chars(":")
        minute()
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun getReadableDate(milliSeconds: Long): String{
        val currentDateTime = Instant.fromEpochMilliseconds(milliSeconds)
        return currentDateTime.format(DateTimeComponents.Format{ byUnicodePattern("yyyy-MM-dd HH:mm:ss")})
    }
}