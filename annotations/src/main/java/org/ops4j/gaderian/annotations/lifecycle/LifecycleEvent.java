package org.ops4j.gaderian.annotations.lifecycle;

/**
 * @author Johan Lindquist
 */
public enum LifecycleEvent
{
    INITIALIZE,
    THREAD_START,
    THREAD_COMPLETED,
    SHUTDOWN_SIGNALLED,
    SHUTDOWN
}
