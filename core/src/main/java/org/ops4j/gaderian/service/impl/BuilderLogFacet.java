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

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.AssemblyParameters;

/**
 * Facet whose value is the <code>Log</code> instance for the service (which is based on the
 * service id, not the class name).
 * 
 * @author Howard Lewis Ship
 */
public class BuilderLogFacet extends BuilderFacet
{
    public Object getFacetValue(AssemblyParameters assemblyParameters, Class targetType)
    {
        return assemblyParameters.getLog();
    }

    public boolean isAssignableToType(AssemblyParameters assemblyParameters, Class targetType)
    {
        return targetType == Log.class;
    }

    protected String getDefaultPropertyName()
    {
        return "log";
    }

    /** @since 1.1 */
    public boolean canAutowireConstructorParameter()
    {
        return true;
    }
}