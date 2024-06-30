package ai.devkaei.restapi_app.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ai.devkaei.restapi_app.AppConfig;
import ai.devkaei.restapi_app.R;
import ai.devkaei.restapi_app.helpers.SyncManager;

public class Launcher extends AppCompatActivity {
    private SyncManager syncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if(AppConfig.getInstance().isNetworkConnected()){
            syncManager = new SyncManager(Launcher.this);
            syncManager.checkRemoteDB();
        } else {
            AppConfig.getInstance().promptUserNetworkNotification();
        }
    }

}