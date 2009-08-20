package org.ops4j.gaderian.annotations;

import org.ops4j.gaderian.annotations.lifecycle.LifecycleEventCallback;
import org.ops4j.gaderian.annotations.lifecycle.LifecycleEvent;

/**
 * @author Johan Lindquist
 */
public class SimpleServiceImpl
{

    // START SNIPPET:initialize-service
    @LifecycleEventCallback(lifecycleEvents = LifecycleEvent.INITIALIZE)
    public void initializeService() throws Exception
    {
        // Initialize service
    }
    // END SNIPPET:initialize-service

    // START SNIPPET:shutdown-service
    @LifecycleEventCallback(lifecycleEvents = {LifecycleEvent.SHUTDOWN_SIGNALLED, LifecycleEvent.SHUTDOWN})
    public void shutdown() throws Exception
    {
        // Initialize service
    }
    // END SNIPPET:shutdown-service

}
