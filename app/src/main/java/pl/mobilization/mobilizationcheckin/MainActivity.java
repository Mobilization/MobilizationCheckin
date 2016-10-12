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
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.recyclerGuest)
    RecyclerView recyclerView;

    @BindView(R.id.editTextFilter)
    EditText editTextFilter;

    @BindView(R.id.textViewCounter)
    TextView textViewCounter;

    private boolean bound;
    public FirebaseService service;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bound = true;
            FirebaseService.LocalBinder binder = (FirebaseService.LocalBinder) iBinder;
            service = binder.getService();

            adapter = service.getAdapter();
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
            recyclerView.setAdapter(null);
        }
    };
    private AttendeesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindService(new Intent(this, FirebaseService.class), serviceConnection, BIND_AUTO_CREATE);

        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(adapter != null) {
                    adapter.setFilter(editable.toString());

                    textViewCounter.setText(String.valueOf(adapter.getStalaSaramaka()));
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(bound)
            unbindService(serviceConnection);
    }

    @OnClick(R.id.imageButtonScan)
    public void initiateScan() {
        new IntentIntegrator(this).initiateScan(); // `this` is the current Activity

    }


    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            editTextFilter.setText(result.getContents());
            editTextFilter.moveCursorToVisibleOffset();
        }
    }
}
