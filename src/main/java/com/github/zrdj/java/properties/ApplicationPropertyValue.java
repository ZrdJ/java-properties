package com.github.zrdj.java.properties;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;


public interface ApplicationPropertyValue {

    final class Immutable implements ApplicationPropertyValue {

        private final String _value;

        public Immutable(final String value) {
            _value = value;
        }

        @Override
        public String asString() {
            return _value;
        }
    }

    final class ImmutableSecured implements ApplicationPropertyValue {

        private final String _value;

        public ImmutableSecured(final String value) {
            _value = value;
        }

        @Override
        public String asStringProtected() {
            final int oneThird = (int) (_value.length() * (20.0f / 100.0f));
            return _value.substring(0, oneThird) + "*******";
        }

        @Override
        public String asString() {
            return _value;
        }
    }

    default Optional<BigDecimal> asBigDecimal() {
        return emptyWhenFailing(() -> Optional.of(new BigDecimal(asString())));
    }

    default Optional<Double> asDouble() {
        return emptyWhenFailing(() -> Optional.of(Double.parseDouble(asString())));
    }

    default Optional<Integer> asInteger() {
        return emptyWhenFailing(() -> Optional.of(Integer.parseInt(asString())));
    }

    default Optional<Long> asLong() {
        return emptyWhenFailing(() -> Optional.of(Long.parseLong(asString())));
    }

    String asString();

    default String asStringProtected() {
        return asString();
    }

    private <T> Optional<T> emptyWhenFailing(Supplier<Optional<T>> runnable) {
        try {
            return runnable.get();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
