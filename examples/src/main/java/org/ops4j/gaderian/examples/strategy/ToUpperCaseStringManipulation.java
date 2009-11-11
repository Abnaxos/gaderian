package org.ops4j.gaderian.examples.strategy;

/** A <code>StringManipulation</code> sub-class intended to signal the wish to convert the specified string into upper case.
 * @author Johan Lindquist
 */
public class ToUpperCaseStringManipulation extends StringManipulation
{
    public ToUpperCaseStringManipulation( final String stringToManipulate )
    {
        super( stringToManipulate );
    }
    
}
