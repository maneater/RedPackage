package cn.easyar.samples.helloarmultitargetst;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.maneater.ar.CreateTargetActivity;

import java.io.File;

import cn.easyar.samples.helloarmultitargetst.camera.CameraActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickCreate(View view) {
//        CreateTargetActivity.launch(MainActivity.this, 1);
        CameraActivity.launch(MainActivity.this, 1);
    }

    public void onClickFind(View view) {
        if (targetBitmap != null) {
            FindTargetActivity.launch(MainActivity.this, targetBitmap.getAbsolutePath());
        } else {
            Toast.makeText(getApplicationContext(), "Please Create Target", Toast.LENGTH_LONG).show();
        }
    }

    public void onClick3D(View view) {
        startActivity(new Intent(this, Image3DActivity.class));
    }


    private File targetBitmap = null;

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String targetPath = data.getStringExtra("target");
            if (targetPath != null) {
                File bitmap = new File(targetPath);
                targetBitmap = bitmap;
                Toast.makeText(getApplicationContext(), "Target Create Success", Toast.LENGTH_LONG).show();
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageURI(null);
                imageView.setImageURI(Uri.fromFile(bitmap));
            }
        }
    }
}
