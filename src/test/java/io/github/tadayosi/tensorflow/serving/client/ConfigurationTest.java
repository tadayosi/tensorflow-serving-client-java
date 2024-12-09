package io.github.tadayosi.tensorflow.serving.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigurationTest {

    @AfterEach
    void cleanSystemProperties() {
        System.clearProperty("tfsc4j.target");
        System.clearProperty("tfsc4j.credentials");
    }

    @Test
    void testLoad() {
        var config = Configuration.load();
        assertNotNull(config);
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    void testSystemProperties() {
        System.setProperty("tfsc4j.target", "dns:///test.com:8501");

        var config = Configuration.load();

        assertEquals("dns:///test.com:8501", config.getTarget().get());
    }
}
