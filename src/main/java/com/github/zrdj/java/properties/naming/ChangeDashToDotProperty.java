package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeDashToDotProperty extends ChangeDelimiterProperty {

    public ChangeDashToDotProperty(final ApplicationProperty property) {
        super(property, "-", "\\.");
    }
}
