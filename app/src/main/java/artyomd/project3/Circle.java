package artyomd.project3;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by artyomd on 5/3/16.
 */
public class Circle extends DrawingObject {
    private Paint paint;
    private DrawingView dView;

    public Circle(float x1, float y1, float rad, int color, DrawingView view) {
        super();
        this.x = x1;
        this.y = y1;
        this.dView = view;
        this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        ValueAnimator animator = ValueAnimator.ofFloat(0, rad);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float) animation.getAnimatedValue();
                dView.invalidate((int) (x - radius), (int) (y - radius), (int) (x + radius), (int) (y + radius));
            }
        });
        animator.start();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, paint);
    }

    @Override
    public float getCenterX() {
        return x;
    }

    @Override
    public float getCenterY() {
        return y;
    }

    @Override
    public void setCenterX(float x) {
        this.x = x;
    }

    @Override
    public void setCenterY(float y) {
        this.y = y;
    }
}
