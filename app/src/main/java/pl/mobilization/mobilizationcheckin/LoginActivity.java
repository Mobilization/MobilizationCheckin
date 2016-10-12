package pl.mobilization.mobilizationcheckin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class LoginActivity extends AppCompatActivity {
    public FirebaseService service;

    private boolean bound = false;

    @BindView(R.id.editTextUsername)
    EditText editTextUsername;

    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

    @BindView(R.id.buttonLogin)
    Button buttonLogin;

    private String TAG = LoginActivity.class.getSimpleName();

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, String.format("onServiceConnected(%s)", componentName));
            FirebaseService.LocalBinder binder = (FirebaseService.LocalBinder) iBinder;
            service = binder.getService();
            service.logged$().subscribe(new Action1<FirebaseService.LoginStatus>() {
                @Override
                public void call(FirebaseService.LoginStatus loginStatus) {
                    Log.d(TAG, String.valueOf(loginStatus));
                    switch(loginStatus) {
                        case LOGGED_IN:
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            break;
                        case NOT_LOGGED_IN:
                            buttonLogin.setEnabled(true);
                            break;
                    }
                }
            });
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean bindService = bindService(new Intent(this, FirebaseService.class), mServiceConnection, Context.BIND_AUTO_CREATE);

        Log.d(TAG, String.format("bindService %s", bindService));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(bound)
            unbindService(mServiceConnection);
    }

    @OnClick(R.id.buttonLogin)
    public void doLogin() {
        service.login(editTextUsername.getText().toString(), editTextPassword.getText().toString());
        buttonLogin.setEnabled(false);
    }

}
