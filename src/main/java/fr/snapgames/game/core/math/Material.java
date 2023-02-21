package fr.snapgames.game.core.math;

/**
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class Material {
    public static final Material DEFAULT = new Material();
    public static final Material RUBBER = new Material("rubber", 0.68, 0.7, 0.63);
    public static final Material SUPER_BALL = new Material("superball", 0.98, 0.7, 0.23);
    public static final Material WOOD = new Material("wood", 0.20, 0.65, 0.50);
    public static final Material STEEL = new Material("steel", 0.10, 1.2, 0.12);
    public static final Material AIR = new Material("air", 0.0, 0.05, 0.99);
    public static final Material WATER = new Material("water", 0.0, 0.90, 0.80);

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

    public Material merge(Material material) {
        return new Material(this.name + ">" + material.name,
                this.elasticity < material.elasticity ? this.elasticity : material.elasticity,
                this.density < material.density ? this.density : this.density,
                this.roughness < material.roughness ? this.density : this.density
        );

    }

    public Material copy() {
        return new Material(name, elasticity, density, roughness);
    }


    public String toString() {
        return String.format("%s:%s[%f,%f,%f]", getClass().getSimpleName(), this.name, this.elasticity, this.density, this.roughness);
    }
}
