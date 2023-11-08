package ads;

import android.content.Context;

public class ADTencentUnit implements ADUnit{
    @Override
    public void initAd(Context context) {

    }

    @Override
    public boolean isInterstitialAdValid() {
        return false;
    }

    @Override
    public void loadInterstitialAd(Context context) {

    }

    @Override
    public void showInterstitialAd(Context context) {

    }
}
