package ads;

import android.content.Context;

public interface ADUnit {
    public abstract void initAd(Context context);
    public abstract boolean isInterstitialAdValid();
    public abstract void loadInterstitialAd(Context context);
    public abstract void showInterstitialAd(Context context);
}
