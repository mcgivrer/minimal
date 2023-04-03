package fr.snapgames.game.core.graphics;

import java.awt.image.BufferedImage;

/**
 * This {@link Animation} object set the Frames as a sprite animation.
 * <p>
 * It contains 2 main buffers; image and time.
 * The {@link Animation} can be played in an infinite loop, and can end at a
 * defined point.
 * This Animation object can be attributed to an {@link Entity}. the
 * corresponding frame
 * will be used in place of the {@link Entity#image}
 * </p>
 *
 * @author Frédéric Delorme
 * @since 1.0.1
 */
public class Animation {
    BufferedImage[] frames;
    int index = 0;
    boolean loop = true;
    boolean end = false;

    double speed = 1.0;

    long animationTime = 0;
    private long[] frameTimes;

    public Animation(BufferedImage[] f, long[] frameTimes) {
        this.frames = f;
        this.frameTimes = frameTimes;

    }

    public Animation setLoop(boolean b) {
        this.loop = b;
        return this;
    }

    public Animation setSpeed(double s) {
        this.speed = s;
        return this;
    }

    public BufferedImage getFrame() {
        if (index < frames.length && frames[index] != null) {
            return frames[index];
        } else {
            return null;
        }
    }

    public void update(long elapsed) {
        this.animationTime += (elapsed * speed);
        if (this.animationTime > this.frameTimes[this.index]) {
            this.animationTime = 0;
            if (this.index + 1 < this.frames.length) {
                this.index++;
            } else {
                if (this.loop) {
                    this.index = 0;
                } else {
                    this.end = true;
                }
            }
        }
    }

    public Animation reset() {
        index = 0;
        return this;
    }
}