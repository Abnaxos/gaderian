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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.internal.Module;

/**
 * Used to manage dynamic access to a property of a specific class.
 * 
 * @author Howard Lewis Ship
 */
public class PropertyAdaptor
{
    private String _propertyName;

    private Class _propertyType;

    private Method _readMethod;

    private Method _writeMethod;

    PropertyAdaptor(String propertyName, Class propertyType, Method readMethod, Method writeMethod)
    {
        _propertyName = propertyName;
        _propertyType = propertyType;
        _readMethod = readMethod;
        _writeMethod = writeMethod;
    }

    /**
     * Returns the name of the method used to read the property, or null if the property is not
     * readable.
     */
    public String getReadMethodName()
    {
        return _readMethod == null ? null : _readMethod.getName();
    }

    /**
     * Returns the name of the method used to write the property, or null if the property is not
     * writable.
     */
    public String getWriteMethodName()
    {
        return _writeMethod == null ? null : _writeMethod.getName();
    }

    public String getPropertyName()
    {
        return _propertyName;
    }

    public Class getPropertyType()
    {
        return _propertyType;
    }

    /**
     * Updates the property of the target object.
     * 
     * @param target
     *            the object to update
     * @param value
     *            the value to be stored into the target object property
     */
    public void write(Object target, Object value)
    {
        if (_writeMethod == null)
            throw new ApplicationRuntimeException(UtilMessages.noPropertyWriter(
                    _propertyName,
                    target), target, null, null);

        try
        {
            _writeMethod.invoke(target, new Object[]
            { value });

        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(UtilMessages.writeFailure(
                    _propertyName,
                    target,
                    ex), target, null, ex);
        }
    }

    public void smartWrite(Module module, Object target, String value)
    {
        Object convertedValue = convertValueForAssignment(module, target, value);

        write(target, convertedValue);
    }

    /** @since 1.1 */
    private Object convertValueForAssignment( Module module, Object target, String value)
    {
        if (value == null || _propertyType.isInstance(value))
            return value;

        try
        {
            return module.stringToObject( value, _propertyType, null );
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(UtilMessages.unableToConvert(
                    value,
                    _propertyType,
                    _propertyName,
                    target,
                    ex), null, ex);
        }
    }

    /**
     * Checks to see if this adaptor's property type has a public constructor that takes a single
     * String argument.
     */

    private Object instantiateViaStringConstructor(String value)
    {
        try
        {
            Constructor c = _propertyType.getConstructor(String.class);

            return c.newInstance(value);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * Returns true if there's a write method for the property.
     */
    public boolean isWritable()
    {
        return _writeMethod != null;
    }

    /**
     * Reads the property of the target object.
     * 
     * @param target
     *            the object to read a property from
     */
    public Object read(Object target)
    {
        if (_readMethod == null)
            throw new ApplicationRuntimeException(UtilMessages.noReader(_propertyName, target),
                    target, null, null);

        try
        {
            return _readMethod.invoke(target, null);

        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(UtilMessages.readFailure(
                    _propertyName,
                    target,
                    ex), target, null, ex);
        }
    }

    /**
     * Returns true if there's a read method for the property.
     */

    public boolean isReadable()
    {
        return _readMethod != null;
    }

}