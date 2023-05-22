package fr.snapgames.game.core.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Optional;

import javax.swing.JFrame;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.configuration.ConfigAttribute;
import fr.snapgames.game.core.resources.ResourceManager;
import fr.snapgames.game.core.utils.StringUtils;

/**
 * The Window object support all window operation and manage the main display
 * for the Game engine.
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
        frame.setIconImage(ResourceManager.getImage("/images/sg-logo-image.png"));

        game.setBackground(Color.BLACK);
        frame.setIgnoreRepaint(true);
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);

        // on which Display the Window must appear ?
        moveToScreen("Display0");

        // define Window content and size.
        frame.setLayout(new GridLayout());
        game.getLayout().layoutContainer(frame);

        frame.setContentPane(game);
        frame.getContentPane().setPreferredSize(dim);

        frame.pack();

        frame.setVisible(true);
        if (frame.getBufferStrategy() == null) {
            frame.createBufferStrategy((int) game.getConfiguration().get(ConfigAttribute.WINDOW_BUFFER_NUMBER));
        }
    }

    /**
     * Move this Window to a specific screen ({@link GraphicsDevice}) named screenId (see {@link GraphicsDevice#getIDstring()}.
     *
     * @param screenId
     */
    public void moveToScreen(String screenId) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        int n = screens.length;
        for (int i = 0; i < n; i++) {
            if (screens[i].getIDstring().contains(screenId)) {
                JFrame dummy = new JFrame(screens[i].getDefaultConfiguration());
                frame.setLocationRelativeTo(dummy);
                dummy.dispose();
            }
        }
    }

    /**
     * Move the {@link Window} at position (x,y) where x,y can be pixels or ratio percentage of the targeted screen.
     *
     * @param screen the unique id of the targeted screen.
     * @param x      the horizontal position on that screen (in pixel or as a ratio  of the screen space)
     * @param y      the vertical position on that screen (in pixel or as a ratio of the screen space)
     */
    public void setLocation(int screen, double x, double y) {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] d = g.getScreenDevices();

        if (screen >= d.length) {
            screen = d.length - 1;
        }

        Rectangle bounds = d[screen].getDefaultConfiguration().getBounds();

        // Is double?
        if (x == Math.floor(x) && !Double.isInfinite(x)) {
            x *= bounds.x;  // Decimal -> percentage
        }
        if (y == Math.floor(y) && !Double.isInfinite(y)) {
            y *= bounds.y;  // Decimal -> percentage
        }

        x = bounds.x + x;
        y = frame.getY() + y;

        if (x > bounds.x) x = bounds.x;
        if (y > bounds.y) y = bounds.y;

        // If double we do want to floor the value either way
        frame.setLocation((int) x, (int) y);
    }

    /**
     * Move the {@link Window} at position (x,y) where x,y can be pixels or ratio percentage of the targeted screen.
     *
     * @param screenId the unique String as id of the targeted screen.
     * @param x        the horizontal position on that screen (in pixel or as a ratio  of the screen space)
     * @param y        the vertical position on that screen (in pixel or as a ratio of the screen space)
     */
    public void setLocation(String screenId, double x, double y) {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] d = g.getScreenDevices();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getIDstring().contains(screenId)) {
                setLocation(i, x, y);
            }
        }
    }

    /**
     * Add a KeyListener to this {@link Window}.
     *
     * @param kl the KeyListener instance to be added.
     * @return the updated Window.
     */
    public Window add(KeyListener kl) {
        frame.addKeyListener(kl);
        return this;
    }

    /**
     * Retrieve the {@link Graphics2D} API for this {@link Window}.
     *
     * @return
     */
    public Graphics2D getGraphics() {
        return (Graphics2D) frame.getGraphics();
    }

    /**
     * Draw onto the window the buffered created by the {@link Renderer} instance.
     *
     * @param r     the {@link Renderer} providing the image buffer to be rendered
     *              on the Window.
     * @param stats a {@link Map} of statistics to be displayed in debug mode in the
     *              debug window bottom line
     * @param scale a double value as a resizing factor to render {@link Renderer}
     *              buffer on the {@link Window}
     */
    public void drawFrom(Renderer r, Map<String, Object> stats, double scale) {
        if (Optional.ofNullable(frame).isPresent()
                && frame.getBufferStrategy() != null) {
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
            // draw Debug Information
            r.drawDebugToWindow(g2, this);
            // draw information bottom line
            if (game.isDebugGreaterThan(0)) {
                g2.setColor(new Color(0.3f, 0.0f, 0.0f, 0.8f));
                g2.fillRect(0, frame.getHeight() - 32, frame.getWidth(), 32);
                g2.setColor(Color.ORANGE);
                String displayLine = StringUtils.prepareStatsString(stats, "[ ", " ]", " | ");
                g2.setFont(g2.getFont().deriveFont(15.0f));
                g2.drawString(displayLine, 16, frame.getHeight() - 16);
            }

            g2.dispose();
            if (frame.getBufferStrategy() != null) {
                frame.getBufferStrategy().show();
            }
        }
    }

    /**
     * Close this {@link Window} instance.
     */
    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
    }

    public JFrame getFrame() {
        return frame;
    }
}
