package org.ops4j.gaderian.examples.strategy;

/** A <code>StringManipulation</code> sub-class intended to signal the wish to convert the specified string into lower case.
 * @author Johan Lindquist
 */
public class ToLowerCaseStringManipulation extends StringManipulation
{
    public ToLowerCaseStringManipulation( final String stringToManipulate )
    {
        super( stringToManipulate );
    }

}