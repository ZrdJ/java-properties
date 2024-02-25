package com.github.zrdj.java.properties.store;

import com.github.zrdj.java.properties.ApplicationProperty;
import com.github.zrdj.java.properties.ApplicationPropertyStore;
import com.github.zrdj.java.properties.ApplicationPropertyValue;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Function;


public final class RetryStore implements ApplicationPropertyStore {

    private final Function<ApplicationProperty, ApplicationProperty> _retryProperty;
    private final ApplicationPropertyStore _store;
    private final Logger _log;

    public RetryStore(final Function<Class<?>, Logger> logFactory, final Function<ApplicationProperty, ApplicationProperty> retryProperty,
                      final ApplicationPropertyStore store) {
        _retryProperty = retryProperty;
        _store = store;
        _log = logFactory.apply(getClass());
    }

    @Override
    public Optional<ApplicationPropertyValue> get(final ApplicationProperty property) {
        return _store.get(property).or(() -> {
            final ApplicationProperty retryProperty = _retryProperty.apply(property);
            _log.debug("{}: key not found, retrying with {}", property.key(), retryProperty.key());
            return _store.get(retryProperty);
        });
    }
}
