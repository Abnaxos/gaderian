package org.ops4j.gaderian.examples.pipeline.impl;

import org.ops4j.gaderian.examples.pipeline.StringToUpperFilter;
import org.ops4j.gaderian.examples.pipeline.StringToUpper;

/**
 * @author Johan Lindquist
 */
public class StringToUpperMagicTokenFilter implements StringToUpperFilter
{
    public String toUpper( final String string, final StringToUpper stringReverser )
    {
        if (string.equals( "hocus" ))
        {
            return "pocus";
        }
        return stringReverser.toUpper( string );
    }
}