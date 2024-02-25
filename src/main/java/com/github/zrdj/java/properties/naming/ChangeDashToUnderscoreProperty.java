package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeDashToUnderscoreProperty extends ChangeDelimiterProperty {

    public ChangeDashToUnderscoreProperty(final ApplicationProperty property) {
        super(property, "-", "_");
    }
}
