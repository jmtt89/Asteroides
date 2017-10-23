package asteroides.example.org.asteroides.models.base;

import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by jmtt_ on 10/22/2017.
 */

public class BoundingCircle {
    private float centerX, centerY;
    private float radius;

    public BoundingCircle(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void moveCircle(float x, float y){
        this.centerX = x;
        this.centerY = y;
    }

    public boolean intersect(BoundingCircle circle){
        double distance = Math.hypot(centerX - circle.getCenterX(), centerX - circle.getCenterY());
        return distance < (radius + circle.getRadius());
    }
}
