package com.github.zrdj.java.properties;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers `property-store`: the {@code ApplicationPropertyStore} default methods {@code or} and
 * {@code decorate}.
 */
public class ApplicationPropertyStoreTest {

    private static final ApplicationProperty PROPERTY = new ApplicationProperty.Immutable("some.key");

    // [impl->req~property-store.or-fallback~1]
    @Test
    public void primaryStoreValueWinsWithoutConsultingFallback() {
        final ApplicationPropertyStore a = property -> Optional.of(new ApplicationPropertyValue.Immutable("a"));
        final ApplicationPropertyStore b = property -> {
            throw new AssertionError("fallback store must not be consulted");
        };

        assertThat(a.or(b).get(PROPERTY)).map(ApplicationPropertyValue::asString).contains("a");
    }

    // [impl->req~property-store.or-fallback~1]
    @Test
    public void emptyPrimaryFallsBackToOther() {
        final ApplicationPropertyStore a = property -> Optional.empty();
        final ApplicationPropertyStore b = property -> Optional.of(new ApplicationPropertyValue.Immutable("b"));

        assertThat(a.or(b).get(PROPERTY)).map(ApplicationPropertyValue::asString).contains("b");
    }

    // [impl->req~property-store.or-fallback~1]
    @Test
    public void bothStoresEmptyYieldsEmpty() {
        final ApplicationPropertyStore a = property -> Optional.empty();
        final ApplicationPropertyStore b = property -> Optional.empty();

        assertThat(a.or(b).get(PROPERTY)).isEmpty();
    }

    // [impl->req~property-store.decorate~1]
    @Test
    public void decorateReturnsFactoryAppliedToTheStore() {
        final ApplicationPropertyStore store = property -> Optional.empty();
        final ApplicationPropertyStore decorated = property -> Optional.of(new ApplicationPropertyValue.Immutable("decorated"));

        final ApplicationPropertyStore result = store.decorate(s -> {
            assertThat(s).isSameAs(store);
            return decorated;
        });

        assertThat(result).isSameAs(decorated);
    }
}
