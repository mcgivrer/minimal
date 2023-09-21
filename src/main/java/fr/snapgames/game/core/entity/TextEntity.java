package fr.snapgames.game.core.entity;

import fr.snapgames.game.core.graphics.TextAlign;

import java.awt.*;
import java.util.Collection;

/**
 * A Specialization of GameEntity to support Text
 *
 * @author Frédéric Delorme
 * @since 0.0.1
 */
public class TextEntity extends GameEntity {

    private String text;
    private Font font;
    private TextAlign textAlign = TextAlign.LEFT;


    /**
     * Create a new TextEntity with a name.
     *
     * @param name Name of the new entity.
     */
    public TextEntity(String name) {
        super(name);
    }

    public TextEntity setText(String text) {
        this.text = text;
        return this;
    }

    public TextEntity setFont(Font font) {
        this.font = font;
        return this;
    }

    public Font getFont() {
        return this.font;
    }


    @Override
    public Collection<String> getDebugInfo() {
        Collection<String> l = super.getDebugInfo();
        l.add(String.format("txt:%s", text));
        return l;
    }

    public TextEntity setTextAlign(TextAlign textAlign) {
        this.textAlign = textAlign;
        return this;
    }

    public TextAlign getTexAlign() {
        return this.textAlign;
    }

    public String getText() {
        return this.text;
    }
}
