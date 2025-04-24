package com.example.chatx.core.extentions

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.formatReadableForChat(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val messageDate = this.date

    return when {
        messageDate == now.date -> {
            LocalDateTime.Format {
                hour()
                chars(":")
                minute()
            }.format(this)
        }
        messageDate.year == now.year -> {
            LocalDateTime.Format {
                dayOfMonth()
                chars(" ")
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                chars(" ")
                hour()
                chars(":")
                minute()
            }.format(this)
        }
        else -> {
            LocalDateTime.Format {
                dayOfMonth()
                chars(" ")
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                chars(" ")
                year()
                chars(" ")
                hour()
                chars(":")
                minute()
            }.format(this)
        }
    }
}
