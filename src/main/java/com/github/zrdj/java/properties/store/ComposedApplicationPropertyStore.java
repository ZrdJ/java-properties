package com.github.zrdj.java.properties.store;

import com.github.zrdj.java.properties.ApplicationProperty;
import com.github.zrdj.java.properties.ApplicationPropertyStore;
import com.github.zrdj.java.properties.ApplicationPropertyValue;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.Function;


public final class ComposedApplicationPropertyStore implements ApplicationPropertyStore {

    private final ApplicationPropertyStore _store;
    private final Logger _log;

    public ComposedApplicationPropertyStore(final Function<Class<?>, Logger> logFactory, final ApplicationPropertyStore store) {
        _store = store;
        _log = logFactory.apply(ComposedApplicationPropertyStore.class);
    }

    @Override
    public Optional<ApplicationPropertyValue> get(final ApplicationProperty property) {
        _log.debug("{}: retrieving key..", property.key());
        return _store.get(property);
    }
}
