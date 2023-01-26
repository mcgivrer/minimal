package fr.snapgames.game.core.config;

import fr.snapgames.game.core.Game;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Configuration oad a properties file and
 * let user gather converted value to
 * <ul>
 *     <li>Integer,</li>
 *     <li>Double,</li>
 *     <li>Boolean,</li>
 *     <li>.</li>
 * </ul>
 * from these properties.
 * The user can also {@link Configuration#save()} values after changes.
 */
public class Configuration {
    private final Properties parameters = new Properties();
    String filePath;

    public Configuration(String file) {
        this.filePath = file;
        try {
            parameters.load(Game.class.getResourceAsStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getInteger(String key, int defaultValue) {
        if (parameters.containsKey(key)) {
            return Integer.parseInt(parameters.getProperty(key));
        }
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue) {
        if (parameters.containsKey(key)) {
            return Double.parseDouble(parameters.getProperty(key));
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (parameters.containsKey(key)) {
            return Boolean.parseBoolean(parameters.getProperty(key));
        }
        return defaultValue;
    }

    public String getString(String key, String defaultValue) {
        if (parameters.containsKey(key)) {
            return parameters.getProperty(key);
        }
        return defaultValue;
    }

    public void parseArguments(String[] args) {
        for (String s : args) {
            String[] p = s.split("=");
            String key = p[0];
            String value = p[1];
            if (parameters.containsKey(s)) {
                parameters.setProperty(key, value);
            }
        }
    }

    public void save() {
        StringWriter fw = new StringWriter();
        try {
            parameters.store(fw, "updated From CommandLine");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
