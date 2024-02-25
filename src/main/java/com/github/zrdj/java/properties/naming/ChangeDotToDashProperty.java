package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeDotToDashProperty extends ChangeDelimiterProperty {

    public ChangeDotToDashProperty(final ApplicationProperty property) {
        super(property, "\\.", "-");
    }
}
