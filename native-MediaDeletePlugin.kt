// ════════════════════════════════════════════════════════════════
// MediaDeletePlugin.kt
//
// El plugin @odion-cloud/capacitor-mediastore NO incluye ningún método
// para borrar archivos (confirmado en su documentación oficial). Por eso
// CMS.deleteMedia(...) en tu JS nunca borraba nada real del dispositivo.
//
// Este archivo añade un plugin nativo mínimo que SÍ borra usando la API
// de Android (MediaStore.createDeleteRequest en Android 10+, con
// confirmación del usuario vía diálogo del sistema).
//
// INSTALACIÓN:
// 1. Ejecuta primero (si no lo has hecho):  npx cap add android
// 2. Copia este archivo a:
//    android/app/src/main/java/com/swipeshot/app/MediaDeletePlugin.kt
//    (ajusta el paquete "com.swipeshot.app" si tu appId es distinto)
// 3. Regístralo en MainActivity.java/.kt (ver instrucciones abajo)
// 4. npx cap sync android
// ════════════════════════════════════════════════════════════════
package com.swipeshot.app

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "MediaDelete")
class MediaDeletePlugin : Plugin() {

    private var pendingCall: PluginCall? = null

    @PluginMethod
    fun deleteMedia(call: PluginCall) {
        val uriString = call.getString("uri")
        if (uriString.isNullOrEmpty()) {
            call.reject("Falta el parámetro 'uri'")
            return
        }
        val uri = Uri.parse(uriString)
        val resolver = context.contentResolver

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+: pide confirmación del sistema para borrar
                val pendingIntent = MediaStore.createDeleteRequest(resolver, listOf(uri))
                pendingCall = call
                startIntentSenderForResult(
                    pendingIntent.intentSender,
                    "deleteResult",
                    Intent(),
                    0, 0, 0, null
                )
            } else {
                // Android 10 y anteriores: borrado directo
                val rows = resolver.delete(uri, null, null)
                val ret = JSObject()
                ret.put("success", rows > 0)
                call.resolve(ret)
            }
        } catch (e: android.app.RecoverableSecurityException) {
            // Android 10: el sistema pide permiso explícito
            pendingCall = call
            startIntentSenderForResult(
                e.userAction.actionIntent.intentSender,
                "deleteResult",
                Intent(),
                0, 0, 0, null
            )
        } catch (e: Exception) {
            call.reject("Error al borrar: " + e.message)
        }
    }

    @ActivityCallback
    private fun deleteResult(result: com.getcapacitor.PluginResult.ActivityResult) {
        val call = pendingCall ?: return
        val ret = JSObject()
        ret.put("success", result.resultCode == Activity.RESULT_OK)
        call.resolve(ret)
        pendingCall = null
    }
}
