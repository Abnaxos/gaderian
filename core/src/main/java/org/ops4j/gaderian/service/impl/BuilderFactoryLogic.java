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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.util.ConstructorUtils;

/**
 * Created by {@link org.ops4j.gaderian.service.impl.BuilderFactory} for each service to be
 * created; encapsulates all the direct and indirect parameters used to construct a service.
 *
 * @author Howard Lewis Ship
 */
public class BuilderFactoryLogic
{
    /**
     * @since 1.1
     */
    private ServiceImplementationFactoryParameters _factoryParameters;

    private String _serviceId;

    private BuilderParameter _parameter;

    private Module _contributingModule;
    private boolean _annotationsEnabled;
    public Class _serviceClass;

    public BuilderFactoryLogic( ServiceImplementationFactoryParameters factoryParameters, BuilderParameter parameter )
    {
        _factoryParameters = factoryParameters;
        _parameter = parameter;

        _serviceId = factoryParameters.getServiceId();

        _contributingModule = factoryParameters.getInvokingModule();

        // Resolve the service class 
        _serviceClass = _contributingModule.resolveType( _parameter.getClassName() );

        // Check whether annotations are available in this class loader or not
        _annotationsEnabled = _contributingModule.getClassResolver().checkForClass( "org.ops4j.gaderian.annotations.validation.Required" ) != null;

    }

    public Object createService()
    {
        try
        {
            Object result = instantiateCoreServiceInstance();

            _parameter.assemble( result, _factoryParameters );

            // We only do validation if we are not skipping the check && annotations are enabled
            if ( _parameter.getPerformNullChecks() && _annotationsEnabled )
            {
                // Validate the constructed instance - annotations and the like
                ServiceInstanceUtils.validate( _serviceClass, result );
            }

            return result;
        }
        catch ( Exception ex )
        {
            throw new ApplicationRuntimeException( ServiceMessages.failureBuildingService(
                    _serviceId,
                    ex ), _parameter.getLocation(), ex );
        }
    }

    private Object instantiateCoreServiceInstance()
    {
        // Class serviceClass = _contributingModule.resolveType( _parameter.getClassName() );

        if ( _serviceClass.isInterface() || ( ( _serviceClass.getModifiers() & Modifier.ABSTRACT ) != 0 ) )
        {
            throw new ApplicationRuntimeException( ServiceMessages.absractClass( _serviceClass.getName() ), _parameter.getLocation(), null );
        }

        // Allow the decorator to decorate for lifecycle calls etc
        Class serviceClass = ClassDecorator.decorate( _serviceClass, _parameter, _annotationsEnabled );

        final List parameters = _parameter.getParameters();

        final Object coreServiceInstance;
        if ( _parameter.getAutowireServices() && parameters.isEmpty() )
        {
            return instantiateConstructorAutowiredInstance( serviceClass );
        }
        else
        {
            return instantiateExplicitConstructorInstance( serviceClass, parameters );
        }

    }

    private Object instantiateExplicitConstructorInstance( Class serviceClass, List builderParameters )
    {
        final int numberOfParams = builderParameters.size();

        final List<Constructor> constructorCandidates = ConstructorUtils.getConstructorsOfLength(
                serviceClass,
                numberOfParams );

        outer:
        for ( final Constructor candidate : constructorCandidates )
        {
            Class[] parameterTypes = candidate.getParameterTypes();

            Object[] parameters = new Object[parameterTypes.length];

            for ( int i = 0; i < numberOfParams; i++ )
            {
                BuilderFacet facet = ( BuilderFacet ) builderParameters.get( i );

                if ( !facet.isAssignableToType( _factoryParameters, parameterTypes[ i ] ) )
                {
                    continue outer;
                }

                parameters[ i ] = facet.getFacetValue( _factoryParameters, parameterTypes[ i ] );
            }

            return ConstructorUtils.invoke( candidate, parameters );
        }

        throw new ApplicationRuntimeException( ServiceMessages.unableToFindAutowireConstructor(), _parameter.getLocation(), null );
    }

    private Object instantiateConstructorAutowiredInstance( Class serviceClass )
    {
        final List<Constructor> serviceConstructorCandidates = getOrderedServiceConstructors( serviceClass );

        outer:
        for ( final Constructor candidate : serviceConstructorCandidates )
        {
            Class[] parameterTypes = candidate.getParameterTypes();

            Object[] parameters = new Object[parameterTypes.length];

            for ( int i = 0; i < parameters.length; i++ )
            {
                BuilderFacet facet = _parameter.getFacetForType( _factoryParameters, parameterTypes[ i ] );

                if ( facet != null && facet.canAutowireConstructorParameter() )
                {
                    parameters[ i ] = facet.getFacetValue( _factoryParameters, parameterTypes[ i ] );
                }
                else if ( _contributingModule.containsService( parameterTypes[ i ] ) )
                {
                    parameters[ i ] = _contributingModule.getService( parameterTypes[ i ] );
                }
                else
                {
                    continue outer;
                }
            }

            return ConstructorUtils.invoke( candidate, parameters );
        }

        throw new ApplicationRuntimeException( ServiceMessages.unableToFindAutowireConstructor(),
                _parameter.getLocation(), null );
    }

    private List<Constructor> getOrderedServiceConstructors( Class serviceClass )
    {
        List<Constructor> orderedInterfaceConstructors = new ArrayList<Constructor>();

        Constructor[] constructors = serviceClass.getDeclaredConstructors();

        outer:
        for ( Constructor constructor : constructors )
        {
            if ( !Modifier.isPublic( constructor.getModifiers() ) )
            {
                continue;
            }

            Class[] parameterTypes = constructor.getParameterTypes();

            if ( parameterTypes.length > 0 )
            {
                Set<Class> seenTypes = new HashSet<Class>();

                for ( Class parameterType : parameterTypes )
                {
                    if ( !parameterType.isInterface() || seenTypes.contains( parameterType ) )
                    {
                        continue outer;
                    }
                    seenTypes.add( parameterType );
                }
            }

            orderedInterfaceConstructors.add( constructor );
        }

        Collections.sort( orderedInterfaceConstructors, new Comparator<Constructor>()
        {
            public int compare( Constructor c1, Constructor c2 )
            {
                return c2.getParameterTypes().length - c1.getParameterTypes().length;
            }
        } );

        return orderedInterfaceConstructors;
    }

}