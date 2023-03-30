package fr.snapgames.game.core.graphics;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.utils.StringUtils;


import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Optional;

/**
 * The Window object support all window operation and manage the main display for the Game engine.
 *
 * @author Frédéric Delorme
 * @since 1.0.1
 */
public class Window {

    private final Game game;
    private JFrame frame;

    public Window(Game game, String title, Dimension dim) {
        this.game = game;
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // define Window content and size.
        frame.setLayout(new GridLayout());

        frame.setContentPane(game);

        frame.setSize(dim);
        frame.setPreferredSize(dim);
        frame.setMinimumSize(dim);
        frame.setMaximumSize(dim);
        frame.setIconImage(ResourceManager.loadImage("/images/sg-logo-image.png"));

        game.setBackground(Color.BLACK);
        frame.setIgnoreRepaint(true);
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setLocationByPlatform(false);
        // define Window content and size.
        frame.setLayout(new GridLayout());
        game.getLayout().layoutContainer(frame);

        frame.setContentPane(game);
        frame.getContentPane().setPreferredSize(dim);

        frame.pack();

        frame.setVisible(true);
        if (frame.getBufferStrategy() == null) {
            frame.createBufferStrategy(game.getConfiguration().getInteger("game.buffer.strategy", 2));
        }
    }

    public Window add(KeyListener kl) {
        frame.addKeyListener(kl);
        return this;
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) frame.getGraphics();
    }


    /**
     * Draw onto the window the buffered created by the {@link Renderer} instance.
     *
     * @param r     the {@link Renderer} providing the image buffer to be rendered on the Window.
     * @param stats a {@link Map} of statistics to be displayed in debug mode in the debug window bottom line
     * @param scale a double value as a resizing factor to render {@link Renderer} buffer on the {@link Window}
     */
    public void drawFrom(Renderer r, Map<String, Object> stats, double scale) {
        if (Optional.ofNullable(frame).isPresent()) {
            if (frame.getBufferStrategy() != null) {
                if (frame.getBufferStrategy().getDrawGraphics() == null) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) frame.getBufferStrategy().getDrawGraphics();
                g2.scale(scale, scale);
                g2.drawImage(r.getBuffer(),
                        0, 0, frame.getWidth(), frame.getHeight(),
                        0, 0, r.getBuffer().getWidth(), r.getBuffer().getHeight(),
                        null);
                g2.scale(1.0 / scale, 1.0 / scale);
                if (game.isDebugGreaterThan(0)) {
                    g2.setColor(new Color(0.3f, 0.0f, 0.0f, 0.8f));
                    g2.fillRect(0, frame.getHeight() - 32, frame.getWidth(), 32);
                    g2.setColor(Color.ORANGE);
                    String displayLine = StringUtils.prepareStatsString(stats);
                    g2.setFont(g2.getFont().deriveFont(15.0f));
                    g2.drawString(displayLine, 16, frame.getHeight() - 16);
                }
                g2.dispose();
                if (frame.getBufferStrategy() != null) {
                    frame.getBufferStrategy().show();
                }
            }
        }
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
    }
}
