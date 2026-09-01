package com.github.zrdj.java.properties.naming;

import com.github.zrdj.java.properties.ApplicationProperty;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers `key-delimiter-transformation`: each of the six {@code naming} classes substitutes
 * every occurrence of one delimiter for another in the wrapped property's key.
 */
public class DelimiterTransformationTest {

    // [impl->req~key-delimiter-transformation.delimiter-substitution~1]
    @Test
    public void dashToDot() {
        final ApplicationProperty property = new ChangeDashToDotProperty(new ApplicationProperty.Immutable("app-name-value"));

        assertThat(property.key()).isEqualTo("app.name.value");
    }

    // [impl->req~key-delimiter-transformation.delimiter-substitution~1]
    @Test
    public void dashToUnderscore() {
        final ApplicationProperty property = new ChangeDashToUnderscoreProperty(new ApplicationProperty.Immutable("app-name-value"));

        assertThat(property.key()).isEqualTo("app_name_value");
    }

    // [impl->req~key-delimiter-transformation.delimiter-substitution~1]
    @Test
    public void dotToDash() {
        final ApplicationProperty property = new ChangeDotToDashProperty(new ApplicationProperty.Immutable("app.name.value"));

        assertThat(property.key()).isEqualTo("app-name-value");
    }

    // [impl->req~key-delimiter-transformation.delimiter-substitution~1]
    @Test
    public void dotToUnderscore() {
        final ApplicationProperty property = new ChangeDotToUnderscoreProperty(new ApplicationProperty.Immutable("app.name.value"));

        assertThat(property.key()).isEqualTo("app_name_value");
    }

    // [impl->req~key-delimiter-transformation.delimiter-substitution~1]
    @Test
    public void underscoreToDash() {
        final ApplicationProperty property = new ChangeUnderscoreToDashProperty(new ApplicationProperty.Immutable("app_name_value"));

        assertThat(property.key()).isEqualTo("app-name-value");
    }

    // [impl->req~key-delimiter-transformation.delimiter-substitution~1]
    @Test
    public void underscoreToDot() {
        final ApplicationProperty property = new ChangeUnderscoreToDotProperty(new ApplicationProperty.Immutable("app_name_value"));

        assertThat(property.key()).isEqualTo("app.name.value");
    }
}
