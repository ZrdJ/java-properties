package com.github.zrdj.java.properties;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers `property-value-access`: {@code ApplicationPropertyValue.asStringProtected()} and the
 * four numeric accessors.
 */
public class ApplicationPropertyValueTest {

    // [impl->req~property-value-access.protected-text~1]
    @Test
    public void unsecuredValueIsNotMasked() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.Immutable("text");

        assertThat(value.asStringProtected()).isEqualTo(value.asString());
    }

    // [impl->req~property-value-access.protected-text~1]
    @Test
    public void securedValueMasksAllButLeadingTwentyPercent() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.ImmutableSecured("0123456789");

        assertThat(value.asStringProtected()).isEqualTo("01*******");
    }

    // [impl->req~property-value-access.numeric-conversion~1]
    @Test
    public void asBigDecimalParsesViaBigDecimalConstructor() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.Immutable("42.5");

        assertThat(value.asBigDecimal()).contains(new BigDecimal("42.5"));
    }

    // [impl->req~property-value-access.numeric-conversion~1]
    @Test
    public void asDoubleParsesViaDoubleParseDouble() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.Immutable("42.5");

        assertThat(value.asDouble()).contains(42.5);
    }

    // [impl->req~property-value-access.numeric-conversion~1]
    @Test
    public void asIntegerParsesViaIntegerParseInt() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.Immutable("42");

        assertThat(value.asInteger()).contains(42);
    }

    // [impl->req~property-value-access.numeric-conversion~1]
    @Test
    public void asLongParsesViaLongParseLong() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.Immutable("42");

        assertThat(value.asLong()).contains(42L);
    }

    // [impl->req~property-value-access.numeric-conversion~1]
    @Test
    public void conversionFailureIsSwallowedForEveryNumericAccessor() {
        final ApplicationPropertyValue value = new ApplicationPropertyValue.Immutable("not-a-number");
        final List<Function<ApplicationPropertyValue, Optional<?>>> accessors = Arrays.asList(
                ApplicationPropertyValue::asBigDecimal, ApplicationPropertyValue::asDouble,
                ApplicationPropertyValue::asInteger, ApplicationPropertyValue::asLong);

        for (final Function<ApplicationPropertyValue, Optional<?>> accessor : accessors) {
            assertThat(accessor.apply(value)).isEmpty();
        }
    }
}
