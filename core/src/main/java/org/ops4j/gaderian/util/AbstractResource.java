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

import org.ops4j.gaderian.Resource;

/**
 * Abstract implementation of {@link org.ops4j.gaderian.Resource}.
 * 
 * <p>Resource instances act as a kind of factory for new instances
 * of the same type, via {@link #newResource(String)}.
 *
 * @author Howard Lewis Ship
 */
public abstract class AbstractResource implements Resource
{
    private String _path;
    private String _name;
    private String _folderPath;
    private Locale _locale;

    protected AbstractResource(String path)
    {
        this(path, null);
    }

    protected AbstractResource(String path, Locale locale)
    {
        _path = path;
        _locale = locale;
    }

    public String getName()
    {
        if (_name == null)
            split();

        return _name;
    }

    public Resource getRelativeResource(String name)
    {
        if (name.startsWith("/"))
        {
            if (name.equals(_path))
                return this;

            return newResource(name);
        }

        if (_folderPath == null)
            split();

        if (name.equals(_name))
            return this;

        return newResource(_folderPath + name);
    }

    public String getPath()
    {
        return _path;
    }

    public Locale getLocale()
    {
        return _locale;
    }


    protected abstract Resource newResource(String path);

    private void split()
    {
        int lastSlashx = _path.lastIndexOf('/');

        _folderPath = _path.substring(0, lastSlashx + 1);
        _name = _path.substring(lastSlashx + 1);
    }


    /**
     * Returns true if the other object is an instance of the
     * same class, and the paths are equal.
     * 
     */

    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;

        if (obj.getClass().equals(getClass()))
        {
            AbstractResource otherLocation = (AbstractResource) obj;

            return _path.equals(otherLocation._path);
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return _path != null ? _path.hashCode() : 0;
    }
}
