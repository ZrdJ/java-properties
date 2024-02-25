package com.github.zrdj.java.properties.naming;


import com.github.zrdj.java.properties.ApplicationProperty;

public class ChangeDelimiterProperty implements ApplicationProperty {

    private final ApplicationProperty _property;
    private final String _originalDelimiter;
    private final String _newDelimiter;

    public ChangeDelimiterProperty(final ApplicationProperty property, final String originalDelimiter, final String newDelimiter) {
        _property = property;
        _originalDelimiter = originalDelimiter;
        _newDelimiter = newDelimiter;
    }

    @Override
    public String key() {
        return _property.key().replaceAll(_originalDelimiter, _newDelimiter);
    }
}
