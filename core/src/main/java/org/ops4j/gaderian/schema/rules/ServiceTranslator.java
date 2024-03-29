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

package org.ops4j.gaderian.schema.rules;

import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.Translator;

/**
 * Used to translate a value into a reference to a Gaderian service within the registry.
 *
 * @author Howard Lewis Ship
 */
public class ServiceTranslator implements Translator
{
    /**
     * Returns null if the input is null or empty. Returns the service with the given name
     * otherwise. Will log an error and return null if an exception is thrown. If the input value is
     * not qualified, the contributing module's id is added as a prefix.
     */
    public Object translate(Module contributingModule, Class propertyType, String inputValue,
            Location location)
    {
        if (Gaderian.isBlank(inputValue))
            return null;

        return contributingModule.getService(inputValue, Object.class);
    }

}