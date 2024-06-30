package ai.devkaei.restapi_app.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import ai.devkaei.restapi_app.AppConfig;

public class SyncManager {
    private Context mContext;
    private DBHelper dbHelper;
    private RequestQueue requestQueue;

    public SyncManager(Context mContext){
        this.mContext = mContext;
        this.dbHelper = new DBHelper(mContext);
        this.requestQueue = Volley.newRequestQueue(mContext);

    }

    public void checkRemoteDB(){
        String remoteAPI = "https://example.com/ " + mContext.getPackageName();
        JsonObjectRequest jsonData = new JsonObjectRequest(Request.Method.GET, remoteAPI, null, apiResponse -> {
            try {
                int removeVersion = apiResponse.getInt("column_version");
                int localVersion = dbHelper.getDBVersion();

                if (removeVersion != localVersion) {
                    syncLocalDB();
                } else {
                    launchMainApp();
                }

            } catch (JSONException error) {
                Log.e(AppConfig.LOG_TAG, "Error parsing API RESPONSE: " + error.getMessage());
            }

        }, apiError -> {});
        requestQueue.add(jsonData);
    }
    private void syncLocalDB(){
        String remoteAPI = "https://example.com" + mContext.getPackageName();
        JsonObjectRequest jsonData = new JsonObjectRequest(Request.Method.GET, remoteAPI, null, apiResponse -> {
            try{
                JSONObject apiData = new JSONObject(apiResponse.getString("record"));
                Log.d(AppConfig.LOG_TAG," API Response: " + apiData);                dbHelper.updateLocalDB(apiData.getInt("column_version"), apiData.getString("column_country"), apiData.getString("column_link"), apiData.getString("column_policylink"), apiData.getInt("column_status"));

            }
            catch (JSONException error){
                Log.e(AppConfig.LOG_TAG, "Error parsing API RESPONSE: " + error.getMessage());
            }
        }, apiError -> {});
        requestQueue.add(jsonData);
    }
public interface RegionCallback{
        void onSuccess(String userRegion);
        void onError(String Error);
}

    private String getUserRegion(RegionCallback regionCallback){
        String remoteAPI = "https://example.com/";
        JsonObjectRequest jsonData = new JsonObjectRequest(Request.Method.GET, remoteAPI, null, apiResponse -> {
            try{
                String userRegion = apiResponse.getString("country");
                regionCallback.onSuccess(userRegion);
            } catch (JSONException error){
                Log.e(AppConfig.LOG_TAG, "Error getting User Region: " + error.getMessage());
                regionCallback.onError("Error API region: " +  error.getMessage());
            }
        }, apiError -> {});
        requestQueue.add(jsonData);
        return remoteAPI;
    }
    private void launchMainApp() {

        Cursor cursor = dbHelper.getDBContent();
        if(cursor.moveToFirst()){

            Log.d(AppConfig.LOG_TAG, "Checking app status");
            @SuppressLint("Range") String region  = cursor.getString(cursor.getColumnIndex("column_country"));
            @SuppressLint("Range") int status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("column_status")));
            cursor.close();

//            if (status ==1 && getUserRegion()){
//
//            }
        } else {


            Log.d(AppConfig.LOG_TAG, "");
        }
        // Implement your logic to launch the main app activity here
        Log.d(AppConfig.LOG_TAG, "Launching main app activity...");
    }
}
