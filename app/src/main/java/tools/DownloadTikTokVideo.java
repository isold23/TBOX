package tools;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.melon.tbox.MainActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTikTokVideo implements DownloadVideo {
    private final String TAG = "DownloadTikTokVideo";
    private Context mContext;
    private AtomicReference<String> vv = new AtomicReference<>();

    @Override
    public void downloadVideo(Context context, String url) {
        mContext = context;
        //url = "https://vt.tiktok.com/ZSRW2WP4N/";
        Log.d(TAG, "url: "+url);
        withoutWatermark(url);
    }

    private String getItemId(String url) {
        if(url == null) return null;
        Log.d(TAG, "url: "+url);
        String new_url = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.connect();
            Log.d(TAG, "Response Code: " + httpsURLConnection.getResponseCode());
            if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP||
            httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
                String ss = httpsURLConnection.getHeaderField("location");
                Log.d(TAG, "location: " + ss);
                int start_pos = ss.indexOf("video/") + 6;
                int end_pos = ss.indexOf("?", start_pos);
                String itemid = ss.substring(start_pos, end_pos);
                Log.d(TAG, "itemid: " + itemid);
                new_url = "https://www.tikwm.com/video/media/hdplay/" + itemid + ".mp4";
            }
        } catch (final Exception e) {
            Log.e("tiktok", "", e);
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return new_url;
    }

    private String getVideoUrl(String url) {
        if(url == null) return null;
        Log.d(TAG, "url: "+url);
        String new_url = null;
        HttpsURLConnection httpsURLConnection = null;
        try {
            httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.connect();
            Log.d(TAG, "Response Code: " + httpsURLConnection.getResponseCode());
            if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                String ss = httpsURLConnection.getHeaderField("location");
                Log.d(TAG, "location: " + ss);
                return ss;
            }
        } catch (final Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return new_url;
    }

    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(final Message msg) {
            if(msg == null || vv == null) return;
            Log.d(TAG, "msg : "+vv.toString());
            DownloadManager downloadManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
            }
            final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(vv.toString()));
            request.setVisibleInDownloadsUi(true);
            request.allowScanningByMediaScanner();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,
                    ("tiktok__" + System.currentTimeMillis()) + "." + "mp4");

            long id = downloadManager.enqueue(request);
            MainActivity.register(mContext, id);
        }
    };

    private String withoutWatermark(final String url) {
        try {
            Thread thread = new Thread(() -> {
                String ss = getVideoUrl(getItemId(url));
                if(ss == null) {
                    vv.set(ss);
                    Log.d(TAG, "notify.");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = vv;
                    handler.sendMessage(message);
                }
            });
            thread.start();
            Log.d(TAG, "wait end.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (vv != null) {
            return vv.get();
        } else {
            return "";
        }
    }
}
