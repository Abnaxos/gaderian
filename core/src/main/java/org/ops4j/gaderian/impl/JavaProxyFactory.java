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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.ops4j.gaderian.ServiceImplementationFactory;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;

/**
 * @author Johan Lindquist
 */
public class JavaProxyFactory implements ServiceImplementationFactory
{
    /** Creates a new <code>Proxy</code> instance, wrapping a <code>InvocationHandler</code> instance in it.
     *
     * @param factoryParameters The factory parameters.
     * @return A new <code>Proxy</code> instance.
     */
    public Object createCoreServiceImplementation(ServiceImplementationFactoryParameters factoryParameters)
    {
        Class serviceInterface = factoryParameters.getServiceInterface();
        InvocationHandler invokactionHandler = (InvocationHandler) factoryParameters.getParameters().get(0);
        return Proxy.newProxyInstance(factoryParameters.getInvokingModule().getClassResolver().getClassLoader(), new Class[]{serviceInterface}, invokactionHandler);
    }


}