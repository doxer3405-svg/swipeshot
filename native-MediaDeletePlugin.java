package com.swipeshot.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Collections;

@CapacitorPlugin(name = "MediaDelete")
public class MediaDeletePlugin extends Plugin {

    private PluginCall pendingCall = null;

    @PluginMethod
    public void deleteMedia(PluginCall call) {
        String uriString = call.getString("uri");
        if (uriString == null || uriString.isEmpty()) {
            call.reject("Falta el parámetro uri");
            return;
        }
        Uri uri = Uri.parse(uriString);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.app.PendingIntent pi = MediaStore.createDeleteRequest(
                    getContext().getContentResolver(),
                    Collections.singletonList(uri)
                );
                pendingCall = call;
                startIntentSenderForResult(
                    pi.getIntentSender(),
                    "deleteResult",
                    new Intent(), 0, 0, 0, null
                );
            } else {
                int rows = getContext().getContentResolver().delete(uri, null, null);
                JSObject ret = new JSObject();
                ret.put("success", rows > 0);
                call.resolve(ret);
            }
        } catch (android.app.RecoverableSecurityException e) {
            pendingCall = call;
            try {
                startIntentSenderForResult(
                    e.getUserAction().getActionIntent().getIntentSender(),
                    "deleteResult",
                    new Intent(), 0, 0, 0, null
                );
            } catch (Exception ex) {
                call.reject("Error: " + ex.getMessage());
            }
        } catch (Exception e) {
            call.reject("Error: " + e.getMessage());
        }
    }

    @ActivityCallback
    private void deleteResult(PluginCall call, com.getcapacitor.PluginResult result) {
        if (call == null) return;
        JSObject ret = new JSObject();
        ret.put("success", result.getResultCode() == Activity.RESULT_OK);
        call.resolve(ret);
        pendingCall = null;
    }
}
