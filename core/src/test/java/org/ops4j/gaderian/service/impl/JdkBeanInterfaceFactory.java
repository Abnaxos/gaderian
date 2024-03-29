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

package org.ops4j.gaderian.service.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.ops4j.gaderian.ServiceImplementationFactory;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;

/**
 * 
 * @author James Carman
 *
 */
public class JdkBeanInterfaceFactory implements ServiceImplementationFactory {

	public Object createCoreServiceImplementation(ServiceImplementationFactoryParameters factoryParameters) {
		return createJdkBean();
	}

	/**
	 * @return
	 */
	public static BeanInterface createJdkBean() {
		return ( BeanInterface )Proxy.newProxyInstance( BeanInterface.class.getClassLoader(), new Class[] { BeanInterface.class }, new InvocationHandler()
				{
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return "Hello, World!";
					}	
				} );
	}
	

}
