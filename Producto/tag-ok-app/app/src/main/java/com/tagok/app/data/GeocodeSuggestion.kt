package com.tagok.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class GeocodeSuggestion(
    val placeName: String,
    val lon: Double,
    val lat: Double,
)

@Serializable
internal data class NominatimResult(
    @SerialName("display_name") val displayName: String,
    val lat: String,
    val lon: String,
)
