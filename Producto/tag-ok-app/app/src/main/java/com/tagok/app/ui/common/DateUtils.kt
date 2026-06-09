package com.tagok.app.ui.common

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.displayHorasMinutos(): String
{
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.toJavaLocalDateTime().format(formatter)
}

fun LocalDateTime.display(): String
{
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")
    return this.toJavaLocalDateTime().format(formatter)
}
