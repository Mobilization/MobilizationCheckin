package pl.mobilization.mobilizationcheckin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by defecins on 15/10/16.
 */
public class FirebaseServiceConnection {
    private static final String TAG = FirebaseServiceConnection.class.getSimpleName();
    private final Context context;
    PublishSubject<AttendeesAdapter> adapterPublishSubject = PublishSubject.create();
    PublishSubject<String> filterSubject = PublishSubject.create();
    PublishSubject<String> loggedInAs = PublishSubject.create();

    private boolean bound;
    private IBinder iBinder;
    private FirebaseService service;
    private MyServiceConnction serviceConnction = new MyServiceConnction();

    public FirebaseServiceConnection(Context context) {
        this.context = context;
    }

    public Observable<FirebaseService.LoginStatus> logout() {
        if (!bound)
            return Observable.empty();

        return service.logout();
    }

    public Observable<FirebaseService.LoginStatus> login(String username, String password) {
        if (!bound)
            return Observable.just(FirebaseService.LoginStatus.LOGGED_OUT);

        return service.login(username, password);
    }

    public Observable adapter$() {
        return adapterPublishSubject.asObservable();
    }

    public void setFilter(String filter) {
        filterSubject.onNext(filter);
    }

    public void unbind() {
        Log.d(TAG, String.format("unbind() - service is %s", bound));
        if (bound)
            FirebaseServiceConnection.this.context.unbindService(serviceConnction);
        bound = false;
    }

    public boolean bind() {
        boolean boundResult = this.context.bindService(new Intent(this.context, FirebaseService.class), serviceConnction, Context.BIND_AUTO_CREATE);
        Log.d(TAG, String.format("bind() - %s", boundResult));
        return boundResult;
    }

    public Observable<String> logged$() {
        return loggedInAs.asObservable();
    }

    private enum BoundResult {
        BOUND,
        NOT_BOUND
    }

    private class MyServiceConnction implements ServiceConnection {
        private CompositeSubscription subscriptions = new CompositeSubscription();

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, String.format("onServiceConnected(%s, %s)", componentName, iBinder));
            bound = true;
            FirebaseServiceConnection.this.iBinder = iBinder;

            FirebaseService.LocalBinder binder = (FirebaseService.LocalBinder) iBinder;
            service = binder.getService();


            Subscription subscription = service.loggedInAs$().subscribe(new Action1<String>() {
                @Override
                public void call(String loggedInAs) {
                    FirebaseServiceConnection.this.loggedInAs.onNext(loggedInAs);
                }
            });

            subscriptions.add(subscription);

            final AttendeesAdapter adapter = service.getAdapter();
            adapterPublishSubject.onNext(adapter);

            Subscription subscription2 = filterSubject.subscribe(new Action1<String>() {
                @Override
                public void call(String filter) {
                    adapter.setFilter(filter);
                }
            });

            subscriptions.add(subscription2);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
            adapterPublishSubject.onNext(null);
            subscriptions.clear();
        }
    }
}
