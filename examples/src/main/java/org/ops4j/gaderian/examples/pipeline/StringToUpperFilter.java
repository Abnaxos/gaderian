package org.ops4j.gaderian.examples.pipeline;

/**
 * @author Johan Lindquist
 */
// START SNIPPET:full
public interface StringToUpperFilter
{
    // START SNIPPET:method
    public String toUpper(String string, StringToUpper stringReverser);
    // END SNIPPET:method
}
// END SNIPPET:full
