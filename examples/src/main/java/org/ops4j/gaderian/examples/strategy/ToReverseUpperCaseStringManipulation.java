package org.ops4j.gaderian.examples.strategy;

/** A <code>StringManipulation</code> sub-class intended to signal the wish to convert the specified string into lover case
 * and reverse it.
 * 
 * @author Johan Lindquist
 */
public class ToReverseUpperCaseStringManipulation extends ToUpperCaseStringManipulation
{
    public ToReverseUpperCaseStringManipulation( final String stringToManipulate )
    {
        super( stringToManipulate );
    }

}