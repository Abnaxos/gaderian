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

import java.util.Map;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.service.ObjectProvider;

/**
 * Implementation of {@link org.ops4j.gaderian.service.ObjectProvider} mapped to prefix
 * "configuration:" for accessing configurations.
 * 
 * @author Howard Lewis Ship
 */
public class ConfigurationObjectProvider implements ObjectProvider
{
    /**
     * A wrapper around {@link Module#getConfiguration(String)}. The locator is interpreteted as a
     * configuration id.
     */
    public Object provideObject(Module contributingModule, Class propertyType, String locator,
            Location location)
    {
        if (propertyType == Map.class && contributingModule.isConfigurationMappable(locator))
            return contributingModule.getConfigurationAsMap(locator);

        return contributingModule.getConfiguration(locator);
    }

}