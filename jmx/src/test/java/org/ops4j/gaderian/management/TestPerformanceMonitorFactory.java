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

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Test of {@link org.ops4j.gaderian.management.impl.PerformanceMonitorFactory}.
 *
 * @author Achim Huegen
 * @since 1.1
 */
public class TestPerformanceMonitorFactory extends GaderianCoreTestCase
{
    /**
     * Tests the creation of a performance interceptor via the registry
     */
    public void testIntegration() throws Exception
    {
        Registry registry = buildFrameworkRegistry("testPerformanceMonitorFactory.xml", false );

        // Get Calculator service so that interceptor gets created
        Calculator calculator = (Calculator) registry.getService(Calculator.class);
        calculator.getSum();

        // Look for the mbean that collects the performance data
        MBeanServer mbeanServer = (MBeanServer) registry.getService(MBeanServer.class);
        ObjectName objectName = new ObjectName(
                "gaderian:module=test.management,type=service,id=Calculator,decorator=PerformanceCollector");
        ObjectInstance instance = mbeanServer.getObjectInstance(objectName);
        assertNotNull(instance);

        // Execute some operations for code coverage
        calculator.add(123);


        // Shutdown the registry, to make sure the JMX server is shutdown

        registry.shutdown();
    }

}