package org.ops4j.gaderian.registry;

import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.Messages;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.internal.ServiceModelFactory;
import org.ops4j.gaderian.schema.Translator;

/**
 * @author Johan Lindquist
 */
public interface ModuleDefinition
{
    public List<ServicePointDefinition> getServicePointDefinitions();

    public String getModuleId();

    public boolean constainsService(Class serviceInterface);

    public <S> S getService(String serviceId, Class<S> serviceInterface);

    public <S> S getService(Class<S> serviceInterface);

    public ServicePointDefinition getServicePointDefinition(String serviceId);

    public ClassResolver getClassResolver();

    public Class resolveType(String type);

    public List getConfiguration(String configurationId);

    public boolean isConfigurationMappable(String configurationId);

    public Map getConfigurationAsMap(String configurationId);

    public Messages getMessages();

    public Translator getTranslator(String translator);

    public ServiceModelFactory getServiceModelFactory(String name);

    public Locale getLocale();

    public String expandSymbols(String input, Location location);

    public ErrorHandler getErrorHandler();

}
