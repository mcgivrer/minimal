package fr.snapgames.game.tests.unit.configuration;

import fr.snapgames.game.core.configuration.IConfigAttribute;

import java.util.function.Function;

public enum TestConfigAttributes implements IConfigAttribute {
    CONFIG_VALUE_STRING(
            "stringValue",
            "test.value.string",
            "this is a String value",
            "default value",
            v -> v
    ),
    CONFIG_BOOLEAN_VALUE(
            "booleanValue",
            "test.value.boolean",
            "this is a boolean value",
            false,
            v -> Boolean.parseBoolean(v)),
    CONFIG_INTEGER_VALUE(
            "integerValue",
            "test.value.integer",
            "this is a integer value",
            0,
            v -> Integer.parseInt(v)),
    CONFIG_DOUBLE_VALUE(
            "doubleValue",
            "test.value.double",
            "this is a double value",
            0.0,
            v -> Double.parseDouble(v)),
    CONFIG_TESTOBJECT_VALUE(
            "testObjectValue",
            "test.value.testobject",
            "this is a TestObject value",
            0.0,
            v -> {
                String[] values = v.substring(
                                "TO(".length(),
                                v.length() - ")".length())
                        .split(",");
                String title = values[0];
                Float vf = Float.parseFloat(values[1]);
                Double vd = Double.parseDouble(values[2]);
                Boolean vb = Boolean.parseBoolean(values[3]);
                TestObject to = new TestObject(title, vf, vd, vb);
                return to;
            }),
    ;
    private final String attrName;
    private final String attrDescription;
    private final Object attrDefaultValue;
    private final Function<String, Object> attrParser;
    private final String attrConfigKey;

    TestConfigAttributes(String attrName, String attrConfigKey, String attrDescription, Object attrDefaultValue, Function<String, Object> parser) {
        this.attrName = attrName;
        this.attrConfigKey = attrConfigKey;
        this.attrDescription = attrDescription;
        this.attrDefaultValue = attrDefaultValue;
        this.attrParser = parser;
    }

    @Override
    public String getAttrName() {
        return attrName;
    }

    @Override
    public Function<String, Object> getAttrParser() {
        return this.attrParser;
    }

    @Override
    public Object getDefaultValue() {
        return this.attrDefaultValue;
    }

    @Override
    public String getAttrDescription() {
        return this.attrDescription;
    }

    @Override
    public String getConfigKey() {
        return attrConfigKey;
    }
}
