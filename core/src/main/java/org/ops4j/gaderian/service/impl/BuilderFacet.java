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

package org.ops4j.gaderian.service.impl;

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.AssemblyParameters;
import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.util.PropertyUtils;

/**
 * Represents one facet of constructing a service implementation instance. A facet is either a
 * property to be set on the constructed instance, or a parameter to the instance class'
 * constructor. Facets are nested properties within
 * {@link org.ops4j.gaderian.service.impl.AssemblyInstructionImpl}, and are additionally used by
 * {@link org.ops4j.gaderian.service.impl.BuilderFactory}.
 * 
 * @author Howard Lewis Ship
 */
public abstract class BuilderFacet extends BaseLocatable
{
    private String _propertyName;

    /**
     * Implemented in subclasses to provide a specific value for the facet (for use as a constructor
     * parameter, or as a value to set a property to).
     * 
     * @param assemblyParameters
     *            the parameters that define the service point and its environment
     * @param targetType
     *            the desired property type (extracted from the property type of the property to be
     *            updated, when a property is known)
     */
    public abstract Object getFacetValue(AssemblyParameters assemblyParameters, Class targetType);

    public abstract boolean isAssignableToType(AssemblyParameters factoryParameters,
            Class targetType);

    public String getPropertyName()
    {
        return _propertyName;
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

    /**
     * Wire (or auto-wire) the property; return the name of the property actually set (if a property
     * is set, which is not always the case).
     */
    public String wireProperty(Object service, AssemblyParameters assemblyParameters)
    {
        try
        {
            // Autowire the property (if possible).

            String autowirePropertyName = autowire(service, assemblyParameters);

            if (autowirePropertyName != null)
                return autowirePropertyName;

            // There will be a facet for log, messages, service-id, etc. even if no
            // property name is specified, so we skip it here. In many cases, those
            // facets will have just done an autowire.

            if (_propertyName == null)
                return null;

            Class targetType = PropertyUtils.getPropertyType(service, _propertyName);

            Object value = getFacetValue(assemblyParameters, targetType);

            PropertyUtils.write(service, _propertyName, value);

            final Log log = assemblyParameters.getLog();

            if (log.isDebugEnabled())
                log.debug("Set property " + _propertyName + " to " + value);

            return _propertyName;
        }
        catch (Exception ex)
        {
            assemblyParameters.getErrorLog().error(ex.getMessage(), getLocation(), ex);

            return null;
        }
    }

    /**
     * Attempts to autowire a property of the target. This requires that
     * <ul>
     * <li>The facet type defines a default property name and facet type
     * <li>The facet instance does not have a specified property name
     * <li>The (default) property is writeable
     * <li>The (default) property is assignable from the facet type
     * </ul>
     * If all conditions are met, then the property is updated to the facet value, and the property
     * name is returned. In all other cases, null is returned.
     * 
     * @param target
     *            The service implementation being constructed
     * @param assemblyParameters
     *            the parameters that define the service point and its environment
     */
    private String autowire(Object target, AssemblyParameters assemblyParameters)
    {
        if (_propertyName != null)
            return null;

        String defaultPropertyName = getDefaultPropertyName();

        if (defaultPropertyName == null)
            return null;

        if (!PropertyUtils.isWritable(target, defaultPropertyName))
            return null;

        Class propertyType = PropertyUtils.getPropertyType(target, defaultPropertyName);

        if (isAssignableToType(assemblyParameters, propertyType))
        {
            Object facetValue = getFacetValue(assemblyParameters, propertyType);

            PropertyUtils.write(target, defaultPropertyName, facetValue);

            Log log = assemblyParameters.getLog();

            if (log.isDebugEnabled())
                log.debug("Autowired property " + defaultPropertyName + " to " + facetValue);

            return defaultPropertyName;
        }

        return null;
    }

    /**
     * Returns null. Subclasses can provide the default name for a property used by
     * {@link #autowire(Object, AssemblyParameters)}.
     */
    protected String getDefaultPropertyName()
    {
        return null;
    }

    /** @since 1.1 */
    public boolean canAutowireConstructorParameter()
    {
        return false;
    }

}