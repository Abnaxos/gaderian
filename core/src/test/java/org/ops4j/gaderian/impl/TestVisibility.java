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

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.internal.ConfigurationPoint;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.internal.Visibility;
import org.ops4j.gaderian.parse.*;
import org.ops4j.gaderian.service.ObjectProvider;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests the logic related to service point and configuration point visibility.
 *
 * @since 1.1
 */
public class TestVisibility extends GaderianCoreTestCase
{
    private static final Log LOG = LogFactory.getLog(TestVisibility.class);

    public void testPublicConfigurationVisibleToOtherModule()
    {
        Module m = createMock(Module.class);

        Module om = createMock(Module.class);

        replayAllRegisteredMocks();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PUBLIC);

        assertEquals(true, cp.visibleToModule(om));

        verifyAllRegisteredMocks();
    }

    public void testPublicConfigurationVisibleToApplication()
    {
        Module m = createMock(Module.class);

        replayAllRegisteredMocks();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PUBLIC);

        assertEquals(true, cp.visibleToModule(null));

        verifyAllRegisteredMocks();
    }

    public void testPrivateConfigurationInvisibleToOtherModule()
    {
        Module m = createMock(Module.class);
        Module om = createMock(Module.class);

        replayAllRegisteredMocks();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PRIVATE);

        assertEquals(false, cp.visibleToModule(om));

        verifyAllRegisteredMocks();
    }

    public void testPrivateConfigurationInvisibleToApplication()
    {
        Module m = createMock(Module.class);

        replayAllRegisteredMocks();

        ConfigurationPointImpl cp = new ConfigurationPointImpl();
        cp.setModule(m);
        cp.setVisibility(Visibility.PRIVATE);

        assertEquals(false, cp.visibleToModule(null));

        verifyAllRegisteredMocks();
    }

    public void testGetServiceNotVisibleToApplication()
    {
        RegistryInfrastructureImpl rf = new RegistryInfrastructureImpl(null, null);

        ServicePoint sp = createMock(ServicePoint.class);

        // Training

        expect(sp.getExtensionPointId()).andReturn("foo.bar.Baz");

        expect(sp.getServiceInterfaceClassName()).andReturn( Runnable.class.getName());

        expect(sp.visibleToModule(null)).andReturn(false);

        replayAllRegisteredMocks();

        rf.addServicePoint(sp);

        try
        {
            rf.getService("foo.bar.Baz", Runnable.class, null);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(ImplMessages.serviceNotVisible("foo.bar.Baz", null), ex.getMessage());
        }

        verifyAllRegisteredMocks();
    }

    public void testGetServiceNotVisibleToModule()
    {
        RegistryInfrastructureImpl rf = new RegistryInfrastructureImpl(null, null);

        ServicePoint sp = createMock(ServicePoint.class);

        ModuleImpl m = new ModuleImpl();
        m.setModuleId("zip.zap.Zoom");

        // Training

        expect(sp.getExtensionPointId()).andReturn( "foo.bar.Baz");

        expect(sp.getServiceInterfaceClassName()).andReturn( Runnable.class.getName());

        expect(sp.visibleToModule(m)).andReturn( false);

        replayAllRegisteredMocks();

        rf.addServicePoint(sp);

        try
        {
            rf.getService("foo.bar.Baz", Runnable.class, m);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(ImplMessages.serviceNotVisible("foo.bar.Baz", m), ex.getMessage());
        }

        verifyAllRegisteredMocks();
    }

    public void testGetConfigurationNotVisibleToModule()
    {
        RegistryInfrastructureImpl rf = new RegistryInfrastructureImpl(null, null);

        ConfigurationPoint point = createMock(ConfigurationPoint.class);

        ModuleImpl m = new ModuleImpl();
        m.setModuleId("zip.zap.Zoom");

        // Training

        expect(point.getExtensionPointId()).andReturn( "foo.bar.Baz");

        expect(point.visibleToModule(m)).andReturn( false);

        replayAllRegisteredMocks();

        rf.addConfigurationPoint(point);

        try
        {
            rf.getConfiguration("foo.bar.Baz", m);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(ImplMessages.configurationNotVisible("foo.bar.Baz", m), ex.getMessage());
        }

        verifyAllRegisteredMocks();
    }

    /**
     * Ensure that, when searching for services (or service points) by service interface, the
     * non-visible service points are filtered out, before any complaint of too few or too many.
     *
     * @since 1.1
     */
    public void testGetServiceMatchesPublicOnly()
    {
        ServicePoint sp1 = createMock(ServicePoint.class);

        ServicePoint sp2 = createMock(ServicePoint.class);

        ObjectProvider service = (ObjectProvider) createMock(ObjectProvider.class);

        // Training

        expect(sp1.getExtensionPointId()).andReturn("foo.Private");

        expect(sp1.getServiceInterfaceClassName()).andReturn(ObjectProvider.class.getName());

        expect(sp2.getExtensionPointId()).andReturn("foo.Public");

        expect(sp2.getServiceInterfaceClassName()).andReturn(ObjectProvider.class.getName());

        expect(sp1.visibleToModule(null)).andReturn(false);

        expect(sp2.visibleToModule(null)).andReturn(true);

        expect(sp2.getService(ObjectProvider.class)).andReturn(service);

        replayAllRegisteredMocks();

        RegistryInfrastructureImpl r = new RegistryInfrastructureImpl(null, null);

        r.addServicePoint(sp1);
        r.addServicePoint(sp2);

        Object actual = r.getService(ObjectProvider.class, null);

        assertSame(service, actual);

        verifyAllRegisteredMocks();
    }

    /**
     * Much older test code would do this purely as an integration test, and use the ugly hooks in
     * GaderianTestCase to intercept the logging output. Instead, I've broken up RegistryBuilder
     * into smaller pieces that can be tested individually. However, its much easier to parse an XML
     * descriptor than to build a ModuleDescriptor instance in code.
     */

    public void testContributionToNonVisibleConfigurationPoint() throws Exception
    {
        ErrorHandler errorHandler = createMock(ErrorHandler.class);

        RegistryAssemblyImpl assembly = new RegistryAssemblyImpl();

        XmlResourceProcessor parser = new XmlResourceProcessor(getClassResolver(), errorHandler);

        RegistryInfrastructureConstructor cons = new RegistryInfrastructureConstructor(
                errorHandler, LOG, assembly);

        ModuleDescriptor md = parser.processResource(getResource("Privates.xml"));

        cons.addModuleDescriptor(md);

        md = parser.processResource(getResource("ContributePrivate.xml"));

        ContributionDescriptor cd = (ContributionDescriptor) md.getContributions().get(0);

        ImplementationDescriptor id = (ImplementationDescriptor) md.getImplementations().get(0);

        InterceptorDescriptor itd = (InterceptorDescriptor) id.getInterceptors().get(0);

        cons.addModuleDescriptor(md);

        assembly.performPostProcessing();

        // Training

        errorHandler
                .error(
                        LOG,
                        "Service point gaderian.test.privates.PrivateService is not visible to module gaderian.test.contribprivates.",
                        id.getInstanceBuilder().getLocation(),
                        null);

        errorHandler
                .error(
                        LOG,
                        "Service point gaderian.test.privates.PrivateService is not visible to module gaderian.test.contribprivates.",
                        itd.getLocation(),
                        null);

        errorHandler
                .error(
                        LOG,
                        "Configuration point gaderian.test.privates.PrivateConfig is not visible to module gaderian.test.contribprivates.",
                        cd.getLocation(),
                        null);

        errorHandler
                .error(
                        LOG,
                        "No module has contributed a service constructor for service point gaderian.test.privates.PrivateService.",
                        null,
                        null);

        replayAllRegisteredMocks();

        cons.constructRegistryInfrastructure(Locale.getDefault());

        verifyAllRegisteredMocks();
    }
}