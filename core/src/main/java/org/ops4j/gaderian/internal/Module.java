// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ops4j.gaderian.internal;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Locatable;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Messages;
import org.ops4j.gaderian.SymbolSource;
import org.ops4j.gaderian.schema.Translator;

/**
 * The definition of a Gaderian Module. A Module is a container of service extension points and
 * configuration extension points. It also acts as a "gateway" so that services and configurations
 * in other modules may be accessed.
 * <p>
 * Why do we expose the Module rather than the
 * {@link org.ops4j.gaderian.internal.RegistryInfrastructure}? It's more than just qualifying ids
 * before passing them up to the RI. At some future point, a concept of visibility will be added to
 * Gaderian. This will make many services and configurations private to the module which defines
 * them and the necessary visibility filtering logic will be here.
 * 
 * @author Howard Lewis Ship
 */
public interface Module extends Locatable, SymbolSource
{
    /**
     * Returns the unique identifier for this module.
     */
    public String getModuleId();

    /**
     * Returns true if a single service exists which implements the specified service interface and
     * is visible to this module.
     *
     * @param serviceInterface
     * @return true if a single visible service for the specified service interface exists
     * @since 1.1
     */
    public boolean containsService(Class serviceInterface);

    /**
     * Returns true if a service with the specified ID exists which implements the specified service
     * interface and is visible to this module.
     *
     * @param serviceId
     *            an unqualified id for a service within this module, or a fully qualified id for a
     *            service in this or any other module
     * @param serviceInterface
     * @return true if a single visible service for the specified service interface exists
     * @since 1.1
     */
    public boolean containsService(String serviceId, Class serviceInterface);

    /**
     * Returns the count of services ivisible to the module that implement the specified service
     * interface.
     *
     * @param serviceInterface The service interface.
     * @return The count of implementing services visible to the module.
     */
    public int getServiceCount( Class<?> serviceInterface );

    /**
     * Looks up the {@link ServicePoint} (throwing an exception if not found) and invokes
     * {@link ServicePoint#getService(Class)}.
     * 
     * @param serviceId
     *            an unqualified id for a service within this module, or a fully qualified id for a
     *            service in this or any other module
     * @param serviceInterface
     *            type the result will be cast to
     */
    public <T> T getService(String serviceId, Class<T> serviceInterface);

    /**
     * Finds a service that implements the provided interface. Exactly one such service may exist or
     * an exception is thrown.
     * 
     * @param serviceInterface
     *            used to locate the service
     */
    public <T> T getService(Class<T> serviceInterface);

    /**
     * Returns the identified service extension point.
     * 
     * @param serviceId
     *            an unqualified id for a service within this module, or a fully qualified id for a
     *            service in this or any other module
     * @throws org.ops4j.gaderian.ApplicationRuntimeException
     *             if no such service extension point exists
     */

    public ServicePoint getServicePoint(String serviceId);

    /**
     * Returns the {@link java.util.List} of elements for the specified configuration point. The
     * returned List is unmodifiable. It may be empty, but won't be null.
     * <p>
     * It is expressly the <em>caller's</em> job to sort the elements into an appropriate order (a
     * copy will have to be made since the returned List is unmodifiable).
     * 
     * @param configurationId
     *            an unqualified id for a configuration within this module, or a fully qualified id
     *            for a configuration in this or any other module
     * @throws ApplicationRuntimeException
     *             if this module does not contain the specified configuration extension point.
     */
    public List getConfiguration(String configurationId);

    /**
     * Returns true if the elements contributed to the given configuration point can be
     * {@link #getConfigurationAsMap(String) retrieved as a Map}.
     * 
     * @see ConfigurationPoint#areElementsMappable()
     * @since 1.1
     */
    public boolean isConfigurationMappable(String configurationId);

    /**
     * Returns the elements of the given configuration point as an unmodifiable {@link Map}. It may
     * be empty, but not null.
     * 
     * @param configurationId
     *            an unqualified id for a configuration within this module, or a fully qualified id
     *            for a configuration in this or any other module.
     * @throws ApplicationRuntimeException
     *             if no configuration point with the given id exists or if the elements can't be
     *             mapped.
     * @see ConfigurationPoint#getElementsAsMap()
     * @see #isConfigurationMappable(String)
     * @since 1.1
     */
    public Map getConfigurationAsMap(String configurationId);

    /**
     * Returns the resource resolver for this module. The resource resolver is used to locate
     * classes by name (using the correct classloader).
     */
    public ClassResolver getClassResolver();

    /**
     * Returns the class matching the type. First, attempts to resolve the type exactly as is. If
     * that fails, resolves the type within the module's defined package.
     * 
     * @param type
     *            the Java type to convert into a class. May be a primitive type, or an array of
     *            objects or primitives.
     * @return the corresponding {@link Class} object.
     * @throws org.ops4j.gaderian.ApplicationRuntimeException
     *             if the type may not be converted into a Class.
     * @since 1.1
     */

    public Class resolveType(String type);

    /**
     * Returns an object that can provide and format localized messages for this module. The
     * messages come from a properties file, <code>module.properties</code> (localized) stored
     * with the Gaderian deployment descriptor in the META-INF/org/ops4j/gaderian folder.
     */

    public Messages getMessages();

    /**
     * @see RegistryInfrastructure#getTranslator(String)
     */
    public Translator getTranslator(String translator);

    /**
     * @see RegistryInfrastructure#getServiceModelFactory(String)
     */
    public ServiceModelFactory getServiceModelFactory(String name);

    /**
     * @see org.ops4j.gaderian.Registry#getLocale()
     */
    public Locale getLocale();

    /**
     * @see org.ops4j.gaderian.internal.RegistryInfrastructure#expandSymbols(String, Location)
     */
    public String expandSymbols(String input, Location location);

    public Object stringToObject(String input, Class<?> target, Location location);

    /**
     * Returns the {@link org.ops4j.gaderian.ErrorHandler} for this Registry.
     */

    public ErrorHandler getErrorHandler();
}