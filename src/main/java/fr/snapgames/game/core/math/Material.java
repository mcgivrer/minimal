package fr.snapgames.game.core.math;

/**
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class Material {
    public static final Material DEFAULT = new Material();

    public String name = "default";
    public double elasticity = 1.0;
    public double roughness = 1.0;
    public double density = 1.0;

    private Material() {

    }

    public Material(String name, double elasticity, double density, double roughness) {
        this.name = name;
        this.density = density;
        this.elasticity = elasticity;
        this.roughness = roughness;
    }
}
