package fr.snapgames.game.core.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@link Configuration} component is used to manage a list of {@link IConfigAttribute} and maintain their value into
 * an internal map. This map is fed-up with arguments from Command line through the {@link Configuration#parseArgs(String[])}.
 * To start, fist initialize the Configuration component with the all enum values you built up upon the ICOnfigAttribute.
 *
 * <pre>
 * public enum ConfigAttribute implements IConfigAttribute{
 *     MY_FIRST_VALUE("myFirstValue","this is the first value",12.0,v->Double::valueOf),
 *     MY_SECOND_VALUE("mySecondValue","this is the Second value","test",v->v);
 *
 *     // implement getters and setters and the internal attributes of this enum.
 *     // ...
 * }
 * </pre>
 * And then in your main application program:
 *
 * <pre>
 * public class MyApp{
 *   Configuration config;
 *   MyApp(String[] args){
 *     config = new Configuration(ConfigAttribute.values());
 *     //...
 *     int firstValue = (int)config.get(ConfigAttribute.MY_FIRST_VALUE);
 *     String secondValue = (String)config.get(ConfigAttribute.MY_SECOND_VALUE);
 *   }
 *
 *   public static void main(String[] args){
 *     MyApp app = new MyApp(args);
 *   }
 * }
 * </pre>
 *
 * @author Frédéric Delorme
 **/
public class Configuration {
    // internal Logger
    private static final Logger logger = Logger.getLogger(Configuration.class.getName());

    // Configuration filename.
    private String configFile;


    IConfigAttribute[] attributes;
    private Map<IConfigAttribute, Object> configurationValues = new ConcurrentHashMap<>();

    /**
     * Create the Configuration instance with the new IConfigAttribute array.
     *
     * @param attributes a new ConfigAttribute array implementation (a new enum).
     */
    public Configuration(IConfigAttribute[] attributes) {
        setAttributes(attributes);
        // initialize all default values.
        Arrays.stream(attributes).forEach(ca -> {
            configurationValues.put(ca, ca.getDefaultValue());
        });
    }

    /**
     * Parse all the values provided by the command line interactive.
     *
     * @param args a String array of arguments from the command line (CLI).
     * @return 0 if all values have been identified and interpreted, else -1.
     */
    public int parseArgs(String[] args) {
        boolean displayHelpMessage = false;

        if (args.length > 0) {
            for (String arg : args) {
                String[] kv = arg.split("=");
                if (!isArgumentFound(kv)) {
                    displayHelpMessage(kv[0], kv[1]);
                    return -1;
                }
            }
            if (displayHelpMessage) {
                displayHelpMessage();
            }
        }
        return 0;
    }

    /**
     * return true of the kv[0] corresponding to an attribute key value is identified.
     *
     * @param kv a String array containing at 0 an attribute name and at 1 its value.
     * @return
     */
    public boolean isArgumentFound(String[] kv) {
        boolean found = false;
        for (IConfigAttribute ca : attributes) {
            if (ca.getAttrName().equals(kv[0]) || ca.getConfigKey().equals(kv[0])) {
                configurationValues.put(ca, ca.getAttrParser().apply(kv[1]));
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Display an error message if argument unknownAttributeName is unknown.
     *
     * @param unknownAttributeName the unknown argument.
     * @param attributeValue       the value for this unknown argument.
     */
    public void displayHelpMessage(String unknownAttributeName, String attributeValue) {
        logger.log(Level.INFO, "The argument {0}={1} is unknown", new Object[]{unknownAttributeName, attributeValue});
        displayHelpMessage();
    }

    /**
     * Display CLI argument help message based on values from the {@link ConfigAttribute} enum.
     */
    public void displayHelpMessage() {
        logger.log(Level.INFO, "Here is the list of possible arguments:");
        Arrays.stream(attributes).forEach(ca -> {
            logger.log(Level.INFO, "- {0} : {1} (default value is {2})", new Object[]{
                    ca.getAttrName(),
                    ca.getAttrDescription(),
                    ca.getDefaultValue().toString()});
        });
    }

    /**
     * Apply an array of {@link IConfigAttribute} as values for the {@link Configuration}.
     *
     * @param values an IConfigAttribute array.
     */
    public void setAttributes(IConfigAttribute[] values) {
        attributes = values;
    }

    /**
     * Retrieve the current value for the requested {@link IConfigAttribute}.
     *
     * @param ca the IConfigAttribute key to retrieve the value for.
     * @return an Object corresponding the retrieved value.
     */
    public Object get(IConfigAttribute ca) {
        return configurationValues.get(ca);
    }

    /**
     * Set configuration properties file to be read from.
     *
     * @param cfgFile the path to the filename of the configuration file.
     * @return the updated Configuration object.
     */
    public Configuration setConfigurationFile(String cfgFile) {
        this.configFile = cfgFile;
        return this;
    }

    /**
     * Parse all the properties file to load values into configurationValues.
     *
     * @return 0 if ok, else -1.
     */
    public int parseConfigFile() {
        int status = 0;
        Properties props = new Properties();
        if (Optional.ofNullable(configFile).isPresent()) {
            try {
                props.load(Configuration.class.getResourceAsStream(this.configFile));
                for (Map.Entry<Object, Object> prop : props.entrySet()) {
                    String[] kv = new String[]{(String) prop.getKey(), (String) prop.getValue()};
                    if (!isArgumentFound(kv)) {
                        logger.log(Level.SEVERE, "file={0} : Unknown property {1} with value {2}", new Object[]{
                                this.configFile,
                                prop.getKey(),
                                prop.getValue()});
                        status = -1;
                    } else {
                        logger.log(Level.INFO, "file={0} : set {1} to {2}", new Object[]{
                                this.configFile,
                                prop.getKey().toString(),
                                prop.getValue().toString()});
                    }
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, "file={0} : Unable to find and parse the configuration file : {1}", new Object[]{
                        this.configFile,
                        e.getMessage()});
            }
        } else {
            status = -1;
        }
        return status;
    }

}
