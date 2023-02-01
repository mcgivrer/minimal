package fr.snapgames.game.core.lang;

import java.util.ResourceBundle;

/**
 * Internationalization class to support multi-languages.
 */
public class I18n {
    private static final ResourceBundle messages = ResourceBundle.getBundle("i18n.messages");

    public static String get(String key) {
        return messages.getString(key);
    }

    public static String get(String key, Object... args) {
        return String.format(messages.getString(key), args);
    }
}
