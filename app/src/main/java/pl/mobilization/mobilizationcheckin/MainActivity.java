package pl.mobilization.mobilizationcheckin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();

    @BindView(R.id.recyclerGuest)
    RecyclerView recyclerView;

    @BindView(R.id.editTextFilter)
    EditText editTextFilter;

    private MyAdapter adapter = new MyAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.setFilter(editable.toString());
            }
        });

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, String.format("onChildAdded(%s, %s)", user, previousChildName));
                user.number = dataSnapshot.getKey();
                adapter.add(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, String.format("onChildChanged(%s, %s)", user, previousChildName));

                user.number = dataSnapshot.getKey();
                adapter.add(user);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = getUser(dataSnapshot);
                Log.d(TAG, String.format("onChildRemoved(%s)", user));

                adapter.remove(user);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                User user = getUser(dataSnapshot);
                adapter.add(user);
            }

            @NonNull
            private User getUser(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.number = dataSnapshot.getKey();
                return user;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateCheckedIn(User user) {
        Log.d(TAG, String.format("updateCheckedIn(%s)", user));
        reference.child(user.number).setValue(user);
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
