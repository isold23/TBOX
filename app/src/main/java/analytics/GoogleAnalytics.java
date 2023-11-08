package analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class GoogleAnalytics {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static void LogEvent(String eventName, String eventContent) {
        Bundle params = new Bundle();
        params.putString("DownloadContent", eventContent);
        mFirebaseAnalytics.logEvent(eventName, params);
    }
}
