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
 * Covers `property-store.retry`: {@code RetryStore} retries once, against the same underlying
 * store, under a transformed property.
 */
public class RetryStoreTest {

    private static final Function<Class<?>, Logger> LOG_FACTORY = LoggerFactory::getLogger;
    private static final ApplicationProperty PROPERTY = new ApplicationProperty.Immutable("original.key");
    private static final ApplicationProperty RETRY_PROPERTY = new ApplicationProperty.Immutable("retry.key");

    // [impl->req~property-store.retry~1]
    @Test
    public void directLookupSucceedsWithoutConsultingRetryProperty() {
        final ApplicationPropertyStore store = property -> property == PROPERTY
                ? Optional.of(new ApplicationPropertyValue.Immutable("direct"))
                : Optional.empty();
        final Function<ApplicationProperty, ApplicationProperty> retryProperty = property -> {
            throw new AssertionError("retryProperty must not be consulted when the direct lookup succeeds");
        };

        final ApplicationPropertyStore retryStore = new RetryStore(LOG_FACTORY, retryProperty, store);

        assertThat(retryStore.get(PROPERTY)).map(ApplicationPropertyValue::asString).contains("direct");
    }

    // [impl->req~property-store.retry~1]
    @Test
    public void directLookupMissesRetriedLookupSucceedsOnTheSameStore() {
        final ApplicationPropertyStore store = property -> property == RETRY_PROPERTY
                ? Optional.of(new ApplicationPropertyValue.Immutable("retried"))
                : Optional.empty();
        final Function<ApplicationProperty, ApplicationProperty> retryProperty = property -> {
            assertThat(property).isSameAs(PROPERTY);
            return RETRY_PROPERTY;
        };

        final ApplicationPropertyStore retryStore = new RetryStore(LOG_FACTORY, retryProperty, store);

        assertThat(retryStore.get(PROPERTY)).map(ApplicationPropertyValue::asString).contains("retried");
    }

    // [impl->req~property-store.retry~1]
    @Test
    public void bothLookupsMissYieldEmpty() {
        final ApplicationPropertyStore store = property -> Optional.empty();
        final Function<ApplicationProperty, ApplicationProperty> retryProperty = property -> RETRY_PROPERTY;

        final ApplicationPropertyStore retryStore = new RetryStore(LOG_FACTORY, retryProperty, store);

        assertThat(retryStore.get(PROPERTY)).isEmpty();
    }
}
