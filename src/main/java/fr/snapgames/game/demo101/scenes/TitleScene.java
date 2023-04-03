package fr.snapgames.game.demo101.scenes;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.configuration.Configuration;
import fr.snapgames.game.core.entity.GameEntity;
import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.graphics.Renderer;
import fr.snapgames.game.core.io.InputHandler;
import fr.snapgames.game.core.lang.I18n;
import fr.snapgames.game.core.math.Vector2D;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.scene.AbstractScene;
import fr.snapgames.game.demo101.io.TitleListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * A title scene added in front of any other in the runtime timeline.
 * <p>
 * Just a message to start game with a key press on <kbd>ENTER</kbd> or <kbd>SPACE</kbd>
 * to chain with the {@link DemoScene}.
 *
 * @author Frédéric Delorme
 * @since 0.0.3
 **/
public class TitleScene extends AbstractScene {
    private final TitleListener titleListener;
    private BufferedImage backgroundImg;

    public TitleScene(Game g, String name) {
        super(g, name);
        titleListener = new TitleListener(g);
    }

    @Override
    public void loadResources(Game g) {
        backgroundImg = ResourceManager.loadImage("/images/backgrounds/ruins.png");
    }

    @Override
    public void create(Game g) {
        Configuration config = g.getConfiguration();
        Dimension viewport = (Dimension) config.get(ConfigAttribute.VIEWPORT_SIZE);

        GameEntity bckImage = new GameEntity("background")
                .setImage(backgroundImg)
                .setPriority(0);
        add(bckImage);


        Graphics2D g2d = (Graphics2D) g.getRenderer().getBuffer().getGraphics();
        Font fontTitle = g2d.getFont().deriveFont(Font.BOLD, 16.0f);
        Font fontMessage = g2d.getFont().deriveFont(Font.BOLD, 9.0f);

        String titleString = I18n.get("game.title.main");
        g2d.setFont(fontTitle);
        int titleTextWidth = g2d.getFontMetrics().stringWidth(titleString);

        TextEntity titleTxt = (TextEntity) new TextEntity(("titleTxt"))
                .setText(titleString)
                .setFont(fontTitle)
                .setColor(Color.WHITE)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(4)
                .setBorderColor(Color.DARK_GRAY)
                .setBorderWidth(1)
                .setPriority(2)
                .setPosition(new Vector2D(((viewport.width - titleTextWidth) * 0.5), viewport.height * 0.4));
        add(titleTxt);

        String msgString = I18n.get("game.title.message");
        g2d.setFont(fontMessage);
        int msgTextWidth = g2d.getFontMetrics().stringWidth(msgString);
        TextEntity msgTxt = (TextEntity) new TextEntity(("messageTxt"))
                .setText(msgString)
                .setFont(fontMessage)
                .setColor(Color.WHITE)
                .setShadowColor(Color.BLACK)
                .setShadowWidth(2)
                .setBorderColor(Color.DARK_GRAY)
                .setBorderWidth(1)
                .setPriority(2)
                .setPosition(new Vector2D(((viewport.width - msgTextWidth) * 0.5), viewport.height * 0.80));
        add(msgTxt);

        g.getInputHandler().addListener(titleListener);
    }

    @Override
    public void input(Game g, InputHandler ih) {
        if (ih.getKey(KeyEvent.VK_ENTER) ||
                ih.getKey(KeyEvent.VK_SPACE)) {

        }
    }

    @Override
    public void dispose(Game g) {
        getEntities().clear();
        game.getPhysicEngine().reset();
        game.getRenderer().reset();
        g.getInputHandler().removeListener(titleListener);
    }
}
