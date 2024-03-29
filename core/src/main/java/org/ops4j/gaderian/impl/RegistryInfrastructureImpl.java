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

import java.beans.Introspector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.ops4j.gaderian.GaderianMessages;
import org.ops4j.gaderian.*;
import org.ops4j.gaderian.internal.ConfigurationPoint;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.internal.ServiceModelFactory;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.internal.ser.ServiceSerializationHelper;
import org.ops4j.gaderian.internal.ser.ServiceSerializationSupport;
import org.ops4j.gaderian.internal.ser.ServiceToken;
import org.ops4j.gaderian.order.Orderer;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.service.ThreadEventNotifier;
import org.ops4j.gaderian.util.Defense;
import org.ops4j.gaderian.util.PropertyUtils;
import org.ops4j.gaderian.util.ToStringBuilder;

/**
 * Implementation of {@link RegistryInfrastructure}.
 *
 * @author Howard Lewis Ship
 */
public final class RegistryInfrastructureImpl implements RegistryInfrastructure,
        ServiceSerializationSupport
{
    private static final String SYMBOL_SOURCES = "gaderian.SymbolSources";

    /**
     * Map of {@link ServicePoint} keyed on fully qualified service id.
     */
    private Map<String,ServicePoint> _servicePoints = new HashMap<String,ServicePoint>();

    /**
     * Map of List (of {@link ServicePoint}, keyed on class name service interface.
     */
    private Map<String,List<ServicePoint>> _servicePointsByInterfaceClassName = new HashMap<String,List<ServicePoint>>();

    /**
     * Map of {@link ConfigurationPoint} keyed on fully qualified configuration id.
     */
    private Map<String,ConfigurationPoint> _configurationPoints = new HashMap<String,ConfigurationPoint>();

    private SymbolSource[] _variableSources;

    private TypeConverter _typeConverter;

    private ErrorHandler _errorHandler;

    private Locale _locale;

    private ShutdownCoordinator _shutdownCoordinator;

    /**
     * Map of {@link org.ops4j.gaderian.internal.ser.ServiceToken}, keyed on service id.
     *
     * @since 1.1
     */

    private Map<String,ServiceToken> _serviceTokens;

    /**
     * Map of {@link ServiceModelFactory}, keyed on service model name, loaded from
     * <code>gaderian.ServiceModels</code> configuration point.
     */
    private Map<String, ServiceModelFactory> _serviceModelFactories;

    private boolean _started = false;

    private boolean _shutdown = false;

    private ThreadEventNotifier _threadEventNotifier;

    private TranslatorManager _translatorManager;

    private final SymbolExpander _expander;

    public RegistryInfrastructureImpl(ErrorHandler errorHandler, Locale locale)
    {
        _errorHandler = errorHandler;
        _locale = locale;

        _translatorManager = new TranslatorManager(this, getTypeConverter(), errorHandler);

        _expander = new SymbolExpander(_errorHandler, this);

    }

    public Locale getLocale()
    {
        return _locale;
    }

    public void addServicePoint(ServicePoint point)
    {
        checkStarted();

        _servicePoints.put(point.getExtensionPointId(), point);

        addServicePointByInterface(point);
    }

    private void addServicePointByInterface(ServicePoint point)
    {
        String key = point.getServiceInterfaceClassName();

        List<ServicePoint> l = _servicePointsByInterfaceClassName.get(key);

        if (l == null)
        {
            l = new LinkedList<ServicePoint>();
            _servicePointsByInterfaceClassName.put(key, l);
        }

        l.add(point);
    }

    public void addConfigurationPoint(ConfigurationPoint point)
    {
        checkStarted();

        _configurationPoints.put(point.getExtensionPointId(), point);
    }

    public ServicePoint getServicePoint(String serviceId, Module module)
    {
        checkShutdown();
        ServicePoint result = _servicePoints.get(serviceId);
        if (result == null)
        {
            if (serviceId.indexOf('.') == -1)
            {
                final List possibleMatches = getMatchingServiceIds(serviceId);
                if (!possibleMatches.isEmpty())
                {
                    final StringBuffer sb = new StringBuffer();
                    for (Iterator i = possibleMatches.iterator(); i.hasNext();)
                    {
                        final String matching = (String) i.next();
                        sb.append('\"');
                        sb.append(matching);
                        sb.append('\"');
                        if (i.hasNext())
                        {
                            sb.append(", ");
                        }
                    }
                    throw new ApplicationRuntimeException(ImplMessages.unqualifiedServicePoint(
                            serviceId,
                            sb.toString()));
                }
            }
            throw new ApplicationRuntimeException(ImplMessages.noSuchServicePoint(serviceId));
        }

        if (!result.visibleToModule(module))
            throw new ApplicationRuntimeException(ImplMessages.serviceNotVisible(serviceId, module));

        return result;
    }

    private List<String> getMatchingServiceIds(String serviceId)
    {
        final List<String> possibleMatches = new LinkedList<String>();
        for (final ServicePoint servicePoint : _servicePoints.values())
        {
            if (servicePoint.getExtensionPointId().equals(servicePoint.getModule().getModuleId() + "." + serviceId))
            {
                possibleMatches.add(servicePoint.getExtensionPointId());
            }
        }
        return possibleMatches;
    }

    public <T> T getService(String serviceId, Class<T> serviceInterface, Module module)
    {
        ServicePoint point = getServicePoint(serviceId, module);

        return point.getService(serviceInterface);
    }

    public <T> T getService(Class<T> serviceInterface, Module module)
    {
        String key = serviceInterface.getName();

        List<ServicePoint> servicePoints = _servicePointsByInterfaceClassName.get(key);

        if (servicePoints == null)
            servicePoints = Collections.emptyList();

        final List<ServicePoint> visibleServicePoints = new ArrayList<ServicePoint>();

        for (final ServicePoint servicePoint : servicePoints)
        {
            if (!servicePoint.visibleToModule(module))
                continue;

            visibleServicePoints.add(servicePoint);
        }

        if (visibleServicePoints.size() == 0)
            throw new ApplicationRuntimeException(ImplMessages
                    .noServicePointForInterface(serviceInterface));

        if (visibleServicePoints.size() > 1)
            throw new ApplicationRuntimeException(ImplMessages.multipleVisibleServicePointsForInterface(
                    serviceInterface,
                    visibleServicePoints));

        return visibleServicePoints.get(0).getService(serviceInterface);
    }

    public ConfigurationPoint getConfigurationPoint(String configurationId, Module module)
    {
        checkShutdown();

        ConfigurationPoint result = _configurationPoints.get(configurationId);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.noSuchConfiguration(configurationId));

        if (!result.visibleToModule(module))
            throw new ApplicationRuntimeException(ImplMessages.configurationNotVisible(
                    configurationId,
                    module));

        return result;
    }

    public List getConfiguration(String configurationId, Module module)
    {
        ConfigurationPoint point = getConfigurationPoint(configurationId, module);

        return point.getElements();
    }

    public boolean isConfigurationMappable(String configurationId, Module module)
    {
        ConfigurationPoint point = getConfigurationPoint(configurationId, module);

        return point.areElementsMappable();
    }

    public Map getConfigurationAsMap(String configurationId, Module module)
    {
        ConfigurationPoint point = getConfigurationPoint(configurationId, module);

        return point.getElementsAsMap();
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("locale", _locale);

        return builder.toString();
    }

    public String expandSymbols(String text, Location location)
    {
        return _expander.expandSymbols(text, location);
    }

    public <T> T stringToObject(Module module, String input, Class<T> target, Location location)
    {
        return getTypeConverter().stringToObject( module, input, target, location );
    }

    public String valueForSymbol(String name)
    {
        checkShutdown();

        SymbolSource[] sources = getSymbolSources();

        for (SymbolSource source : sources)
        {
            String value = source.valueForSymbol(name);

            if (value != null)
            {
                return value;
            }
        }

        return null;
    }

    private synchronized SymbolSource[] getSymbolSources()
    {
        if (_variableSources != null)
            return _variableSources;

        List<SymbolSourceContribution> contributions = getConfiguration(SYMBOL_SOURCES, null);

        Log log = LogFactory.getLog(SYMBOL_SOURCES);
        Orderer o = new Orderer(log, _errorHandler, ImplMessages.symbolSourceContribution());

        for (final SymbolSourceContribution symbolSourceContribution : contributions)
        {
            // check that there is actually a source set
            // TODO: this checks only that source is not null; it can't check for a overwritten source
            // (if both class="..." and service-id="..." are specified, service-id silently wins)
            if (symbolSourceContribution.getSource() == null)
            {
                _errorHandler.error(log, ImplMessages.noSymbolSourceImplementation(symbolSourceContribution.getName()),
                        symbolSourceContribution.getLocation(), null);
            }
            else
            {
                o.add(symbolSourceContribution, symbolSourceContribution.getName(), symbolSourceContribution.getPrecedingNames(), symbolSourceContribution.getFollowingNames());
            }
        }

        List sources = o.getOrderedObjects();

        int count = sources.size();

        _variableSources = new SymbolSource[count];

        for (int j = 0; j < count; j++)
        {
            SymbolSourceContribution c = (SymbolSourceContribution) sources.get(j);
            _variableSources[j] = c.getSource();
        }

        return _variableSources;
    }

    @SuppressWarnings({ "unchecked" })
    private synchronized TypeConverter getTypeConverter()
    {
        if (_typeConverter == null)
        {
            _typeConverter = new TypeConverterImpl( this );
        }
        return _typeConverter;
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    /**
     * Invokes {@link ShutdownCoordinator#shutdown()}, then releases the coordinator, modules and
     * variable sources.
     */
    public synchronized void shutdown()
    {
        checkShutdown();

        ServiceSerializationHelper.setServiceSerializationSupport(null);

        // Allow service implementations and such to shutdown.

        ShutdownCoordinator coordinatorService = (ShutdownCoordinator) getService(
                "gaderian.ShutdownCoordinator",
                ShutdownCoordinator.class,
                null);

         coordinatorService.shutdown();

        // TODO: Should this be moved earlier?

        _shutdown = true;

        // Shutdown infrastructure items, such as proxies.

        _shutdownCoordinator.shutdown();

        _servicePoints = null;
        _servicePointsByInterfaceClassName = null;
        _configurationPoints = null;
        _shutdownCoordinator = null;
        _variableSources = null;
        _serviceModelFactories = null;
        _threadEventNotifier = null;
        _serviceTokens = null;

        // It is believed that the cache held by PropertyUtils can affect application shutdown
        // and reload in some servlet containers (such as Tomcat); this should clear that up.

        PropertyUtils.clearCache();

        synchronized (Gaderian.INTROSPECTOR_MUTEX)
        {
            Introspector.flushCaches();
        }
    }

    /**
     * Technically, this should be a synchronized method, but the _shutdown variable hardly ever
     * changes, and the consequences are pretty minimal. See HIVEMIND-104.
     */

    private void checkShutdown()
    {
        if (_shutdown)
            throw new ApplicationRuntimeException(GaderianMessages.registryShutdown());
    }

    private void checkStarted()
    {
        if (_started)
            throw new IllegalStateException(ImplMessages.registryAlreadyStarted());
    }

    /**
     * Starts up the Registry after all service and configuration points have been defined. This
     * locks down the Registry so that no further extension points may be added. This method may
     * only be invoked once.
     * <p>
     * This instance is stored into
     * {@link ServiceSerializationHelper#setServiceSerializationSupport(ServiceSerializationSupport)}.
     * This may cause errors (and incorrect behavior) if multiple Registries exist in a single JVM.
     * <p>
     * In addition, the service <code>gaderian.Startup</code> is obtained and <code>run()</code>
     * is invoked on it. This allows additional startup, provided in the
     * <code>gaderian.Startup</code> configuration point, to be executed.
     */
    public void startup()
    {
        checkStarted();

        ServiceSerializationHelper.setServiceSerializationSupport(this);

        _started = true;

        Runnable startup = (Runnable) getService("gaderian.Startup", Runnable.class, null);

        startup.run();
    }

    public synchronized ServiceModelFactory getServiceModelFactory(String name)
    {
        if (_serviceModelFactories == null)
            readServiceModelFactories();

        ServiceModelFactory result = _serviceModelFactories.get(name);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.unknownServiceModel(name));

        return result;
    }

    private void readServiceModelFactories()
    {
        final List<ServiceModelContribution> serviceModelContributions = getConfiguration("gaderian.ServiceModels", null);

        _serviceModelFactories = new HashMap<String, ServiceModelFactory>();

        for ( ServiceModelContribution serviceModelContribution : serviceModelContributions )
        {
            String name = serviceModelContribution.getName();
            _serviceModelFactories.put(name, serviceModelContribution.getFactory());
        }
    }

    public synchronized void cleanupThread()
    {
        if (_threadEventNotifier == null)
            _threadEventNotifier = (ThreadEventNotifier) getService(
                    "gaderian.ThreadEventNotifier",
                    ThreadEventNotifier.class,
                    null);

        _threadEventNotifier.fireThreadCleanup();
    }

    public boolean containsConfiguration(String configurationId, Module module)
    {
        checkShutdown();

        ConfigurationPoint result =  _configurationPoints.get(configurationId);

        return result != null && result.visibleToModule(module);
    }

    public boolean containsService(Class serviceInterface, Module module)
    {
        return getServiceCount( serviceInterface, module ) == 1;
    }

    public int getServiceCount(Class serviceInterface, Module module)
    {
        checkShutdown();

        String key = serviceInterface.getName();

        List<ServicePoint> servicePoints = _servicePointsByInterfaceClassName.get(key);

        if (servicePoints == null)
            return 0;

        int count = 0;

        for (final ServicePoint servicePoint : servicePoints)
        {
            if (servicePoint.visibleToModule(module))
            {
                count++;
            }
        }

        return count;
    }

    public boolean containsService(String serviceId, Class serviceInterface, Module module)
    {
        checkShutdown();

        ServicePoint point = _servicePoints.get(serviceId);

        if (point == null)
            return false;

        return point.visibleToModule(module)
                && point.getServiceInterface().equals(serviceInterface);
    }

    public ErrorHandler getErrorHander()
    {
        return _errorHandler;
    }

    public Translator getTranslator(String constructor)
    {
        return _translatorManager.getTranslator(constructor);
    }

    public Object getServiceFromToken(ServiceToken token)
    {
        Defense.notNull(token, "token");

        checkShutdown();

        String serviceId = token.getServiceId();

        ServicePoint sp = _servicePoints.get(serviceId);

        return sp.getService(Object.class);
    }

    public synchronized ServiceToken getServiceTokenForService(String serviceId)
    {
        Defense.notNull(serviceId, "serviceId");

        checkShutdown();

        if (_serviceTokens == null)
            _serviceTokens = new HashMap<String,ServiceToken>();

        ServiceToken result = _serviceTokens.get(serviceId);

        if (result == null)
        {
            result = new ServiceToken(serviceId);
            _serviceTokens.put(serviceId, result);
        }

        return result;
    }

    /**
     * Sets the current RI up as the ServiceSerializationSupport. Any service proxy tokens that are
     * de-serialized will find their proxies within this Registry.
     *
     * @since 1.1
     */

    public void setupThread()
    {
        ServiceSerializationHelper.setServiceSerializationSupport(this);
    }

    public Module getModule(String moduleId)
    {
        for (final ServicePoint servicePoint : _servicePoints.values())
        {
            if (servicePoint.getModule().getModuleId().equals(moduleId))
            {
                return servicePoint.getModule();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ops4j.gaderian.internal.RegistryInfrastructure#getServiceIds(java.lang.Class)
     */
    public List<String> getServiceIds(Class serviceInterface)
    {
        final List<String> serviceIds = new LinkedList<String>();
        if( serviceInterface == null )
        {
            return serviceIds;
        }
        for (final ServicePoint servicePoint : _servicePoints.values())
        {
            if (serviceInterface.getName().equals(servicePoint.getServiceInterfaceClassName()) && servicePoint.visibleToModule(null))
            {
                serviceIds.add(servicePoint.getExtensionPointId());
            }

        }
        return serviceIds;
    }
}
