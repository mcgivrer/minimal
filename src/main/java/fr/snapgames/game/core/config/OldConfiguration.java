package fr.snapgames.game.core.config;

import fr.snapgames.game.core.Game;
import fr.snapgames.game.core.math.Vector2D;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * {@link OldConfiguration} loads a properties file and
 * let user gather converted value to
 * <ul>
 *     <li>Integer,</li>
 *     <li>Double,</li>
 *     <li>Boolean,</li>
 *     <li>String.</li>
 *     <li>{@link Vector2D} a new mathematical 2 dimensions vector used in the {@link fr.snapgames.game.core.math.PhysicEngine} (mainly)</li>
 *     <li>{@link fr.snapgames.game.core.math.Material} the physic engine required material definition to make math processing.</li>
 * </ul>
 * from these properties.
 * The user can also {@link OldConfiguration#save()} values after changes.
 *
 * @author Frédéric Delorme
 * @since 0.0.1
 * @deprecated preferred use of {@link fr.snapgames.game.core.configuration.Configuration},
 * {@link fr.snapgames.game.core.configuration.IConfigAttribute} and
 * {@link fr.snapgames.game.core.configuration.ConfigAttribute}
 */
public class OldConfiguration {
    private final Properties parameters = new Properties();
    String filePath;

    /**
     * Initialize the {@link OldConfiguration} with a file properties.
     *
     * @param file the path to the properties file to load into OldConfiguration.
     */
    public OldConfiguration(String file) {
        this.filePath = file;
        try {
            parameters.load(Game.class.getResourceAsStream(filePath));
            System.out.printf("OldConfiguration:The configuration file %s has been loaded%n", file);
        } catch (IOException e) {
            System.err.printf("OldConfiguration:The configuration file %s can not been loaded: %s%n", file, e.getMessage());
        }
    }

    /**
     * Retrieve a value as Integer from {@link OldConfiguration}.
     *
     * @param key          name of the configuration key to be loaded
     * @param defaultValue if no value exists in {@link OldConfiguration}, instead use the defaultValue.
     * @return the corresponding int value
     */
    public int getInteger(String key, int defaultValue) {
        if (parameters.containsKey(key)) {
            return Integer.parseInt(parameters.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * Retrieve a value as Double from {@link OldConfiguration}.
     *
     * @param key          name of the configuration key to be loaded
     * @param defaultValue if no value exists in {@link OldConfiguration}, instead use the defaultValue.
     * @return the corresponding double value
     */
    public double getDouble(String key, double defaultValue) {
        if (parameters.containsKey(key)) {
            return Double.parseDouble(parameters.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * Retrieve a value as Boolean from {@link OldConfiguration}.
     *
     * @param key          name of the configuration key to be loaded
     * @param defaultValue if no value exists in {@link OldConfiguration}, instead use the defaultValue.
     * @return the corresponding boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        if (parameters.containsKey(key)) {
            return Boolean.parseBoolean(parameters.getProperty(key));
        }
        return defaultValue;
    }

    /**
     * Retrieve a value as String from {@link OldConfiguration}.
     *
     * @param key          name of the configuration key to be loaded
     * @param defaultValue if no value exists in {@link OldConfiguration}, instead use the defaultValue.
     * @return the corresponding String value
     */
    public String getString(String key, String defaultValue) {
        if (parameters.containsKey(key)) {
            return parameters.getProperty(key);
        }
        return defaultValue;
    }


    /**
     * Retrieve a value as Dimension from {@link OldConfiguration} from a string format "dim([width],[height])".
     *
     * @param key          name of the configuration key to be loaded
     * @param defaultValue if no value exists in {@link OldConfiguration}, instead use the defaultValue.
     * @return the corresponding Dimension value
     */
    public Dimension getDimension(String key, Dimension defaultValue) {
        String param = parameters.getProperty(key, "");
        if (!param.equals("") && param.startsWith("dim(") && param.endsWith(")")) {
            String[] k = param.substring("dim(".length(), param.length() - 1).split(",");
            int width = Integer.valueOf(k[0]);
            int height = Integer.valueOf(k[0]);
            return new Dimension(width, height);
        } else {
            System.err.printf("OldConfiguration: Dimension value not found for %s, use %s as default value.%n", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Retrieve a value as Vector2D from {@link OldConfiguration} from a string format "dim([width],[height])".
     *
     * @param key          name of the configuration key to be loaded
     * @param defaultValue if no value exists in {@link OldConfiguration}, instead use the defaultValue.
     * @return the corresponding Vector2D value
     */
    public Vector2D getVector2D(String key, Vector2D defaultValue) {
        String param = parameters.getProperty(key, "");
        if (!param.equals("") && param.startsWith("v2d(") && param.endsWith(")")) {
            String[] k = param.substring("v2d(".length(), param.length() - 1).split(",");
            double x = Double.valueOf(k[0]);
            double y = Double.valueOf(k[1]);
            return new Vector2D(x, y);
        } else {
            System.err.printf("OldConfiguration: Dimension value not found for %s, use %s as default value.%n", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Parse common CLI arguments to retrieve values from.
     *
     * @param args a String array of value with "key=value" format.
     */
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

    /**
     * Save changes to the previously loaded properties file.
     */
    public void save() {
        try {
            String rootPath = this.getClass().getResource("/").getPath();
            OutputStream output = new FileOutputStream(rootPath + filePath);
            parameters.store(output, "updated From CommandLine");
            System.out.printf("OldConfiguration:OldConfiguration saved into the properties file: %s%n", filePath);
            System.out.printf("OldConfiguration:Content: %s%n", parameters);
        } catch (IOException e) {
            System.err.printf("OldConfiguration:Unable to save configuration into properties file: %s%n", e.getMessage());
        }
    }

}
