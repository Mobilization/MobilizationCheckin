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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerGuests)
    RecyclerView recyclerView;

    @BindView(R.id.editTextFilter)
    EditText editTextFilter;

    @BindView(R.id.textViewInfo)
    TextView textViewInfo;

    private boolean bound;


    private FirebaseServiceConnection serviceConnection = new FirebaseServiceConnection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        serviceConnection.bind();
    }

    @Override
    protected void onResume() {
        super.onResume();

        serviceConnection.adapter$().subscribe(new Action1<AttendeesAdapter>() {
            @Override
            public void call(AttendeesAdapter attendeesAdapter) {
                recyclerView.setAdapter(attendeesAdapter);
            }
        });

        textViewInfo.setText("If list is empty please consider logging in");


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
        super.onPause();

        serviceConnection.unbind();
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

    @OnClick(R.id.imageButtonLogin)
    public void onClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
