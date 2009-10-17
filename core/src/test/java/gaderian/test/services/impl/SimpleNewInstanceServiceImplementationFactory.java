package gaderian.test.services.impl;

import org.ops4j.gaderian.*;

/**
 * @author SafeNet Technologies B.V.
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
