package fr.snapgames.game.core.graphics.plugins;

import fr.snapgames.game.core.entity.TextEntity;
import fr.snapgames.game.core.graphics.Renderer;

import java.awt.*;
import java.util.Optional;

/**
 * TextEntityRenderer plugin to draw TextEntity on screen.
 *
 * @author Frédéric Delorme
 * @since 0.0.2
 **/
public class TextEntityRenderer implements RendererPlugin<TextEntity> {

    @Override
    public Class<?> getObjectClass() {
        return TextEntity.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, TextEntity te) {
        g.setFont(te.getFont());
        te.size.x = g.getFontMetrics().stringWidth(te.getText());
        te.size.y = g.getFontMetrics().getHeight();
        double offX = 0;
        switch (te.getTexAlign()) {
            case RIGHT -> {
                offX = -te.size.x;
            }
            case LEFT -> {
                offX = 0;
            }
            case CENTER -> {
                offX = te.size.x * 0.5;
            }
        }
        drawTextShadow(g, te, offX);
        drawTextOutLine(g, te, offX);
        drawText(g, te, offX);
    }

    private void drawText(Graphics2D g, TextEntity te, double offX) {
        // draw text
        g.setColor(te.color);
        g.drawString(te.getText(), (int) (te.position.x + offX), (int) te.position.y);
    }

    private void drawTextOutLine(Graphics2D g, TextEntity te, double offX) {
        if (Optional.ofNullable(te.borderColor).isPresent()) {
            g.setColor(te.borderColor);
            for (int c = -te.borderWidth; c <= te.borderWidth; c++) {
                for (int d = -te.borderWidth; d <= te.borderWidth; d++) {
                    g.drawString(te.getText(), (int) (te.position.x + offX + d), (int) te.position.y + c);
                }
            }
        }
    }

    private void drawTextShadow(Graphics2D g, TextEntity te, double offX) {
        if (Optional.ofNullable(te.shadowColor).isPresent()) {
            g.setColor(te.shadowColor);
            for (int d = 0; d <= te.shadowWidth; d++) {
                g.drawString(te.getText(), (int) (te.position.x + offX + d), (int) te.position.y + d);
            }
        }
    }

    @Override
    public void drawDebug(Renderer r, Graphics2D g, TextEntity te, double scaleX, double scaleY) {
        double offX = 0;
        switch (te.getTexAlign()) {
            case RIGHT -> {
                offX = -te.size.x;
            }
            case LEFT -> {
                offX = 0;
            }
            case CENTER -> {
                offX = te.size.x * 0.5;
            }
        }
        Font f = g.getFont();
        g.setFont(te.getFont());
        FontMetrics fm = g.getFontMetrics();
        g.setFont(f);
        // draw text area
        g.setColor(Color.ORANGE);
        g.drawRect(
                (int) ((te.position.x + offX) * scaleX),
                (int) ((te.position.y - fm.getAscent()) * scaleY),
                (int) (te.size.x * scaleX), (int) ((fm.getAscent() + fm.getDescent()) * scaleY));
        // draw text position
        g.setColor(Color.WHITE);
        g.drawRect(
                (int) (te.position.x * scaleX), (int) (te.position.y * scaleY),
                1, 1);
        // draw metadata
        g.setColor(Color.ORANGE);
        int il = 0;
        for (String s : te.getDebugInfo()) {
            g.drawString(s, (int) ((te.position.x + te.size.x + offX + 4.0) * scaleX), (int) ((te.position.y * scaleY) + il));
            il += 10;
        }
    }
}
