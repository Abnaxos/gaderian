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

package gaderian.test.config;

import gaderian.test.FrameworkTestCase;
import gaderian.test.config.impl.BooleanHolder;
import gaderian.test.config.impl.Child;
import gaderian.test.config.impl.Datum;
import gaderian.test.config.impl.DatumHolder;
import gaderian.test.config.impl.FrobableHolder;
import gaderian.test.config.impl.IntHolder;
import gaderian.test.config.impl.Parent;
import gaderian.test.config.impl.ResourceHolder;

import java.util.List;
import java.util.Locale;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.impl.RegistryBuilder;
import org.ops4j.gaderian.impl.XmlModuleDescriptorProvider;
import org.ops4j.gaderian.util.ClasspathResource;

/**
 * A number of tests related to processing of extension points.
 *
 * @author Howard Lewis Ship
 */
public class TestConfigurationPoint extends FrameworkTestCase
{

    public void testEmpty() throws Exception
    {
        Registry r = buildFrameworkRegistry("Empty.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Empty");

        assertEquals(0, l.size());
    }

    public void testSimple() throws Exception
    {
        Registry r = buildFrameworkRegistry("Simple.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Simple");

        assertEquals(2, l.size());

        Datum d = (Datum) l.get(0);

        assertEquals("key1", d.getKey());
        assertEquals("value1", d.getValue());
        assertNotNull(d.getLocation());

        d = (Datum) l.get(1);

        assertEquals("key2", d.getKey());
        assertEquals("value2", d.getValue());
    }

    public void testNullElement() throws Exception
    {
        Registry r = buildFrameworkRegistry("Null.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Null");

        assertEquals(1, l.size());

        assertNull(l.get(0));
    }

    public void testAttributeDefaults() throws Exception
    {
        Registry r = buildFrameworkRegistry("AttributeDefaults.xml", false );

        List l = r.getConfiguration("gaderian.test.config.AttributeDefaults");

        assertEquals(1, l.size());
        Datum d = (Datum) l.get(0);

        assertEquals("DEFAULT_KEY", d.getKey());
        assertNull(d.getValue());
    }

    public void testNested() throws Exception
    {
        Registry r = buildFrameworkRegistry("Nested.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Nested");

        assertEquals(1, l.size());

        DatumHolder h = (DatumHolder) l.get(0);

        assertListsEqual(new Object[]
        { "fred", "wilma" }, h.getKeys());

        Datum d = h.getDatum("fred");
        assertNotNull(d.getLocation());
        assertEquals("barney", d.getValue());
    }

    public void testStructured() throws Exception
    {
        Registry r = buildFrameworkRegistry("Structured.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Structured");

        assertEquals(2, l.size());

        Datum d = (Datum) l.get(0);

        assertEquals("key_1", d.getKey());
        assertEquals("value_1", d.getValue());
        assertNotNull(d.getLocation());

        d = (Datum) l.get(1);

        assertEquals("key_2", d.getKey());
        assertEquals("value_2", d.getValue());
    }

    public void testSetParent() throws Exception
    {
        Registry r = buildFrameworkRegistry("SetParent.xml", false );

        List l = r.getConfiguration("gaderian.test.config.SetParent");

        assertEquals(1, l.size());

        Parent p1 = (Parent) l.get(0);

        assertEquals("key1", p1.getKey());
        assertEquals("value1", p1.getValue());

        l = p1.getChildren();
        assertEquals(2, l.size());

        Child c1 = (Child) l.get(0);

        assertSame(p1, c1.getParent());

        assertEquals("detailkey1", c1.getKey());
        assertEquals("detailvalue1", c1.getValue());

        Child c2 = (Child) l.get(1);

        assertSame(p1, c2.getParent());

        assertEquals("detailkey2", c2.getKey());
        assertEquals("detailvalue2", c2.getValue());
    }

    public void testBooleanTranslator() throws Exception
    {
        interceptLogging();

        Registry r = buildFrameworkRegistry("BooleanTranslator.xml", false );

        List l = r.getConfiguration("gaderian.test.config.BooleanTranslator");

        // Trigger the proxy

        l.size();

        assertLoggedMessagePattern("Unable to process attribute value \\(of element flag\\): "
                + "'maybe' is not a boolean value \\(which should be either 'true' or 'false'\\)\\.");

        assertEquals(3, l.size());

        BooleanHolder h = (BooleanHolder) l.get(0);

        assertEquals(true, h.getValue());

        h = (BooleanHolder) l.get(1);
        assertEquals(false, h.getValue());

        h = (BooleanHolder) l.get(2);
        assertEquals(false, h.getValue());
    }

    public void testIntTranslator() throws Exception
    {
        interceptLogging();

        Registry r = buildFrameworkRegistry("IntTranslator.xml", false );

        List l = r.getConfiguration("gaderian.test.config.IntTranslator");

        // Convert the proxy into a real list.

        l.size();

        List events = getInterceptedLogEvents();

        assertLoggedMessagePattern("Unable to process attribute value \\(of element int\\): "
                + "Value 2 is less than minimum value 5\\.", events);
        assertLoggedMessagePattern("Value 12 is greater than maximum value 10\\.", events);
        assertLoggedMessagePattern("'fred' is not an integer value\\.", events);

        assertEquals(5, l.size());

        IntHolder h = (IntHolder) l.get(0);

        assertEquals(7, h.getValue());

        h = (IntHolder) l.get(1);
        assertEquals(0, h.getValue());

        h = (IntHolder) l.get(2);

        assertEquals(0, h.getValue());

        h = (IntHolder) l.get(3);

        assertEquals(0, h.getValue());

        h = (IntHolder) l.get(3);

        assertEquals(0, h.getValue());

    }

    public void testInstanceTranslator() throws Exception
    {
        Registry r = buildFrameworkRegistry("InstanceTranslator.xml", false );

        List l = r.getConfiguration("gaderian.test.config.InstanceTranslator");

        assertEquals(1, l.size());

        FrobableHolder h = (FrobableHolder) l.get(0);

        Frobable f = h.getFrobable();

        assertNotNull(f);
        assertEquals(true, f.frob());
    }

    public void testSymbols() throws Exception
    {
        interceptLogging();

        Registry r = buildFrameworkRegistry("Symbols.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Symbols");

        assertEquals(3, l.size());

        Datum d = (Datum) l.get(0);

        assertEquals("wife", d.getKey());
        assertEquals("wilma", d.getValue());

        d = (Datum) l.get(1);

        assertEquals("husband", d.getKey());
        assertEquals("fred", d.getValue());

        d = (Datum) l.get(2);

        assertEquals("work", d.getKey());
        assertEquals("${work}", d.getValue());

        assertLoggedMessagePattern("No value available for symbol 'work'");
    }

    public void testNoSchema() throws Exception
    {
        Registry r = buildFrameworkRegistry("NoSchema.xml", false );

        List l = r.getConfiguration("gaderian.test.config.NoSchema");

        assertEquals(2, l.size());

        Element e = (Element) l.get(0);
        assertEquals("datum", e.getElementName());
        assertEquals("key1", e.getAttributeValue("key"));
        assertEquals("value1", e.getAttributeValue("value"));

        // Show that symbols are NOT expanded in non-schema
        // contributions.

        e = (Element) l.get(1);
        assertEquals("datum", e.getElementName());
        assertEquals("key2", e.getAttributeValue("key"));
        assertEquals("${value2}", e.getAttributeValue("value"));
    }

    public void testLocalized() throws Exception
    {
        Registry r = buildFrameworkRegistry("Localized.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Localized");
        assertEquals(1, l.size());

        Datum d = (Datum) l.get(0);

        assertEquals("message", d.getKey());
        assertEquals("Some Damn Thing", d.getValue());
    }

    public void testElementsProxyList() throws Exception
    {
        Registry r = buildFrameworkRegistry("Simple.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Simple");

        assertEquals("<Element List Proxy for gaderian.test.config.Simple>", l.toString());

        assertEquals(true, l.equals(l));
        assertEquals(false, l.equals(null));

        assertEquals(2, l.size());

        List l2 = r.getConfiguration("gaderian.test.config.Simple");

        assertNotSame(l, l2);
        assertEquals(l, l2);

        assertEquals(l2.toString(), l.toString());
    }

    public void testTooFew() throws Exception
    {

        interceptLogging(RegistryBuilder.class.getName());

        Registry r = buildFrameworkRegistry("TooFew.xml", false );

        r.getConfiguration("gaderian.test.config.TooFew");

        assertLoggedMessage("Configuration point gaderian.test.config.TooFew contains no contributions but expects at least one contribution.");

    }

    public void testTooMany() throws Exception
    {
        interceptLogging();

        Registry r = buildFrameworkRegistry("TooMany.xml", false );

        r.getConfiguration("gaderian.test.config.TooMany");

        assertLoggedMessage("Configuration point gaderian.test.config.TooMany contains 2 contributions but expects an optional contribution.");
    }

    public void testBadAttributes() throws Exception
    {
        Registry r = buildFrameworkRegistry("BadAttributes.xml", false );

        List l = r.getConfiguration("gaderian.test.config.BadAttributes");

        try
        {
            l.size();

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            Throwable t = findNestedException(ex);
            assertExceptionSubstring(t, "Element datum (at");
            assertExceptionSubstring(
                    t,
                    "Attribute 'xey' is not defined in the schema. Attribute 'key' is required but no value was provided.");
        }
    }

    public void testBadElement() throws Exception
    {
        Registry r = buildFrameworkRegistry("BadElement.xml", false );

        interceptLogging("gaderian.test.config");

        List l = r.getConfiguration("gaderian.test.config.BadElement");

        assertEquals(1, l.size());

        assertLoggedMessagePattern("Error at .*?: Element xatum is not allowed here\\.");
    }

    public void testCustomRule() throws Exception
    {
        Registry r = buildFrameworkRegistry("CustomRule.xml", false );

        List l = r.getConfiguration("gaderian.test.config.CustomRule");

        Datum d = (Datum) l.get(0);

        // Put this check second, just to get some code coverage
        // on ElementsInnerProxyList

        assertEquals(2, l.size());

        assertEquals("gaderian.test.config", d.getContributingModule().getModuleId());
    }

    public void testCustomRuleFailure() throws Exception
    {
        try
        {
            parse("CustomRuleFailure.xml");

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to create instance of Rule class gaderian.test.config.XetContributingModuleRule");
        }
    }

    public void testResourceTranslator() throws Exception
    {
        RegistryBuilder builder = new RegistryBuilder();

        Resource moduleResource = new ClasspathResource(_resolver,
                "/gaderian/test/config/ResourceTranslator.xml");

        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(_resolver));
        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(_resolver,
                moduleResource));

        Registry r = builder.constructRegistry(Locale.FRENCH);

        List l = r.getConfiguration("gaderian.test.config.ResourceTranslator");

        interceptLogging();

        assertEquals(4, l.size());

        ResourceHolder h = (ResourceHolder) l.get(0);

        assertEquals(moduleResource.getRelativeResource("Empty.xml"), h.getResource());

        h = (ResourceHolder) l.get(1);

        assertEquals(moduleResource.getRelativeResource("Localized_fr.properties"), h.getResource());

        h = (ResourceHolder) l.get(2);
        assertNull(h.getResource());

        h = (ResourceHolder) l.get(3);
        assertNull(h.getResource());

        assertLoggedMessagePattern("Unable to process content of element resource: "
                + "Unable to localize resource DoesNotExist\\.xml for module gaderian\\.test\\.config\\.");
    }

    public void testShutdown() throws Exception
    {
        Registry r = buildFrameworkRegistry("Simple.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Simple");

        assertEquals(2, l.size());

        r.shutdown();

        try
        {
            l.size();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The Gaderian Registry has been shutdown.");
        }

    }

    /**
     * Test for contribution to unknown configuration extension point.
     */

    public void testUnknownContribution() throws Exception
    {
        interceptLogging();

        buildFrameworkRegistry("UnknownContribution.xml", false );

        assertLoggedMessagePattern("Module gaderian\\.test\\.config has contributed to unknown configuration point UnresolvedSchema\\. "
                + "The contribution has been ignored\\.");

    }
}