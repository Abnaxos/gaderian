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

import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

/**
 * Used to ensure that {@link javassist.ClassPool#appendClassPath(javassist.ClassPath)} is invoked
 * with a synchronized lock. Additionally, wraps around a shared
 * {@link org.ops4j.gaderian.service.impl.ClassFactoryClassLoader}.
 *
 * @author Howard Lewis Ship
 */
public class GaderianClassPool extends ClassPool
{
    private ClassFactoryClassLoader _loader = new ClassFactoryClassLoader(GaderianClassPool.class.getClassLoader());

    /**
     * Used to identify which class loaders have already been integrated into the pool.
     */
    private Set _loaders = new HashSet();

    public GaderianClassPool()
    {
        super(null);

        appendClassLoader(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Convienience method for adding to the ClassPath for a particular class loader.
     */
    public synchronized void appendClassLoader(ClassLoader loader)
    {
        if (loader == null || loader == _loader || _loaders.contains(loader))
            return;

        _loader.addDelegateLoader(loader);

        ClassPath path = new LoaderClassPath(loader);

        appendClassPath(path);

        _loaders.add(loader);
    }

    /**
     * Invoked to convert an fabricated class into a real class. The new classes' class loader will
     * be the delegating ClassFactoryClassLoader, which has visibility to all class loaders for all
     * modules.
     *
     * @since 1.1
     */
    public Class toClass(CtClass ctClass) throws CannotCompileException
    {
        return ctClass.toClass(_loader, GaderianClassPool.class.getProtectionDomain());
    }
}