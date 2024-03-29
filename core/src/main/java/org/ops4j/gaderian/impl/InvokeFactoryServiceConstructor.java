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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.AssemblyInstruction;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Occurances;
import org.ops4j.gaderian.ServiceImplementationFactory;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServiceImplementationConstructor;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.schema.Schema;

/**
 * Constructs a new service by invoking methods on another service (which implements the
 * {@link org.ops4j.gaderian.ServiceImplementationFactory} interface.
 *
 * @author Howard Lewis Ship
 */
public final class InvokeFactoryServiceConstructor extends BaseLocatable implements
        ServiceImplementationConstructor
{
    private String _factoryServiceId;

    private ServicePoint _serviceExtensionPoint;

    private Module _contributingModule;

    /**
     * List of {@link org.ops4j.gaderian.Element}, the raw XML parameters.
     */
    private List _parameters;

    /**
     * The factory service to be invoked.
     */
    private ServiceImplementationFactory _factory;

    /**
     * The parameters converted to objects as per the factory's parameter schema.
     */
    private List _convertedFactoryParameters;

    /**
     * The assembly instructions converted to objects as per the factory's parameter schema.
     */
    private List<AssemblyInstruction> _convertedAssemblyInstructions;

    public Object constructCoreServiceImplementation()
    {
        setupFactoryAndParameters();

        try
        {
            ServiceImplementationFactoryParameters factoryParameters = new ServiceImplementationFactoryParametersImpl(
                    _serviceExtensionPoint, _contributingModule, _convertedFactoryParameters);

            final Object result = _factory.createCoreServiceImplementation(factoryParameters);

            if (_convertedAssemblyInstructions != null)
            {
                for ( final AssemblyInstruction convertedAssemblyInstruction : _convertedAssemblyInstructions )
                {
                    convertedAssemblyInstruction.assemble( result, factoryParameters );
                }
            }

            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

    // A lot of changes to synchronization and service construction occured between 1.1 and 1.1.1;
    // this method was split off and made synchronized ... otherwise, it was possible for the
    // pooled or threaded services to get into a potential race condition through this code.

    private synchronized void setupFactoryAndParameters()
    {
        if (_factory == null)
        {
            final ServicePoint factoryPoint = retrieveServiceFactory();

            final Occurances expected = factoryPoint.getParametersCount();

            _factory = factoryPoint.getService(ServiceImplementationFactory.class);

            final Schema schema = factoryPoint.getParametersSchema();

            final ErrorLog errorLog = _serviceExtensionPoint.getErrorLog();

            final SchemaProcessorImpl processor = new SchemaProcessorImpl(errorLog, schema);

            processor.process(_parameters, _contributingModule);

            _convertedFactoryParameters = processor.getElements();

            _convertedAssemblyInstructions = extractAssemblyInstructions(_convertedFactoryParameters);

            checkParameterCounts(errorLog, expected);
        }
    }

    private ServicePoint retrieveServiceFactory()
    {
        try
        {
            return _contributingModule.getServicePoint(_factoryServiceId);
        }
        catch (Exception e)
        {
            throw new ApplicationRuntimeException(ImplMessages.errorResolvingServiceImplementationFactory(_factoryServiceId, _contributingModule.getModuleId()), _contributingModule.getLocation(), e);
        }
    }

    /**
     * Extracts and removes the {@link AssemblyInstruction} objects from the converted parameter
     * elements.
     */
    private List<AssemblyInstruction> extractAssemblyInstructions(List parameters)
    {
        List<AssemblyInstruction> result = null;

        for (Iterator i = parameters.iterator(); i.hasNext();)
        {
            Object parameter = i.next();

            if (parameter instanceof AssemblyInstruction)
            {
                if (result == null)
                {
                    result = new ArrayList<AssemblyInstruction>();
                }

                result.add( ( AssemblyInstruction ) parameter );

                i.remove();
            }
        }

        return result;
    }

    /**
     * Checks that the number of parameter elements matches the expected count.
     */
    private void checkParameterCounts(ErrorLog log, Occurances expected)
    {
        int actual = _convertedFactoryParameters.size();

        if (expected.inRange(actual))
        {
            return;
        }

        String message = ImplMessages.wrongNumberOfParameters(_factoryServiceId, actual, expected);

        log.error(message, getLocation(), null);
    }

    public Module getContributingModule()
    {
        return _contributingModule;
    }

    public void setContributingModule(Module module)
    {
        _contributingModule = module;
    }

    public List getParameters()
    {
        return _parameters;
    }

    public ServicePoint getServiceExtensionPoint()
    {
        return _serviceExtensionPoint;
    }

    public void setParameters(List list)
    {
        _parameters = list;
    }

    public void setFactoryServiceId(String string)
    {
        _factoryServiceId = string;
    }

    public void setServiceExtensionPoint(ServicePoint point)
    {
        _serviceExtensionPoint = point;
    }

}
