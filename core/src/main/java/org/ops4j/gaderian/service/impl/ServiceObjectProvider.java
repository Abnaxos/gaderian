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

import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.service.ObjectProvider;

/**
 * An {@link org.ops4j.gaderian.service.ObjectProvider} that provides access to services. Returns
 * null if the input is null. Deployed with the prefix "service".
 *
 * @author Howard Lewis Ship
 */
public class ServiceObjectProvider implements ObjectProvider
{
    /**
     * Interprets the locator as a service id, and passes it to
     * {@link Module#getService(String, Class)}.
     */
    public Object provideObject(Module contributingModule, Class propertyType, String locator,
            Location location)
    {
        if (Gaderian.isBlank(locator))
            return null;

        return contributingModule.getService(locator, Object.class);
    }

}
