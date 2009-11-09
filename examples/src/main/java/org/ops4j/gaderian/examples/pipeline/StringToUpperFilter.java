package org.ops4j.gaderian.examples.pipeline;

/**
 * @author Johan Lindquist
 */
// START SNIPPET:full
public interface StringToUpperFilter
{
    public String toUpper(String string, StringToUpper stringReverser);
}
// END SNIPPET:full
