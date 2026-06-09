package com.tagok.app.ui.historial.utils

import java.text.NumberFormat
import java.util.Locale

fun Double.formatCurrency(): String
{
    return NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(this)
}

fun Double.formatCompactCurrency(): String
{
    return when
    {
        this >= 1_000_000 -> "$${(this / 1_000_000).format(1)}M"
        this >= 1_000 -> "$${(this / 1_000).format(1)}K"
        else -> formatCurrency()
    }
}

private fun Double.format(decimals: Int): String
{
    return "%.${decimals}f".format(this)
}