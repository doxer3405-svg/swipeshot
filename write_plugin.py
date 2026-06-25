import sys, os, re

main_path = sys.argv[1]
with open(main_path) as f:
    content = f.read()

pkg = re.search(r'^package\s+([\w.]+)', content, re.M).group(1)
dir_path = os.path.join(
    os.path.dirname(main_path),
    *[]
)
plugin_path = os.path.join(os.path.dirname(main_path), "MediaDeletePlugin.java")

code = """\
package {pkg};

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.Collections;

@CapacitorPlugin(name = "MediaDelete")
public class MediaDeletePlugin extends Plugin {{
    private static final int REQ = 42;
    private PluginCall saved;

    @PluginMethod
    public void deleteMedia(PluginCall call) {{
        String u = call.getString("uri");
        if (u == null) {{ call.reject("no uri"); return; }}
        saved = call;
        try {{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {{
                android.app.PendingIntent pi = MediaStore.createDeleteRequest(
                    getContext().getContentResolver(),
                    Collections.singletonList(Uri.parse(u)));
                getActivity().startIntentSenderForResult(
                    pi.getIntentSender(), REQ, new Intent(), 0, 0, 0);
            }} else {{
                int n = getContext().getContentResolver().delete(Uri.parse(u), null, null);
                JSObject r = new JSObject();
                r.put("success", n > 0);
                call.resolve(r);
                saved = null;
            }}
        }} catch (Exception e) {{ call.reject(e.getMessage()); saved = null; }}
    }}

    @Override
    protected void handleOnActivityResult(int req, int res, Intent data) {{
        super.handleOnActivityResult(req, res, data);
        if (req == REQ && saved != null) {{
            JSObject r = new JSObject();
            r.put("success", res == Activity.RESULT_OK);
            saved.resolve(r);
            saved = null;
        }}
    }}
}}
""".format(pkg=pkg)

with open(plugin_path, 'w') as f:
    f.write(code)
print("Plugin escrito en:", plugin_path)
print("PKG:", pkg)
