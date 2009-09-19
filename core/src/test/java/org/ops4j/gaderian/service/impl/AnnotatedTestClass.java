package org.ops4j.gaderian.service.impl;

import org.ops4j.gaderian.annotations.validation.Required;

/**
 * @author Johan Lindquist
 */
public class AnnotatedTestClass
{
    private String _nonRequiredField;

    @Required()
    private String _requiredField;

    public String getNonRequiredField()
    {
        return _nonRequiredField;
    }

    public void setNonRequiredField( final String nonRequiredField )
    {
        _nonRequiredField = nonRequiredField;
    }

    public String getRequiredField()
    {
        return _requiredField;
    }

    public void setRequiredField( final String requiredField )
    {
        _requiredField = requiredField;
    }
}