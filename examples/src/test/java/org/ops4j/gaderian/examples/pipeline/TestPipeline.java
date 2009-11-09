package org.ops4j.gaderian.examples.pipeline;

import junit.framework.TestCase;
import org.ops4j.gaderian.examples.ExampleUtils;
import org.ops4j.gaderian.Registry;

/**
 * @author Johan Lindquist
 */
public class TestPipeline extends TestCase
{
    public StringToUpper _stringToUpper;

    @Override
    protected void setUp() throws Exception
    {
        Registry registry = ExampleUtils.buildClasspathRegistry("/org/ops4j/gaderian/examples/pipeline/pipeline.xml");
        _stringToUpper = registry.getService( StringToUpper.class );
    }

    public void testInvokation() throws Exception
    {
        final String upperString = _stringToUpper.toUpper( "fred" );
        assertEquals("FRED", upperString);
    }

    public void testFilter() throws Exception
    {
        try
        {
            _stringToUpper.toUpper( null );
        }
        catch ( IllegalArgumentException e )
        {
            assertEquals("Specified string is null",e.getMessage());
        }
    }

    public void testMagicFilter() throws Exception
    {
        final String upperString = _stringToUpper.toUpper( "hocus" );
        assertEquals("pocus", upperString);
    }

}
