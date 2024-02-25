package com.github.zrdj.java.properties.store;

import com.github.zrdj.java.properties.ApplicationProperty;
import com.github.zrdj.java.properties.ApplicationPropertyStore;
import com.github.zrdj.java.properties.ApplicationPropertyValue;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Function;


public final class SystemPropertyStore implements ApplicationPropertyStore {

    private final Logger _log;

    public SystemPropertyStore(final Function<Class<?>, Logger> logFactory) {
        _log = logFactory.apply(getClass());
    }

    @Override
    public Optional<ApplicationPropertyValue> get(final ApplicationProperty property) {
        final String systemProperty = System.getProperty(property.key());
        if (systemProperty == null) {
            _log.debug("{}: key not found", property.key());
            return Optional.empty();
        }
        final ApplicationPropertyValue value = property.value(systemProperty);
        _log.debug("{}: key found. value is {}", property.key(), value.asStringProtected());
        return Optional.of(value);
    }
}
