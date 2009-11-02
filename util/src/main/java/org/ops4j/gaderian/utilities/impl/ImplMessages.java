// Copyright 2004-2006 The Apache Software Foundation
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

package org.ops4j.gaderian.utilities.impl;

import javax.naming.Context;

import org.ops4j.gaderian.impl.MessageFormatter;

/**
 * Messages for the lib.impl package.
 * 
 * @author Howard Lewis Ship
 */
final class ImplMessages
{
    private static final MessageFormatter FORMATTER = new MessageFormatter(ImplMessages.class);

    static String ejbProxyDescription(String serviceId, Class serviceInterface, String jndiName)
    {
        return FORMATTER.format(
                "ejb-proxy-description",
                serviceId,
                serviceInterface.getName(),
                jndiName);
    }

    static String unableToLookup(String name, Context context)
    {
        return FORMATTER.format("unable-to-lookup", name, context);
    }

    static String noObject(String name, Class expectedType)
    {
        return FORMATTER.format("no-object", name, expectedType);
    }

    static String wrongType(String name, Object object, Class expectedType)
    {
        return FORMATTER.format("wrong-type", name, object, expectedType);
    }

    static String coordinatorLocked(String methodName)
    {
        return FORMATTER.format("coordinator-locked", methodName);
    }

}