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

package org.ops4j.gaderian.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.schema.Schema;
import org.ops4j.gaderian.schema.impl.SchemaImpl;
import org.ops4j.gaderian.util.ToStringBuilder;

/**
 * Representation of a Gaderian module descriptor, as parsed by
 * {@link org.ops4j.gaderian.parse.DescriptorParser}. Corresponds to the root &lt;module&gt;
 * element.
 * 
 * @author Howard Lewis Ship
 */
public final class ModuleDescriptor extends BaseAnnotationHolder
{
    /** @since 1.1 */
    private static final Log LOG = LogFactory.getLog(ModuleDescriptor.class);

    private String _moduleId;

    private String _version;

    /** @since 1.1 */

    private String _packageName;

    private List<ServicePointDescriptor> _servicePoints;

    private List<ImplementationDescriptor> _implementations;

    private List<ConfigurationPointDescriptor> _configurationPoints;

    private List<ContributionDescriptor> _contributions;

    private List<SubModuleDescriptor> _subModules;

    private List<DependencyDescriptor> _dependencies;

    /** @since 1.1 */
    private Map<String, SchemaImpl> _schemas;

    private ClassResolver _resolver;

    /** @since 1.1 */
    private ErrorHandler _errorHandler;

    /** Defines the default service implementation factory id for this module
     * @since Gaderian 1.1
     */
    private String _defaultServiceImplementationFactoryId;

    public ModuleDescriptor(ClassResolver resolver, ErrorHandler errorHandler)
    {
        _resolver = resolver;
        _errorHandler = errorHandler;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("moduleId", _moduleId);
        builder.append("version", _version);
        builder.append("_defaultServiceImplementationFactoryId", _defaultServiceImplementationFactoryId);

        return builder.toString();
    }

    public void addServicePoint(final ServicePointDescriptor service)
    {
        if (_servicePoints == null)
            _servicePoints = new ArrayList<ServicePointDescriptor>();

        _servicePoints.add(service);
    }

    public List getServicePoints()
    {
        return _servicePoints;
    }

    public void addImplementation(final ImplementationDescriptor descriptor)
    {
        if (_implementations == null)
            _implementations = new ArrayList<ImplementationDescriptor>();

        _implementations.add(descriptor);
    }

    public List getImplementations()
    {
        return _implementations;
    }

    public void addConfigurationPoint(final ConfigurationPointDescriptor descriptor)
    {
        if (_configurationPoints == null)
            _configurationPoints = new ArrayList<ConfigurationPointDescriptor>();

        _configurationPoints.add(descriptor);
    }

    public List getConfigurationPoints()
    {
        return _configurationPoints;
    }

    public void addContribution(final ContributionDescriptor descriptor)
    {
        if (_contributions == null)
            _contributions = new ArrayList<ContributionDescriptor>();

        _contributions.add(descriptor);
    }

    public List getContributions()
    {
        return _contributions;
    }

    public void addSubModule(final SubModuleDescriptor subModule)
    {
        if (_subModules == null)
            _subModules = new ArrayList<SubModuleDescriptor>();

        _subModules.add(subModule);
    }

    public List<SubModuleDescriptor> getSubModules()
    {
        return _subModules;
    }

    public void addDependency(final DependencyDescriptor dependency)
    {
        if (_dependencies == null)
            _dependencies = new ArrayList<DependencyDescriptor>();

        _dependencies.add(dependency);
    }

    public List<DependencyDescriptor> getDependencies()
    {
        return _dependencies;
    }

    /**
     * Adds a schema to this module descriptor. If a schema with the same id already has been added,
     * an error is reported and the given schema is ignored.
     * 
     * @since 1.1
     */
    public void addSchema(final SchemaImpl schema)
    {
        if (_schemas == null)
            _schemas = new HashMap<String, SchemaImpl>();

        final String schemaId = schema.getId();

        final Schema existing = getSchema(schemaId);

        if (existing != null)
        {
            _errorHandler.error(LOG, ParseMessages.duplicateSchema(
                    _moduleId + '.' + schemaId,
                    existing), schema.getLocation(), null);
            return;
        }

        _schemas.put(schemaId, schema);
    }

    /** @since 1.1 */
    public Schema getSchema(final String id)
    {
        return _schemas == null ? null : _schemas.get(id);
    }

    /**
     * Returns a Collection of {@link org.ops4j.gaderian.schema.impl.SchemaImpl}.
     * 
     * @since 1.1
     */
    public Collection<SchemaImpl> getSchemas()
    {
        return _schemas != null ? _schemas.values() : Collections.<SchemaImpl>emptyList();
    }

    public String getModuleId()
    {
        return _moduleId;
    }

    public String getVersion()
    {
        return _version;
    }

    public void setModuleId(final String string)
    {
        _moduleId = string;
    }

    public void setVersion(final String string)
    {
        _version = string;
    }

    public ClassResolver getClassResolver()
    {
        return _resolver;
    }

    /**
     * Returns the name of the package to search for class names within. By default, the package
     * name will match the module id, but this can be overridden in the module descriptor.
     * 
     * @since 1.1
     */

    public String getPackageName()
    {
        return _packageName;
    }

    /** @since 1.1 */

    public void setPackageName(final String packageName)
    {
        _packageName = packageName;
    }

    /** Returns the service identifier of the default service implementation factory to use within this module.  If
     * this is not specified the default BuilderFactory from Gaderian will be used
     * @return The service id of the service factory
     * @since Gaderian 1.1
     */
    public String getDefaultServiceImplementationFactoryId()
    {
        return _defaultServiceImplementationFactoryId;
    }

    /** Sets the default service implementation factory as read from the module
     *
     * @param defaultServiceImplementationFactoryId The default service implementation factory id as read from the module
     * @since Gaderian 1.1
     */
    public void setDefaultServiceImplementationFactoryId(final String defaultServiceImplementationFactoryId)
    {
        _defaultServiceImplementationFactoryId = defaultServiceImplementationFactoryId;
    }
}