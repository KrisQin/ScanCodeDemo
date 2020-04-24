/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zxing.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmdluck.scandemo.R;
import com.google.zxing.Result;
import com.zxing.ScanListener;
import com.zxing.ScanManager;
import com.zxing.decode.DecodeThread;
import com.zxing.decode.Utils;


public final class CommonScanActivity extends Activity implements ScanListener, View.OnClickListener {
    SurfaceView scanPreview = null;
    View scanContainer;
    View scanCropView;
    ImageView scanLine;
    ScanManager scanManager;
    Button qrcodeAlburm, btnTorch;
    TextView tvTorch;
    final int PHOTOREQUESTCODE = 1111;
    //开关灯逻辑
    boolean mFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_pay_fee_acitivity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initScanView();
    }


    void initScanView() {
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_containter);
        scanCropView = findViewById(R.id.capture_crop_layout);
        tvTorch = (TextView) findViewById(R.id.aqs_tv_torch);

        TextView tvGuideInfo = (TextView) findViewById(R.id.scan_intro);
        tvGuideInfo.setText("请将条形码放入扫描框中");
        TextView tvAlbumInfo = (TextView) findViewById(R.id.tv_album_info);
        tvAlbumInfo.setText("图片条形码");

        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        qrcodeAlburm = (Button) findViewById(R.id.btn_album);
        qrcodeAlburm.setOnClickListener(this);
        btnTorch = (Button) findViewById(R.id.btn_torch);
        btnTorch.setOnClickListener(this);
        //构造出扫描管理器
        scanManager = new ScanManager(this, scanPreview,
                scanContainer, scanCropView, scanLine, DecodeThread.ALL_MODE, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        scanManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanManager.onPause();
    }

    /**
     *
     */
    public void scanResult(Result rawResult, Bundle bundle) {
        //扫描成功后，扫描器不会再连续扫描，如需连续扫描，调用reScan()方法。
        //scanManager.reScan();

        System.out.println("新扫描结果" + rawResult.getText());
        Intent intent = new Intent();
        intent.putExtra("ScoreScanCode", rawResult.getText());
        setResult(RESULT_OK, intent);
        finish();

    }

    //开始进行重新扫描
    void startScan() {
        scanManager.reScan();
    }

    @Override
    public void scanError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        //相机扫描出错时
        if (e.getMessage() != null && e.getMessage().startsWith("相机")) {
            scanPreview.setVisibility(View.INVISIBLE);
        }
    }

    public void showPictures(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photo_path;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTOREQUESTCODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(colum_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                        }
                        scanManager.scanningImage(photo_path);
                    }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_album:
                showPictures(PHOTOREQUESTCODE);
                break;
            case R.id.btn_torch:

                if (mFlag == true) {
                    mFlag = false;
                    btnTorch.setBackgroundResource(R.drawable.ic_lamp_open_new);
                    tvTorch.setText("关灯");
                } else {
                    mFlag = true;
                    btnTorch.setBackgroundResource(R.drawable.ic_lamp_close_new);
                    tvTorch.setText("开灯");
                }
                scanManager.switchLight();
                break;
            default:
                break;
        }
    }

}