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

package org.ops4j.gaderian.strategy.impl;

import org.ops4j.gaderian.impl.MessageFormatter;
import org.ops4j.gaderian.service.ClassFabUtils;

/**
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
class UtilMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(UtilMessages.class);

    static String duplicateRegistration(Class subjectClass)
    {
        return _formatter.format("duplicate-registration", ClassFabUtils
                .getJavaClassName(subjectClass));
    }

    static String strategyNotFound(Class subjectClass)
    {
        return _formatter
                .format("strategy-not-found", ClassFabUtils.getJavaClassName(subjectClass));
    }
}