package com.swipeshot.app

import android.app.Activity
import android.content.Intent
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
            call.reject("Falta el parámetro uri")
            return
        }
        val uri = Uri.parse(uriString)
        val resolver = context.contentResolver
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pendingIntent = MediaStore.createDeleteRequest(resolver, listOf(uri))
                pendingCall = call
                startIntentSenderForResult(
                    pendingIntent.intentSender,
                    "deleteResult",
                    Intent(), 0, 0, 0, null
                )
            } else {
                val rows = resolver.delete(uri, null, null)
                val ret = JSObject()
                ret.put("success", rows > 0)
                call.resolve(ret)
            }
        } catch (e: android.app.RecoverableSecurityException) {
            pendingCall = call
            startIntentSenderForResult(
                e.userAction.actionIntent.intentSender,
                "deleteResult",
                Intent(), 0, 0, 0, null
            )
        } catch (e: Exception) {
            call.reject("Error: " + e.message)
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
