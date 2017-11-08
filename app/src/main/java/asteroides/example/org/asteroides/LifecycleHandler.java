package asteroides.example.org.asteroides;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by jmtt_ on 11/8/2017.
 */

public class LifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "LifecycleHandler";
    private Asteroids.BackgroundListener listener;
    private Activity previus;
    private Activity actual;

    public LifecycleHandler(Asteroids.BackgroundListener backgroundListener) {
        listener = backgroundListener;
        actual = null;
        previus = null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(actual == null){
            listener.onSendForeground();
            Log.d(TAG, "onActivityResumed: app go to foreground");
        }
        actual = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        previus = activity;
        if(actual != null && previus.getLocalClassName().equals(actual.getLocalClassName())){
            actual = null;
            listener.onSendBackground();
            Log.d(TAG, "onActivityStopped: app go to background");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
