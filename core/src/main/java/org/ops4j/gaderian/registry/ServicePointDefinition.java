package org.ops4j.gaderian.registry;

import org.ops4j.gaderian.Occurances;
import org.ops4j.gaderian.schema.Schema;

/**
 * @author Johan Lindquist
 */
public interface ServicePointDefinition
{
 /**
     * Returns the type of the service, the interface the service implements. This may be a
     * synthetic interface when the interface for the service point is, in fact, a class.
     */
    public Class getServiceInterface();

    /**
     * Returns the interface for the service as specified in the descriptor; starting with release
     * 1.1 it is possible to define a service in terms of a class (as the interface) and a synthetic
     * interface is generated where appropriate.
     *
     * @since 1.1
     */
    public Class getDeclaredInterface();

    /**
     * Returns the fully qualified class name of the service interface. This is useful so that
     * loading the actual service interface class can be deferred as late as possible. This is the
     * value, as specified in the descriptor (except that simple names in the descriptor are
     * prefixed with the module's package name). Starting in release 1.1, this may be the name of a
     * ordinary class, not an interface.
     *
     * @since 1.1
     */
    public String getServiceInterfaceClassName();

    /**
     * Obtains the full service implementation for this service extension point, an object that
     * implements the service interface. Because of the different service models, and because of the
     * possibility of interceptors, the exact class and object returned can't be specified (and may
     * vary at different times), but that is not relevant to client code, which is assured that it
     * can invoke the service methods defined by the service interface.
     *
     * @param interfaceClass
     *            the class that the service will be cast to; a check is made that the service is
     *            assignable to the indicated interface. It does not have to, necessarily, match the
     *            service interface (it could be a super-interface, for example).
     * @return the outermost interceptor for the service, or the core implementation if there are no
     *         interceptors.
     * @throws org.ops4j.gaderian.ApplicationRuntimeException
     *             if there is any problem creating the service.
     */
    public <S> S getService(Class<S> interfaceClass);

    /**
     * Returns the {@link org.ops4j.gaderian.schema.Schema} used to process any parameters passed to the service. Service
     * implementation factories and service interceptor factories allow parameters.
     */

    public Schema getParametersSchema();

    /**
     * Returns the number of parameter object expected; generally this is the default of exactly one (
     * {@link org.ops4j.gaderian.Occurances#REQUIRED}).
     */
    public Occurances getParametersCount();

    /**
     * Forces the service to be fully instantiated immediately, rather than lazily.
     */

    public void forceServiceInstantiation();

}
