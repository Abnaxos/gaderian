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

import java.util.Locale;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.Translator;

/**
 * Translator that converts the value to be a resource relative to
 * the contributing module's deployment descriptor.
 *
 * @author Howard Lewis Ship
 */
public class ResourceTranslator implements Translator
{
    /**
     * Finds the resource.  If the inputValue is blank, then returns null.
     * Interprets the inputValue as a relative path from the contributing module's descriptor.
     * In addition, a localized resource will be returned if avaiable (localized to
     * the {@link org.ops4j.gaderian.Registry#getLocale() registry's locale}.
     *
     */
    public Object translate(
        Module contributingModule,
        Class propertyType,
        String inputValue,
        Location location)
    {
        if (Gaderian.isBlank(inputValue))
            return null;

        Locale locale = contributingModule.getLocale();

        Resource descriptor = contributingModule.getLocation().getResource();

        Resource baseResource = descriptor.getRelativeResource(inputValue);

        Resource result = baseResource.getLocalization(locale);

        if (result == null)
            throw new ApplicationRuntimeException(
                RulesMessages.resourceLocalizationError(inputValue, contributingModule));

        return result;
    }

}
