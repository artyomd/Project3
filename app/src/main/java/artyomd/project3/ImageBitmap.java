package artyomd.project3;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by artyomd on 5/3/16.
 */
public class ImageBitmap extends DrawingObject {
    private Integer key;
    public static Map<Integer, Bitmap> map = new HashMap<>();
    private Paint paint;

    public Integer getKey() {
        return key;
    }

    @Override
    public float getCenterX() {
        return x + map.get(key).getWidth() / 2;
    }

    @Override
    public float getCenterY() {
        return y + map.get(key).getHeight() / 2;
    }

    @Override
    public void setCenterX(float x) {
        this.x = x - map.get(key).getWidth() / 2;
    }

    @Override
    public void setCenterY(float y) {
        this.y = y - map.get(key).getHeight() / 2;
    }

    public ImageBitmap(float x, float y, Bitmap bitmap) {
        super();
        radius = 0;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        Bitmap bmp = getCircularBitmap(bitmap);
        key = bmp.hashCode();
        map.put(key, bmp);
        this.x = x - bmp.getWidth() / 2;
        this.y = y - bmp.getHeight() / 2;

    }


    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }
        Paint paint = new Paint();
        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        if (bitmap.getWidth() > bitmap.getHeight()) {
            radius = bitmap.getHeight() / 2;
        } else {
            radius = bitmap.getWidth() / 2;
        }
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(map.get(key), x, y, paint);
    }
}
