package com.github.zrdj.java.properties;

import java.util.Optional;
import java.util.function.Function;


public interface ApplicationPropertyStore {

    default ApplicationPropertyStore decorate(final Function<ApplicationPropertyStore, ApplicationPropertyStore> factory) {
        return factory.apply(this);
    }

    Optional<ApplicationPropertyValue> get(final ApplicationProperty property);

    default ApplicationPropertyStore or(final ApplicationPropertyStore other) {
        return name -> this.get(name).or(() -> other.get(name));
    }

}
