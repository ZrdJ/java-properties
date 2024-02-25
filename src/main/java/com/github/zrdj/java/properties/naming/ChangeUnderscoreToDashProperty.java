package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeUnderscoreToDashProperty extends ChangeDelimiterProperty {

    public ChangeUnderscoreToDashProperty(final ApplicationProperty property) {
        super(property, "_", "-");
    }
}
