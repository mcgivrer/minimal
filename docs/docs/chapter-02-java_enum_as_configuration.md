# Enum as Configuration

_(TO BE REVIEWED)_

One of the first thing you have to provide, before any game mechanic, it's a way to set configuration values.

To satisfy this need, we will use a well known java structure with in a particular way: `enum` !

## Introduction

First we need to understand what would be the age for our configuration. we need to define some value to be set by
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

## The attribute interface

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

![The Configuration class to operate IConfigAttribute values](https://www.plantuml.com/plantuml/png/RL5lRxem67pVJz7VEynlVW6o69d0PZQLA2vBbeLKU81kRI7zus8Ml_leG8UuDqtptDtJk-jKQIAruGpzqoLmm3KZLA2IPe29rfBkZ0Q5gD0WSv82diygaTF2Es5V2F_71MWxM18EpiZTD90ekdbBVuJ34B027rGgQQrf9OQm3panJ3yJPkpErUuTKjsJHaGga0qI6f7Q1RUj_0QtrCcGY8v8bTKd7lZlfMnJ3t4EkgmoXK0OVWLINZFW8BJwgnWlYf9u7Zy52Fl1tVO-MQpyvXwypECa3ZPnCbjjbt4Ihhr5Cru7IpzO9s6qQQocZ2ZLFMSaiG5_cbQWBuOXPU3apM6xxneSkHUKJdOsvknjfyeT6dvz6xdxYcPToSu77Auu_O_8gAePEJm0gnUW52d0-LrGszOzMIY7D4TdpBv0XJxZxN8RuQGyeWlhU3oUN1Nhzdvi2Y8pd7q3
"The Configuration class to operate IConfigAttribute values")

But first, initialize the attributes with their default values:

```Java
public class Configuration {

    IConfigAttribute[] attributes;
    private Map<IConfigAttribute, Object> configurationValues = new ConcurrentHashMap<>();

    public Configuration(IConfigAttribute[] attributes) {
        setAttributes(attributes);
        // initialize all default values.
        Arrays.stream(attributes).forEach(ca -> {
            configurationValues.put(ca, ca.getDefaultValue());
        });
    }
    //...
}
```

Now we can delegate the argument parsing to our Configuration class, to extract values from:

```Java

public class App implements Game {
    //...
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

    private boolean isArgumentFound(String[] kv) {
        boolean found = false;
        for (IConfigAttribute ca : attributes) {
            if (ca.getAttrName().equals(kv[0]) || ca.getConfigKey().equals(kv[0])) {
                configurationValues.put(ca, ca.getAttrParser().apply(kv[1]));
                found = true;
            }
        }
        return found;
    }
    //...
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

And when al least we get some configuration values, we can get it :

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

The properties file is a standard one and our own will figure out as below:

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

You can notice that error are output on the `System.err` output stream, and information are out on the `System.out`
output stream.

## Conclusion

We spent maybe too much time on configuration, but you may see that this is essential.

Like in the previous episode, you will find the corresponding code (and a lot more!) on this GitHub
repository https://github.com/SnapGames/game101 on
tag [create-config](https://github.com/SnapGames/game101/releases/tag/create-config "go and see files from tag 'create-config'")
and create-config-test proposing some unit tests on this topic.

That’s all folks!

McG.