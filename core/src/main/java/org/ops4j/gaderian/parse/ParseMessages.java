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

package org.ops4j.gaderian.parse;

import java.util.Collection;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.impl.MessageFormatter;
import org.ops4j.gaderian.schema.ElementModel;
import org.ops4j.gaderian.schema.Schema;

/**
 * Messages for the parse package.
 * 
 * @author Howard Lewis Ship
 */
class ParseMessages
{
    protected static MessageFormatter _formatter = new MessageFormatter(ParseMessages.class);

    static String dupeAttributeMapping(AttributeMappingDescriptor newDescriptor,
            AttributeMappingDescriptor existingDescriptor)
    {
        return _formatter.format(
                "dupe-attribute-mapping",
                newDescriptor.getAttributeName(),
                existingDescriptor.getLocation());
    }

    static String extraMappings(Collection extraNames, ElementModel model)
    {
        return _formatter.format("extra-mappings", extraNames, model.getElementName());
    }

    static String multipleContributionsSchemas(String configurationId, Location location)
    {
        return _formatter.format("multiple-contributions-schemas", configurationId, location);
    }

    static String multipleParametersSchemas(String serviceId, Location location)
    {
        return _formatter.format("multiple-parameters-schemas", serviceId, location);
    }

    static String notModule(String elementName, Location location)
    {
        return _formatter.format("not-module", elementName, location);
    }

    static String requiredAttribute(String name, String path, Location location)
    {
        return _formatter.format("required-attribute", name, path, location);
    }

    static String unknownAttribute(String name, String path)
    {
        return _formatter.format("unknown-attribute", name, path);
    }

    static String booleanAttribute(String value, String name, String path)
    {
        return _formatter.format("boolean-attribute", new Object[]
        { value, name, path });
    }

    static String invalidAttributeValue(String value, String name, String path)
    {
        return _formatter.format("invalid-attribute-value", new Object[]
        { value, name, path });

    }

    static String invalidNumericValue(String value, String name, String path)
    {
        return _formatter.format("invalid-numeric-value", new Object[]
        { value, name, path });
    }

    static String unableToInitialize(Throwable cause)
    {
        return _formatter.format("unable-to-initialize", cause);
    }

    static String badRuleClass(String className, Location location, Throwable cause)
    {
        return _formatter.format("bad-rule-class", className, location, cause);
    }

    static String errorReadingDescriptor(Resource resource, Throwable cause)
    {
        return _formatter.format("error-reading-descriptor", resource, cause);
    }

    static String missingResource(Resource resource)
    {
        return _formatter.format("missing-resource", resource);
    }

    static String unexpectedElement(String elementName, String elementPath)
    {
        return _formatter.format("unexpected-element", elementName, elementPath);
    }

    static String invalidAttributeFormat(String attributeName, String value, String elementPath,
            String formatKey)
    {
        String inputValueFormat = _formatter.getMessage(formatKey);

        return _formatter.format("invalid-attribute-format", new Object[]
        { attributeName, value, elementPath, inputValueFormat });
    }

    static String duplicateSchema(String schemaId, Schema existingSchema)
    {
        return _formatter.format("duplicate-schema", schemaId, existingSchema.getLocation());
    }

    static String invalidElementKeyAttribute(String schemaId, Throwable cause)
    {
        return _formatter.format("invalid-element-key-attribute", schemaId, cause);
    }
}