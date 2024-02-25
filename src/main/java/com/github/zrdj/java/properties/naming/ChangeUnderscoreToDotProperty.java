package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeUnderscoreToDotProperty extends ChangeDelimiterProperty {

    public ChangeUnderscoreToDotProperty(final ApplicationProperty property) {
        super(property, "_", "\\.");
    }
}
