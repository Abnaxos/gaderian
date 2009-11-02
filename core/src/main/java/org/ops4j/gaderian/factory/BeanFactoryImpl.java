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

package org.ops4j.gaderian.factory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.BeanFactory;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.util.PropertyUtils;
import org.ops4j.gaderian.util.StringUtils;

/**
 * Implementation of {@link org.ops4j.gaderian.BeanFactory}.
 *
 * @author Howard Lewis Ship
 */
public class BeanFactoryImpl extends BaseLocatable implements BeanFactory
{
    private ErrorLog _errorLog;

    private Class _vendType;

    private Map<String,BeanFactoryContribution> _contributions = new HashMap<String,BeanFactoryContribution>();

    private Map<String,Object> _cache = new HashMap<String,Object>();

    private boolean _defaultCacheable;

    public BeanFactoryImpl(ErrorLog errorLog, Class vendType, List<BeanFactoryContribution> contributions, boolean defaultCacheable)
    {
        _errorLog = errorLog;
        _vendType = vendType;
        _defaultCacheable = defaultCacheable;

        processContributions(contributions);
    }

    public boolean contains(String locator)
    {
        int commax = locator.indexOf(',');

        String name = commax < 0 ? locator.trim() : locator.substring(0, commax);

        return _contributions.containsKey(name);
    }

    private void processContributions(List<BeanFactoryContribution> beanFactoryContributions)
    {

      for (final BeanFactoryContribution beanFactoryContribution : beanFactoryContributions)
      {
        Class beanClass = beanFactoryContribution.getBeanClass();

        if (beanClass.isInterface() || beanClass.isArray() || beanClass.isPrimitive())
        {
          _errorLog.error(FactoryMessages.invalidContributionClass(beanFactoryContribution), beanFactoryContribution.getLocation(), null);
          continue;
        }

        if (!_vendType.isAssignableFrom(beanClass))
        {
          _errorLog.error(FactoryMessages.wrongContributionType(beanFactoryContribution, _vendType), beanFactoryContribution.getLocation(), null);
          continue;
        }

        _contributions.put(beanFactoryContribution.getName(), beanFactoryContribution);
      }
    }

    public synchronized Object get(String locator)
    {
        Object result = _cache.get(locator);

        if (result == null)
            result = create(locator);

        return result;
    }

    // Implicitly synchronized by get()

    private Object create(String locator)
    {
        int commax = locator.indexOf(',');

        String name = commax < 0 ? locator.trim() : locator.substring(0, commax);
        String initializer = commax < 0 ? null : locator.substring(commax + 1).trim();

        BeanFactoryContribution c = _contributions.get(name);

        if (c == null)
            throw new ApplicationRuntimeException(FactoryMessages.unknownContribution(name));

        Object result = construct(c, initializer);

        if (c.getStoreResultInCache(_defaultCacheable))
            _cache.put(locator, result);

        return result;
    }

    private Object construct(BeanFactoryContribution contribution, String initializer)
    {
        Class beanClass = contribution.getBeanClass();

        try
        {
            if (Gaderian.isBlank(initializer))
                return beanClass.newInstance();

            try
            {
                // Attempt to construct the class using a string constructor (the default)
                final Constructor c = beanClass.getConstructor(String.class);
                return c.newInstance(initializer);
            }
            catch (NoSuchMethodException e)
            {
                // Class does not contain a string the constructor - attempt using empty constructor
                // and property utils to initialize the bean with the corresponding value

                final String[] splitInitializer = StringUtils.split('=',initializer);
                if (splitInitializer.length != 2 || splitInitializer[0] == null || splitInitializer[0].length() == 0)
                {
                    throw new ApplicationRuntimeException(FactoryMessages.invalidInitializer(beanClass,initializer),contribution.getLocation(),null);
                }

                // Create the new instance
                final Object o = beanClass.newInstance();

                // Check if the property is writable
                if (!PropertyUtils.isWritable(o, splitInitializer[0]))
                {
                    // Throw error
                    throw new ApplicationRuntimeException(FactoryMessages.invalidInitializerProperty(beanClass, splitInitializer[0], initializer), contribution.getLocation(), null);
                }
                // It is writable - attempt to write it
                PropertyUtils.smartWrite(o, splitInitializer[0], splitInitializer[1]);

                // And finally return the new instance
                return o;

            }
        }
        catch(ApplicationRuntimeException e)
        {
            throw e;
        }

        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(FactoryMessages
                    .unableToInstantiate(beanClass, ex), contribution.getLocation(), ex);

        }
    }

}