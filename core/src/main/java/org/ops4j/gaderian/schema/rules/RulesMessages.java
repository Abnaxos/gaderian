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

import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.impl.MessageFormatter;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;

/**
 * Messages related to the rules package.
 *
 * @author Howard Lewis Ship
 */
class RulesMessages
{

    protected static MessageFormatter _formatter = new MessageFormatter(RulesMessages.class);

    static String unableToSetElementProperty(String propertyName, Object target,
            SchemaProcessor processor, Element element, Throwable cause)
    {
        return _formatter.format("unable-to-set-element-property", new Object[]
        { propertyName, target, processor.getElementPath(), Gaderian.getLocationString(element),
                cause });
    }

    static String unableToSetProperty(String propertyName, Object target, Throwable cause)
    {
        return _formatter.format("unable-to-set-property", propertyName, target, cause);
    }

    static String invalidBooleanValue(String inputValue)
    {
        return _formatter.format("invalid-boolean-value", inputValue);
    }

    static String invalidDoubleValue(String inputValue)
    {
        return _formatter.format("invalid-double-value", inputValue);
    }

    static String minDoubleValue(String inputValue, double minValue)
    {
        return _formatter.format("min-double-value", inputValue, new Double(minValue));
    }

    static String maxDoubleValue(String inputValue, double maxValue)
    {
        return _formatter.format("max-double-value", inputValue, new Double(maxValue));
    }

    static String enumNotRecognized(String inputValue)
    {
        return _formatter.format("enum-not-recognized", inputValue);
    }

    static String enumError(Class enumClass, String fieldName, Throwable cause)
    {
        return _formatter.format("enum-error", enumClass.getName(), fieldName, cause);
    }

    static String invalidIntValue(String inputValue)
    {
        return _formatter.format("invalid-int-value", inputValue);
    }

    static String minIntValue(String inputValue, int minValue)
    {
        return _formatter.format("min-int-value", inputValue, new Integer(minValue));
    }

    static String maxIntValue(String inputValue, int maxValue)
    {
        return _formatter.format("max-int-value", inputValue, new Integer(maxValue));
    }

    static String errorInvokingMethod(String methodName, Object parent, Location location,
            Throwable cause)
    {
        return _formatter.format("error-invoking-method", new Object[]
        { methodName, parent, location, cause });
    }

    static String invalidLongValue(String inputValue)
    {
        return _formatter.format("invalid-long-value", inputValue);
    }

    static String minLongValue(String inputValue, long minValue)
    {
        return _formatter.format("min-long-value", inputValue, new Long(minValue));
    }

    static String maxLongValue(String inputValue, long maxValue)
    {
        return _formatter.format("max-long-value", inputValue, new Long(maxValue));
    }

    static String readAttributeFailure(String attributeName, Element element,
            SchemaProcessor processor, Throwable cause)
    {
        return _formatter.format("read-attribute-failure", new Object[]
        { attributeName, processor.getElementPath(), cause });
    }

    static String readContentFailure(SchemaProcessor processor, Element element, Throwable cause)
    {
        return _formatter.format("read-content-failure", processor.getElementPath(), cause);
    }

    static String resourceLocalizationError(String inputValue, Module contributingModule)
    {
        return _formatter.format("resource-localization-error", inputValue, contributingModule
                .getModuleId());
    }

    static String invalidInitializer(String initializer)
    {
        return _formatter.format("invalid-initializer", initializer);
    }

    static String noPropertyEditor(Class propertyType)
    {
        return _formatter.format("no-property-editor", propertyType.getName());
    }

    static String smartTranslatorError(String inputValue, Class propertyType, Throwable cause)
    {
        return _formatter.format(
                "smart-translator-error",
                inputValue,
                propertyType.getName(),
                cause);
    }
}