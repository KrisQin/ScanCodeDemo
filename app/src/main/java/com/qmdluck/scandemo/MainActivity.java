package com.qmdluck.scandemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zxing.IdentifyBitmap;
import com.zxing.activity.CommonScanActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tv_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_code = findViewById(R.id.tv_code);

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resourcePic();
            }
        });
    }

    public void test(View view) {
        getCameraPic();
    }

    public void resourcePic() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scan_code);

        IdentifyBitmap.scanningImage(bitmap, new IdentifyBitmap.CallbackResult() {
            @Override
            public void callback(final String result) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_code.append(result+"\n");
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // 返回的条形码数据
            if (null != data) {
                String code = data.getStringExtra("ScoreScanCode");
                // 输入文本输入框中
                Log.d("tag","code: "+code);

                tv_code.setText(code);
            }
        }

    }

    private static final  int requestFaceCode = 101;

    private void getCameraPic() {
        //打开摄像头
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestFaceCode);

            } else {
                startActivity();
            }
        } else {
            startActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        //人脸识别
        if (requestCode == MainActivity.requestFaceCode) {

            boolean isCan = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isCan = false;
                }
            }

            if (isCan) {
                startActivity();
            }else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

        }

    }

    private void startActivity() {
        Intent intent = new Intent();
        intent.setClass(this, CommonScanActivity.class);
        startActivityForResult(intent, 0);

    }

}
