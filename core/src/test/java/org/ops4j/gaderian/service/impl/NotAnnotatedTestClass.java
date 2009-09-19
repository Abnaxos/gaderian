package org.ops4j.gaderian.service.impl;

/**
 * @author Johan Lindquist
 */
public class NotAnnotatedTestClass
{
    private String _nonRequiredField;

    public String getNonRequiredField()
    {
        return _nonRequiredField;
    }

    public void setNonRequiredField( final String nonRequiredField )
    {
        _nonRequiredField = nonRequiredField;
    }
}
