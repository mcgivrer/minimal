# Enum as Configuration

One of the first things you have to provide, before any game mechanic, it's a way to set configuration values.

There is two possibilities:

- the first one consists in using a simple Configuration class loading a properties file, with
  the possibilities to save it with new values.
- the second one would be more structured by using a well-known java structure, the enumeration, to define configuration
  keys, values and description with help support.

## Introduction

In the ideal world, we need to understand what would be the age for our configuration. we need to define some value to
be set by
configuration (via a file), or fall back to a default value. the default model must support providing a description. We
will also need to override the configuration value at execution time by providing a different value through the CLI.

- name in configuration file (a key configuration name),
- a name for command line interface ( a simple name),
- a description,
- a default value.

And as values coming from file or CLI, we need to get a converter from text to the real configuration attribute value we
want to provide.

- a converter to translate a String value to a Java Type (native or custom).

So our implementation will be a little more complex to allow an easy way to manager enumeration values:

![the configuration attributes implementation](https://www.plantuml.com/plantuml/png/RP1VIyGm4CJVyodYcwAVG4Jkj5KE_rp1qzSZrqxRbMHRaWqYudStfGebtkl-PfQPMGKnNgApwaHwEEZH6cBLKmDjH3mTeY0eOe5lJGszkHsufMxznxwKFjSTP3ey6uVJiykNXBZxlS_o1tpcO38K2BMdKMZW71TeJRJoPcn4Ojl3EsfPM2lZ0tmYTv7hwS7LRB_Gi_Gw5wfl5VhXGsVv5otdrZbta7veWW97zm-I6oqSMCAnOTPLjTzjTe-bqaLvytg_VzeMPSgn0ZTfyXy0)

Ok, first things first let's start with a simple configuration class.

## Configuration from properties

### the properties file as input

To start simple, create a properties file:

```properties
game.debug.level=0
game.window.fullscreen.mode=false
game.window.title=My Main Window Title
game.window.size=dim(640,400)
game.physic.acceleration.max=20.0
game.physic.world.material=mat(test,1.0,0.2,0.8)
game.physic.world.gravity=v(0,-0.981)
game.physic.world.play.area=r2d(1000.0,1000.0)
```

So here are defined multiple keys with specific
values:  `integer`, `boolean`, `String`, `Dimension`, `Double`, `Material`, `Vector2D`,
and `Rectangle2D`.

In our first implementation, the value will be converted at access time with a dedicated getter.

### the Configuration class

This first implementation, where values a converted at getting time may fit at start to define some minimum
configuration keys. but each time you will access it with getter, the conversion will take some nanoseconds.

Anyway, let's start with this one:

![The first Configuration implementation](http://www.plantuml.com/plantuml/png/VP6nJiCm48RtF8NPOa4Nnam5kh1L87L13DVcubfSpf7lN0aXtfrGxAGW1G_sVxxlky5UYqBKr5DrfKViqGm4dgi3WOXv2DnvboAe3_nH6RCodIVSWXwRPusUtVaETCpxf2ZDDeO1etgKQZMkiNtzUqhzPu1jJi6tC-nG7rdhDtpFm8rfiOD4kWDAs7XM-xqL-3u4Gk1bOVy3s1AAk0bfJxZijUU-pUTa-HvPVE1bSKBi78k-muE6UBjPjWjXfrJSfx46TeJ_wHUGlIpc1nuW2jv5btfabHgYappgvJL_neW9dB3EvMUrHcvJJry0)

so the corresponding code could be:

```java
public class Configuration {
    private final Properties parameters = new Properties();
    String filePath;

    public Configuration(String file) {
        this.filePath = file;
        try {
            parameters.load(Game.class.getResourceAsStream(filePath));
            System.out.printf("Configuration:The configuration file %s has been loaded%n", file);
        } catch (IOException e) {
            System.err.printf("Configuration:The configuration file %s can not been loaded: %s%n", file, e.getMessage());
        }
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

    //...
    public void save() {
        try {
            String rootPath = this.getClass().getResource("/").getPath();
            OutputStream output = new FileOutputStream(rootPath + filePath);
            parameters.store(output, "updated From CommandLine");
            System.out.printf("Configuration:Configuration saved into the properties file: %s%n", filePath);
            System.out.printf("Configuration:Content: %s%n", parameters);
        } catch (IOException e) {
            System.err.printf("Configuration:Unable to save configuration into properties file: %s%n", e.getMessage());
        }
    }
    //...
}
```

In this first part, we found :

- `Configuration(String)` the constructor to start reading the properties file,
- `parseArguments(String[])` to read arguments from the Command line,
- `save()` to save loaded or parsed values to the properties file.

In the second part, we detail only two getters, a basic one with boolean and a more intelligent one to convert a string
value to a Rectangle2D object:

```java
public class Configuration {
    //...
    public int getInteger(String key, int defaultValue) {
        if (parameters.containsKey(key)) {
            return Integer.parseInt(parameters.getProperty(key));
        }
        return defaultValue;
    }

    //...
    public Rectangle2D getRectangle2D(String key, Rectangle2D defaultValue) {
        String param = parameters.getProperty(key, "");
        if (!param.equals("") && param.startsWith("r2d(") && param.endsWith(")")) {
            String[] k = param.substring("r2d(".length(), param.length() - 1).split(",");
            double width = Double.valueOf(k[0]);
            double height = Double.valueOf(k[0]);
            return new Rectangle2D.Double(0, 0, width, height);
        } else {
            System.err.printf("Configuration: Rectangle2D value not found for %s, use %s as default value.%n", key, defaultValue);
            return defaultValue;
        }
    }
    //...
}
```

- the first getter, `getInteger()` is reading the property value as String and convert it to an `int` type value, for
  another type like boolean, double, float, you can use the same kind of implementation, relying on the `parse[TYPE]()`
  method.
- the second one, `getRectangle2D`, convert a coded string value `r2d([WIDTH],[HEIGHT]` into an instance
  of `Rectangle2D` class.

## Configuration and ConfigAttribute

For this second solution, we will go further into structuration of the configuration keys and their values.
We will use a specific interface to define the way to define and implement an enumeration that will contain every
configuration value required.

let's start with the interface `IConfigAttribute`, the `ConfigAttribute` enumeration, and then the main `Configuration`
class.

### The attribute interface

the `IConfigAttribute` will be our interface to enumeration to define configuration attribute to be provided through a
file of command line:

```java
public interface IConfigAttribute {

    String getAttrName();

    Function<String, Object> getAttrParser();

    Object getDefaultValue();

    String getAttrDescription();

    String getConfigKey();
}
```

So we can first provide an implementation of the enumeration :

```java
public enum ConfigAttribute implements IConfigAttribute {
    APP_TITLE(
            "appTitle",
            "app.main.title",
            "Set the name of the application to be displayed in log and UI",
            "GDemoApp",
            v -> v),
    DEBUG_MODE(
            "debugMode",
            "app.debug.mode",
            "Setting the debug mode (0 to 5)",
            0,
            v -> Integer.valueOf(v)),
    //... other values will enhance the enum.
    ;

    // internal attributes of the enum entry.
    private final String attrName;
    private final String attrDescription;
    private final Object attrDefaultValue;
    private final Function<String, Object> attrParser;
    private String attrConfigKey;

    ConfigAttribute(
            String attrName,
            String attrConfigKey,
            String attrDescription,
            Object attrDefaultValue,
            Function<String, Object> parser) {
        this.attrName = attrName;
        this.attrConfigKey = attrConfigKey;
        this.attrDescription = attrDescription;
        this.attrDefaultValue = attrDefaultValue;
        this.attrParser = parser;
    }

    // implementation of getters
    //...
}
```

So referencing one configuration key will be nothing else than using the `ConfigAttribut.DEBUG_MODE`, for example.

but this is nothing without the `Configuration` system to retrieve and manage values:

## Configuration class

The `Configuration` system will provide access to the configuration attribute values, from file AND from command line
argument.

![The Configuration class to operate IConfigAttribute values](http://www.plantuml.com/plantuml/png/RLBRJeCm6BxlKzGx5jqB4BCC1-YYsyZ6Z8anf8CVgBQ5z32pcBsxLDXXp9k6_D_X__hHiT94QiCPkj4bSC0r8rIWacQ0YTQIRem6XQZG87EI0fvEAf7JmZjWFmZVuWAq7Sm91sVWhXf85DsyfJ_3hWzOWGygbRJMj19zs8US6APV2JFMSx7vX5IVIICY5SW62Or8hS5zAx_3BNNK9w8z0c4uxkN7bDSCE0Yj_hkR5qL9F8rV0eJzu67zcCmMVlCTl4tZ90vMSJ9PhETn4YwzMJDU1dC_M2PXj6gsfOmerQqpajYCVsKfq1V3a39mykQmzNKb3DmBIYSx1dEsjXRbzWE_VmpSRKMphYVd2tPNdFCkCwfgHdPFmEePA4KAS4u6jItEauLoI7DqmUm6AlGHRytj5GxAFRg8ZeV3qLMnxjdJKcNrVVLJJdZx5Fiu1rrMcK9Z49dXx1S0
"The Configuration class to operate IConfigAttribute values")

But first, initialize the attributes with their default values:

```Java
public class Configuration {
    // TODO get implementation...
}
```

Now we can delegate the argument parsing to our Configuration class, to extract values from:

```Java

public class Game {
    //...
    private Configruation config;
    //...
    public Game(String configFilePath){
      config = new Configuration(ConfigAttribute.values())
              .setConfigurationFile(configFilePath)
              .parseConfigFile();
    }
    
    //...
    public int initialize(String[] args) {
        config.parseArgs(args);
        //...
    }
}
```

We also can display a help message in case of error during argument parsing:

```Java
public class Configuration {

    private void displayHelpMessage(String unknownAttributeName, String attributeValue) {
        System.out.printf("INFO | Configuration : The argument %s=%s is unknown%n", unknownAttributeName, attributeValue);
        displayHelpMessage();
    }

    private void displayHelpMessage() {
        System.out.printf("INFO | Configuration : Here is the list of possible arguments:%n--%n");
        Arrays.stream(attributes).forEach(ca -> {
            System.out.printf("INFO | Configuration : - %s : %s (default value is %s)%n",
                    ca.getAttrName(),
                    ca.getAttrDescription(),
                    ca.getDefaultValue().toString());
        });
    }
}
```

And when at least we get some configuration values, we can get it :

```Java
public class Configuration {
    public Object get(IConfigAttribute ca) {
        return configurationValues.get(ca);
    }
}
```

## Load configuration from file

And, as we discussed before, we can retrieve configuration values from a file, we will use a properties file (a
standard java way to set values) :

### The configuration file :

The properties file is standard and our own will figure out as below:

```properties
app.main.title=GDemoApp-test
app.debug.mode=1
app.test.counter=20
app.render.fps=15
```

So now we can spend some time on the "read" implementation.

### implementing the configuration file

```Java
public class Configuration {
    //...
    public Configuration setConfigurationFile(String cfgFile) {
        this.configFile = cfgFile;
        return this;
    }

    //...
    public int parseConfigFile() {
        int status = 0;
        Properties props = new Properties();
        if (Optional.ofNullable(configFile).isPresent()) {
            try {
                props.load(Configuration.class.getResourceAsStream(this.configFile));
                for (Map.Entry<Object, Object> prop : props.entrySet()) {
                    String[] kv = new String[]{(String) prop.getKey(), (String) prop.getValue()};
                    if (!isArgumentFound(kv)) {
                        System.err.printf("ERR | Configuration file=%s : Unknown property %s with value %s%n",
                                cfgFile,
                                prop.getKey(),
                                prop.getValue());
                    } else {
                        System.out.printf("INFO | Configuration file=%s : set '%s' to %s%n",
                                cfgFile,
                                prop.getKey(),
                                prop.getValue());
                    }
                }

            } catch (IOException e) {
                System.err.printf("ERR | Configuration file=%s : Unable to find and parse the configuration file : %s%n",
                        cfgFile,
                        e.getMessage());
            }
        } else {
            status = -1;
        }
        return status;
    }
}
```

the provided new methods are:

- `setConfigurationFile` to define the configuration file to be read,
- `parseConfigFile` to parse values from the provided file according to existing defined arguments in
  the `ConfigAttribute` enumeration.

You can notice that error is output on the `System.err` output stream, and information is out on the `System.out`
output stream.

## Conclusion

We spent maybe too much time on configuration, but you may see that this is essential.

Like in the previous episode, you will find the corresponding code (and a lot more!) on this GitHub
repository https://github.com/SnapGames/game101 on
tag [create-config](https://github.com/SnapGames/game101/releases/tag/create-config "go and see files from tag 'create-config'")
and create-config-test proposing some unit tests on this topic.

That’s all folks!

McG.