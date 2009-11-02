// Copyright 2005 The Apache Software Foundation
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

package org.ops4j.gaderian.management;

import java.util.List;
import javax.management.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.easymock.EasyMock.*;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.events.RegistryShutdownListener;
import org.ops4j.gaderian.impl.DefaultErrorHandler;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.management.impl.MBeanRegistrationContribution;
import org.ops4j.gaderian.management.impl.MBeanRegistryImpl;
import org.ops4j.gaderian.management.impl.ObjectNameBuilderImpl;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Test of {@link org.ops4j.gaderian.management.impl.MBeanRegistryImpl}.
 *
 * @author Achim Huegen
 * @since 1.1
 */
public class TestMBeanRegistry extends GaderianCoreTestCase
{
    private ErrorHandler errorHandler;

    private Log log;

    private MBeanServer server;

    private ObjectNameBuilder objectNameBuilder;

    public void setUp()
    {
        errorHandler = new DefaultErrorHandler();
        log = LogFactory.getLog(MBeanRegistry.class);
        server = createMock(MBeanServer.class);
        objectNameBuilder = new ObjectNameBuilderImpl();
    }

    /**
     * Tests the registration of MBeans via contribution
     */
    public void testContribution() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testMBeanRegistry.xml", false );
        List mBeanList = registry.getConfiguration("gaderian.management.MBeans");

        // Training
        ServicePoint sp1 = ((MBeanRegistrationContribution) mBeanList.get(0)).getServicePoint();
        Object mBean1 = registry.getService("test.management.MBean1", Runnable.class);
        ObjectName on1 = objectNameBuilder.createServiceObjectName(sp1);

        ObjectInstance oin1 = new ObjectInstance(on1, mBean1.getClass().getName());

        expect(server.registerMBean(mBean1, on1)).andReturn(oin1);

        Object mBean2 = registry.getService("test.management.MBean2", Runnable.class);
        ObjectName on2 = new ObjectName("gaderian:name=bean2");
        expect(server.registerMBean(mBean2, on2)).andReturn(new ObjectInstance(on2, mBean2.getClass().getName()));

        // This is a special case. A class without interface
        Object mBean3 = registry.getService("test.management.MBean3", MBeanNonInterfaceTestService.class);
        ObjectName on3 = new ObjectName("gaderian:name=bean3");
        expect(server.registerMBean(mBean3, on3)).andReturn(new ObjectInstance(on3, mBean3.getClass().getName()));

        // Call from unregisterBean
        expect(server.getObjectInstance(on1)).andReturn(oin1);

        server.unregisterMBean(on1);
        // The automatically unregistered beans get unregistered in reverse order
        server.unregisterMBean(on3);
        server.unregisterMBean(on2);

        replayAllRegisteredMocks();

        MBeanRegistry mbeanRegistry = new MBeanRegistryImpl(errorHandler, log, server,
                objectNameBuilder, mBeanList);

        // Unregister one bean manually the other one during registry shutdown
        mbeanRegistry.unregisterMBean(on1);
        ((RegistryShutdownListener) mbeanRegistry).registryDidShutdown();

        verifyAllRegisteredMocks();

        assertTrue("start method has not been called", ((MBeanTestService) mBean1).isStartCalled());

        registry.shutdown();
    }

    /**
     * Tests the handling of a not compliant mbean
     */
    public void testNotCompliantHandling() throws Exception
    {
        Calculator calculatorMBean = new CalculatorImpl();
        ObjectName objectName = new ObjectName("gaderian:module=test");

        // Training
        expect(server.registerMBean(calculatorMBean, objectName)).andThrow(new NotCompliantMBeanException("Not compliant"));
        replayAllRegisteredMocks();

        // Registration must fail since the bean is not mbean compliant and a management
        // interface is not provided
        MBeanRegistry mbeanRegistry = new MBeanRegistryImpl(errorHandler, log, server,
                objectNameBuilder, null);
        try
        {
            mbeanRegistry.registerMBean(calculatorMBean, null, objectName);
            fail("Not compliant MBean registered");
        }
        catch (NotCompliantMBeanException expected)
        {
        }

        verifyAllRegisteredMocks();
    }

    /**
     * Tests the handling of registrations errors during processing of the contributed mbeans
     */
    public void testRegistrationException() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testMBeanRegistry.xml", false );
        List mBeanList = registry.getConfiguration("gaderian.management.MBeans");

        ServicePoint sp1 = ((MBeanRegistrationContribution) mBeanList.get(0)).getServicePoint();
        ObjectName on1 = objectNameBuilder.createServiceObjectName(sp1);

        // Training
        expect(server.registerMBean( isA( MBeanTestService.class ), eq(on1))).andThrow(new MBeanRegistrationException(new Exception("Registration failed")));
        expect(server.registerMBean( isA( MBeanTestService.class ), isA( ObjectName.class ))).andThrow(new MBeanRegistrationException(new Exception("Registration failed")));
        expect(server.registerMBean( isA( MBeanNonInterfaceTestService.class ), isA( ObjectName.class ))).andThrow(new MBeanRegistrationException(new Exception("Registration failed")));

        replayAllRegisteredMocks();

        interceptLogging(MBeanRegistry.class.getName());

        new MBeanRegistryImpl(errorHandler, log, server, objectNameBuilder, mBeanList);

        assertLoggedMessage("Registering MBean " + on1.toString() + " failed");

        verifyAllRegisteredMocks();
    }

    /**
     * Ensures that a bean that doesn't implement one of the standard JMX
     * interfaces (like DynamicMBean) is registered as StandardMBean.
     */
    public void testStandardMBean() throws Exception
    {
        Calculator calculatorMBean = new CalculatorImpl();
        ObjectName objectName = new ObjectName("gaderian:module=test");

        // Training
        expect(server.registerMBean(calculatorMBean, objectName)).andThrow(new NotCompliantMBeanException("Not compliant"));

        expect(server.registerMBean( isA(StandardMBean.class), eq(objectName))).andReturn( new ObjectInstance(objectName, StandardMBean.class.getName()) );
        
        replayAllRegisteredMocks();

        MBeanRegistry mbeanRegistry = new MBeanRegistryImpl(errorHandler, log, server,
                objectNameBuilder, null);

        // Management interface is specified
        mbeanRegistry.registerMBean(calculatorMBean, Calculator.class, objectName);

        verifyAllRegisteredMocks();
    }

}