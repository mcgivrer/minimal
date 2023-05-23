package fr.snapgames.game.core.scene;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.PhysicType;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.math.World;

import java.awt.*;

/**
 * An Default Scene to make Game not crash without dame.scene.list and or game.scene.default not defined
 * in the Properties configuration file.
 */
public class DefaultScene extends AbstractScene {
    public DefaultScene(Game g, String name) {
        super(g, name);
    }

    @Override
    public void loadResources(Game g) {

    }

    @Override
    public void create(Game g) {
        World world = g.getPhysicEngine().getWorld();

        Graphics2D g2d = (Graphics2D) g.getRenderer().getBuffer().getGraphics();
        Font fontTitle = g2d.getFont().deriveFont(Font.BOLD, 16.0f);
        Font fontMessage = g2d.getFont().deriveFont(Font.BOLD, 9.0f);

        String titleString = I18n.get("game.title.main");
        g2d.setFont(fontTitle);
        int titleTextWidth = g2d.getFontMetrics().stringWidth(titleString);

        TextEntity titleTxt = (TextEntity) new TextEntity(("titleTxt"))
                .setText(titleString)
                .setFont(fontTitle)
                .setPhysicType(PhysicType.STATIC)
                .setColor(Color.WHITE)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(4)
                .setBorderColor(Color.DARK_GRAY)
                .setColliderFlag(false)
                .setBorderWidth(1)
                .setPriority(2)
                .setPosition(new Vector2D(((world.getPlayArea().width - titleTextWidth) * 0.5), world.getPlayArea().height * 0.4));
        add(titleTxt);
    }

    @Override
    public void dispose(Game g) {

    }
}
