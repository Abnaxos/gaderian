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

package org.ops4j.gaderian.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Attribute;
import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.schema.AttributeModel;
import org.ops4j.gaderian.schema.ElementModel;
import org.ops4j.gaderian.schema.Rule;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.schema.rules.NullTranslator;

/**
 * A wrapper around {@link org.ops4j.gaderian.schema.ElementModel} used by
 * {@link org.ops4j.gaderian.impl.SchemaProcessorImpl}.
 * 
 * @author Howard Lewis Ship
 */
final class SchemaElement
{
    private SchemaProcessor _processor;

    private ElementModel _model;

    private List _requiredAttributes = new ArrayList();

    private Set _knownAttributes = new HashSet();

    private String _keyAttribute;

    private Map _nestedElements;

    /**
     * Keyed on attribute name, value is string (possibly null) used to access a translator.
     */
    private Map _attributeTranslators = new HashMap();

    private Map _attributeDefaults = new HashMap();

    /**
     * Map of Maps. The outer key is an attribute name string, this indicates that the attribute
     * values should be unique. Inner map is keyed on attribute value (as a string), the value is
     * the {@link org.ops4j.gaderian.Location} defining that value.
     */
    private Map _attributeValues = new HashMap();

    SchemaElement(SchemaProcessor processor, ElementModel model)
    {
        _processor = processor;
        _model = model;
        _keyAttribute = model.getKeyAttribute();

        List attributeModels = model.getAttributeModels();
        int count = attributeModels.size();

        for (int i = 0; i < count; i++)
        {
            AttributeModel am = (AttributeModel) attributeModels.get(i);

            String name = am.getName();

            _knownAttributes.add(name);

            if (am.isRequired())
                _requiredAttributes.add(name);

            // If the attribute should be unique, add a map for that attribute
            // to track unique values for that attribute.

            if (am.isUnique())
                _attributeValues.put(name, new HashMap());

            if (name.equals(_keyAttribute))
            {
                _requiredAttributes.add(name);
                _attributeValues.put(name, new HashMap());
            }

            if (am.getDefault() != null)
                _attributeDefaults.put(am.getName(), am.getDefault());

            _attributeTranslators.put(name, am.getTranslator());
        }
    }

    /**
     * Returns a {@link SchemaElement} for a nested element, or null if no such element exists.
     */
    SchemaElement getNestedElement(String elementName)
    {
        if (_nestedElements == null)
            buildNestedElements();

        return (SchemaElement) _nestedElements.get(elementName);
    }

    private void buildNestedElements()
    {
        _nestedElements = new HashMap();

        List l = _model.getElementModel();
        int count = l.size();

        for (int i = 0; i < count; i++)
        {
            ElementModel nested = (ElementModel) l.get(i);

            SchemaElement nestedElement = new SchemaElement(_processor, nested);

            // TODO: Check for duplicates here, or at parse!

            _nestedElements.put(nested.getElementName(), nestedElement);
        }

    }

    /**
     * Validates the attributes of the element; checks that all required attributes are present and
     * that all attributes are defined. Validation errors result in logged error messages.
     */
    void validateAttributes(Element element)
    {
        List l = element.getAttributes();
        int count = l.size();
        Set required = new HashSet(_requiredAttributes);
        List errors = new ArrayList();

        for (int i = 0; i < count; i++)
        {
            Attribute a = (Attribute) l.get(i);
            String name = a.getName();

            if (!_knownAttributes.contains(name))
                errors.add(ImplMessages.unknownAttribute(name));

            checkUniquness(name, a.getValue(), element.getLocation(), errors);

            required.remove(name);
        }

        Iterator it = required.iterator();

        while (it.hasNext())
        {
            String name = (String) it.next();
            errors.add(ImplMessages.missingAttribute(name));
        }

        count = errors.size();

        if (count == 0)
            return;

        StringBuffer buffer = new StringBuffer();

        buffer.append(ImplMessages.elementErrors(_processor, element));

        for (int i = 0; i < count; i++)
        {
            buffer.append(' ');
            buffer.append(errors.get(i).toString());
        }

        // TODO: refactor to use the ErrorHandler rather than throw an exception
        // (these errors are somewhat recoverable).

        throw new ApplicationRuntimeException(buffer.toString(), element.getLocation(), null);
    }

    private void checkUniquness(String name, String value, Location location, List errors)
    {
        Map valuesMap = (Map) _attributeValues.get(name);

        // If null, then not checking on uniqueness.

        if (valuesMap == null)
            return;

        Object translatedValue = getAttributeTranslator(name).translate(
                _processor.getContributingModule(),
                Object.class,
                value,
                location);

        Location prior = (Location) valuesMap.get(translatedValue);

        if (prior == null)
        {
            valuesMap.put(translatedValue, location);
            return;
        }

        // A conflict.

        errors.add(ImplMessages.uniqueAttributeConstraintBroken(
                name,
                translatedValue.toString(),
                prior));
    }

    void fireBegin(Element element)
    {
        List rules = _model.getRules();
        int count = rules.size();

        for (int i = 0; i < count; i++)
        {
            Rule r = (Rule) rules.get(i);

            r.begin(_processor, element);

        }
    }

    void fireEnd(Element element)
    {
        List rules = _model.getRules();
        int count = rules.size();

        for (int i = count - 1; i >= 0; i--)
        {
            Rule r = (Rule) rules.get(i);

            r.end(_processor, element);

        }
    }

    private Translator _nullTranslator = new NullTranslator();

    private Translator _contentTranslator;

    public Translator getContentTranslator()
    {
        if (_contentTranslator == null)
            _contentTranslator = getTranslator(_model.getContentTranslator());

        return _contentTranslator;
    }

    private Translator getTranslator(String translator)
    {
        if (translator == null)
            return _nullTranslator;

        return _processor.getTranslator(translator);
    }

    public Translator getAttributeTranslator(String attributeName)
    {
        String translator = (String) _attributeTranslators.get(attributeName);

        return getTranslator(translator);
    }

    public String getAttributeDefault(String attributeName)
    {
        return (String) _attributeDefaults.get(attributeName);
    }

    public String getKeyAttribute()
    {
        return _keyAttribute;
    }
}