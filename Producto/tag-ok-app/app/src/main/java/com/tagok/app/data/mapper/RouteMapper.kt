package com.tagok.app.data.mapper

import com.tagok.app.data.dto.PorticoRuta
import com.tagok.app.data.dto.RouteResponse
import com.tagok.app.domain.model.Point
import com.tagok.app.domain.model.Portico
import com.tagok.app.domain.model.Route
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun RouteResponse.toDomain(): Route
{
    return Route(
        points = parseGeometry(mergedRouteGeometry),
        totalCost = totalCost,
        porticos = porticos.map { it.toDomain() }
    )
}

fun PorticoRuta.toDomain(): Portico {
    return Portico(
        codigo = codigo,
        nombre = nombre,
        sentido = sentido,
        autopista = autopista,
        latitud = latitud,
        longitud = longitud,
        tarifa = tarifa,
        valor = valor
    )
}

private fun parseGeometry(geoJson: String?): List<Point>
{
    if (geoJson == null)
        return emptyList()

    return try
    {
        val json = Json.parseToJsonElement(geoJson).jsonObject
        extractPoints(json)
    }
    catch (e: Exception)
    {
        emptyList()
    }
}

private fun extractPoints(json: JsonObject): List<Point>
{
    val type = json["type"]?.jsonPrimitive?.content ?: return emptyList()

    return when (type)
    {
        "LineString" -> extractCoords(json["coordinates"]?.jsonArray)
        "MultiLineString" -> {
            val array = json["coordinates"]?.jsonArray ?: return emptyList()
            array.flatMap { extractCoords(it.jsonArray) }
        }
        else -> emptyList()
    }
}

private fun extractCoords(coords: JsonArray?): List<Point>
{
    if (coords == null)
        return emptyList()

    return coords.map { elem ->
        val arr = elem.jsonArray
        Point(
            lon = arr[0].jsonPrimitive.double,
            lat = arr[1].jsonPrimitive.double)
    }
}