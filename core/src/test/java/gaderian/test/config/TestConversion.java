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

import java.util.List;

import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactory;

/**
 * Tests for the &lt;conversion&gt; and &lt;map&gt; elements
 * within a module deployment descriptor.
 * 
 * TODO: test {@link org.ops4j.gaderian.parse.ConversionDescriptor#setParentMethodName(String)}.
 *
 * @author Howard Lewis Ship
 */
public class TestConversion extends FrameworkTestCase
{
    public void testBasics() throws Exception
    {
        Registry r = buildFrameworkRegistry("Basics.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Basics");

        assertEquals(1, l.size());

        DataItem d = (DataItem) l.get(0);

        assertEquals("builder", d.getName());
        assertEquals(5, d.getCount());

        ServiceImplementationFactory builderFactory =
            (ServiceImplementationFactory) r.getService(
                "gaderian.BuilderFactory",
                ServiceImplementationFactory.class);

        assertSame(builderFactory, d.getFactory());
    }

    public void testParentMethod() throws Exception
    {
        Registry r = buildFrameworkRegistry("ConversionParentMethod.xml", false );

        List l = r.getConfiguration("gaderian.test.config.ConversionParentMethod");

        assertEquals(1, l.size());

        DataItem d = (DataItem) l.get(0);

        assertEquals("builder", d.getName());
        assertEquals(5, d.getCount());

        ServiceImplementationFactory builderFactory =
            (ServiceImplementationFactory) r.getService(
                "gaderian.BuilderFactory",
                ServiceImplementationFactory.class);

        assertSame(builderFactory, d.getFactory());
    }

    public void testPropertyNameDefaultsToAttributeName() throws Exception
    {
        Registry r = buildFrameworkRegistry("Basics2.xml", false );

        List l = r.getConfiguration("gaderian.test.config.Basics2");

        assertEquals(1, l.size());

        DataItem d = (DataItem) l.get(0);

        assertEquals("underworld", d.getName());
        assertEquals(18, d.getCount());

        ServiceImplementationFactory builderFactory =
            (ServiceImplementationFactory) r.getService(
                "gaderian.BuilderFactory",
                ServiceImplementationFactory.class);

        assertSame(builderFactory, d.getFactory());
    }

    public void testComplexAttributeName() throws Exception
    {
        Registry r = buildFrameworkRegistry("ComplexAttributeName.xml", false );

        List l = r.getConfiguration("gaderian.test.config.ComplexAttributeName");

        assertEquals(1, l.size());

        ComplexNameItem cni = (ComplexNameItem) l.get(0);

        assertEquals("fred", cni.getComplexAttributeName());
    }

    public void testExtraAttributeNames() throws Exception
    {
        interceptLogging();

        Registry r = buildFrameworkRegistry("ExtraAttributeNames.xml", false );

        assertLoggedMessagePattern(
            "Mappings for unknown attribute\\(s\\) \\[extra\\] "
                + "\\(for element data-item\\) have been ignored\\.");

        List l = r.getConfiguration("gaderian.test.config.ExtraAttributeNames");

        assertEquals(1, l.size());

        DataItem d = (DataItem) l.get(0);

        assertEquals("lamb", d.getName());
        assertEquals(95, d.getCount());

        ServiceImplementationFactory builderFactory =
            (ServiceImplementationFactory) r.getService(
                "gaderian.BuilderFactory",
                ServiceImplementationFactory.class);

        assertSame(builderFactory, d.getFactory());
    }

    public void testDuplicateAttribute() throws Exception
    {
        interceptLogging();

		Registry r = buildFrameworkRegistry("DuplicateAttribute.xml", false );

        assertLoggedMessagePattern(
            "Mapping for attribute item-name conflicts with a previous "
                + "mapping \\(at .*\\) and has been ignored\\.");

        List l = r.getConfiguration("gaderian.test.config.DuplicateAttribute");

        assertEquals(1, l.size());

        DataItem d = (DataItem) l.get(0);

        assertEquals("wesley", d.getName());
        assertEquals(15, d.getCount());

        ServiceImplementationFactory builderFactory =
            (ServiceImplementationFactory) r.getService(
                "gaderian.BuilderFactory",
                ServiceImplementationFactory.class);

        assertSame(builderFactory, d.getFactory());
    }
}
