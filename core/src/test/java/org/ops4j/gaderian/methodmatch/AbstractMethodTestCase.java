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

package org.ops4j.gaderian.methodmatch;

import java.lang.reflect.Method;

import junit.framework.AssertionFailedError;

import org.ops4j.gaderian.service.MethodSignature;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Base class for a number of tests related to {@link org.ops4j.gaderian.methodmatch.MethodMatcher}.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractMethodTestCase extends GaderianCoreTestCase
{
    protected MethodSignature getMethodSignature(Class target, String name)
    {
        Method[] methods = target.getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
                return new MethodSignature(methods[i]);
        }

        throw new AssertionFailedError(
            "Class " + target.getName() + " does not contain a method named: '" + name + "'.");
    }

    protected MethodSignature getMethodSignature(Object object, String name)
    {
        return getMethodSignature(object.getClass(), name);
    }

}
