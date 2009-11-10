package org.ops4j.gaderian.examples.strategy;

import junit.framework.TestCase;
import org.ops4j.gaderian.examples.ExampleUtils;
import org.ops4j.gaderian.examples.pipeline.StringToUpper;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.testutils.GaderianTestCase;

/**
 * @author Johan Lindquist
 */
public class TestStrategy extends GaderianTestCase
{
    public StringManipulator _stringManipulator;

    @Override
    protected void setUp() throws Exception
    {
        Registry registry = ExampleUtils.buildClasspathRegistry("/org/ops4j/gaderian/examples/strategy/strategy.xml");
        _stringManipulator = registry.getService( StringManipulator.class );
    }

    public void testToUpperCaseStringManipulation() throws Exception
    {
        final String upperString = _stringManipulator.manipulateString( new ToUpperCaseStringManipulation( "fred" ) );
        assertEquals("FRED", upperString);
    }

    public void testToReverseUpperCaseStringManipulation() throws Exception
    {
        // This doesn't exactly do what is promised, but shows the tree structure of the strategy factory
        final String upperString = _stringManipulator.manipulateString( new ToReverseUpperCaseStringManipulation( "fred" ) );
        assertEquals("FRED", upperString);
    }

    public void testToLowerCaseStringManipulation() throws Exception
    {
        final String lowerString = _stringManipulator.manipulateString( new ToLowerCaseStringManipulation( "FRED" ) );
        assertEquals("fred", lowerString);
    }

    public void testUnhandledStringManipulation() throws Exception
    {
        try
        {
            _stringManipulator.manipulateString( new ReverseStringManipulation( "FRED" ) );
            unreachable();
        }
        catch ( IllegalStateException e )
        {
            assertEquals("Unhandled string manipulation: class org.ops4j.gaderian.examples.strategy.ReverseStringManipulation",e.getMessage());
        }
    }

}