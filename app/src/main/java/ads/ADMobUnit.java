package ads;

import static android.provider.Settings.System.getString;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.melon.tbox.BuildConfig;
import com.melon.tbox.MainActivity;
import com.melon.tbox.R;

import java.util.Locale;

public class ADMobUnit implements ADUnit {
    private String TAG = "ADMOB";
    private AdCallback mAdCallback;
    private InterstitialAd mInterstitialAd;
    public static long lastShowAdTimestamp;

    public interface AdCallback{
        public abstract void onAdLoaded();
        public abstract void onAdShowComplete();
    }

    public void setCallback(AdCallback callback) {
        this.mAdCallback = callback;
    }

    public void initAd(Context context) {
        lastShowAdTimestamp = System.currentTimeMillis();
        MobileAds.initialize(context, initializationStatus -> {
        });
    }

    public boolean isInterstitialAdValid() {
        return mInterstitialAd != null;
    }

    public void loadInterstitialAd(Context context) {
        if(mInterstitialAd != null) return;
        Activity activity = (Activity) context;
        AdRequest adRequest = new AdRequest.Builder().build();

//      This is an ad unit ID for an interstitial test ad. Replace with your own interstitial ad unit id.
//      For more information, see https://support.google.com/admob/answer/3052638

        String interstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712";
        if(!BuildConfig.DEBUG) {
            interstitialAdUnitId = "ca-app-pub-9104439309705517/5121695742";
        }
        InterstitialAd.load(context, interstitialAdUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d(TAG, "The ad was dismissed.");
                        mAdCallback.onAdShowComplete();
                        loadInterstitialAd(context);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitialAd = null;
                        Log.d(TAG, "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        Log.d(TAG, "The ad was shown.");
                    }
                });
                Log.d(TAG, "InterstitialAd loaded.");
                mAdCallback.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;

                String error = String.format(Locale.ENGLISH, "domain: %s, code: %d, message: %s", loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                Log.d(TAG, "InterstitialAd load failed. error info: " + error);
            }
        });
    }

    public void showInterstitialAd(Context context) {
        Activity activity = (Activity) context;
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        long now = System.currentTimeMillis();
        if (mInterstitialAd != null && (now - lastShowAdTimestamp) > 10*1000) {
            mInterstitialAd.show(activity);
            lastShowAdTimestamp = now;
        } else {
            Log.d(TAG, "Ad did not load");
        }
    }

}
