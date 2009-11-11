package org.ops4j.gaderian.examples.strategy;

/** A <code>StringManipulation</code> sub-class intended to signal the wish to reverse the specified string.
 * @author Johan Lindquist
 */
public class ReverseStringManipulation extends StringManipulation
{
    public ReverseStringManipulation( final String stringToManipulate )
    {
        super( stringToManipulate );
    }

}