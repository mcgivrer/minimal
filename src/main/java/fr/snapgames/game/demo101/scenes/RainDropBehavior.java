package fr.snapgames.game.demo101.scenes;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.behaviors.Behavior;
import fr.snapgames.game.core.behaviors.ParticleBehavior;
import fr.snapgames.game.core.entity.EntityType;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.ParticlesEntity;
import fr.snapgames.game.core.math.Material;
import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;

import java.awt.*;

public class RainDropBehavior implements ParticleBehavior<ParticlesEntity> {
    private final World world;
    private final Color color;
    int nbParticles = 0;

    int counter = 0;

    public RainDropBehavior(World world, Color dropColor, int nbParticles) {
        this.world = world;
        this.color = dropColor;
        this.nbParticles = nbParticles;
    }


}
