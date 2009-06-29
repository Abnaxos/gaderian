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

import org.ops4j.gaderian.AssemblyParameters;
import org.ops4j.gaderian.ClassResolver;

/**
 * {@link org.ops4j.gaderian.service.impl.BuilderFacet} whose value is the
 * {@link org.ops4j.gaderian.ClassResolver} of the contributing module.
 * 
 * @author Howard Lewis Ship
 */
public class BuilderClassResolverFacet extends BuilderFacet
{

    public Object getFacetValue(AssemblyParameters assemblyParameters, Class targetType)
    {
        return assemblyParameters.getInvokingModule().getClassResolver();
    }

    public boolean isAssignableToType(AssemblyParameters assemblyParameters, Class targetType)
    {
        return targetType == ClassResolver.class;
    }

    protected String getDefaultPropertyName()
    {
        return "classResolver";
    }

    /** @since 1.1 */
    public boolean canAutowireConstructorParameter()
    {
        return true;
    }
}