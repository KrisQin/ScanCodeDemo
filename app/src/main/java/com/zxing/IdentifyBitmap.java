package com.zxing;

import android.graphics.Bitmap;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.zxing.decode.DecodeThread;
import com.zxing.decode.PhotoScanHandler;
import com.zxing.decode.RGBLuminanceSource;

import java.util.EnumMap;
import java.util.Map;

public class IdentifyBitmap {

    public interface CallbackResult {
        void callback(String result);
    }

    /**
     * 识别二维码或者一维码图片
     * @param bitmap 图片
     * @return
     */
    public  static void scanningImage(final Bitmap bitmap, final CallbackResult callbakcResult) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                //获取初始化的设置器
                Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);;
                hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
                RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
                BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                MultiFormatReader multiFormatReader=new MultiFormatReader();
                try {

                    Result rawResult = multiFormatReader.decode(bitmap1, hints);

                    System.out.println("扫描结果" + rawResult.getText());

                    if (callbakcResult != null) {
                        callbakcResult.callback(rawResult.getText());
                    }

                } catch (Exception e) {

                    if (callbakcResult != null) {
                        callbakcResult.callback("识别失败，图片有误，或者图片模糊！");
                    }
                }
            }
        }).start();
    }

}
