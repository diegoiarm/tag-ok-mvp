package com.tagok.app.ui.boleta.comparacion

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.tagok.app.domain.model.boleta.ArchivoFactura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

/**
 * Carga la factura elegida por el usuario (PDF, foto de cámara o imagen de
 * galería) en un [ArchivoFactura] listo para subir. Las imágenes se
 * re-escalan y comprimen a JPEG: acelera la subida y la lectura de la IA
 * sin perder legibilidad.
 */
object FacturaPicker
{
    private const val MAX_DIMENSION = 2048
    private const val JPEG_QUALITY = 85

    /** Uri temporal (FileProvider sobre cache) para que la cámara escriba la foto. */
    fun crearUriFoto(context: Context): Uri
    {
        val directorio = File(context.cacheDir, "facturas").apply { mkdirs() }
        val archivo = File(directorio, "factura_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo)
    }

    suspend fun cargar(context: Context, uri: Uri): ArchivoFactura = withContext(Dispatchers.IO)
    {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri) ?: ""
        val nombre = obtenerNombre(resolver, uri)

        if (mime == "application/pdf")
        {
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IllegalStateException("No se pudo abrir el archivo")

            ArchivoFactura(
                nombre = nombre ?: "factura.pdf",
                mimeType = "application/pdf",
                bytes = bytes)
        }
        else
        {
            ArchivoFactura(
                nombre = ((nombre ?: "factura").substringBeforeLast('.')) + ".jpg",
                mimeType = "image/jpeg",
                bytes = comprimirImagen(resolver, uri))
        }
    }

    private fun comprimirImagen(resolver: ContentResolver, uri: Uri): ByteArray
    {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }

        if (limites.outWidth <= 0 || limites.outHeight <= 0)
        {
            throw IllegalStateException("El archivo no es una imagen válida")
        }

        var sampleSize = 1
        while (max(limites.outWidth, limites.outHeight) / (sampleSize * 2) >= MAX_DIMENSION)
        {
            sampleSize *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        } ?: throw IllegalStateException("No se pudo leer la imagen")

        return try
        {
            ByteArrayOutputStream().use { salida ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, salida)
                salida.toByteArray()
            }
        }
        finally
        {
            bitmap.recycle()
        }
    }

    private fun obtenerNombre(resolver: ContentResolver, uri: Uri): String?
    {
        return try
        {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else null
                }
        }
        catch (_: Exception)
        {
            null
        }
    }
}
