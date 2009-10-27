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

package org.ops4j.gaderian.impl.servicemodel;

import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.test.GaderianTestCase;

/**
 * @author James Carman
 * @version 1.0
 */
public class TestRegistryShutdownListenerServices extends GaderianTestCase
{
    private void executeShutdownListenerTest(String type) throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml", false );
        Simple simple = (Simple) registry.getService("gaderian.test." + type + "Simple", Simple.class);
        final Counter counter = new Counter();
        simple.setCounter(counter);
        registry.shutdown();
        assertEquals(1, counter.getValue());
    }

    public void testPooledCalled() throws Exception
    {
        executeShutdownListenerTest("pooledManual");
        executeShutdownListenerTest("pooledAuto");
    }

    public void testSingleton() throws Exception
    {
        executeShutdownListenerTest("singletonManual");
        executeShutdownListenerTest("singletonAuto");
    }

    public void testPrimitive() throws Exception
    {
        executeShutdownListenerTest("primitiveManual");
        executeShutdownListenerTest("primitiveAuto");
    }

    public void testSingletonBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml", false );
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "gaderian.test.registryShutdownBeanSingleton", RegistryShutdownBean.class );
        bean.someMethod();
    }

    public void testThreadedBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml", false );
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "gaderian.test.registryShutdownBeanThreaded", RegistryShutdownBean.class );
        bean.someMethod();
    }
    public void testPooledBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml", false );
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "gaderian.test.registryShutdownBeanPooled", RegistryShutdownBean.class );
        bean.someMethod();
    }
    public void testPrimitiveBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = buildFrameworkRegistry("ShutdownListenerServices.xml", false );
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "gaderian.test.registryShutdownBeanPrimitive", RegistryShutdownBean.class );
        bean.someMethod();
    }
}
