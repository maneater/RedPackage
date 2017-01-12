/*
 *
 * CameraActivity.java
 * 
 * Created by Wuwang on 2016/11/14
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package cn.easyar.samples.helloarmultitargetst.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.maneater.ar.camera.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;

import cn.easyar.samples.helloarmultitargetst.R;
import cn.easyar.samples.helloarmultitargetst.utils.PermissionUtils;

/**
 * Description:
 */
public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "Create";
    private CameraView mCameraView;
    private ImageView vResultImage;
    private View vCenterView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionUtils.askPermission(this, new String[]{Manifest.permission.CAMERA, Manifest
                .permission.WRITE_EXTERNAL_STORAGE}, 10, initViewRunnable);
    }

    private Runnable initViewRunnable = new Runnable() {
        @Override
        public void run() {
            setContentView(R.layout.activity_camera);
            mCameraView = (CameraView) findViewById(R.id.mCameraView);
            vResultImage = (ImageView) findViewById(R.id.vResultImage);
            vCenterView = findViewById(R.id.vCenterView);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode == 10, grantResults, initViewRunnable,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("切换摄像头").setTitle("切换摄像头").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name = item.getTitle().toString();
        if (name.equals("切换摄像头")) {
            mCameraView.switchCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(Activity activity, int req) {
        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivityForResult(intent, req);
    }

    public void onClickCreate(View view) {
        if (targetBitmap != null) {
            Intent intent = new Intent();
            intent.putExtra("target", ImageUtil.outputFile(targetBitmap, new File(getCacheDir(), "target")).getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            mCameraView.takePicture(new ICamera.TakePhotoCallback() {
                @Override
                public void onTakePhoto(byte[] data, int width, int height) {
                    File pictureFile = new File(getCacheDir(), System.currentTimeMillis() + ".jpg");
                    try {
                        pictureFile.createNewFile();
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(data);
                        fos.close();
                        ImageView vResultImage = (ImageView) findViewById(R.id.vResultImage);
                        Bitmap corpBitmap = corpBitmap(ImageUtil.getRotatedBitmap(pictureFile.getAbsolutePath(), vResultImage.getWidth(), vResultImage.getHeight()));
                        if (corpBitmap != null) {
                            targetBitmap = corpBitmap;
                        }
                        vResultImage.setImageBitmap(corpBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private Bitmap targetBitmap = null;

    private Bitmap corpBitmap(Bitmap rotatedBitmap) {
        Log.e(TAG, rotatedBitmap.getWidth() + "|" + rotatedBitmap.getHeight());
        Rect mCropRect = createCropRect(rotatedBitmap.getWidth(), rotatedBitmap.getHeight());

        return Bitmap.createBitmap(rotatedBitmap, mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());
    }

    /**
     * 初始化截取的矩形区域
     */
    private Rect createCropRect(int picW, int picH) {
        int cameraWidth = picW;
        int cameraHeight = picH;

        Log.i(TAG, "[cameraResolution]" + cameraWidth + "|" + cameraHeight);


        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        vCenterView.getLocationInWindow(location);

        ViewGroup parentView = (ViewGroup) vCenterView.getParent();

        int cropLeft = location[0];
        int cropTop = location[1];

        int cropWidth = vCenterView.getWidth();
        int cropHeight = vCenterView.getHeight();

        int containerW = parentView.getWidth();
        int containerH = parentView.getHeight();

        int x = (int) (cropLeft * 1.0f / containerW * cameraWidth);
        int y = (int) (cropTop * 1.0f / containerH * cameraHeight);

        int width = (int) (cropWidth * 1.0f / containerW * cameraWidth);
        int height = (int) (cropHeight * 1.0f / containerH * cameraHeight);

        Rect rect = new Rect(x, y, x + width, y + height);

        Log.i(TAG, "[mCropRect]" + rect.toString());
        /** 生成最终的截取的矩形 */
        return rect;

    }
}
