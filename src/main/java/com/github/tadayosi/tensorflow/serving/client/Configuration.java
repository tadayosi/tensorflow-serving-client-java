package com.github.tadayosi.tensorflow.serving.client;

import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import io.grpc.ChannelCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    public static final String TFSC4J_PROPERTIES = "tfsc4j.properties";
    public static final String TFSC4J_PREFIX = "tfsc4j.";

    public static final String TARGET = "target";
    public static final String CREDENTIALS = "credentials";

    private final Optional<String> target;
    private final Optional<ChannelCredentials> credentials;

    private Configuration() {
        Properties props = loadProperties();

        this.target = loadProperty(TARGET, props);
        //this.credentials = loadProperty(CREDENTIALS, props);
        this.credentials = Optional.empty();
    }

    static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream(TFSC4J_PROPERTIES);
            properties.load(is);
        } catch (Exception e) {
            // Ignore
            LOG.debug("Failed to load properties file: {}", e.getMessage());
        }
        return properties;
    }

    /**
     * Order of precedence: System properties > environment variables > properties file
     */
    static Optional<String> loadProperty(String key, Properties properties) {
        String tsc4jKey = TFSC4J_PREFIX + key;
        Optional<String> value = Optional.ofNullable(System.getProperty(tsc4jKey))
            .or(() -> Optional.ofNullable(System.getenv(tsc4jKey.toUpperCase().replace(".", "_"))))
            .or(() -> Optional.ofNullable(properties.getProperty(key)));
        LOG.debug("Loaded property {}: {}", key, value.orElse(null));
        return value;
    }

    public static Configuration load() {
        return new Configuration();
    }

    public Optional<String> getTarget() {
        return target;
    }

    public Optional<ChannelCredentials> getCredentials() {
        return credentials;
    }
}
