package fr.snapgames.game.core.scene.transition;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.scene.Scene;

import java.util.Map;

public class AbstractTransition implements Transition {

    private final String name;
    private Scene src;
    private Scene dst;

    private float fade = 0.0f;

    /**
     * Duration for this transition
     */
    protected long duration;

    protected long internalTransitionTime;


    public AbstractTransition(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void start(Scene src, Scene dst) {
        this.src = src;
        this.dst = dst;
        this.internalTransitionTime = 0;
    }

    @Override
    public void stop() {

    }

    @Override
    public void update(Game g, double dt) {
        if (this.internalTransitionTime < this.duration) {
            this.internalTransitionTime += dt;
            src.update(g, dt);
            dst.update(g, dt);
            fade = (duration / internalTransitionTime);
        }
    }

    @Override
    public void draw(Game g, Renderer r, Map<String, Object> stats) {
        r.draw(0, src, stats);
        r.draw(1, dst, stats);

    }
}
