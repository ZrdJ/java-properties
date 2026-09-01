package com.github.zrdj.java.properties.store;

import com.github.zrdj.java.properties.ApplicationProperty;
import com.github.zrdj.java.properties.ApplicationPropertyStore;
import com.github.zrdj.java.properties.ApplicationPropertyValue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers `property-store.logging-passthrough`: {@code ComposedApplicationPropertyStore} logs and
 * returns the wrapped store's result unchanged.
 */
public class ComposedApplicationPropertyStoreTest {

    private static final Function<Class<?>, Logger> LOG_FACTORY = LoggerFactory::getLogger;
    private static final ApplicationProperty PROPERTY = new ApplicationProperty.Immutable("some.key");

    // [impl->req~property-store.logging-passthrough~1]
    @Test
    public void wrappedStoreValueIsReturnedUnchanged() {
        final ApplicationPropertyStore wrapped = property -> Optional.of(new ApplicationPropertyValue.Immutable("value"));
        final ApplicationPropertyStore store = new ComposedApplicationPropertyStore(LOG_FACTORY, wrapped);

        assertThat(store.get(PROPERTY)).map(ApplicationPropertyValue::asString).contains("value");
    }

    // [impl->req~property-store.logging-passthrough~1]
    @Test
    public void wrappedStoreEmptyResultIsReturnedUnchanged() {
        final ApplicationPropertyStore wrapped = property -> Optional.empty();
        final ApplicationPropertyStore store = new ComposedApplicationPropertyStore(LOG_FACTORY, wrapped);

        assertThat(store.get(PROPERTY)).isEmpty();
    }
}
