package org.ops4j.gaderian.examples.strategy.impl;

import org.ops4j.gaderian.examples.strategy.StringManipulation;
import org.ops4j.gaderian.examples.strategy.StringManipulator;
import org.ops4j.gaderian.examples.strategy.ToUpperCaseStringManipulation;

/** Implementation of <code>StringManipulator</code> that turns the specified string into upper case.
 * @author Johan Lindquist
 */
public class ToUpperCaseStringManipulator implements StringManipulator
{
    public String manipulateString( final StringManipulation stringManipulation )
    {
        // This cast is safe as Gaderian will only invoke our service if the types match
        ToUpperCaseStringManipulation toUpperCaseStringManipulation = ( ToUpperCaseStringManipulation ) stringManipulation;
        return toUpperCaseStringManipulation.getStringToManipulate().toUpperCase();
    }
}