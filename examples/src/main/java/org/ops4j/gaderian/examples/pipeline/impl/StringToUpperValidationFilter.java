package org.ops4j.gaderian.examples.pipeline.impl;

import org.ops4j.gaderian.examples.pipeline.StringToUpperFilter;
import org.ops4j.gaderian.examples.pipeline.StringToUpper;

/**
 * @author Johan Lindquist
 */
public class StringToUpperValidationFilter implements StringToUpperFilter
{
    public String toUpper( final String string, final StringToUpper stringReverser )
    {
        if (string == null)
        {
            throw new IllegalArgumentException( "Specified string is null" );
        }
        return stringReverser.toUpper( string );
    }
}
