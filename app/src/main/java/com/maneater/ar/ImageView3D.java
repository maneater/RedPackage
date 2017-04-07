package com.maneater.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ImageView3D extends View {


    private List<String> fileList = null;
    private static Bitmap[] bitmapList = null;
    private int startX;
    private int currentX;
    private int currentIndex;
    private int maxNum;
    private Bitmap bitmap;

    public ImageView3D(Context context) {
        super(context);
        init();
    }


    public ImageView3D(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageView3D(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }


    private void init() {
        setWillNotDraw(false);
        try {
            fileList = Arrays.asList(getResources().getAssets().list("success"));
            Collections.sort(fileList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if (o1.length() == o2.length()) {
                        return o1.compareTo(o2);
                    } else if (o1.length() < o2.length()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            maxNum = fileList.size();
            if (bitmapList == null) {
                bitmapList = new Bitmap[maxNum];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmap(int index) {
        if (bitmapList[index] == null) {
            try {
                bitmapList[index] = BitmapFactory.decodeStream(getResources().getAssets().open("success/" + fileList.get(index)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmapList[index];
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.save();
            canvas.translate(getWidth() / 2 - bitmap.getWidth() / 2, getHeight() / 2 - bitmap.getHeight() / 2);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.restore();
        }
    }

    private boolean isAuto = false;

    public void startAuto() {
        isAuto = true;
        postDelayed(autoRunnable, 0);
    }

    public void stopAuto() {
        isAuto = false;
        removeCallbacks(autoRunnable);
    }

    private Runnable autoRunnable = new Runnable() {
        @Override
        public void run() {
            modifySrcL();
            postDelayed(this, 30);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        stopAuto();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                // 判断手势滑动方向，并切换图片
                if (currentX - startX > 10) {
                    modifySrcR();
                } else if (currentX - startX < -10) {
                    modifySrcL();
                }
                // 重置起始位置
                startX = (int) event.getX();
                break;

        }
        return true;
    }

    // 向右滑动修改资源
    private void modifySrcR() {
        currentIndex++;
        if (currentIndex >= maxNum) {
            currentIndex = 0;
        }
        if (currentIndex >= 0 && currentIndex < maxNum) {
            bitmap = getBitmap(currentIndex);
        }
        invalidate();

    }


    // 向左滑动修改资源
    private void modifySrcL() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = maxNum - 1;
        }
        if (currentIndex >= 0 && currentIndex < maxNum) {
            bitmap = getBitmap(currentIndex);
        }
        invalidate();
    }
}