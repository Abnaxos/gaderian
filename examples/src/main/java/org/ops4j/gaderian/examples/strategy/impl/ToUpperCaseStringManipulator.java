package org.ops4j.gaderian.examples.strategy.impl;

import org.ops4j.gaderian.examples.strategy.StringManipulation;
import org.ops4j.gaderian.examples.strategy.StringManipulator;

/**
 * @author Johan Lindquist
 */
public class ToUpperCaseStringManipulator implements StringManipulator
{
    public String manipulateString( final StringManipulation stringManipulation )
    {
        return stringManipulation.getStringToManipulate().toUpperCase();
    }
}