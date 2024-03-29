// Copyright 2006 The Apache Software Foundation
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

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.AssemblyParameters;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.util.Defense;

/**
 * Wrapper around a {@link org.ops4j.gaderian.internal.ServicePoint} and the
 * {@link Module invoking module}, passed to a {@link org.ops4j.gaderian.AssemblyInstruction}.
 * 
 * @author Knut Wannheden
 * @since 1.2
 */
public class AssemblyParametersImpl implements AssemblyParameters
{
    private ServicePoint _servicePoint;

    private Module _invokingModule;

    public AssemblyParametersImpl(ServicePoint servicePoint, Module invokingModule)
    {
        Defense.notNull(servicePoint, "servicePoint");
        Defense.notNull(invokingModule, "invokingModule");

        _servicePoint = servicePoint;
        _invokingModule = invokingModule;
    }

    public String getServiceId()
    {
        return _servicePoint.getExtensionPointId();
    }

    public Class getServiceInterface()
    {
        return _servicePoint.getServiceInterface();
    }

    public Log getLog()
    {
        return _servicePoint.getLog();
    }

    public ErrorLog getErrorLog()
    {
        return _servicePoint.getErrorLog();
    }

    public Module getInvokingModule()
    {
        return _invokingModule;
    }
}
