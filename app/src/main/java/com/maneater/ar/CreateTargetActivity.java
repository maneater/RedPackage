package com.maneater.ar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.maneater.ar.camera.CameraManager;
import com.maneater.ar.camera.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

import cn.easyar.samples.helloarmultitargetst.R;

public class CreateTargetActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = CreateTargetActivity.class.getSimpleName();
    private CameraManager cameraManager;
    private SurfaceView vSurfaceView = null;
    private View vCenterView = null;

    private boolean isHasSurface = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_create_target);
        vSurfaceView = (SurfaceView) findViewById(R.id.vSurfaceView);
        vCenterView = findViewById(R.id.vCenterView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        if (isHasSurface) {
            initCamera(vSurfaceView.getHolder());
        } else {
            vSurfaceView.getHolder().addCallback(this);
        }
    }

    @Override
    protected void onPause() {
        cameraManager.closeDriver();
        if (!isHasSurface) {
            vSurfaceView.getHolder().removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            cameraManager.startPreview();
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }


    /**
     * 初始化截取的矩形区域
     */
    private Rect createCropRect() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        Log.i(TAG, "[cameraResolution]" + cameraWidth + "|" + cameraHeight);


        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        vCenterView.getLocationInWindow(location);

        ViewGroup parentView = (ViewGroup) vCenterView.getParent();

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

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

    private int getStatusBarHeight() {
        //是否考虑状态栏的高度
        if (false) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                return getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public static void launch(Activity activity, int reqCode) {
        Intent intent = new Intent(activity, CreateTargetActivity.class);
        activity.startActivityForResult(intent, reqCode);
    }


    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
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
    };

    private Bitmap targetBitmap = null;

    private Bitmap corpBitmap(Bitmap rotatedBitmap) {
        Log.e(TAG, rotatedBitmap.getWidth() + "|" + rotatedBitmap.getHeight());
        Rect mCropRect = createCropRect();
        return Bitmap.createBitmap(rotatedBitmap, mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());
    }


    public void takePhoto(View view) {
        if (targetBitmap != null) {
            Intent intent = new Intent();
            intent.putExtra("target", ImageUtil.outputFile(targetBitmap, new File(getCacheDir(), "target")).getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            cameraManager.takePicture(mPicture);
        }
    }


}
