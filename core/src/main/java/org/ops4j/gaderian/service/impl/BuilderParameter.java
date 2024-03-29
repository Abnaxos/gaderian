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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ops4j.gaderian.AssemblyParameters;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.impl.BaseLocatable;

/**
 * Parameter object used with {@link org.ops4j.gaderian.service.impl.BuilderFactory}.
 * <p>
 * A BuilderParameter is a wrapper around an {@link org.ops4j.gaderian.service.impl.BuilderFacet}
 * 
 * @author Howard Lewis Ship
 */
public class BuilderParameter extends BaseLocatable
{
    private String _className;

    private List<BuilderFacet> _parameters = new ArrayList();

    /** @since 1.1 */
    private Map<Class,BuilderFacet> _typeFacetMap = new HashMap<Class,BuilderFacet>();

    private AssemblyInstructionImpl _assemblyInstruction = new AssemblyInstructionImpl();

    public void setClassName(String string)
    {
        _className = string;
    }

    public String getClassName()
    {
        return _className;
    }

    public void addParameter(BuilderFacet facet)
    {
        _parameters.add(facet);
    }

    public List<BuilderFacet> getParameters()
    {
        return _parameters;
    }

    public void addProperty(BuilderFacet facet)
    {
        _assemblyInstruction.addProperty(facet);
    }

    public List getProperties()
    {
        return _assemblyInstruction.getProperties();
    }

    public void addEventRegistration(EventRegistration registration)
    {
        _assemblyInstruction.addEventRegistration(registration);
    }

    public List getEventRegistrations()
    {
        return _assemblyInstruction.getEventRegistrations();
    }

    public void setInitializeMethod(String string)
    {
        _assemblyInstruction.setInitializeMethod(string);
    }

    public String getInitializeMethod()
    {
        return _assemblyInstruction.getInitializeMethod();
    }

    public void setAutowireServices(boolean autowireServices)
    {
        _assemblyInstruction.setAutowireServices(autowireServices);
    }

    public boolean getAutowireServices()
    {
        return _assemblyInstruction.getAutowireServices();
    }

    public void setPerformNullChecks(boolean performNullChecks)
    {
        _assemblyInstruction.setPerformNullChecks(performNullChecks);
    }
    
    public boolean getPerformNullChecks()
    {
        return _assemblyInstruction.getPerformNullChecks();
    }
    
    /** @since 1.1 */
    public BuilderFacet getFacetForType(ServiceImplementationFactoryParameters factoryParameters,
            Class targetType)
    {
        BuilderFacet result = _typeFacetMap.get(targetType);

        if (result == null)
        {
            for (Iterator i = getProperties().iterator(); i.hasNext();)
            {
                BuilderFacet facet = (BuilderFacet) i.next();

                if (facet.canAutowireConstructorParameter()
                        && facet.isAssignableToType(factoryParameters, targetType))
                {
                    result = facet;
                    break;
                }
            }

            _typeFacetMap.put(targetType, result);
        }

        return result;
    }

    public void assemble(Object service, AssemblyParameters assemblyParameters)
    {
        _assemblyInstruction.assemble(service, assemblyParameters);
    }

    public void setLocation(Location location)
    {
        super.setLocation(location);
        _assemblyInstruction.setLocation(location);
    }

}