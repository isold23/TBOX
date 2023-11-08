package tools;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import com.melon.tbox.MainActivity;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadKuaiShouVideo implements DownloadVideo{
    private final String TAG = "DownloadKuaiShouVideo";
    private Context mContext;
    private final AtomicReference<String> vv = new AtomicReference<>();

    @Override
    public void downloadVideo(Context context, String url) {
        if(url == null) return;
        mContext = context;
        //
        String new_url = url.substring(url.indexOf("https"), url.indexOf(' '));
        Log.d(TAG, "download from douyin, url: " + new_url);
        withoutWatermark(new_url);
    }

    private String getItemId(final String url) {
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
                int start_pos = ss.indexOf("video/") + 6;
                int end_pos = ss.indexOf("/", start_pos);
                String itemid = ss.substring(start_pos, end_pos);
                Log.d(TAG, "itemid: " + itemid);
                new_url = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=" + itemid;
            }
        } catch (final Exception e) {
            Log.e("douyin", "", e);
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
        return new_url;
    }

    private String getVideoUrl(final String url) {
        if(url == null) return null;
        AtomicReference<String> v = new AtomicReference<>();
        HttpsURLConnection httpsURLConnection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer;
        try {
            httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.connect();
            Log.d(TAG, "Response Code: " + httpsURLConnection.getResponseCode());
            if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = httpsURLConnection.getInputStream();
                isr = new InputStreamReader(is, "UTF8");
                bufferedReader = new BufferedReader(isr);
                stringBuffer = new StringBuffer();
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    stringBuffer.append(readLine);
                }
                JSONObject json = new JSONObject(stringBuffer.toString());
                Object videoUrl = json.getJSONArray("item_list")
                        .getJSONObject(0)
                        .getJSONObject("video")
                        .getJSONObject("play_addr")
                        .getJSONArray("url_list")
                        .get(0);
                v.set(videoUrl.toString().replace("/playwm/", "/play/"));
                Log.d(TAG, "video url: "+ v.toString());
            }
        } catch (final Exception e) {
            Log.e(TAG, "", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (v != null) {
            return v.get();
        } else {
            return "";
        }
    }

    private String getVideoUrl1(final String url) {
        if(url == null) return null;
        String info_url = "https://api.cooluc.com/?url="+url;
        AtomicReference<String> v = new AtomicReference<>();
        HttpsURLConnection httpsURLConnection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer;
        String videoUrl = "";
        try {
            httpsURLConnection = (HttpsURLConnection) new URL(info_url).openConnection();
            httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.connect();
            Log.d(TAG, "Response Code: " + httpsURLConnection.getResponseCode());
            if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = httpsURLConnection.getInputStream();
                isr = new InputStreamReader(is, "UTF8");
                bufferedReader = new BufferedReader(isr);
                stringBuffer = new StringBuffer();
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    stringBuffer.append(readLine);
                }
                JSONObject json = new JSONObject(stringBuffer.toString());
                videoUrl = json.getString("video");
                Log.d(TAG, "video url: "+ videoUrl);
            }
        } catch (final Exception e) {
            Log.e(TAG, "", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (httpsURLConnection != null) {
                    httpsURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return videoUrl;
    }

    private String getMp4Url(final String url) {
        if(url == null) return null;
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
            Log.e("douyin", "", e);
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
            if(msg == null) return;
            Log.d(TAG, "msg : "+vv.toString());
            /*
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse(vv.toString()));

            PackageManager packageManager = mContext.getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent1, 0);
            boolean isIntentSafe = activities.size() > 0;
            mContext.startActivity(intent1);
            */

            DownloadManager downloadManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                downloadManager = (DownloadManager) mContext.getSystemService(DOWNLOAD_SERVICE);
            }

            final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(vv.toString()));

            request.setTitle("TBOX Download KuaiShou Video");
            request.setVisibleInDownloadsUi(true);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,
                    ("kuaishou__" + System.currentTimeMillis()) + "." + "mp4");
            if (downloadManager != null) {
                long id = downloadManager.enqueue(request);
                if (id == -1) {
                    Log.e(TAG, "Failed to enqueue the download request");
                } else {
                    Log.d(TAG, "Download enqueued successfully");
                    MainActivity.register(mContext, id);
                }
            } else {
                Log.e(TAG, "DownloadManager not available");
            }
        }
    };

    private String withoutWatermark(final String url) {
        if(url == null) return null;
        try {
            Thread thread = new Thread(() -> {
                //String ss = getMp4Url(getVideoUrl(getItemId(url)));
                String ss = getVideoUrl1(url);
                if(ss != null) {
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
