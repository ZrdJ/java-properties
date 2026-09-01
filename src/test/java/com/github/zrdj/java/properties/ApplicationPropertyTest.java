package com.github.zrdj.java.properties;

import com.github.zrdj.java.properties.error.MissingApplicationPropertyException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers `property-value-access`: {@code ApplicationProperty.value(String)} and
 * {@code ApplicationProperty.exception()}.
 */
public class ApplicationPropertyTest {

    private static final class SecretProperty implements ApplicationProperty {
        @Override
        public String key() {
            return "secret.key";
        }

        @Override
        public boolean isSecret() {
            return true;
        }
    }

    // [impl->req~property-value-access.value-wrapping~1]
    @Test
    public void nonSecretPropertyWrapsAsImmutable() {
        final ApplicationProperty property = new ApplicationProperty.Immutable("plain.key");

        final ApplicationPropertyValue value = property.value("text");

        assertThat(value).isInstanceOf(ApplicationPropertyValue.Immutable.class);
    }

    // [impl->req~property-value-access.value-wrapping~1]
    @Test
    public void secretPropertyWrapsAsImmutableSecured() {
        final ApplicationProperty property = new SecretProperty();

        final ApplicationPropertyValue value = property.value("text");

        assertThat(value).isInstanceOf(ApplicationPropertyValue.ImmutableSecured.class);
    }

    // [impl->req~property-value-access.missing-property-exception~1]
    @Test
    public void exceptionMessageNamesTheKey() {
        final ApplicationProperty property = new ApplicationProperty.Immutable("aws.dynamo.region");

        final MissingApplicationPropertyException exception = property.exception();

        assertThat(exception.getMessage()).contains("aws.dynamo.region");
    }
}
