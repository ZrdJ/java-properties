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
 * Covers `property-store.system-sources`: {@code SystemPropertyStore} and
 * {@code SystemEnvironmentStore}.
 */
public class SystemSourcesTest {

    private static final Function<Class<?>, Logger> LOG_FACTORY = LoggerFactory::getLogger;

    // [impl->req~property-store.system-sources~1]
    @Test
    public void systemPropertyPresentReturnsWrappedValue() {
        final String key = "zrdj.java.properties.test.system-property";
        System.setProperty(key, "text");
        try {
            final ApplicationPropertyStore store = new SystemPropertyStore(LOG_FACTORY);

            final Optional<ApplicationPropertyValue> value = store.get(new ApplicationProperty.Immutable(key));

            assertThat(value).map(ApplicationPropertyValue::asString).contains("text");
        } finally {
            System.clearProperty(key);
        }
    }

    // [impl->req~property-store.system-sources~1]
    @Test
    public void systemPropertyAbsentReturnsEmpty() {
        final String key = "zrdj.java.properties.test.system-property.absent";
        System.clearProperty(key);
        final ApplicationPropertyStore store = new SystemPropertyStore(LOG_FACTORY);

        assertThat(store.get(new ApplicationProperty.Immutable(key))).isEmpty();
    }

    // [impl->req~property-store.system-sources~1]
    @Test
    public void environmentVariablePresentReturnsWrappedValue() {
        // PATH is set by every process launched through the shell that runs the test suite --
        // on every OS the JDK targets. Comparing against System.getenv() itself keeps the test
        // independent of what that value actually is.
        final String key = "PATH";
        final String expected = System.getenv(key);
        assertThat(expected).as("PATH must be set in the process environment running this test").isNotNull();

        final ApplicationPropertyStore store = new SystemEnvironmentStore(LOG_FACTORY);

        final Optional<ApplicationPropertyValue> value = store.get(new ApplicationProperty.Immutable(key));

        assertThat(value).map(ApplicationPropertyValue::asString).contains(expected);
    }

    // [impl->req~property-store.system-sources~1]
    @Test
    public void environmentVariableAbsentReturnsEmpty() {
        final String key = "ZRDJ_JAVA_PROPERTIES_TEST_ABSENT_ENV_VAR";
        assertThat(System.getenv(key)).isNull();

        final ApplicationPropertyStore store = new SystemEnvironmentStore(LOG_FACTORY);

        assertThat(store.get(new ApplicationProperty.Immutable(key))).isEmpty();
    }
}
