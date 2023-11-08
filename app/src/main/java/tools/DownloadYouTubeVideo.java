package tools;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

public class DownloadYouTubeVideo implements DownloadVideo {
    private final String TAG = "DownloadYoutubeVideo";
    private Context mContext;
    private AtomicReference<String> vv = new AtomicReference<>();

    public class Youtube {
        private String sUserUrl = "";
        private String sDownLoadURL = "";
        private String sFileName = "";

        public Youtube(String sUserUrl) {
            this.sUserUrl = sUserUrl;
        }

        public String getUserUrl() {
            return sUserUrl;
        }

        public void setUserUrl(String sUserUrl) {
            this.sUserUrl = sUserUrl;
        }

        public String getDownLoadURL() {
            return sDownLoadURL;
        }

        public void setDownLoadURL(String sDownLoadURL) {
            this.sDownLoadURL = sDownLoadURL;
        }

        public String getFileName() {
            return sFileName;
        }

        public void setFileName(String sFileName) {
            this.sFileName = sFileName;
        }

        @NonNull
        @Override
        public String toString() {
            return "Youtube [sUserUrl=" + sUserUrl + ", sDownLoadURL="
                    + sDownLoadURL + ", sFileName=" + sFileName + "]";
        }
    }

    private Youtube parseResponse(String content) {
        Youtube youtube = new Youtube(content);
        Vector url_encoded_fmt_stream_map = new Vector();
        int begin = 0;
        int end   = content.indexOf(",");

        while (end != -1) {
            url_encoded_fmt_stream_map.addElement(content.substring(begin, end));
            begin = end + 1;
            end   = content.indexOf(",", begin);
        }

        url_encoded_fmt_stream_map.addElement(content.substring(begin, content.length()));
        String result = "";
        Enumeration url_encoded_fmt_stream_map_enum = url_encoded_fmt_stream_map.elements();
        while (url_encoded_fmt_stream_map_enum.hasMoreElements()) {
            content = (String)url_encoded_fmt_stream_map_enum.nextElement();
            begin = content.indexOf("itag=");
            if (begin != -1) {
                end = content.indexOf("&", begin + 5);

                if (end == -1) {
                    end = content.length();
                }

                int fmt = Integer.parseInt(content.substring(begin + 5, end));

                if (fmt == 35) {
                    begin = content.indexOf("url=");
                    if (begin != -1) {
                        end = content.indexOf("&", begin + 4);
                        if (end == -1) {
                            end = content.length();
                        }
                        result = URLDecoder.decode(content.substring(begin + 4, end));
                        youtube.sDownLoadURL = result;
                        break;
                    }
                }
            }
        }
        return youtube;
    }

    private Youtube getYoutubeObj(String url) {
        String redirectUrl = "";
        boolean isRedirect = false;
        Youtube youtube = new Youtube(url);
        HttpsURLConnection httpsURLConnection = null;
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer;
        try {
            httpsURLConnection = (HttpsURLConnection) new URL(url).openConnection();
            //httpsURLConnection.setInstanceFollowRedirects(false);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();
            Log.d(TAG, "Response Code: " + httpsURLConnection.getResponseCode());
            if (httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = httpsURLConnection.getInputStream();
                isr = new InputStreamReader(is, "UTF8");
                bufferedReader = new BufferedReader(isr);
                stringBuffer = new StringBuffer();
                char[] buf=new char[262144];
                int chars_read;
                while ((chars_read = bufferedReader.read(buf, 0, 262144)) != -1) {
                    stringBuffer.append(buf, 0, chars_read);
                }
                String tmpstr=stringBuffer.toString();

                int begin  = tmpstr.indexOf("url_encoded_fmt_stream_map=");
                int end = tmpstr.indexOf("&", begin + 27);
                if (end == -1) {
                    end = tmpstr.indexOf("\"", begin + 27);
                }
                tmpstr = URLDecoder.decode(tmpstr.substring(begin + 27, end));
                youtube.sDownLoadURL = tmpstr;
            } else if(httpsURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                redirectUrl = httpsURLConnection.getHeaderField("location");
                Log.d(TAG, "location: " + redirectUrl);
                isRedirect = true;
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
                if(isRedirect) {
                    youtube = getYoutubeObj(redirectUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return youtube;
    }

    private void downloadYoutubeVideo(Youtube youtube) {
        try {
            URL urlVideoURL = new URL(youtube.getDownLoadURL());
            URLConnection urlConnection = urlVideoURL.openConnection();
            InputStream inStreamVideo = urlConnection.getInputStream();

            OutputStream outStreamVideo = new BufferedOutputStream(
                    new FileOutputStream(
                            Environment.DIRECTORY_DCIM + "/" + "youtube__" + System.currentTimeMillis() + ".mp4"));
            for (int b; (b = inStreamVideo.read()) != -1; ) {
                outStreamVideo.write(b);
            }
            outStreamVideo.close();
            inStreamVideo.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg == null) return;
            Log.d(TAG, "Download Completed!");
            Toast.makeText(mContext, "Download Completed!",
                    Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void downloadVideo(Context context, String url) {
        mContext = context;
        //https://www.youtube.com/watch?v=cXWpbrIm1kI
        try {
            Thread thread = new Thread(() -> {
                Youtube youtube = getYoutubeObj(url);
                youtube = parseResponse(youtube.sDownLoadURL);
                downloadYoutubeVideo(youtube);
                Message message = Message.obtain();
                message.what = 1;
                message.obj = youtube;
                handler.sendMessage(message);
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
