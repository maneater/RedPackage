/**
 * Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
 * EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
 * and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
 */

package cn.easyar.samples.helloarmultitargetst;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.maneater.ar.ARManager;
import com.maneater.ar.GLView;

import java.io.File;


public class FindTargetActivity extends AppCompatActivity {

    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloARMultiTarget-ST
    *      Package Name: cn.easyar.samples.helloarmultitargetst
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    static String key = "PEMgJBOg6BWttMXFRVwOYVWyRvZlqWGgWgLUOFHAN4bgsVRzITJotRxpiezkPP265WJuTKzIE1eoW7gWnVnNacGXPJD9dMuFFzKM19556e43853a5931c980c43727b9eac70o0xNywsU6Nh4i90vZRYTMvuZwionTLWJ4ttB5Sov0hneahtp7Ta7qy5u5m6th3JOfpI";

    ARManager arManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        arManager = new ARManager(this, key);
        arManager.init();


        arManager.loadTarget(getIntent().getStringExtra("file"));

        ImageView targetView = (ImageView) findViewById(R.id.targetView);
        targetView.setImageURI(Uri.fromFile(new File(getIntent().getStringExtra("file"))));

        GLView glView = new GLView(this);
        glView.setRenderer(arManager.getRender());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        arManager.onRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        arManager.onRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arManager.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        arManager.onPause();
    }

    public static void launch(Activity activity, String targetFile) {
        Intent intent = new Intent(activity, FindTargetActivity.class);
        intent.putExtra("file", targetFile);
        activity.startActivity(intent);
    }
}
