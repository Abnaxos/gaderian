package org.ops4j.gaderian.examples.pipeline;

/**
 * @author Johan Lindquist
 */
// START SNIPPET:full
public interface StringToUpper
{
    /** Converts the specified string to upper-case.
     * @param string The string to convert
     * @return The converted string
     */
    public String toUpper(String string);
}
// END SNIPPET:full
