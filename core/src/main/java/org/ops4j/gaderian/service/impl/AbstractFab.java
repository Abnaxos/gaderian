// Copyright 2005 The Apache Software Foundation
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

import java.util.HashMap;
import java.util.Map;

import javassist.CtClass;

/**
 * Common code for {@link org.ops4j.gaderian.service.impl.ClassFabImpl} and
 * {@link org.ops4j.gaderian.service.impl.InterfaceFabImpl}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class AbstractFab
{

    private final CtClass _ctClass;

    private final CtClassSource _source;

    public AbstractFab(CtClassSource source, CtClass ctClass)
    {
        _ctClass = ctClass;
        _source = source;
    }

    /**
     * Map from Class to CtClass.
     * 
     * @since 1.1
     */
    private Map _ctClassCache = new HashMap();

    public void addInterface(Class interfaceClass)
    {
        CtClass ctInterfaceClass = _source.getCtClass(interfaceClass);

        _ctClass.addInterface(ctInterfaceClass);
    }

    protected CtClass[] convertClasses(Class[] inputClasses)
    {
        if (inputClasses == null || inputClasses.length == 0)
            return null;

        int count = inputClasses.length;
        CtClass[] result = new CtClass[count];

        for (int i = 0; i < count; i++)
        {
            CtClass ctClass = convertClass(inputClasses[i]);

            result[i] = ctClass;
        }

        return result;
    }

    public boolean canConvert(Class inputClass)
    {
    	return _source.canConvert(inputClass);
    }
    
    /**
     * @since 1.1
     */
    protected CtClass convertClass(Class inputClass)
    {
        CtClass result = (CtClass) _ctClassCache.get(inputClass);

        if (result == null)
        {
            result = _source.getCtClass(inputClass);
            _ctClassCache.put(inputClass, result);
        }

        return result;
    }

    public Class createClass()
    {
        return _source.createClass(_ctClass);
    }

    protected CtClass getCtClass()
    {
        return _ctClass;
    }

    protected CtClassSource getSource()
    {
        return _source;
    }

}