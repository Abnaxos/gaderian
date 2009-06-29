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
 * Translator that converts a class name into an instance of a class.
 *
 * @author Howard Lewis Ship
 */
public class ClassTranslator implements Translator
{
    public Object translate(Module contributingModule, Class propertyType, String inputValue,
            Location location)
    {
        if (Gaderian.isBlank(inputValue))
            return null;

        return contributingModule.resolveType(inputValue);
    }
}