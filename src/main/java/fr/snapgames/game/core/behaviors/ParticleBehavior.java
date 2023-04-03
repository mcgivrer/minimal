package fr.snapgames.game.core.behaviors;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.ParticlesEntity;

public interface ParticleBehavior<T> extends Behavior<T> {
    default void create(Game g, T parent) {

    }
}
