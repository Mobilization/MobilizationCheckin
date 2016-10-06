package pl.mobilization.mobilizationcheckin;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FirebaseIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_LOGIN = "pl.mobilization.mobilizationcheckin.action.LOGIN";
    private static final String ACTION_BAZ = "pl.mobilization.mobilizationcheckin.action.BAZ";

    private static final String PARAM_LOGIN = "pl.mobilization.mobilizationcheckin.extra.LOGIN";
    private static final String PARAM_PASSWORD = "pl.mobilization.mobilizationcheckin.extra.PASSWORD";

    public FirebaseIntentService() {
        super("FirebaseIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionLogin(Context context, String login, String password) {
        Intent intent = new Intent(context, FirebaseIntentService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(PARAM_LOGIN, login);
        intent.putExtra(PARAM_PASSWORD, password);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FirebaseIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(PARAM_LOGIN, param1);
        intent.putExtra(PARAM_PASSWORD, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOGIN.equals(action)) {
                final String param1 = intent.getStringExtra(PARAM_LOGIN);
                final String param2 = intent.getStringExtra(PARAM_PASSWORD);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(PARAM_LOGIN);
                final String param2 = intent.getStringExtra(PARAM_PASSWORD);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
