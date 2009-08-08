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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.net.URL;

/**
 * ClassLoader used to properly instantiate newly created classes.
 *
 * @author Howard Lewis Ship / Essl Christian
 * @see org.ops4j.gaderian.service.impl.CtClassSource
 */
class ClassFactoryClassLoader extends ClassLoader
{
    private List<ClassLoader> _loaders = new CopyOnWriteArrayList<ClassLoader>();

    public ClassFactoryClassLoader(ClassLoader parent)
    {
        super(parent);
    }

    /**
     * Adds a delegate class loader to the list of delegate class loaders.
     */
    public synchronized void addDelegateLoader(ClassLoader loader)
    {
        _loaders.add(loader);
    }

    /**
     * Searches each of the delegate class loaders for the given class.
     */
    protected synchronized Class findClass(String name) throws ClassNotFoundException
    {
      for (ClassLoader loader : _loaders)
      {
        try
        {
            return loader.loadClass(name);
        }
        catch (ClassNotFoundException ex)
        {
            //
        }

      }

      // Not found .. through the first exception
      throw new ClassNotFoundException(name);
    }

  protected URL findResource(String name)
  {
    for (ClassLoader loader : _loaders)
    {
      URL rv = loader.getResource(name);
      if (rv != null)
      {
          return rv;
      }
    }
    return super.findResource(name);
  }

}