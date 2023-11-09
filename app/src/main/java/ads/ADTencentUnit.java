package ads;

import android.content.Context;
import android.util.Log;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.rewardvideo.ServerSideVerificationOptions;
import com.qq.e.comm.managers.GDTAdSdk;
import com.qq.e.comm.managers.setting.GlobalSetting;
import com.qq.e.comm.util.AdError;

import java.util.Locale;
import java.util.Map;

public class ADTencentUnit implements ADUnit, RewardVideoADListener {
    private String TAG = "ADTENCENT";
    private String appID = ADConstants.TENCENTAPPID_TEST;
    private String posID = ADConstants.TENCENTPOSID_TEST;
    private RewardVideoAD mRewardVideoAD;
    private boolean mADLoaded = false;
    private boolean mADCached = false;
    private AdCallback mAdCallback;

    public void setCallback(AdCallback callback) {
        this.mAdCallback = callback;
    }
    @Override
    public void initAd(Context context) {
        GlobalSetting.setEnableCollectAppInstallStatus(true);
        GDTAdSdk.init(context, appID);
        GlobalSetting.setChannel(3);
        mRewardVideoAD = new RewardVideoAD(context, posID, this);
    }

    @Override
    public boolean isInterstitialAdValid() {
        return mRewardVideoAD.isValid() && mADLoaded && mADCached;
    }

    @Override
    public void loadInterstitialAd(Context context) {
        mRewardVideoAD.loadAD();
        mADLoaded = false;
        mADCached = false;
    }

    @Override
    public void showInterstitialAd(Context context) {
        if (mADLoaded) {//广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
            if (!mRewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
                //广告展示检查3：展示广告前判断广告数据未过期
                if (mRewardVideoAD.isValid()) {
                    mRewardVideoAD.showAD();
                } else {
                    Log.d(TAG, "腾讯激励视频广告已过期，请再次请求广告后进行广告展示！");
                }
            } else {
                Log.d(TAG, "腾讯激励视频广告 此条广告已经展示过，请再次请求广告后进行广告展示！");
            }
        } else {
            Log.d(TAG, "腾讯激励视频广告 成功加载广告后再进行广告展示！");
        }
    }

    @Override
    public void onADLoad() {
        mADLoaded = true;
        Log.d(TAG, "Tencent AD loaded.");
        mAdCallback.onAdLoaded();
    }

    @Override
    public void onVideoCached() {
        mADCached = true;
        Log.d(TAG, "Tencent Video AD Cached.");
    }

    @Override
    public void onADShow() {
        Log.d(TAG, "Tencent AD Show.");
    }

    @Override
    public void onADExpose() {
        Log.d(TAG, "Tencent AD Expose.");
    }

    @Override
    public void onReward(Map<String, Object> map) {
        Log.d(TAG, "Tencent AD Reward." + map.get(ServerSideVerificationOptions.TRANS_ID));
    }

    @Override
    public void onADClick() {
        Log.d(TAG, "Tencent AD Click.");
    }

    @Override
    public void onVideoComplete() {
        Log.d(TAG, "Tencent AD Complete.");
        mAdCallback.onAdShowComplete();
    }

    @Override
    public void onADClose() {
        Log.d(TAG, "Tencent AD Close.");
        mAdCallback.onAdShowComplete();
    }

    @Override
    public void onError(AdError adError) {
        String msg = String.format(Locale.getDefault(), " error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
        Log.d(TAG, "Tencent AD Error." + msg);
    }
}
