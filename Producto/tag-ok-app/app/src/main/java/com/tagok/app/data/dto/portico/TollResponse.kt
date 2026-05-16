package com.tagok.app.data.dto.portico

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = TollResponseSerializer::class)
sealed class TollResponse

object TollResponseSerializer : JsonContentPolymorphicSerializer<TollResponse>(TollResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<TollResponse>
    {
        val type = element.jsonObject["type"]?.jsonPrimitive?.contentOrNull
            ?: throw IllegalArgumentException("Campo 'type' no encontrado en $element")

        return when (type)
        {
            "PORTICO" -> PorticoResponse.serializer()
            "TRAMO" -> PorticoTramoResponse.serializer()
            else -> throw IllegalArgumentException("Tipo de TollResponse desconocido: $type")
        }
    }
}
