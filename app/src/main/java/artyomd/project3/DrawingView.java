package artyomd.project3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawingView extends View {

    private List<DrawingObject> objects;
    private GestureDetectorCompat mDetector;
    private DrawingObject current_object;
    private boolean drawingMode;
    private Bitmap bmp;
    private boolean isShare;
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint;

    public DrawingView(Context context) {
        super(context);
        blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());
        objects = new ArrayList<>();
        drawingMode = true;
        numColumns = 4;
        numRows = 8;
        isShare = false;
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDetector = new GestureDetectorCompat(getContext(), new MyGestureListener());
        objects = new ArrayList<>();
        drawingMode = true;
        numColumns = 4;
        numRows = 8;
        isShare = false;
    }

    public void setDrawingMode(boolean drawingMode) {
        this.drawingMode = drawingMode;
        if (drawingMode) {
            bmp.recycle();
            bmp = null;
        }
    }

    public void setBmp(Bitmap bmp) {

        this.bmp = bmp.copy(bmp.getConfig(), true);
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        mDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN):
                for (int i = this.objects.size() - 1; i >= 0; i--) {
                    DrawingObject cr = this.objects.get(i);
                    Double length = Math.sqrt((x - cr.getCenterX()) * (x - cr.getCenterX()) + (y - cr.getCenterY()) * (y - cr.getCenterY()));
                    if (length < cr.getRadius()) {
                        this.current_object = cr;
                        return true;
                    }

                }
                if (drawingMode) {
                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    int Low = 10;
                    int High = 300;
                    int Result = rnd.nextInt(High - Low) + Low;
                    Circle circle = new Circle(x, y, Result, color, this);
                    objects.add(circle);
                } else {
                    ImageBitmap imgBitmap = new ImageBitmap(x, y, bmp);
                    objects.add(imgBitmap);
                    float x1 = imgBitmap.getCenterX();
                    float y1 = imgBitmap.getCenterY();
                    float rad = imgBitmap.getRadius();
                    invalidate((int) (x1 - rad), (int) (y1 - rad), (int) (x1 + rad), (int) (y1 + rad));
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (current_object != null) {
                    this.objects.remove(current_object);
                    float x1 = current_object.getCenterX();
                    float y1 = current_object.getCenterY();
                    this.current_object.setCenterX(x);
                    this.current_object.setCenterY(y);
                    this.objects.add(current_object);
                    float rad = current_object.getRadius();
                    invalidate((int) (x1 - rad), (int) (y1 - rad), (int) (x + rad), (int) (y + rad));
                }
                return true;
            case MotionEvent.ACTION_UP:
                this.current_object = null;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        for (DrawingObject obj : objects) {
            obj.draw(canvas);
        }
        if (!isShare) {
            int width = getWidth();
            int height = getHeight();

            for (int i = 1; i < numColumns; i++) {
                canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
            }

            for (int i = 1; i < numRows; i++) {
                canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
            }
        }
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }
        cellWidth = getWidth() / numColumns;
        cellHeight = getHeight() / numRows;
        invalidate();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            objects.remove(current_object);
            float x = current_object.getCenterX();
            float y = current_object.getCenterY();
            float rad = current_object.getRadius();
            invalidate((int) (x - rad), (int) (y - rad), (int) (x + rad), (int) (y + rad));
            if (current_object instanceof ImageBitmap) {
                int count = 0;
                int key = ((ImageBitmap) current_object).getKey();
                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i) instanceof ImageBitmap) {
                        if (((ImageBitmap) objects.get(i)).getKey() == key) {
                            count++;
                        }
                    }
                }
                if (count == 0) {
                    ImageBitmap.map.remove(key);
                }
            }
            current_object = null;
        }
    }
}