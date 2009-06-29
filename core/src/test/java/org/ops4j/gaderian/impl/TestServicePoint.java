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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServiceModel;
import org.ops4j.gaderian.internal.ServiceModelFactory;
import org.ops4j.gaderian.test.GaderianTestCase;
import org.easymock.MockControl;

/**
 * Test for {@link org.ops4j.gaderian.impl.ServicePointImpl}. Much of the testing is done using
 * integration style tests, this just fills in some important gaps.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestServicePoint extends GaderianTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();

        result.setClassResolver(getClassResolver());
        result.setPackageName("");
        result.setRegistry(new RegistryInfrastructureImpl(new StrictErrorHandler(), Locale
                .getDefault()));
        return result;
    }

    public void testUnknownInterfaceClass()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ServicePointImpl sp = new ServicePointImpl();
        sp.setModule(module);
        sp.setServiceInterfaceName("foo.bar.Baz");
        sp.setExtensionPointId("zip.zap");
        sp.setLocation(l);

        try
        {
            sp.getServiceInterface();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Unable to find interface foo.bar.Baz (for service zip.zap).", ex
                    .getMessage());
            assertSame(l, ex.getLocation());
        }

        verifyControls();
    }

    public void testDefaultInterceptorOrdering()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ServicePointImpl sp = new ServicePointImpl();
        sp.setModule(module);
        sp.setServiceInterfaceName("foo.bar.Baz");
        sp.setExtensionPointId("zip.zap");
        sp.setLocation(l);
        final ServiceInterceptorContributionImpl interceptor1 = new ServiceInterceptorContributionImpl();
        interceptor1.setFactoryServiceId("SomeFactory1");
        sp.addInterceptorContribution(interceptor1);
        final ServiceInterceptorContributionImpl interceptor2 = new ServiceInterceptorContributionImpl();
        interceptor2.setFactoryServiceId("SomeFactory2");
        sp.addInterceptorContribution(interceptor2);
        sp.setExtensionPointId("ExtensionPointId");
        final List ordered = sp.getOrderedInterceptorContributions();
        assertNotNull(ordered);
        assertEquals(2, ordered.size());
        assertEquals(interceptor1, ordered.get(0));
        assertEquals(interceptor2, ordered.get(1));
        verifyControls();
    }

    public void testCustomInterceptorOrdering()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ServicePointImpl sp = new ServicePointImpl();
        sp.setModule(module);
        sp.setServiceInterfaceName("foo.bar.Baz");
        sp.setExtensionPointId("zip.zap");
        sp.setLocation(l);
        final ServiceInterceptorContributionImpl interceptor1 = new ServiceInterceptorContributionImpl();
        interceptor1.setFactoryServiceId("SomeFactory1");
        sp.addInterceptorContribution(interceptor1);
        final ServiceInterceptorContributionImpl interceptor2 = new ServiceInterceptorContributionImpl();
        interceptor2.setFactoryServiceId("SomeFactory2");
        interceptor2.setFollowingInterceptorIds("SomeFactory1");
        sp.addInterceptorContribution(interceptor2);
        sp.setExtensionPointId("ExtensionPointId");
        final List ordered = sp.getOrderedInterceptorContributions();
        assertNotNull(ordered);
        assertEquals(2, ordered.size());
        assertEquals(interceptor2, ordered.get(0));
        assertEquals(interceptor1, ordered.get(1));
        verifyControls();
    }

    public void testInterceptorOrderingByName()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ServicePointImpl sp = new ServicePointImpl();
        sp.setModule(module);
        sp.setServiceInterfaceName("foo.bar.Baz");
        sp.setExtensionPointId("zip.zap");
        sp.setLocation(l);
        final ServiceInterceptorContributionImpl interceptor1 = new ServiceInterceptorContributionImpl();
        interceptor1.setFactoryServiceId("SomeFactory1");
        interceptor1.setName("Interceptor1");
        sp.addInterceptorContribution(interceptor1);
        final ServiceInterceptorContributionImpl interceptor2 = new ServiceInterceptorContributionImpl();
        interceptor2.setFactoryServiceId("SomeFactory2");
        interceptor2.setFollowingInterceptorIds("Interceptor1");
        sp.addInterceptorContribution(interceptor2);
        sp.setExtensionPointId("ExtensionPointId");
        final List ordered = sp.getOrderedInterceptorContributions();
        assertNotNull(ordered);
        assertEquals(2, ordered.size());
        assertEquals(interceptor2, ordered.get(0));
        assertEquals(interceptor1, ordered.get(1));
        verifyControls();
    }

    public void testResultNotAssignableToServiceInterface()
    {
        Location l = newLocation();
        ServicePointImpl sp = new ServicePointImpl();

        MockControl modulec = newControl(Module.class);
        Module module = (Module) modulec.getMock();

        Object service = new ArrayList();

        MockControl factoryc = newControl(ServiceModelFactory.class);
        ServiceModelFactory factory = (ServiceModelFactory) factoryc.getMock();

        MockControl modelc = newControl(ServiceModel.class);
        ServiceModel model = (ServiceModel) modelc.getMock();

        module.getServiceModelFactory("fred");
        modulec.setReturnValue(factory);

        factory.createServiceModelForService(sp);
        factoryc.setReturnValue(model);

        model.getService();
        modelc.setReturnValue(service);

        module.resolveType("java.util.List");
        modulec.setReturnValue(List.class);

        replayControls();

        sp.setExtensionPointId("foo.bar");
        sp.setServiceInterfaceName("java.util.List");
        sp.setLocation(l);
        sp.setModule(module);
        sp.setServiceModel("fred");

        try
        {
            sp.getService(Map.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Service foo.bar does not implement the requested interface (java.util.Map).  The declared service interface type is java.util.List.",
                    ex.getMessage());
            assertSame(l, ex.getLocation());
        }
    }
}