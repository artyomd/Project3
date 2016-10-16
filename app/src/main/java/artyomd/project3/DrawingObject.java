package artyomd.project3;

import android.graphics.Canvas;

/**
 * Created by artyomd on 5/3/16.
 */
public abstract class DrawingObject {
    protected float x;
    protected float y;
    protected float radius;

    public DrawingObject() {
        x = 0;
        y = 0;
        radius = 0;
    }

    public float getRadius() {
        return radius;
    }

    abstract public void draw(Canvas canvas);

    abstract public float getCenterX();

    abstract public float getCenterY();

    abstract public void setCenterX(float x);

    abstract public void setCenterY(float y);
}
