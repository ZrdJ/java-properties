package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeDotToUnderscoreProperty extends ChangeDelimiterProperty {

    public ChangeDotToUnderscoreProperty(final ApplicationProperty property) {
        super(property, "\\.", "_");
    }
}
