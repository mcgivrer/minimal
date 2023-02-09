package fr.snapgames.game.core.entity;

/**
 * Define the Type of Light.
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 */
public enum LightType {
    /**
     * a local spotlight
     */
    SPOT,
    /**
     * a Cone light with a target.
     */
    CONE,
    /**
     * a global ambient
     */
    AMBIENT
}
