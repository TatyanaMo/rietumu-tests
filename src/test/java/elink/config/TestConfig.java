package elink.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Properties properties = new Properties();

    static {
        try(InputStream input = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
          if (input==null) {
              throw new RuntimeException("config.properties not found in resources folder");
          }
          properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config properties", e);
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new RuntimeException("Missing property in config.properties:" + key);
        }
        return value;
    }
}
