package model;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.softlang.megalib.visualizer.models.transformation.ConfigurationBuilder;
import org.softlang.megalib.visualizer.models.transformation.TransformerConfiguration;

public class GefConfigurationBuilder implements ConfigurationBuilder{


    public TransformerConfiguration buildConfiguration() {
        Properties props = loadFromClassPath();
        return propertiesToConfiguration(props);
    }

    private Properties loadFromClassPath() {
        Properties prop = new Properties();
        try {
        	String resource = "gef.properties";
        	URL u = this.getClass().getResource(resource);
        	prop.load(u.openStream());
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
