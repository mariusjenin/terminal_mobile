package terminal.mobilite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import terminal.mobilite.activity.TerminalActivity;
import terminal.mobilite.manager.LocalizationManager;

/**
 * Application Terminal
 */
public class TerminalApp extends Application implements LifecycleObserver {

    private static Application TERMINAL_APP;
    @SuppressLint("StaticFieldLeak")
    private static LocalizationManager gpsTracker;

    @SuppressLint("StaticFieldLeak")
    private static TerminalActivity currentAct;

    public static Application getApplication() {
        return TERMINAL_APP;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    public static TerminalActivity getCurrentAct() {
        return currentAct;
    }

    public static void setCurrentAct(TerminalActivity cA) {
        currentAct = cA;
    }

    public static boolean FOREGROUND;

    @Override
    public void onCreate() {
        super.onCreate();
        FOREGROUND = true;
        TERMINAL_APP = this;
        gpsTracker = new LocalizationManager(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public static void onAppBackgrounded() {
        //App in background
        FOREGROUND = false;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public static void onAppForegrounded() {
        // App in foreground
        FOREGROUND = true;
    }

    public static boolean isForeground() {
        return FOREGROUND;
    }

    public static LocalizationManager getGpsTracker() {
        return gpsTracker;
    }
}
