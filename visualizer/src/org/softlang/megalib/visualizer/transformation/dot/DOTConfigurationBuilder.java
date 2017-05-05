/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.transformation.dot;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import org.softlang.megalib.visualizer.models.transformation.ConfigurationBuilder;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class DOTConfigurationBuilder implements ConfigurationBuilder {

    @Override
    public TransformerConfiguration buildConfiguration() {
        Properties props = loadFromFile().orElseGet(this::loadFromClassPath);
        return propertiesToConfiguration(props);
    }

    private Optional<Properties> loadFromFile() {
        // Check if a graphviz.properties file exists
        // Otherwise load the default properties resources provided by this project's resources
        Path filePath = Paths.get("graphviz.properties");
        if (!filePath.toFile().exists()) {
            return Optional.empty();
        }
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(filePath.toFile()));

            return Optional.of(prop);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Properties loadFromClassPath() {
        Properties prop = new Properties();
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("graphviz.properties");
            prop.load(stream);

            return prop;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TransformerConfiguration propertiesToConfiguration(Properties props) {
        TransformerConfiguration result = new TransformerConfiguration();

        for (String key : props.stringPropertyNames()) {
            String[] keys = key.split("\\.");
            String value = props.getProperty(key);
            result.put(keys[0], keys[1], value);
        }

        return result;
    }

}
