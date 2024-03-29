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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.internal.Module;

/**
 * Provides access to an object (of a particular class) as a set of individual property that may be
 * read or updated.
 * 
 * @author Howard Lewis Ship
 */
class ClassAdaptor
{
    private final Map<String, PropertyAdaptor> _propertyAdaptorMap = new HashMap<String, PropertyAdaptor>();

    ClassAdaptor(PropertyDescriptor[] properties)
    {
        for ( PropertyDescriptor d : properties )
        {
            final String name = d.getName();
            _propertyAdaptorMap.put( name, new PropertyAdaptor( name, d.getPropertyType(), d
                    .getReadMethod(), d.getWriteMethod() ) );
        }
    }

    /**
     * Updates the property of the target object.
     * 
     * @param target
     *            the object to update
     * @param value
     *            the value to be stored into the target object property
     */
    public void write(Object target, String propertyName, Object value)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        a.write(target, value);
    }

    /**
     * An improved version of {@link #write(Object, String, Object)} that can convert a string value
     * to an appropriate property type value.
     * 
     * @since 1.1
     */

    public void smartWrite( Module module, Object target, String propertyName, String value)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        a.smartWrite( module, target, value );
    }

    /**
     * Reads the property of the target object.
     * 
     * @param target
     *            the object to read
     * @param propertyName
     *            the name of the property to read
     */
    public Object read(Object target, String propertyName)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        return a.read(target);
    }

    /**
     * Returns the type of the named property.
     * 
     * @param target
     *            the object to examine
     * @param propertyName
     *            the name of the property to check
     */
    public Class getPropertyType(Object target, String propertyName)
    {
        PropertyAdaptor a = getPropertyAdaptor(target, propertyName);

        return a.getPropertyType();
    }

    /**
     * Returns true if the named property exists and is readable.
     */

    public boolean isReadable(String propertyName)
    {
        PropertyAdaptor result = _propertyAdaptorMap.get(propertyName);

        return result != null && result.isReadable();
    }

    /**
     * Returns true if the named property exists and is writable.
     */

    public boolean isWritable(String propertyName)
    {
        PropertyAdaptor result = _propertyAdaptorMap.get(propertyName);

        return result != null && result.isWritable();
    }

    PropertyAdaptor getPropertyAdaptor(Object target, String propertyName)
    {
        PropertyAdaptor result = _propertyAdaptorMap.get(propertyName);

        if (result == null)
            throw new ApplicationRuntimeException(
                    UtilMessages.noSuchProperty(target, propertyName), target, null, null);

        return result;
    }

    /**
     * Returns a List of the names of readable properties (properties with a non-null getter).
     */
    public List<String> getReadableProperties()
    {
        List<String> result = new ArrayList<String>(_propertyAdaptorMap.size());

        for ( final PropertyAdaptor propertyAdaptor : _propertyAdaptorMap.values() )
        {

            if ( propertyAdaptor.isReadable() )
            {
                result.add( propertyAdaptor.getPropertyName() );
            }
        }

        return result;
    }

    /**
     * Returns a List of the names of readable properties (properties with a non-null setter).
     */
    public List<String> getWriteableProperties()
    {
        List<String> result = new ArrayList<String>(_propertyAdaptorMap.size());

        for ( final PropertyAdaptor propertyAdaptor : _propertyAdaptorMap.values() )
        {
            if ( propertyAdaptor.isWritable() )
            {
                result.add( propertyAdaptor.getPropertyName() );
            }
        }

        return result;
    }

    /**
     * Does the grunt work for
     * {@link org.ops4j.gaderian.util.PropertyUtils#configureProperties(Object, String)}.
     * 
     * @since 1.1
     */

    public void configureProperties(Module module, Object target, String initializer)
    {
        StringTokenizer tokenizer = new StringTokenizer(initializer, ",");

        while (tokenizer.hasMoreTokens())
        {
            configurePropertyFromToken(module, target, tokenizer.nextToken());
        }
    }

    /**
     * The token is either:
     * <ul>
     * <li>propertyName=value</li>
     * <li>propertyName</li>
     * <li>!propertyName</li>
     * </ul>
     * The later two are for boolean properties (true and false, respectively).
     * 
     * @since 1.1
     */
    private void configurePropertyFromToken(Module module, Object target, String token)
    {
        int equalsx = token.indexOf('=');

        if (equalsx > 0)
        {
            String propertyName = token.substring(0, equalsx).trim();
            String value = token.substring(equalsx + 1);

            smartWrite(module, target, propertyName, value);
            return;
        }

        boolean negate = token.startsWith("!");

        String propertyName = negate ? token.substring(1) : token;

        Boolean value = negate ? Boolean.FALSE : Boolean.TRUE;

        write(target, propertyName, value);
    }
}