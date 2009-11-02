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

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.ConfigurationPoint;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.internal.Visibility;
import org.ops4j.gaderian.parse.ConfigurationPointDescriptor;
import org.ops4j.gaderian.parse.ContributionDescriptor;
import org.ops4j.gaderian.parse.ModuleDescriptor;
import org.ops4j.gaderian.schema.impl.SchemaImpl;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link RegistryInfrastructureConstructor}.
 *
 * @author Knut Wannheden
 * @since 1.1
 */
public class TestRegistryInfrastructureConstructor extends GaderianCoreTestCase
{
    public void testFound()
    {
        SchemaImpl schema = new SchemaImpl();
        schema.setId("Baz");

        DefaultErrorHandler errorHandler = new DefaultErrorHandler();

        ModuleDescriptor fooBar = new ModuleDescriptor(null, errorHandler);
        fooBar.setModuleId("foo.bar");

        fooBar.addSchema(schema);

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, errorHandler);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");

        zipZoop.addConfigurationPoint(cpd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(errorHandler,
                LogFactory.getLog(TestRegistryInfrastructureConstructor.class), null);

        ric.addModuleDescriptor(fooBar);
        ric.addModuleDescriptor(zipZoop);

        RegistryInfrastructure registry = ric.constructRegistryInfrastructure(Locale.getDefault());

        ConfigurationPoint point = registry.getConfigurationPoint("zip.zoop.Zap", null);
        assertEquals(schema, point.getContributionsSchema());
    }

    public void testNotVisible()
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        SchemaImpl schema = new SchemaImpl();
        schema.setId("Baz");
        schema.setVisibility(Visibility.PRIVATE);

        Location l = newLocation();

        eh.error(log, ImplMessages.schemaNotVisible("foo.bar.Baz", "zip.zoop"), l, null);

        replayAllRegisteredMocks();

        ModuleDescriptor fooBar = new ModuleDescriptor(null, eh);
        fooBar.setModuleId("foo.bar");

        fooBar.addSchema(schema);

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, eh);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");
        cpd.setLocation(l);

        zipZoop.addConfigurationPoint(cpd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(fooBar);
        ric.addModuleDescriptor(zipZoop);

        ric.constructRegistryInfrastructure(Locale.getDefault());

        verifyAllRegisteredMocks();
    }

    public void testNotFound()
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        Location l = newLocation();

        eh.error(log, ImplMessages.unableToResolveSchema("foo.bar.Baz"), l, null);

        replayAllRegisteredMocks();

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, eh);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");
        cpd.setLocation(l);

        zipZoop.addConfigurationPoint(cpd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(zipZoop);

        ric.constructRegistryInfrastructure(Locale.getDefault());

        verifyAllRegisteredMocks();
    }

    private Element newElement(String name)
    {
        ElementImpl e = new ElementImpl();

        e.setElementName(name);

        return e;
    }

    public void testConditionalExpressionTrue()
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        replayAllRegisteredMocks();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("class " + Location.class.getName());

        cd.addElement(newElement("foo"));

        md.addContribution(cd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(md);

        RegistryInfrastructure ri = ric.constructRegistryInfrastructure(Locale.getDefault());

        List l = ri.getConfiguration("zip.zoop.Fred", null);

        Element e = (Element) l.get(0);

        assertEquals("foo", e.getElementName());

        verifyAllRegisteredMocks();
    }

    public void testConditionalExpressionFalse()
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        replayAllRegisteredMocks();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("class foo.bar.Baz");

        cd.addElement(newElement("bar"));

        md.addContribution(cd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(md);

        RegistryInfrastructure ri = ric.constructRegistryInfrastructure(Locale.getDefault());

        List l = ri.getConfiguration("zip.zoop.Fred", null);

        assertTrue(l.isEmpty());

        verifyAllRegisteredMocks();
    }

    public void testConditionalExpressionError()
    {
        ErrorHandler eh = createMock(ErrorHandler.class);

        Log log = LogFactory.getLog(TestRegistryInfrastructureConstructor.class);

        Location location = newLocation();

        eh.error( eq(log),
                eq("Unexpected token <AND> in expression 'and class foo'."),
                eq(location),
                isA(RuntimeException.class));

        replayAllRegisteredMocks();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("and class foo");
        cd.setLocation(location);

        cd.addElement(newElement("bar"));

        md.addContribution(cd);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(eh, log, null);

        ric.addModuleDescriptor(md);

        RegistryInfrastructure ri = ric.constructRegistryInfrastructure(Locale.getDefault());

        List l = ri.getConfiguration("zip.zoop.Fred", null);

        assertTrue(l.isEmpty());

        verifyAllRegisteredMocks();
    }
}