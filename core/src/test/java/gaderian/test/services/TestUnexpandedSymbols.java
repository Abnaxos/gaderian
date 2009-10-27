package gaderian.test.services;

import gaderian.test.FrameworkTestCase;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ApplicationRuntimeException;

/**
 * @author Johan Lindquist
 */
public class TestUnexpandedSymbols extends FrameworkTestCase
{

    public void testSimple() throws Exception
    {
        try
        {
            final Registry registry = buildFrameworkRegistry( "unexpanded-symbols.xml", true );
            registry.expandSymbols( "${a}", null );
            unreachable();
        }
        catch ( ApplicationRuntimeException e )
        {
            e.printStackTrace();
            assertEquals( "unexpected error", "Could not resolve value for symbol 'a'", e.getMessage() );
        }
    }

}
