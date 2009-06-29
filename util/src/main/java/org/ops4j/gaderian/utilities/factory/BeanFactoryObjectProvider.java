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

package org.ops4j.gaderian.utilities.factory;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.utilities.BeanFactory;
import org.ops4j.gaderian.service.ObjectProvider;

/**
 * An {@link org.ops4j.gaderian.service.ObjectProvider}
 * that references a named (or named and initialized) bean from a
 * {@link org.ops4j.gaderian.utilities.BeanFactory}.  The translator string is of the form:
 * <code>service-id:name[,initializer]</code>. That is, the text after the colon
 * is an initializer passed to {@link org.ops4j.gaderian.utilities.BeanFactory#get(String)}.
 *
 * @author Howard Lewis Ship
 */
public class BeanFactoryObjectProvider implements ObjectProvider
{
    public Object provideObject(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (Gaderian.isBlank(inputValue))
            return null;

        int colonx = inputValue.indexOf(':');

        if (colonx < 0)
            throw new ApplicationRuntimeException(
                FactoryMessages.invalidBeanTranslatorFormat(inputValue),
                location,
                null);

        String serviceId = inputValue.substring(0, colonx);

        if (serviceId.length() == 0)
            throw new ApplicationRuntimeException(
                FactoryMessages.invalidBeanTranslatorFormat(inputValue),
                location,
                null);

        String locator = inputValue.substring(colonx + 1);

        if (locator.length() == 0)
            throw new ApplicationRuntimeException(
                FactoryMessages.invalidBeanTranslatorFormat(inputValue),
                location,
                null);

        BeanFactory f = (BeanFactory) contributingModule.getService(serviceId, BeanFactory.class);

        return f.get(locator);
    }

}
