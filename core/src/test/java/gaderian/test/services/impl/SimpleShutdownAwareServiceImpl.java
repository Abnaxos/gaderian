package gaderian.test.services.impl;

import gaderian.test.services.SimpleShutdownAwareService;
import org.apache.commons.logging.Log;

/**
 * @author Johan Lindquist
 */
public class SimpleShutdownAwareServiceImpl implements SimpleShutdownAwareService
{
    private Log _log;

    public void setLog(final Log log)
    {
        _log = log;
    }

    public int add(final int a, final int b)
    {
        return a+b;
    }

    public void registryDidShutdown()
    {
        _log.debug("registryDidShutdown --- SimpleShutdownAware");
    }
}
