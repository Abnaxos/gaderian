package org.ops4j.gaderian.examples.strategy.impl;

import org.ops4j.gaderian.examples.strategy.StringManipulation;
import org.ops4j.gaderian.examples.strategy.StringManipulator;

/** Default implementation of <code>StringManipulator</code> simply indicates to the caller that there is no
 * strategy available to handle the specified <code>StringManipulation</code> type.
 * @author Johan Lindquist
 */
public class UnhandledStringManipulator implements StringManipulator
{
    public String manipulateString( final StringManipulation stringManipulation )
    {
        // Simply throw an illegal state exception, indicating that the specified type is not handled.
        throw new IllegalStateException( "Unhandled string manipulation: " + stringManipulation.getClass() );
    }
}
