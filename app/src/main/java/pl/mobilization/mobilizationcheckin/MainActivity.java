package pl.mobilization.mobilizationcheckin;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerGuests)
    RecyclerView recyclerView;

    @BindView(R.id.editTextFilter)
    EditText editTextFilter;

    @BindView(R.id.textViewChecked)
    TextView textViewChecked;

    @BindView(R.id.textViewTotal)
    TextView textViewTotal;

    @BindView(R.id.textViewStala)
    TextView textViewStala;

    private boolean bound;

    private FirebaseServiceConnection serviceConnection = new FirebaseServiceConnection(this);
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("%s.onCreate()", System.identityHashCode(this)));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        serviceConnection.bind();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, String.format("%s.onResume()", System.identityHashCode(this)));
        super.onResume();

        Subscription subscription =  serviceConnection.adapter$().subscribe(new Action1<AttendeesAdapter>() {
            @Override
            public void call(AttendeesAdapter attendeesAdapter) {
                recyclerView.setAdapter(attendeesAdapter);

                Subscription subscription1 = attendeesAdapter.stalaSaramaka$().subscribe(new Action1<Float>() {
                    @Override
                    public void call(Float aFloat) {
                        textViewStala.setText(String.format("Stała Saramaka is %s", aFloat));
                    }
                });

                Subscription subscription2 = attendeesAdapter.checkedInCount$().subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        textViewChecked.setText(String.valueOf(aLong));
                    }
                });

                Subscription subscription3 = attendeesAdapter.totalCount$().subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        textViewTotal.setText(String.valueOf(aLong));
                    }
                });

                subscriptions.add(subscription1);
                subscriptions.add(subscription2);
                subscriptions.add(subscription3);
            }
        });

        subscriptions.add(subscription);


        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                serviceConnection.setFilter(editable.toString());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onPause() {
        Log.d(TAG, String.format("%s.onPause()", System.identityHashCode(this)));
        super.onPause();

        subscriptions.clear();

        serviceConnection.unbind();
    }

    @OnClick(R.id.imageButtonScan)
    public void initiateScan() {
        new IntentIntegrator(this).initiateScan(); // `this` is the current Activity

    }


    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, String.format("%s.onActivityResult(%s,%s)", System.identityHashCode(this), requestCode, resultCode));
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            editTextFilter.setText(result.getContents());
        }
    }

    @OnClick(R.id.imageButtonLogin)
    public void onClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
