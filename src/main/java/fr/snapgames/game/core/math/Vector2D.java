package fr.snapgames.game.core.math;

import java.util.List;

/**
 * Internal Class to manipulate simple Vector2D.
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
public class Vector2D {
    public double x, y;

    public Vector2D() {
        x = 0.0f;
        y = 0.0f;
    }

    /**
     * @param x
     * @param y
     */
    public Vector2D(double x, double y) {
        super();
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D substract(Vector2D v1) {
        return new Vector2D(x - v1.x, y - v1.y);
    }

    public Vector2D multiply(double f) {
        return new Vector2D(x * f, y * f);
    }

    public double dot(Vector2D v1) {

        return v1.x * y + v1.y * x;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public double distance(Vector2D v1) {
        return substract(v1).length();
    }

    public Vector2D divide(double f) {
        return new Vector2D(x / f, y / f);
    }

    public Vector2D normalize() {
        return divide(length());
    }

    public Vector2D negate() {
        return new Vector2D(-x, -y);
    }

    public double angle(Vector2D v1) {
        double vDot = this.dot(v1) / (this.length() * v1.length());
        if (vDot < -1.0)
            vDot = -1.0;
        if (vDot > 1.0)
            vDot = 1.0;
        return Math.acos(vDot);

    }

    public Vector2D addAll(List<Vector2D> forces) {
        Vector2D sum = new Vector2D();
        for (Vector2D f : forces) {
            sum = sum.add(f);
        }
        return sum;
    }

    public String toString() {
        return String.format("{x:%04.2f,y:%04.2f}", x, y);
    }

    public Vector2D maximize(double maxAccel) {
        maximize(maxAccel, maxAccel);
        return this;
    }

    public Vector2D maximize(double maxAccelX, double maxAccelY) {
        if (Math.abs(x) > maxAccelX) {
            x = Math.signum(x) * maxAccelX;
        }
        if (Math.abs(y) > maxAccelY) {
            y = Math.signum(y) * maxAccelY;
        }
        return this;
    }

    public Vector2D thresholdToZero(double minValue) {
        if (Math.abs(x) < minValue) {
            x = 0.0;
        }
        if (Math.abs(y) < minValue) {
            y = 0.0;
        }
        return this;
    }
}
