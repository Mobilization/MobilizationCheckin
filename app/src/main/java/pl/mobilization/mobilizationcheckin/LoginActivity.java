package pl.mobilization.mobilizationcheckin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.editTextUsername)
    EditText editTextUsername;

    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

    @BindView(R.id.buttonLogin)
    Button buttonLogin;

    @BindView(R.id.buttonLogout)
    Button buttonLogout;

    @BindView(R.id.textViewInfo)
    TextView textViewInfo;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    private String TAG = LoginActivity.class.getSimpleName();

    private FirebaseServiceConnection mServiceConnection = new FirebaseServiceConnection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mServiceConnection.bind();

        Subscription subscription = mServiceConnection.logged$().subscribe(new Action1<String>() {
            @Override
            public void call(String loggedInAs) {
                textViewInfo.setText(loggedInAs);
                editTextUsername.setText(loggedInAs);
            }
        });

        subscriptions.add(subscription);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mServiceConnection.unbind();
        subscriptions.clear();
    }


    @OnClick(R.id.buttonLogin)
    public void doLogin() {
        Subscription subscription = mServiceConnection.login(editTextUsername.getText().toString(), editTextPassword.getText().toString()).subscribe(new Action1<FirebaseService.LoginStatus>() {
            @Override
            public void call(FirebaseService.LoginStatus loginStatus) {
                switch (loginStatus) {
                    case LOGGED_IN:
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        break;
                    case LOGGED_OUT:
                        Toast.makeText(LoginActivity.this, "Login failed", LENGTH_LONG).show();
                        break;
                }
            }
        });

        subscriptions.add(subscription);
    }

    @OnClick(R.id.buttonLogout)
    public void doLogout() {
        final Subscription subscription = mServiceConnection.logout().subscribe(new Action1<FirebaseService.LoginStatus>() {
            @Override
            public void call(FirebaseService.LoginStatus loginResult) {

                if (FirebaseService.LoginStatus.LOGGED_OUT.equals(loginResult))
                    Toast.makeText(LoginActivity.this, "Logged out", LENGTH_LONG).show();
            }
        });
        subscriptions.add(subscription);
    }

}
