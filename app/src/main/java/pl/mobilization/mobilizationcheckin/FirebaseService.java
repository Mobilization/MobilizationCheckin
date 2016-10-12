package pl.mobilization.mobilizationcheckin;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FirebaseService extends Service {
    private static final String ACTION_LOGIN = "pl.mobilization.mobilizationcheckin.action.LOGIN";
    private static final String ACTION_BAZ = "pl.mobilization.mobilizationcheckin.action.BAZ";

    private static final String PARAM_LOGIN = "pl.mobilization.mobilizationcheckin.extra.LOGIN";
    private static final String PARAM_PASSWORD = "pl.mobilization.mobilizationcheckin.extra.PASSWORD";


    private static final String TAG = FirebaseService.class.getSimpleName();


    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference();

    private AttendeesAdapter adapter = new AttendeesAdapter(this);
    private ChildEventListener childEventListener;
    private BehaviorSubject<LoginStatus> loginSubject = BehaviorSubject.<LoginStatus>create(LoginStatus.NOT_LOGGED_IN);

    public FirebaseService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, String.format("onAuthStateChanged", firebaseAuth.getCurrentUser()));
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                if(currentUser != null) {
                    loginSubject.onNext(LoginStatus.LOGGED_IN);
                }
                else {
                    loginSubject.onNext(LoginStatus.NOT_LOGGED_IN);
                }
            }
        };


        childEventListener = new ChildEventListener() {
            private User getUser(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setNumber(dataSnapshot.getKey());
                return user;
            }

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User user = getUser(dataSnapshot);
                Log.d(TAG, String.format("onChildAdded(%s,%s)", user, previousChildName));
                adapter.add(user);
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User user = getUser(dataSnapshot);
                Log.d(TAG, String.format("onChildChanged(%s,%s)", user, previousChildName));

                adapter.add(user);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = getUser(dataSnapshot);
                Log.d(TAG, String.format("onChildRemoved(%s,%s)"));
                adapter.remove(user);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                User user = getUser(dataSnapshot);
                Log.d(TAG, String.format("onChildMoved(%s,%s)", user, previousChildName));
                adapter.add(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addChildEventListener(childEventListener);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mAuth.removeAuthStateListener(mAuthListener);
        reference.removeEventListener(childEventListener);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
  }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        mAuth.addAuthStateListener(mAuthListener);
        return new LocalBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind()");
        mAuth.addAuthStateListener(mAuthListener);
    }


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    public void login(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password);
    }

    public Observable<LoginStatus> logged$() {
        return loginSubject.asObservable();
    }


    public AttendeesAdapter getAdapter() {
        return adapter;
    }

    public void updateCheckedIn(User user) {
        Log.d(TAG, String.format("updateCheckedIn(%s)", user));
        reference.child(user.getNumber()).setValue(user);
    }


    public class LocalBinder extends Binder {
        FirebaseService getService() {
            return FirebaseService.this;
        }
    }

    public enum LoginStatus {
        LOGGED_IN,
        NOT_LOGGED_IN
    }
}
