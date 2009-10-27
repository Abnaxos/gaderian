package gaderian.test.services.impl;

import org.ops4j.gaderian.*;

/**
 * @author Johan Lindquist
 * @version $Revision$
 */
public class SimpleNewInstanceServiceImplementationFactory implements ServiceImplementationFactory
{
    public Object createCoreServiceImplementation(final ServiceImplementationFactoryParameters factoryParameters)
    {
        try
        {
            return ((Class)factoryParameters.getFirstParameter()).newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
