package org.ops4j.gaderian.examples.strategy;

/**
 * @author Johan Lindquist
 */
// START SNIPPET:full
public interface StringManipulator
{
    /** Generic method for manipulating a string
     * @param stringManipulation The command specifying the string manipulation
     * @return The manipulated string
     */
    public String manipulateString(StringManipulation stringManipulation);
}
// END SNIPPET:full
