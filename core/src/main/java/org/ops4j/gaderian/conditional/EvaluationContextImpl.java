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

package org.ops4j.gaderian.conditional;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.util.Defense;

/**
 * @author Howard M. Lewis Ship
 */
public class EvaluationContextImpl implements EvaluationContext
{
    private ClassResolver _resolver;

    public EvaluationContextImpl(ClassResolver resolver)
    {
        Defense.notNull(resolver, "resolver");

        _resolver = resolver;
    }

    /** Simply attempts to retrieve the operating system information from the system property 'os.name'.
     *
     * @param defaultValue The default value to use if the operating system can not be determined
     * @return The system property if defined, otherwise the default value
     * @since Gaderian 1.1
     */
    public String getOperatingSystemProperty( final String defaultValue )
    {
        return System.getProperty( "os.name", defaultValue).toLowerCase();
    }

    public boolean isPropertySet(String propertyName)
    {
        return Boolean.getBoolean(propertyName);
    }

    public boolean doesClassExist(String className)
    {
        try
        {
            _resolver.findClass(className);

            return true;
        }
        catch (ApplicationRuntimeException ex)
        {
            return false;
        }
    }

}