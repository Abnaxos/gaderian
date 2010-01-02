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
public class ModuleDefinitionImpl implements ModuleDefinition
{
    public List<ServicePointDefinition> getServicePointDefinitions()
    {
        return null;
    }

    public String getModuleId()
    {
        return null;
    }

    public boolean constainsService( final Class serviceInterface )
    {
        return false;
    }

    public <S> S getService( final String serviceId, final Class<S> serviceInterface )
    {
        return null;
    }

    public <S> S getService( final Class<S> serviceInterface )
    {
        return null;
    }

    public ServicePointDefinition getServicePointDefinition( final String serviceId )
    {
        return null;
    }

    public ClassResolver getClassResolver()
    {
        return null;
    }

    public Class resolveType( final String type )
    {
        return null;
    }

    public List getConfiguration( final String configurationId )
    {
        return null;
    }

    public boolean isConfigurationMappable( final String configurationId )
    {
        return false;
    }

    public Map getConfigurationAsMap( final String configurationId )
    {
        return null;
    }

    public Messages getMessages()
    {
        return null;
    }

    public Translator getTranslator( final String translator )
    {
        return null;
    }

    public ServiceModelFactory getServiceModelFactory( final String name )
    {
        return null;
    }

    public Locale getLocale()
    {
        return null;
    }

    public String expandSymbols( final String input, final Location location )
    {
        return null;
    }

    public ErrorHandler getErrorHandler()
    {
        return null;
    }
}
