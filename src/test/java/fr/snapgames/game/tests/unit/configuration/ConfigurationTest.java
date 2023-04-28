package fr.snapgames.game.tests.unit.configuration;

import fr.snapgames.game.core.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    Configuration config;

    @BeforeEach
    void setUp() {
        config = new Configuration(TestConfigAttributes.values());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void parseArgs() {
        config.parseArgs(new String[]{
                "booleanValue=true",
                "integerValue=256",
                "doubleValue=123.45",
        "TO(test,12.1f,567.891,true)"});
    }

    @Test
    void displayHelpMessage() {
    }

    @Test
    void testDisplayHelpMessage() {
    }

    @Test
    void testGetBoolean() {
    }
    @Test
    void testGetInteger() {
    }
    @Test
    void testGetDouble() {
    }
    @Test
    void testGetString() {
    }


    @Test
    void save() {
    }
}