package com.tagok.app.data.dto.tarifa

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = CruceResponseSerializer::class)
sealed class CruceResponse

object CruceResponseSerializer : JsonContentPolymorphicSerializer<CruceResponse>(CruceResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<CruceResponse> {
        val jsonObj = element.jsonObject
        val hasPorticoId = jsonObj.containsKey("porticoId")

        return if (hasPorticoId)
        {
            CrucePorticoResponse.serializer()
        }
        else
        {
            CruceTramoResponse.serializer()
        }
    }
}