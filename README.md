# Appflyer-and-Adjust-Setup-
Set up your mobile app to integrate Adjust and AppsFlyer event tracking. This ensures accurate measurement and analysis of user interactions, helping you optimize your app's performance and marketing efforts effectively.


package ai.devkaei.restapi_app;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustThirdPartySharing;
import com.adjust.sdk.LogLevel;
import com.appsflyer.AppsFlyerConsent;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.Map;

import ai.devkaei.restapi_app.R; // Ensure this import is correct

public class AppConfig extends Application {
    public static final String LOG_TAG = "CDVTools";
    private static AppConfig _instance;
    private boolean isAFInitialized = false;
    private boolean isAdjustInitialized = false;
    private boolean isAFStarted = false;
    private boolean isAdjustStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this; // Initialize the singleton instance

        initAF("xxxxxxxxx");
        initAdjust("xxxxxxxxxx");
    }

    public static AppConfig getInstance() {
        return _instance;
    }

    public synchronized void initAF(String appsFlyerID) {
        if (!isAFInitialized && isNetworkConnected()) {
            AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
                @Override
                public void onConversionDataSuccess(Map<String, Object> map) {
                    for (String attrName : map.keySet()) {
                        Log.d(LOG_TAG, "Conversion Success: " + attrName + "\nDatamap: " + map.get(attrName));
                    }
                    String status = map.get("af_status").toString();
                    if (status.equals("Organic")) {
                        Log.d(LOG_TAG, "Organic Install");
                    }
                }

                @Override
                public void onConversionDataFail(String s) {
                    Log.e(LOG_TAG, "Conversion Data Failed: " + s);
                }

                @Override
                public void onAppOpenAttribution(Map<String, String> map) {
                    for (String attrName : map.keySet()) {
                        Log.d(LOG_TAG, "App Open Attribution: " + attrName + "\nDatamap: " + map.get(attrName));
                    }
                }

                @Override
                public void onAttributionFailure(String s) {
                    Log.e(LOG_TAG, "Attribution Failed: " + s);
                }
            };

            AppsFlyerLib.getInstance().init(appsFlyerID, conversionListener, this);
            AppsFlyerLib.getInstance().setDebugLog(true);
            AppsFlyerLib.getInstance().start(this);
            // Initialize Adjust SDK with AppsFlyer
            isAFInitialized = true;
            isAFStarted = true;
        } else {
            promptUserNetworkNotification();
        }
    }

    public synchronized void initAdjust(String adjustDevToken) {
        if (!isAdjustInitialized && isNetworkConnected()) {
            String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
            AdjustConfig config = new AdjustConfig(this, adjustDevToken, environment);
            config.setLogLevel(LogLevel.VERBOSE);
            Adjust.onCreate(config);

            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {

                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {

                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {

                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            });
            AdjustThirdPartySharing adjustThirdPartySharing = new AdjustThirdPartySharing(null);
            Adjust.trackThirdPartySharing(adjustThirdPartySharing);

            // Initialize Adjust SDK with adjustDevTokens
            isAdjustInitialized = true;
            isAdjustStarted = true; // Or based on actual initialization status
        } else {
            promptUserNetworkNotification();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network netStatus = connectivityManager.getActiveNetwork();
            if (netStatus != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(netStatus);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
            }
        }
        return false;
    }

    public void promptUserNetworkNotification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View uiPrompt = LayoutInflater.from(this).inflate(R.layout.ui_networkpromp, null);
        builder.setView(uiPrompt);

        Button btnRetry = uiPrompt.findViewById(R.id.btnRetry);
        Button btnQuit = uiPrompt.findViewById(R.id.btnQuit);

        btnRetry.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                builder.create().dismiss();
                initAF("xxxxxxxxx"); // Retry AppsFlyer initialization
                initAdjust("xxxxxxxxxx"); // Retry Adjust initialization
            }
        });

        btnQuit.setOnClickListener(v -> System.exit(1));
        builder.setCancelable(false);
        builder.show();
    }

    public void PromptUserNetworkNotification() {
    }
}
