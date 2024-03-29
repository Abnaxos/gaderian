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

package org.ops4j.gaderian.util;

import java.util.Locale;

import org.ops4j.gaderian.ClassResolver;

/**
 * Searches for a localization of a
 * particular resource in the classpath (using
 * a {@link org.ops4j.gaderian.ClassResolver}.
 * 
 *
 * @author Howard Lewis Ship
 */
public class LocalizedResourceFinder
{
    private ClassResolver _resolver;

    public LocalizedResourceFinder(ClassResolver resolver)
    {
        _resolver = resolver;
    }

    /**
     * Resolves the resource, returning a path representing
     * the closest match (with respect to the provided locale).
     * Returns null if no match.
     * 
     * <p>The provided path is split into a base path
     * and a suffix (at the last period character).  The locale
     * will provide different suffixes to the base path
     * and the first match is returned. 
     */
    
    public LocalizedResource resolve(String resourcePath, Locale locale)
    {
        int dotx = resourcePath.lastIndexOf('.');
        String basePath;
        String suffix;
        if (dotx >= 0) {
        	basePath = resourcePath.substring(0, dotx);
        	suffix = resourcePath.substring(dotx);
        }
        else
        {
        	// Resource without extension
        	basePath = resourcePath;
        	suffix = "";
        }

        LocalizedNameGenerator generator = new LocalizedNameGenerator(basePath, locale, suffix);

        while (generator.more())
        {
            String candidatePath = generator.next();

            if (_resolver.getResource(candidatePath) != null)
                return new LocalizedResource(candidatePath, generator.getCurrentLocale());
        }

        return null;
    }
}
