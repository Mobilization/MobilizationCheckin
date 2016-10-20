package pl.mobilization.mobilizationcheckin;

import android.app.Application;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by defecins on 15/10/16.
 */
public class MobilizationCheckInApplication extends Application {
    private static final String TAG = MobilizationCheckInApplication.class.getSimpleName();
    private boolean bound;

    private FirebaseServiceConnection serviceConnection = new FirebaseServiceConnection(this);

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Log.d(TAG, "onCreate()");


        serviceConnection.bind();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate()");
        serviceConnection.unbind();
        super.onTerminate();
    }
}
