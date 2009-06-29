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

package org.ops4j.gaderian;

import org.ops4j.gaderian.impl.MessageFormatter;
import org.ops4j.gaderian.service.ClassFabUtils;

/**
 * Used to format general-purpose messages used in code within Gaderian.
 *
 * @author Howard Lewis Ship
 */
public class GaderianMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(GaderianMessages.class);

    public static String unimplementedMethod(Object instance, String methodName)
    {
        return _formatter.format("unimplemented-method", instance.getClass().getName(), methodName);
    }

    public static String registryShutdown()
    {
        return _formatter.getMessage("registry-shutdown");
    }

    public static String unknownLocation()
    {
        return _formatter.getMessage("unknown-location");
    }

    /** @since 1.1 */
    public static String paramNotNull(String parameterName)
    {
        return _formatter.format("param-not-null", parameterName);
    }

    /** @since 1.1 */
    public static String paramWrongType(String parameterName, Object parameter, Class expectedType)
    {
        return _formatter.format("param-wrong-type", parameterName, ClassFabUtils
                .getJavaClassName(parameter.getClass()), ClassFabUtils
                .getJavaClassName(expectedType));
    }
}