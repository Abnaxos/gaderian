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

import java.util.*;

import gaderian.test.services.StringHolder;
import gaderian.test.services.impl.StringHolderImpl;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.schema.impl.AttributeModelImpl;
import org.ops4j.gaderian.schema.impl.ElementModelImpl;
import org.ops4j.gaderian.schema.impl.SchemaImpl;
import org.ops4j.gaderian.schema.rules.*;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.schema.SchemaProcessor} and
 * {@link org.ops4j.gaderian.impl.SchemaElement}.
 *
 * @author Howard Lewis Ship
 */
public class TestSchemaProcessor extends GaderianCoreTestCase
{

    public void testGetContentTranslator()
    {
        Module m = createMock(Module.class);

        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("fred");
        em.setContentTranslator("smart");

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);
        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);

        expect(m.resolveType("gaderian.test.services.impl.StringHolderImpl")).andReturn(StringHolderImpl.class);

        expect(m.expandSymbols("flintstone", null)).andReturn("flintstone");

        expect(m.getTranslator("smart")).andReturn(new NullTranslator());

        replayAllRegisteredMocks();

        p.process(elements, m);

        List l = p.getElements();

        assertEquals(1, l.size());
        StringHolder h = (StringHolder) l.get(0);

        assertEquals("flintstone", h.getValue());

        verifyAllRegisteredMocks();
    }

    public void testGetContentTranslatorUnspecified()
    {
        Module m = createMock(Module.class);

        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("fred");
        // No content handler specified

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);
        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);

        expect(m.resolveType("gaderian.test.services.impl.StringHolderImpl")).andReturn(StringHolderImpl.class);

        expect(m.expandSymbols("flintstone", null)).andReturn("flintstone");

        replayAllRegisteredMocks();

        p.process(elements, m);

        List l = p.getElements();

        assertEquals(1, l.size());
        StringHolder h = (StringHolder) l.get(0);

        assertEquals("flintstone", h.getValue());

        verifyAllRegisteredMocks();
    }

    public void testGetAttributeTranslator()
    {
        final Module m = createMock(Module.class);

        ElementModelImpl em = new ElementModelImpl();

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("wife");
        am.setTranslator("service");

        em.setElementName("fred");
        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadAttributeRule rule = new ReadAttributeRule();
        rule.setPropertyName("value");
        rule.setAttributeName("wife");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);
        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.addAttribute(new AttributeImpl("wife", "wilma"));

        List elements = Collections.singletonList(element);

        expect(m.resolveType("gaderian.test.services.impl.StringHolderImpl")).andReturn(StringHolderImpl.class);
        expect(m.expandSymbols("wilma", null)).andReturn("wilma");
        expect(m.getTranslator("service")).andReturn(new NullTranslator());

        replayAllRegisteredMocks();

        p.process(elements, m);

        List l = p.getElements();

        assertEquals(1, l.size());
        StringHolder h = (StringHolder) l.get(0);

        assertEquals("wilma", h.getValue());

        verifyAllRegisteredMocks();
    }

    /**
     * Tests for when the stack is empty.
     */
    public void testStackEmpty()
    {
        SchemaProcessor sp = new SchemaProcessorImpl(null, null);

        // The sp is pushed onto the stack itself

        sp.pop();

        try
        {
            sp.pop();
            unreachable();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {

        }

        try
        {
            sp.peek();
            unreachable();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
        }

    }

    public void testKeyedElement()
    {
        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("cartoon");
        em.setKeyAttribute("name");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("name");
        am.setTranslator("cartoon");

        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule("StringHolderImpl"));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        Module m = createMock(Module.class);

        schema.setModule(m);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("cartoon");
        element.setContent("${fred}");
        element.addAttribute(new AttributeImpl("name", "${flintstone}"));

        List elements = Collections.singletonList(element);

        expect(m.getTranslator("cartoon")).andReturn(new NullTranslator());

        expect(m.resolveType("StringHolderImpl")).andReturn(StringHolderImpl.class);

        expect(m.expandSymbols("${fred}", null)).andReturn("fred");

        expect(m.expandSymbols("${flintstone}", null)).andReturn("flintstone");

        Translator t = createMock(Translator.class);

        expect(m.getTranslator("cartoon")).andReturn(t);

        Object flintstoneKey = new Object();
        expect(t.translate(m, Object.class, "flintstone", element.getLocation())).andReturn(flintstoneKey);

        replayAllRegisteredMocks();

        p.process(elements, m);

        Map map = p.getMappedElements();

        assertEquals(1, map.size());
        StringHolder h = (StringHolder) map.get(flintstoneKey);

        assertEquals("fred", h.getValue());

        verifyAllRegisteredMocks();
    }

    /**
     * Test contributing 2 elements from 2 modules to a configuration-point with an attribute that
     * is marked unique and is translated by translator 'qualified-id'. Both contributed elements
     * use same untranslated value in the unique attribute. Fixes HIVEMIND-100.
     */
    public void testUniqueElement()
    {
        ElementModelImpl em = new ElementModelImpl();

        em.setElementName("cartoon");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("name");
        am.setTranslator("qualified-id");
        am.setUnique(true);

        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule("StringHolderImpl"));

        ReadAttributeRule rule = new ReadAttributeRule();
        rule.setAttributeName("name");
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl();
        schema.addElementModel(em);

        Module m1 = createMock(Module.class);
        Module m2 = createMock(Module.class);

        schema.setModule(m1);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        Location location1 = newLocation();
        ElementImpl element1 = new ElementImpl();
        element1.setElementName("cartoon");
        element1.addAttribute(new AttributeImpl("name", "flintstone"));
        element1.setLocation(location1);

        List elements1 = Collections.singletonList(element1);

        Location location2 = newLocation();
        ElementImpl element2 = new ElementImpl();
        element2.setElementName("cartoon");
        element2.addAttribute(new AttributeImpl("name", "flintstone"));
        element2.setLocation(location2);

        List elements2 = Collections.singletonList(element2);

        Translator t1 = createMock(Translator.class);

        expect(m1.getTranslator("qualified-id")).andReturn(t1);

        String flintstoneKeyModule1 = "m1.flintstone";
        expect(t1.translate(m1, Object.class, "flintstone", element1.getLocation())).andReturn(flintstoneKeyModule1);

        expect(m1.resolveType("StringHolderImpl")).andReturn(StringHolderImpl.class);

        expect(m1.expandSymbols("flintstone", location1)).andReturn("flintstone");

        expect(m1.getTranslator("qualified-id")).andReturn(t1);

        expect(t1.translate(m1, String.class, "flintstone", element1.getLocation())).andReturn(flintstoneKeyModule1);

        expect(m1.resolveType("StringHolderImpl")).andReturn(StringHolderImpl.class);

        Translator t2 = createMock(Translator.class);

        expect(m2.getTranslator("qualified-id")).andReturn(t2);

        String flintstoneKeyModule2 = "m2.flintstone";
        expect(t2.translate(m2, Object.class, "flintstone", element2.getLocation())).andReturn(flintstoneKeyModule2);

        expect(m2.expandSymbols("flintstone", location2)).andReturn("flintstone");

        expect(m2.getTranslator("qualified-id")).andReturn(t2);

        expect(t2.translate(m2, String.class, "flintstone", element2.getLocation())).andReturn(flintstoneKeyModule2);

        replayAllRegisteredMocks();

        p.process(elements1, m1);
        p.process(elements2, m2);

        List list = p.getElements();

        assertEquals(2, list.size());

        Set keys = new TreeSet();
        for (Iterator iter = list.iterator(); iter.hasNext();)
        {
            StringHolderImpl element = (StringHolderImpl) iter.next();
            keys.add(element.getValue());
        }

        assertTrue(keys.contains(flintstoneKeyModule1));
        assertTrue(keys.contains(flintstoneKeyModule2));

        verifyAllRegisteredMocks();
    }

    public void testGetAttributeDefault()
    {
        ElementModelImpl em = new ElementModelImpl();
        em.setElementName("fred");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("wife");
        am.setDefault("wilma");

        em.addAttributeModel(am);

        SchemaElement sel = new SchemaElement(null, em);

        assertEquals("wilma", sel.getAttributeDefault("wife"));
        assertNull(sel.getAttributeDefault("husband"));
    }
}