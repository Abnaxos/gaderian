package org.ops4j.gaderian.examples.pipeline.impl;

import org.ops4j.gaderian.examples.pipeline.StringToUpper;

/**
 * @author Johan Lindquist
 */
public class StringToUpperImpl implements StringToUpper
{
    public String toUpper( final String string )
    {
        return string.toUpperCase();
    }
}
