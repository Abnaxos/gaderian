package gaderian.test.parse;

import java.util.List;

import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.Registry;
import gaderian.test.services.SimpleService;
import gaderian.test.SimpleBean;

/**
 * @author Johan Lindquist
 */
public class TestModuleParsingWithSchema extends GaderianCoreTestCase
{

    public void testParseModuleWithNamespaces()
    {
        try
        {
            final Registry registry = buildFrameworkRegistry( "ModuleWithNamespaces.xml", true );

            final SimpleService service = registry.getService( SimpleService.class );
            assertEquals(2,service.add( 1,1 ));

            final List<SimpleBean> configuration = registry.getConfiguration( "gaderian.test.parse.Config" );
            assertEquals( "not correct size", 1, configuration.size() );

            SimpleBean simpleBean = configuration.get( 0 );

            assertEquals("bad value", "bar", simpleBean.getValue());


        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

    public void testParseModuleWithMixedNamespaces()
    {
        try
        {
            final Registry registry = buildFrameworkRegistry( "ModuleWithMixedNamespaces.xml", true );

            final SimpleService service = registry.getService( SimpleService.class );
            assertEquals(2,service.add( 1,1 ));

            final List<SimpleBean> configuration = registry.getConfiguration( "gaderian.test.parse.Config" );
            assertEquals( "not correct size", 2, configuration.size() );

            SimpleBean simpleBean = configuration.get( 0 );
            assertEquals("bad value", "bar", simpleBean.getValue());
            simpleBean = configuration.get( 1 );
            assertEquals("bad value", "bar", simpleBean.getValue());


        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

    public void testParseModuleWithGaderianNamespaces()
    {
        try
        {
            final Registry registry = buildFrameworkRegistry( "ModuleWithGaderianNamespaces.xml", true );

            final SimpleService service = registry.getService( SimpleService.class );
            assertEquals(2,service.add( 1,1 ));

            final List<SimpleBean> configuration = registry.getConfiguration( "gaderian.test.parse.Config" );
            assertEquals( "not correct size", 2, configuration.size() );

            SimpleBean simpleBean = configuration.get( 0 );
            assertEquals("bad value", "bar", simpleBean.getValue());
            simpleBean = configuration.get( 1 );
            assertEquals("bad value", "bar", simpleBean.getValue());


        }
        catch ( Exception e )
        {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

}
