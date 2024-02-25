package com.github.zrdj.java.properties.store;

import com.github.zrdj.java.properties.ApplicationProperty;
import com.github.zrdj.java.properties.ApplicationPropertyStore;
import com.github.zrdj.java.properties.ApplicationPropertyValue;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public class PropertiesFileStore implements ApplicationPropertyStore {

    private static final class PropertiesFile {

        private final String _name;

        private PropertiesFile(String name) {
            _name = name;
        }

        public Map<String, String> loadFromProperties() {
            final Optional<InputStream> is = getResourceAsStream();
            if (is.isEmpty()) {
                return Collections.emptyMap();
            }

            try (InputStreamReader streamReader = new InputStreamReader(is.get(), StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                final Map<String, String> result = new HashMap<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    final String[] property = line.split("=");
                    final String propertyKey = property.length > 0 ? property[0] : null;
                    final String propertyValue = property.length > 1 ? property[1] : null;
                    result.put(propertyKey, propertyValue);
                }
                return result;

            } catch (IOException e) {
                return Collections.emptyMap();
            }
        }

        private Optional<InputStream> getResourceAsStream() {
            final ClassLoader classLoader = getClass().getClassLoader();
            final InputStream inputStream = classLoader.getResourceAsStream(_name);

            return Optional.ofNullable(inputStream);
        }
    }
    private final Logger _log;
    private final Map<String, String> _properties;

    public PropertiesFileStore(final Function<Class<?>, Logger> logFactory, final String environment) {
        _log = logFactory.apply(getClass());
        _log.info("loading properties from [" + environment + ".properties]");
        _properties = new PropertiesFile(environment + ".properties").loadFromProperties();
    }

    @Override
    public Optional<ApplicationPropertyValue> get(final ApplicationProperty property) {
        final String propertyFromFile = _properties.get(property.key());
        if (propertyFromFile == null) {
            _log.debug("{}: key not found", property.key());
            return Optional.empty();
        }
        final ApplicationPropertyValue value = property.value(propertyFromFile);
        _log.debug("{}: key found. value is {}", property.key(), value.asStringProtected());
        return Optional.of(value);
    }
}
