package com.example.waffle.waffledemo.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

/**
 * Created by Waffle on 2016/7/22.
 */

public class BitmapUtil {
    public static Bitmap toRoundBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);
        return backgroundBmp;
    }
}
