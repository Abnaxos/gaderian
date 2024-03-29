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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.internal.Module;

/**
 * A collection of static methods used to perform property-level access on arbitrary objects.
 *
 * @author Howard Lewis Ship
 */
public class PropertyUtils
{
    private static final Map<Class,ClassAdaptor> _classAdaptors = new HashMap<Class,ClassAdaptor>();

    // Prevent instantiation
    private PropertyUtils()
    {
    }

    /**
     * Updates the property of the target object.
     *
     * @param target
     *            the object to update
     * @param propertyName
     *            the name of the property to be updated
     * @param value
     *            the value to be stored into the target object property
     */
    public static void write(Object target, String propertyName, Object value)
    {
        ClassAdaptor a = getAdaptor(target);

        a.write(target, propertyName, value);
    }

    /**
     * An improved version of {@link #write(Object, String, Object)} where the value starts as a
     * string and is converted to the correct property type before being assigned.
     *
     * @since 1.1
     */
    public static void smartWrite( Module module, Object target, String propertyName, String value)
    {
        ClassAdaptor a = getAdaptor(target);

        a.smartWrite(module, target, propertyName, value);
    }

    /**
     * Initializes the properties of an object from a string. The string is a comma-seperated
     * sequence of property names and values. Property names are seperated from values be an equals
     * sign. Spaces before and after the property names are trimmed.
     * For boolean properties, the equals sign and value may be omitted (a value of true is
     * assumed), or the property name may be prefixed with an exclamation point to indicated false
     * value. Example: <code>validate,maxLength=10,displayName=User Id</code>.
     *
     * @param target
     *            the object to be configured
     * @param initializer
     *            the string encoding the properties and values to be configured in the target
     *            object
     * @since 1.1
     */

    public static void configureProperties(Module module, Object target, String initializer)
    {
        ClassAdaptor a = getAdaptor(target);

        a.configureProperties(module, target, initializer);
    }

    /**
     * Returns true of the instance contains a writable property of the given type.
     *
     * @param target
     *            the object to inspect
     * @param propertyName
     *            the name of the property to check
     */

    public static boolean isWritable(Object target, String propertyName)
    {
        return getAdaptor(target).isWritable(propertyName);
    }

    public static boolean isReadable(Object target, String propertyName)
    {
        return getAdaptor(target).isReadable(propertyName);
    }

    /**
     * Updates the property of the target object.
     *
     * @param target
     *            the object to update
     * @param propertyName
     *            the name of a property toread
     */

    public static Object read(Object target, String propertyName)
    {
        ClassAdaptor a = getAdaptor(target);

        return a.read(target, propertyName);
    }

    /**
     * Returns the type of the named property.
     *
     * @param target
     *            the object to examine
     * @param propertyName
     *            the name of the property to check
     */
    public static Class getPropertyType(Object target, String propertyName)
    {
        ClassAdaptor a = getAdaptor(target);

        return a.getPropertyType(target, propertyName);
    }

    /**
     * Returns the {@link PropertyAdaptor} for the given target object and property name.
     *
     * @throws ApplicationRuntimeException
     *             if the property does not exist.
     */
    public static PropertyAdaptor getPropertyAdaptor(Object target, String propertyName)
    {
        ClassAdaptor a = getAdaptor(target);

        return a.getPropertyAdaptor(target, propertyName);
    }

    /**
     * Returns an unordered List of the names of all readable properties of the target.
     */
    public static List getReadableProperties(Object target)
    {
        return getAdaptor(target).getReadableProperties();
    }

    /**
     * Returns an unordered List of the names of all writable properties of the target.
     */
    public static List getWriteableProperties(Object target)
    {
        return getAdaptor(target).getWriteableProperties();
    }

    private static ClassAdaptor getAdaptor(Object target)
    {
        if (target == null)
            throw new ApplicationRuntimeException(UtilMessages.nullObject());

        Class targetClass = target.getClass();

        synchronized (Gaderian.INTROSPECTOR_MUTEX)
        {
            ClassAdaptor result = (ClassAdaptor) _classAdaptors.get(targetClass);

            if (result == null)
            {
                result = buildClassAdaptor(target, targetClass);
                _classAdaptors.put(targetClass, result);
            }

            return result;
        }
    }

    private static ClassAdaptor buildClassAdaptor(Object target, Class targetClass)
    {
        try
        {
            BeanInfo info = Introspector.getBeanInfo(targetClass);

            return new ClassAdaptor(info.getPropertyDescriptors());
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(UtilMessages.unableToIntrospect(targetClass, ex),
                    target, null, ex);
        }
    }

    /**
     * Clears all cached information. Invokes {@link Introspector#flushCaches()}.
     */
    public static void clearCache()
    {
        synchronized (Gaderian.INTROSPECTOR_MUTEX)
        {
            _classAdaptors.clear();
            Introspector.flushCaches();
        }
    }

}