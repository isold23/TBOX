package com.melon.tbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        showTermsPrivacyDialog();
    }

    private void showTermsPrivacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("用户协议和隐私政策");

        // 设置弹窗布局
        final TextView textView = new TextView(this);
        textView.setText("在这里放置用户协议和隐私政策的内容。");
        textView.setTextSize(16);

        builder.setView(textView);

        // 设置同意按钮
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理用户同意的逻辑
                dialog.dismiss();
            }
        });

        // 设置取消按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 处理用户取消的逻辑
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}