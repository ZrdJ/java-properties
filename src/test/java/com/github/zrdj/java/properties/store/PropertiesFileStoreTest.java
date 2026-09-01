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
 * Covers `property-store.properties-file-source`: {@code PropertiesFileStore} loads
 * {@code src/test/resources/testenv.properties} from the test classpath.
 */
public class PropertiesFileStoreTest {

    private static final Function<Class<?>, Logger> LOG_FACTORY = LoggerFactory::getLogger;

    // [impl->req~property-store.properties-file-source~1]
    @Test
    public void propertyPresentInFileReturnsWrappedValue() {
        final ApplicationPropertyStore store = new PropertiesFileStore(LOG_FACTORY, "testenv");

        final Optional<ApplicationPropertyValue> value = store.get(new ApplicationProperty.Immutable("key"));

        assertThat(value).map(ApplicationPropertyValue::asString).contains("value");
    }

    // [impl->req~property-store.properties-file-source~1]
    @Test
    public void missingResourceFileYieldsEmptyWithoutException() {
        final ApplicationPropertyStore store = new PropertiesFileStore(LOG_FACTORY, "does-not-exist");

        assertThat(store.get(new ApplicationProperty.Immutable("key"))).isEmpty();
    }

    // [impl->req~property-store.properties-file-source~1]
    @Test
    public void valueContainingEqualsSignIsTruncatedAtTheFirstOccurrence() {
        final ApplicationPropertyStore store = new PropertiesFileStore(LOG_FACTORY, "testenv");

        final Optional<ApplicationPropertyValue> value = store.get(new ApplicationProperty.Immutable("dup"));

        assertThat(value).map(ApplicationPropertyValue::asString).contains("a");
    }
}
