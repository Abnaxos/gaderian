package org.ops4j.gaderian.service.impl;

/** Decorator responsible for decorating the specified instance with lifecycle methods according
 * to what the service creation parameters & class annotations specifies
 *
 * @author Johan Lindquist
 */
public class ClassDecorator
{
    /** Decorates the specified class with any required lifecycle methods.
     *
     * @param serviceClass The class to decorate
     * @param parameter The builder factory parameters specified
     * @param annotationsEnabled
     * @return The decorated instance (if at all required)
     */
    public static Class decorate( final Class serviceClass, final BuilderParameter parameter, final boolean annotationsEnabled )
    {
        // Is the class annotated with lifecycle annotations?

        // Are the parameters defining lifecycle methods

        // Does the class define any (default) lifecycle methods
        
        return serviceClass;
    }
}
