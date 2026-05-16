package com.tagok.app.data.dto.route

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = CobroRutaResponseSerializer::class)
sealed class CobroRutaResponse

object CobroRutaResponseSerializer : JsonContentPolymorphicSerializer<CobroRutaResponse>(CobroRutaResponse::class)
{
    override fun selectDeserializer(element: JsonElement) = when
    {
        element.jsonObject.containsKey("porticoId") -> CobroPorticoResponse.serializer()
        element.jsonObject.containsKey("entradaId") -> CobroTramoResponse.serializer()
        else -> throw IllegalArgumentException("No se puede determinar el tipo de CobroRutaResponse: $element")
    }
}