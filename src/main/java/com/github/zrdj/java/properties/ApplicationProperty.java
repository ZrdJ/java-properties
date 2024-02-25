package com.github.zrdj.java.properties;


import com.github.zrdj.java.properties.error.MissingApplicationPropertyException;

public interface ApplicationProperty {

    final class Immutable implements ApplicationProperty {

        private final String _key;

        public Immutable(final String key) {
            _key = key;
        }

        @Override
        public String key() {
            return _key;
        }
    }

    default MissingApplicationPropertyException exception() {
        return new MissingApplicationPropertyException(this);
    }

    String key();

    default boolean isSecret() {
        return false;
    }

    default ApplicationPropertyValue value(String value) {
        return isSecret() ? new ApplicationPropertyValue.ImmutableSecured(value) : new ApplicationPropertyValue.Immutable(value);
    }
}
