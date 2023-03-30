package fr.snapgames.game.core.math;

/**
 * The {@link Material} class host all Materials characteristics used by {@link PhysicEngine} and {@link fr.snapgames.game.core.entity.GameEntity}
 * and {@link fr.snapgames.game.core.entity.Influencer} to compute
 * Newton's laws physic on the {@link fr.snapgames.game.core.entity.GameEntity}.
 *
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
    public static final Material WATER = new Material("water", 0.1, 0.95, 0.40);

    public String name;
    public double elasticity;
    public double roughness;
    public double density;

    /**
     * Create a new Material with default values.
     */
    private Material() {
        this.name = "default";
        this.density = 1.0;
        this.elasticity = 1.0;
        this.roughness = 1.0;
    }

    /**
     * Create a new {@link Material} with its name and ots physic characteristics of density, elasticity and roughness
     *
     * @param name       name of this new {@link Material}
     * @param elasticity elasticity used to compute bouncing behavior
     * @param density    the density of this {@link Material}, used to compute applied forces
     * @param roughness  the roughness used to compute friction on collision, or in {@link fr.snapgames.game.core.entity.Influencer} intersection.
     */
    public Material(String name, double elasticity, double density, double roughness) {
        this.name = name;
        this.density = density;
        this.elasticity = elasticity;
        this.roughness = roughness;
    }

    /**
     * Merge 2 materials into one new {@link Material} having the lowest characteristics from the 2.
     *
     * @param material the {@link Material} to merge with
     * @return a new {@link Material} resulting from the merge of that 2.
     */
    public Material merge(Material material) {
        return new Material(this.name + ">" + material.name,
                this.elasticity * material.elasticity,
                this.density * material.density,
                this.roughness * material.roughness
        );

    }

    /**
     * Create a copy of this {@link Material} into a new {@link Material} instance.
     *
     * @return a new {@link Material} instance corresponding to a copy of the characteristics
     */
    public Material copy() {
        return new Material(name, elasticity, density, roughness);
    }


    @Override
    public String toString() {
        return String.format("%s:%s[%04.2f,%04.2f,%04.2f]", getClass().getSimpleName(), this.name, this.elasticity, this.density, this.roughness);
    }
}
