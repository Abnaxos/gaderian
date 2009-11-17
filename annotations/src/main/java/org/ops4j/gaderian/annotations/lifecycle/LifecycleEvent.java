package org.ops4j.gaderian.annotations.lifecycle;

/**
 * @author Johan Lindquist
 */
public enum LifecycleEvent
{
    INITIALIZE,
    ACTIVATE,
    INVOCATION_STARTED,
    INVOCATION_COMPLETED,
    PASSIVATE,
    SHUTDOWN_SIGNALLED,
    SHUTDOWN
}
