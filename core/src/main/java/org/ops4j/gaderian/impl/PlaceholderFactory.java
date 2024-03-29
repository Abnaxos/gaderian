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

import org.ops4j.gaderian.DefaultImplementationBuilder;
import org.ops4j.gaderian.ServiceImplementationFactory;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;

/**
 * Wrapper around {@link org.ops4j.gaderian.DefaultImplementationBuilder} that is used to
 * create default implementations of services.
 * 
 * @author Howard Lewis Ship
 */
public class PlaceholderFactory extends BaseLocatable implements ServiceImplementationFactory
{
    private DefaultImplementationBuilder _builder;

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters)
    {
        return _builder.buildDefaultImplementation(factoryParameters.getServiceInterface());
    }

    public void setBuilder(DefaultImplementationBuilder builder)
    {
        _builder = builder;
    }

}