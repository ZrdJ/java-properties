package com.github.zrdj.java.properties.error;


import com.github.zrdj.java.properties.ApplicationProperty;

public class MissingApplicationPropertyException extends RuntimeException {

    private static final long serialVersionUID = -3092078564412729408L;

    public MissingApplicationPropertyException(final ApplicationProperty property) {
        super("Missing application property " + property.key() + ". Did you forget to define it?");
    }
}
