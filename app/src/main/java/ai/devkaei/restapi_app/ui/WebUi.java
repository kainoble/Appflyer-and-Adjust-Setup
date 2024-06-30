package ai.devkaei.restapi_app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.adjust.sdk.Adjust;
import com.appsflyer.AppsFlyerLib;

import org.apache.cordova.engine.SystemWebView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ai.devkaei.restapi_app.AppConfig;
import ai.devkaei.restapi_app.R;

public class WebUi extends AppCompatActivity {

    public static void openURL(Context mContext, String mLink) {
        Intent cdvIntent = new Intent(mContext, WebUi.class);
        cdvIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        cdvIntent.putExtra("mLink", mLink);
        mContext.startActivity(cdvIntent); // Start activity with intent
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_ui);

        // Enable EdgeToEdge for immersive experience
        EdgeToEdge.enable(this);

        // Apply padding to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the WebView from layout and configure settings
        SystemWebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webSettings.setDomStorageEnabled(true); // Enable DOM Storage
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // Enable opening windows via JavaScript
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); // Set cache mode

        // Load the URL from intent extra
        String url = getIntent().getStringExtra("mLink");
        if (url != null) {
            webView.loadUrl(url);
        }

        // Add JavaScript interface to handle callbacks from JavaScript
        webView.addJavascriptInterface(new JsObject(this), "Android");
    }

    // Inner class to define JavaScript interface methods
    // APPSLFYER
    class JsObject {
        private Context mContext;

        public JsObject(Context mContext) {
            this.mContext = mContext;
        }

        @JavascriptInterface
        public void postMessage(String eventName, String eventData) {
            try {
                Log.i("JSObject", "Event: " + eventName + "\nData: " + eventData);
                if (TextUtils.isEmpty(eventName) || eventData == null) {
                    Log.e("JSObject", "Unable to parse event data");
                    return;
                }
                if (eventName.equals("openWindow")) {
                    JSONObject appData = new JSONObject(eventData);
                    String url = appData.getString("url");
                    openExternal(mContext, url);
                } else {
                    Log.d("JSObject", "Sending event to AppsFlyer: " + eventName);
                    AppsFlyerLib.getInstance().logEvent(mContext, eventName, (Map<String, Object>) new JSONObject(eventData));
                }
            } catch (JSONException ex) {
                Log.e(AppConfig.LOG_TAG, "Data object passed to postMessage has caused a JSON error: " + ex.getMessage());
            }
        }

        private void openExternal(Context mContext, String url) {
            // Implement logic to open an external URL
        }
// ADJUST
        @JavascriptInterface
        public void postMessage(String message) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                String method = jsonObject.optString("method");
                JSONObject params = jsonObject.optJSONObject("cvParams");

                if (TextUtils.isEmpty(method) || params == null) {
                    return;
                }
                if (method.equals("trackAdjustEvent")) {
                    String eventToken = params.optString("eventToken");
                    JSONObject cbParams = params.optJSONObject("cbparams");
                    JSONObject revenues = params.optJSONObject("revenues");
                    trackEvent(eventToken, cbParams, revenues);
                } else if (method.equals("openUrlByBrowser")) {
                    String url = params.optString("url");
                    openExternal(mContext,url);
                }

            } catch (JSONException ex) {
                Log.e(AppConfig.LOG_TAG, "Data object passed to postMessage has caused a JSON error: " + ex.getMessage());
            }
        }

        private void trackEvent(String eventToken, JSONObject cbParams, JSONObject revenues) {
            // Implement Adjust event tracking here
//            Adjust.trackEvent(eventToken, cbParams, revenues);
        }
    }
}
