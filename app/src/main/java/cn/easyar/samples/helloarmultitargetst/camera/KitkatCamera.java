/*
 *
 * KatkitCamera.java
 * 
 * Created by Wuwang on 2016/11/12
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package cn.easyar.samples.helloarmultitargetst.camera;

import android.graphics.ImageFormat;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.util.Size;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Description:
 */
public class KitkatCamera implements ICamera {

    private Config mConfig;
    private Camera mCamera;
    private CameraSizeComparator sizeComparator;

    private Camera.Size picSize;
    private Camera.Size preSize;

    private Point mPicSize;
    private Point mPreSize;

    public KitkatCamera() {
        this.mConfig = new Config();
        mConfig.minPreviewWidth = 720;
        mConfig.minPictureWidth = 720;
        mConfig.rate = 1.778f;
        sizeComparator = new CameraSizeComparator();
    }

    @Override
    public boolean open(int cameraId) {
        mCamera = Camera.open(cameraId);

        if (mCamera != null) {
            Camera.Parameters param = mCamera.getParameters();

            for (Camera.Size size : param.getSupportedPictureSizes()) {
                Log.e("wuwang", "support pic size :" + size.width + "/" + size.height);
            }

            picSize = getPropPictureSize(param.getSupportedPictureSizes(), mConfig.rate,
                    mConfig.minPictureWidth);


            preSize = getPropPreviewSize(param.getSupportedPreviewSizes(), mConfig.rate, mConfig
                    .minPreviewWidth);

            for (Camera.Size size : param.getSupportedPreviewSizes()) {
                Log.e("wuwang", "support pre size :" + size.width + "/" + size.height);
            }


            param.setPictureSize(picSize.width, picSize.height);
            param.setPreviewSize(preSize.width, preSize.height);
            param.setPictureFormat(ImageFormat.JPEG);
            param.setJpegQuality(100);

            mCamera.setParameters(param);

            Camera.Size pre = param.getPreviewSize();
            Camera.Size pic = param.getPictureSize();
            mPicSize = new Point(pic.height, pic.width);
            mPreSize = new Point(pre.height, pre.width);
            Log.e("wuwang", "camera previewSize:" + mPreSize.x + "/" + mPreSize.y);
            Log.e("wuwang", "camera pictureSize:" + mPicSize.x + "/" + mPicSize.y);
            return true;
        }
        return false;
    }

    public void setPreviewTexture(SurfaceTexture texture) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setConfig(Config config) {
        this.mConfig = config;
    }

    @Override
    public boolean preview() {
        if (mCamera != null) {
            mCamera.startPreview();
        }
        return false;
    }


    @Override
    public boolean switchTo(int cameraId) {
        close();
        open(cameraId);
        return false;
    }

    @Override
    public void takePhoto(final TakePhotoCallback callback) {

        Log.e("Camera", mCamera.getParameters().getPictureSize().width
                + "|" + mCamera.getParameters().getPictureSize().height
                + "|" + mCamera.getParameters().getPictureFormat());
        mCamera.setParameters(mCamera.getParameters());
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                callback.onTakePhoto(data, 0, 0);
            }
        });
    }

    @Override
    public boolean close() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Point getPreviewSize() {
        return mPreSize;
    }

    @Override
    public Point getPictureSize() {
        return mPicSize;
    }

    @Override
    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
                }
            });
        }
    }

    public void addBuffer(byte[] buffer) {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(buffer);
        }
    }

    public void setOnPreviewFrameCallbackWithBuffer(final PreviewFrameCallback callback) {
        if (mCamera != null) {
            Log.e("wuwang", "Camera set CallbackWithBuffer");
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
                }
            });
        }
    }


    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            if ((s.height >= minWidth) && equalRate(s, th)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;
        }
        return list.get(i);
    }

    private boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.height == rhs.height) {
                return 0;
            } else if (lhs.height > rhs.height) {
                return 1;
            } else {
                return -1;
            }
        }

    }


}
