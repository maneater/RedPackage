package cn.easyar.samples.helloarmultitargetst;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maneater.ar.ImageView3D;

public class Image3DActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image3_d);
        ImageView3D imageView3D = (ImageView3D) findViewById(R.id.vImageView3d);
        imageView3D.startAuto();
    }
}
