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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Map;
import java.lang.reflect.Constructor;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.Translator;

/**
 * A "smart" translator that attempts to automatically convert from string types to object or
 * wrapper types, using {@link java.beans.PropertyEditor}s.
 * 
 * @author Howard Lewis Ship
 */
public class SmartTranslator implements Translator
{
    private String _default;

    public SmartTranslator()
    {
    }

    /**
     * Initializers:
     * <ul>
     * <li>default: default value for empty input
     * </ul>
     */
    public SmartTranslator(String initializer)
    {
        Map m = RuleUtils.convertInitializer(initializer);

        _default = (String) m.get("default");
    }

    public <T> T translate(Module contributingModule, Class<T> propertyType, String inputValue,
            Location location)
    {
        // HIVEMIND-10: Inside JavaWebStart you (strangely) can't rely on
        // a PropertyEditor for String (even though it is trivial).

        if (inputValue == null)
        {
            if (_default == null)
                return null;

            inputValue = _default;
        }

        if (propertyType.equals(String.class) || propertyType.equals(Object.class))
            return (T) inputValue;

        // TODO: This duplicates logic inside PropertyAdaptor.

        try
        {
            PropertyEditor e = PropertyEditorManager.findEditor(propertyType);

          if (e == null)
          {
              Object convertedValue = instantiateViaStringConstructor(propertyType, inputValue);

              if (convertedValue != null)
                  return (T) convertedValue;

              throw new ApplicationRuntimeException(RulesMessages.noPropertyEditor(
                      propertyType));
          }

            e.setAsText(inputValue);

            return (T) e.getValue();
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(RulesMessages.smartTranslatorError(
                    inputValue,
                    propertyType,
                    ex), location, ex);

        }
    }

    /**
     * Checks to see if this adaptor's property type has a public constructor that takes a single
     * String argument.
     */

    private <T> T instantiateViaStringConstructor(Class<T> propertyType, String value)
    {
        try
        {
            Constructor<T> c = propertyType.getConstructor(String.class);

            return c.newInstance(value);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

}