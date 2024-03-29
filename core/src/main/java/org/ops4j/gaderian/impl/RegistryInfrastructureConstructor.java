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

package org.ops4j.gaderian.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Occurances;
import org.ops4j.gaderian.ShutdownCoordinator;
import org.ops4j.gaderian.conditional.EvaluationContextImpl;
import org.ops4j.gaderian.conditional.Node;
import org.ops4j.gaderian.conditional.Parser;
import org.ops4j.gaderian.internal.ConfigurationPoint;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.parse.ConfigurationPointDescriptor;
import org.ops4j.gaderian.parse.ContributionDescriptor;
import org.ops4j.gaderian.parse.DependencyDescriptor;
import org.ops4j.gaderian.parse.ImplementationDescriptor;
import org.ops4j.gaderian.parse.InstanceBuilder;
import org.ops4j.gaderian.parse.InterceptorDescriptor;
import org.ops4j.gaderian.parse.ModuleDescriptor;
import org.ops4j.gaderian.parse.ServicePointDescriptor;
import org.ops4j.gaderian.schema.ElementModel;
import org.ops4j.gaderian.schema.Schema;
import org.ops4j.gaderian.schema.impl.SchemaImpl;
import org.ops4j.gaderian.util.IdUtils;

/**
 * Fed a series of {@link org.ops4j.gaderian.parse.ModuleDescriptor}s, this class will assemble
 * them into a final {@link org.ops4j.gaderian.internal.RegistryInfrastructure} as well as perform
 * some validations.
 * <p>
 * This class was extracted from {@link org.ops4j.gaderian.impl.RegistryBuilder}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class RegistryInfrastructureConstructor
{
    private ErrorHandler _errorHandler;

    private Log _log;

    private RegistryAssembly _assembly;

    /** @since 1.1 */

    private Parser _conditionalExpressionParser;

    public RegistryInfrastructureConstructor(ErrorHandler errorHandler, Log log,
            RegistryAssembly assembly)
    {
        _errorHandler = errorHandler;
        _log = log;
        _assembly = assembly;
    }

    /**
     * Map of {@link ModuleDescriptor} keyed on module id.
     */

    private Map<String, ModuleDescriptor> _moduleDescriptors = new HashMap<String, ModuleDescriptor>();

    /**
     * Map of {@link ModuleImpl} keyed on module id.
     */
    private Map<String, ModuleImpl> _modules = new HashMap<String, ModuleImpl>();

    /**
     * Map of {@link Schema} keyed on fully qualified module id.
     */
    private Map<String, Schema> _schemas = new HashMap<String, Schema>();

    private ElementModel _assemblyElementModel;

    /**
     * Map of {@link ServicePointImpl} keyed on fully qualified id.
     */

    private Map<String, ServicePointImpl> _servicePoints = new HashMap<String, ServicePointImpl>();

    /**
     * Map of {@link ConfigurationPointImpl} keyed on fully qualified id.
     */

    private Map<String, ConfigurationPointImpl> _configurationPoints = new HashMap<String, ConfigurationPointImpl>();

    /**
     * Shutdown coordinator shared by all objects.
     */

    private ShutdownCoordinator _shutdownCoordinator = new ShutdownCoordinatorImpl();

    /**
     * This class is used to check the dependencies of a ModuleDescriptor. As the checker is run it
     * will log errors to the ErrorHandler if dependencies don't resolve or the versions dont match.
     */
    private class ModuleDependencyChecker implements Runnable
    {
        private ModuleDescriptor _source;

        public ModuleDependencyChecker(ModuleDescriptor source)
        {
            _source = source;
        }

        public void run()
        {
            List dependencies = _source.getDependencies();
            int count = size(dependencies);

            for (int i = 0; i < count; i++)
            {
                DependencyDescriptor dependency = (DependencyDescriptor) dependencies.get(i);
                checkDependency(dependency);
            }
        }

        private void checkDependency(DependencyDescriptor dependency)
        {
            ModuleDescriptor requiredModule = _moduleDescriptors.get(dependency
                    .getModuleId());

            if (requiredModule == null)
            {
                _errorHandler.error(
                        _log,
                        ImplMessages.dependencyOnUnknownModule(dependency),
                        dependency.getLocation(),
                        null);
                return;
            }

            if (dependency.getVersion() != null && !dependency.getVersion().equals(requiredModule.getVersion()))
            {
                _errorHandler.error(
                        _log,
                        ImplMessages.dependencyVersionMismatch(dependency),
                        dependency.getLocation(),
                        null);
                return;
            }
        }
    }

    /**
     * Constructs the registry infrastructure, based on data collected during the prior calls to
     * {@link #addModuleDescriptor(ModuleDescriptor)}. Expects that all post-processing of the
     * {@link RegistryAssembly} has already occured.
     */
    public RegistryInfrastructure constructRegistryInfrastructure(Locale locale)
    {
        RegistryInfrastructureImpl result = new RegistryInfrastructureImpl(_errorHandler, locale);

        addServiceAndConfigurationPoints(result);

        addImplementationsAndContributions();

        checkForMissingServices();

        checkContributionCounts();

        result.setShutdownCoordinator(_shutdownCoordinator);

        addModulesToRegistry(result);

        // The caller is responsible for invoking startup().

        return result;
    }

    public void addModuleDescriptor(ModuleDescriptor md)
    {
        String id = md.getModuleId();

        if (_log.isDebugEnabled())
            _log.debug("Processing module " + id);

        if (_modules.containsKey(id))
        {
            Module existing = _modules.get(id);

            _errorHandler.error(_log, ImplMessages.duplicateModuleId(id, existing.getLocation(), md
                    .getLocation()), null, null);

            // Ignore the duplicate module descriptor.
            return;
        }

        ModuleImpl module = new ModuleImpl();

        module.setLocation(md.getLocation());
        module.setModuleId(id);
        module.setPackageName(md.getPackageName());
        module.setClassResolver(md.getClassResolver());

        if (size(md.getDependencies()) > 0)
            _assembly.addPostProcessor(new ModuleDependencyChecker(md));

        for ( final SchemaImpl schema : md.getSchemas() )
        {
            schema.setModule( module );

            _schemas.put( IdUtils.qualify( id, schema.getId() ), schema );
        }

        _modules.put(id, module);

        _moduleDescriptors.put(id, md);
    }

    private void addServiceAndConfigurationPoints(RegistryInfrastructureImpl infrastructure)
    {
        for ( final ModuleDescriptor md : _moduleDescriptors.values() )
        {
            String id = md.getModuleId();

            ModuleImpl module = _modules.get( id );

            addServicePoints( infrastructure, module, md );

            addConfigurationPoints( infrastructure, module, md );
        }
    }

    private void addServicePoints(RegistryInfrastructureImpl infrastructure, Module module,
            ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        List services = md.getServicePoints();
        int count = size(services);

        for (int i = 0; i < count; i++)
        {
            ServicePointDescriptor sd = (ServicePointDescriptor) services.get(i);

            String pointId = moduleId + "." + sd.getId();

            ServicePoint existingPoint = _servicePoints.get(pointId);

            if (existingPoint != null)
            {
                _errorHandler.error(_log, ImplMessages.duplicateExtensionPointId(
                        pointId,
                        existingPoint), sd.getLocation(), null);
                continue;
            }

            if (_log.isDebugEnabled())
            {
                _log.debug("Creating service point " + pointId);
            }

            // Choose which class to instantiate based on
            // whether the service is create-on-first-reference
            // or create-on-first-use (deferred).

            ServicePointImpl point = new ServicePointImpl();

            point.setExtensionPointId(pointId);
            point.setLocation(sd.getLocation());
            point.setModule(module);

            point.setServiceInterfaceName(sd.getInterfaceClassName());

            point.setParametersSchema(findParametersSchema(sd.getParametersSchema(), module, sd
                    .getParametersSchemaId(), point.getLocation()));

            point.setParametersCount(sd.getParametersCount());
            point.setVisibility(sd.getVisibility());

            point.setShutdownCoordinator(_shutdownCoordinator);

            infrastructure.addServicePoint(point);

            // Save this for the second phase, where contributions
            // from other modules are applied.

            _servicePoints.put(pointId, point);

            addInternalImplementations(module, pointId, sd);
        }
    }

    private void addConfigurationPoints(RegistryInfrastructureImpl registry, Module module,
            ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        List points = md.getConfigurationPoints();
        int count = size(points);

        for (int i = 0; i < count; i++)
        {
            ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(i);

            String pointId = moduleId + "." + cpd.getId();

            ConfigurationPoint existingPoint = _configurationPoints.get(pointId);

            if (existingPoint != null)
            {
                _errorHandler.error(_log, ImplMessages.duplicateExtensionPointId(
                        pointId,
                        existingPoint), cpd.getLocation(), null);
                continue;
            }

            if (_log.isDebugEnabled())
            {
                _log.debug("Creating configuration point " + pointId);
            }

            ConfigurationPointImpl point = new ConfigurationPointImpl();

            point.setExtensionPointId(pointId);
            point.setLocation(cpd.getLocation());
            point.setModule(module);
            point.setExpectedCount(cpd.getCount());

            point.setContributionsSchema(findContributionsSchema(
                    cpd.getContributionsSchema(),
                    module,
                    cpd.getContributionsSchemaId(),
                    cpd.getLocation()));

            point.setVisibility(cpd.getVisibility());

            point.setShutdownCoordinator(_shutdownCoordinator);

            registry.addConfigurationPoint(point);

            // Needed later when we reconcile the rest
            // of the configuration contributions.

            _configurationPoints.put(pointId, point);
        }
    }

    private void addContributionElements(Module sourceModule, ConfigurationPointImpl point,
            List elements)
    {
        if (size(elements) == 0)
            return;

        if (_log.isDebugEnabled())
            _log
                    .debug("Adding contributions to configuration point "
                            + point.getExtensionPointId());

        ContributionImpl c = new ContributionImpl();
        c.setContributingModule(sourceModule);
        c.addElements(elements);

        point.addContribution(c);
    }

    private void addModulesToRegistry(RegistryInfrastructureImpl registry)
    {
        // Add each module to the registry.

        for ( final ModuleImpl module : _modules.values() )
        {
            if ( _log.isDebugEnabled() )
            {
                _log.debug( "Adding module " + module.getModuleId() + " to registry" );
            }
            module.setRegistry( registry );
        }
    }

    private void addImplementationsAndContributions()
    {
        for ( final ModuleDescriptor md : _moduleDescriptors.values() )
        {
            if ( _log.isDebugEnabled() )
            {
                _log.debug( "Adding contributions from module " + md.getModuleId() );
            }

            addImplementations( md );
            addContributions( md );
        }
    }

    private void addImplementations(ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        Module sourceModule = _modules.get(moduleId);

        List implementations = md.getImplementations();
        int count = size(implementations);

        for (int i = 0; i < count; i++)
        {
            ImplementationDescriptor impl = (ImplementationDescriptor) implementations.get(i);

            if (!includeContribution(impl.getConditionalExpression(), sourceModule, impl
                    .getLocation()))
                continue;

            String pointId = impl.getServiceId();
            String qualifiedId = IdUtils.qualify(moduleId, pointId);

            addImplementations(sourceModule, qualifiedId, impl);
        }

    }

    private void addContributions(ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();
        Module sourceModule = _modules.get(moduleId);

        List contributions = md.getContributions();
        int count = size(contributions);

        for (int i = 0; i < count; i++)
        {
            ContributionDescriptor cd = (ContributionDescriptor) contributions.get(i);

            if (!includeContribution(cd.getConditionalExpression(), sourceModule, cd.getLocation()))
                continue;

            String pointId = cd.getConfigurationId();
            String qualifiedId = IdUtils.qualify(moduleId, pointId);

            ConfigurationPointImpl point = _configurationPoints.get(qualifiedId);

            if (point == null)
            {
                _errorHandler.error(_log, ImplMessages.unknownConfigurationPoint(moduleId, cd), cd
                        .getLocation(), null);

                continue;
            }

            if (!point.visibleToModule(sourceModule))
            {
                _errorHandler.error(_log, ImplMessages.configurationPointNotVisible(
                        point,
                        sourceModule), cd.getLocation(), null);
                continue;
            }

            addContributionElements(sourceModule, point, cd.getElements());
        }
    }

    /**
     * Finds and returns a service factory parameter schema. The schema is if required first
     * extended with the gaderian.Assembly schema.
     */
    private Schema findParametersSchema(SchemaImpl schema, Module module, String schemaId,
            Location location)
    {
        final SchemaImpl result = findSchema(schema, module, schemaId, location);

        if (result != null && !result.getElementModel().contains(getAssemblyElementModel()))
        {
            result.addElementModel(getAssemblyElementModel());
        }

        return result;
    }

    private Schema findContributionsSchema(SchemaImpl schema, Module module, String schemaId,
            Location location)
    {
        return findSchema(schema, module, schemaId, location);
    }

    private SchemaImpl findSchema(SchemaImpl schema, Module module, String schemaId,
            Location location)
    {
        SchemaImpl result = null;

        if (schema != null)
        {
            result = schema;
            result.setModule(module);
        }
        else if (schemaId != null)
        {
            String moduleId = module.getModuleId();
            String qualifiedId = IdUtils.qualify(moduleId, schemaId);

            result = getSchema(qualifiedId, moduleId, location);
        }

        return result;
    }

    private SchemaImpl getSchema(String schemaId, String referencingModule, Location reference)
    {
        SchemaImpl schema = (SchemaImpl) _schemas.get(schemaId);

        if (schema == null)
        {
            _errorHandler.error(_log, ImplMessages.unableToResolveSchema(schemaId), reference, null);
        }
        else if (!schema.visibleToModule(referencingModule))
        {
            _errorHandler.error(
                    _log,
                    ImplMessages.schemaNotVisible(schemaId, referencingModule),
                    reference,
                    null);
            schema = null;
        }

        return schema;
    }

    private ElementModel getAssemblyElementModel()
    {
        if (_assemblyElementModel == null)
        {
            final Schema assemblySchema = _schemas.get("gaderian.Assembly");

            if (assemblySchema != null)
                _assemblyElementModel = (ElementModel) assemblySchema.getElementModel().get(0);
        }

        return _assemblyElementModel;
    }

    /**
     * Adds internal service contributions; the contributions provided inplace with the service
     * definition.
     */
    private void addInternalImplementations(Module sourceModule, String pointId,
            ServicePointDescriptor spd)
    {
        InstanceBuilder builder = spd.getInstanceBuilder();
        List interceptors = spd.getInterceptors();

        if (builder == null && interceptors == null)
            return;

        if (builder != null)
            addServiceInstanceBuilder(sourceModule, pointId, builder, true);

        if (interceptors == null)
            return;

        int count = size(interceptors);

        for (int i = 0; i < count; i++)
        {
            InterceptorDescriptor id = (InterceptorDescriptor) interceptors.get(i);
            addInterceptor(sourceModule, pointId, id);
        }
    }

    /**
     * Adds ordinary service contributions.
     */

    private void addImplementations(Module sourceModule, String pointId, ImplementationDescriptor id)
    {
        InstanceBuilder builder = id.getInstanceBuilder();
        List interceptors = id.getInterceptors();

        if (builder != null)
            addServiceInstanceBuilder(sourceModule, pointId, builder, false);

        int count = size(interceptors);
        for (int i = 0; i < count; i++)
        {
            InterceptorDescriptor ind = (InterceptorDescriptor) interceptors.get(i);

            addInterceptor(sourceModule, pointId, ind);
        }
    }

    /**
     * Adds an {@link InstanceBuilder} to a service extension point.
     */
    private void addServiceInstanceBuilder(Module sourceModule, String pointId,
            InstanceBuilder builder, boolean isDefault)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Adding " + builder + " to service extension point " + pointId);
        }

        ServicePointImpl point = _servicePoints.get(pointId);

        if (point == null)
        {
            _errorHandler.error(
                    _log,
                    ImplMessages.unknownServicePoint(sourceModule, pointId),
                    builder.getLocation(),
                    null);
            return;
        }

        if (!point.visibleToModule(sourceModule))
        {
            _errorHandler.error(
                    _log,
                    ImplMessages.servicePointNotVisible(point, sourceModule),
                    builder.getLocation(),
                    null);
            return;
        }

        if (point.getServiceConstructor(isDefault) != null)
        {
            _errorHandler.error(
                    _log,
                    ImplMessages.duplicateFactory(sourceModule, pointId, point),
                    builder.getLocation(),
                    null);

            return;
        }

        point.setServiceModel(builder.getServiceModel());
        point.setServiceConstructor(builder.createConstructor(point, sourceModule), isDefault);
    }

    private void addInterceptor(Module sourceModule, String pointId, InterceptorDescriptor id)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Adding " + id + " to service extension point " + pointId);
        }

        ServicePointImpl point = _servicePoints.get(pointId);

        String sourceModuleId = sourceModule.getModuleId();

        if (point == null)
        {
            _errorHandler.error(_log, ImplMessages.unknownServicePoint(sourceModule, pointId), id
                    .getLocation(), null);

            return;
        }

        if (!point.visibleToModule(sourceModule))
        {
            _errorHandler.error(_log, ImplMessages.servicePointNotVisible(point, sourceModule), id
                    .getLocation(), null);
            return;
        }

        ServiceInterceptorContributionImpl sic = new ServiceInterceptorContributionImpl();

        // Allow the factory id to be unqualified, to refer to an interceptor factory
        // service from within the same module.

        sic.setFactoryServiceId(IdUtils.qualify(sourceModuleId, id.getFactoryServiceId()));
        sic.setLocation(id.getLocation());

        sic.setFollowingInterceptorIds(IdUtils.qualifyList(sourceModuleId, id.getBefore()));
        sic.setPrecedingInterceptorIds(IdUtils.qualifyList(sourceModuleId, id.getAfter()));
        sic.setName(id.getName() != null ? IdUtils.qualify(sourceModuleId, id.getName()) : null);
        sic.setContributingModule(sourceModule);
        sic.setParameters(id.getParameters());

        point.addInterceptorContribution(sic);
    }

    /**
     * Checks that each service has at service constructor.
     */
    private void checkForMissingServices()
    {
        for ( final ServicePointImpl servicePoint : _servicePoints.values() )
        {
            if ( servicePoint.getServiceConstructor() != null )
            {
                continue;
            }
            _errorHandler.error( _log, ImplMessages.missingService( servicePoint ), null, null );
        }
    }

    /**
     * Checks that each configuration extension point has the right number of contributions.
     */

    private void checkContributionCounts()
    {

        for ( final ConfigurationPointImpl configurationPoint : _configurationPoints.values() )
        {
            Occurances expected = configurationPoint.getExpectedCount();

            int actual = configurationPoint.getContributionCount();

            if ( expected.inRange( actual ) )
            {
                continue;
            }

            _errorHandler.error( _log, ImplMessages.wrongNumberOfContributions(
                    configurationPoint,
                    actual,
                    expected ), configurationPoint.getLocation(), null );
        }

    }

    /**
     * Filters a contribution based on an expression. Returns true if the expression is null, or
     * evaluates to true. Returns false if the expression if non-null and evaluates to false, or an
     * exception occurs evaluating the expression.
     * 
     * @param expression
     *            to parse and evaluate
     * @param location
     *            of the expression (used if an error is reported)
     * @since 1.1
     */

    private boolean includeContribution(String expression, Module module, Location location)
    {
        if (expression == null)
        {
            return true;
        }

        if (_conditionalExpressionParser == null)
        {
            _conditionalExpressionParser = new Parser();
        }

        try
        {
            Node node = _conditionalExpressionParser.parse(expression);

            return node.evaluate(new EvaluationContextImpl(module.getClassResolver()));
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(_log, ex.getMessage(), location, ex);

            return false;
        }
    }

    private static int size(Collection c)
    {
        return c == null ? 0 : c.size();
    }
}