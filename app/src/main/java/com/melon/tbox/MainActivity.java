package com.melon.tbox;


import ads.ADTencentUnit;
import ads.AdCallback;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
//import android.view.View;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ads.ADMobUnit;
import analytics.GoogleAnalytics;
import com.melon.tbox.games.sudoku.SudokuActivity;
import tools.DownloadDouYinVideo;
import tools.DownloadKuaiShouVideo;
import tools.DownloadTikTokVideo;
import tools.DownloadYouTubeVideo;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static BroadcastReceiver broadcastReceiver;

    private final String[] downloadPlatform = new String[]{"抖音", "快手", "TikTok"};
    private final List<String> list = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String select_item;
    private Button mDownloadButton;
    private Button mSudokuButton;
    private TextView mDownloadTextView;
    
    //douyin download
    private final DownloadDouYinVideo downloadDouYinVideo = new DownloadDouYinVideo();
    private final DownloadKuaiShouVideo downloadKuaiShouVideo = new DownloadKuaiShouVideo();
    //tiktok download
    private final DownloadTikTokVideo downloadTiktokVideo = new DownloadTikTokVideo();
    //youtube download
    private final DownloadYouTubeVideo downloadYoutubeVideo = new DownloadYouTubeVideo();

    private Context mContext;
    private final ADMobUnit mobAD = new ADMobUnit();
    private final ADTencentUnit tencentAD = new ADTencentUnit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showAlertDialog();
        mContext = this;
        //admob
        mobAD.initAd(this);
        mobAD.loadInterstitialAd(this);
        tencentAD.initAd(this);
        tencentAD.loadInterstitialAd(this);
        setCallback();
        //google analytics
        GoogleAnalytics.init(mContext);

        //douyin download
        // Create the next level button, which tries to show an interstitial when clicked.
        mDownloadButton = findViewById(R.id.download_button);
        mDownloadButton.setEnabled(true);
        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tencentAD.isInterstitialAdValid()) {
                    tencentAD.showInterstitialAd(mContext);
                } else if (mobAD.isInterstitialAdValid()) {
                    mobAD.showInterstitialAd(mContext);
                } else {
                    download();
                }
            }
        });

        mSudokuButton = findViewById(R.id.sudoku_button);
        mSudokuButton.setEnabled(true);
        mSudokuButton.setVisibility(View.INVISIBLE);
        mSudokuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SudokuActivity.class);
                startActivity(intent);
            }
        });

        mDownloadTextView = (TextView) findViewById(R.id.editTextTextPersonName);
        Collections.addAll(list, downloadPlatform);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sp = findViewById(R.id.download_type);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                select_item = adapter.getItem(position);
                Log.d(TAG, "select item: "+select_item);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregister(mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCallback() {
        mobAD.setCallback(new AdCallback() {
            @Override
            public void onAdLoaded() {
                //mDownloadButton.setEnabled(true);
                Log.d(TAG, "MainActivity onAdLoaded.");
            }

            @Override
            public void onAdShowComplete() {
                Log.d(TAG, "MainActivity onAdShowComplete");
                download();
            }
        });
        tencentAD.setCallback(new AdCallback() {
            @Override
            public void onAdLoaded() {
                //mDownloadButton.setEnabled(true);
                Log.d(TAG, "TencentAD MainActivity onAdLoaded.");
            }

            @Override
            public void onAdShowComplete() {
                Log.d(TAG, "MainActivity onAdShowComplete");
                download();
            }
        });

    }

    private void download() {
        if(select_item.equals(downloadPlatform[0])) {
            GoogleAnalytics.LogEvent("DouYinDownload", mDownloadTextView.getText().toString());
            downloadDouYinVideo.downloadVideo(mContext, mDownloadTextView.getText().toString());
        } else if(select_item.equals(downloadPlatform[1])) {
            GoogleAnalytics.LogEvent("KuaiShouDownload", mDownloadTextView.getText().toString());
            downloadKuaiShouVideo.downloadVideo(mContext, mDownloadTextView.getText().toString());
        } else if(select_item.equals(downloadPlatform[2])) {
            GoogleAnalytics.LogEvent("TikTokDownload", mDownloadTextView.getText().toString());
            downloadTiktokVideo.downloadVideo(mContext, mDownloadTextView.getText().toString());
        } else if(select_item.equals(downloadPlatform[3])) {
            GoogleAnalytics.LogEvent("YouTubeDownload", mDownloadTextView.getText().toString());
            downloadYoutubeVideo.downloadVideo(mContext, mDownloadTextView.getText().toString());
        }
    }

    public static void register(Context context, final long Id) {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(Id);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        @SuppressLint("Range") int status = cursor.getInt(
                                cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_FAILED) {
                            @SuppressLint("Range") int reason = cursor.getInt(
                                    cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            String fmsg = "Download failed. Reason: " + reason;
                            Toast.makeText(context, fmsg,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Download Video Completed!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    cursor.close();
                }
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public static void unregister(Context context) {
        if(broadcastReceiver != null && broadcastReceiver.isOrderedBroadcast()) {
            context.unregisterReceiver(broadcastReceiver);
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setLineSpacing(10, 1);
        textView.setPadding(50, 30, 50, 30);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(R.string.privacy);

        builder.setTitle("服务协议和隐私政策")
                .setView(textView)
                .setCancelable(false)
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when the positive button is clicked
                    }
                })
                .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do something when the negative button is clicked
                        finish();
                    }
                });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}