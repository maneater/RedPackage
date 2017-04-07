package com.maneater.ar;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.easyar.engine.EasyAR;

public class ARManager {
    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("helloar");
    }

    public interface TraceSuccessListener {
        void onSuccess();
    }

    private native void nativeInitGL();

    private native void nativeResizeGL(int w, int h);

    private native boolean nativeRender();

    private native boolean nativeInit();

    private native void nativeOnScroll(float xOff, float yOff);

    private native void nativeLoadTarget(String targetFilePath);

    private native void nativeDestroy();

    private native void nativeRotationChange(boolean portrait);


    public ARManager(Activity activity, String key) {
        EasyAR.initialize(activity, key);
    }

    public void onDestroy() {
        traceSuccessListener = null;
        nativeDestroy();
    }

    public void onResume() {
        EasyAR.onResume();
    }

    public void onPause() {
        EasyAR.onPause();
    }

    private TraceSuccessListener traceSuccessListener = null;

    public void setTraceSuccessListener(TraceSuccessListener traceSuccessListener) {
        this.traceSuccessListener = traceSuccessListener;
    }

    private void onTraceSuccess() {
        if (traceSuccessListener != null) {
            traceSuccessListener.onSuccess();
        }
    }

    private GLSurfaceView.Renderer renderer = null;

    public GLSurfaceView.Renderer getRender() {
        if (renderer == null) {
            renderer = new GLView.TouchRender() {
                @Override
                public void onScroll(float xOff, float yOff) {
                    nativeOnScroll(xOff, yOff);
                }

                @Override
                public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                    nativeInitGL();
                }

                @Override
                public void onSurfaceChanged(GL10 gl, int width, int height) {
                    nativeResizeGL(width, height);
                }

                @Override
                public void onDrawFrame(GL10 gl) {
                    if (nativeRender()) {
                        onTraceSuccess();
                    }
                }
            };
        }

        return renderer;
    }


    public void onRotationChange(boolean port) {
        nativeRotationChange(port);
    }

    public void init() {
        nativeInit();
    }

    public void loadTarget(String taregetFilePath) {
        nativeLoadTarget(taregetFilePath);
    }
}
