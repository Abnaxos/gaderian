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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Gaderian;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.service.impl.ClassFactoryImpl;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.impl.ServicePropertyFactory}.
 *
 * @author Howard Lewis Ship
 */
public class TestServicePropertyFactory extends GaderianCoreTestCase
{
    private ServicePropertyFactory newFactory()
    {
        ServicePropertyFactory result = new ServicePropertyFactory();

        result.setClassFactory(new ClassFactoryImpl());

        return result;
    }

    private List newParameters(WonkSource service, String propertyName)
    {
        ServicePropertyFactoryParameter p = new ServicePropertyFactoryParameter();

        final ServicePoint sp= createMock( ServicePoint.class );

        expect(sp.getServiceInterface()).andReturn( WonkSource.class );
        expect(sp.getService( WonkSource.class )).andReturn( service );
        p.setServicePoint(sp);
        p.setPropertyName(propertyName);
        p.setLocation(newLocation());

        return Collections.singletonList(p);
    }

    public void testSuccess()
    {
        ServicePropertyFactory f = newFactory();

        Wonk wonk = createMock(Wonk.class);

        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        List parameters = newParameters(new WonkHolder(wonk), "wonk");

        wonk.wonkVoid();
        expect(wonk.wonkString("zebra")).andReturn("stripes");

        expect(fp.getParameters()).andReturn(parameters);

        expect(fp.getServiceInterface()).andReturn(Wonk.class);

        expect(fp.getServiceId()).andReturn("foo.bar");

        replayAllRegisteredMocks();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation(fp);

        proxy.wonkVoid();
        assertEquals("stripes", proxy.wonkString("zebra"));

        assertEquals(
                "<ServicePropertyProxy foo.bar(org.ops4j.gaderian.impl.Wonk) for property 'wonk' of <WonkHolder>>",
                proxy.toString());

        verifyAllRegisteredMocks();
    }

    public void testWithJdkProxies()
    {
    	ServicePropertyFactory f = newFactory();

        Wonk wonk = createMock(Wonk.class);

        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        final WonkHolder delegate = new WonkHolder( wonk );
        final WonkSource jdkProxy = ( WonkSource )Proxy.newProxyInstance( WonkSource.class.getClassLoader(), new Class[] { WonkSource.class }, new InvocationHandler()
        		{
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return method.invoke( delegate, args );
					}

        		} );
        List parameters = newParameters(jdkProxy, "wonk");

        wonk.wonkVoid();
        expect(wonk.wonkString("zebra")).andReturn("stripes");

        expect(fp.getParameters()).andReturn(parameters);

        expect(fp.getServiceInterface()).andReturn(Wonk.class);

        expect(fp.getServiceId()).andReturn("foo.bar");

        replayAllRegisteredMocks();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation(fp);

        proxy.wonkVoid();
        assertEquals("stripes", proxy.wonkString("zebra"));

        assertEquals(
                "<ServicePropertyProxy foo.bar(org.ops4j.gaderian.impl.Wonk) for property 'wonk' of <WonkHolder>>",
                proxy.toString());

        verifyAllRegisteredMocks();
    }
    public void testPropertyNull()
    {
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "wonk");

       expect(fp.getParameters()).andReturn(parameters);

        expect(fp.getServiceInterface()).andReturn(Wonk.class);

        expect(fp.getServiceId()).andReturn("foo.bar");

        replayAllRegisteredMocks();

        Wonk proxy = (Wonk) f.createCoreServiceImplementation(fp);

        try
        {
            proxy.wonkVoid();
            unreachable();
        }
        catch (NullPointerException ex)
        {
            assertEquals("Property 'wonk' of <WonkHolder> is null.", ex.getMessage());
        }

        verifyAllRegisteredMocks();
    }

    public void testWriteOnlyProperty()
    {
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "writeOnly");

        expect(fp.getParameters()).andReturn(parameters);

        replayAllRegisteredMocks();

        try
        {
            f.createCoreServiceImplementation(fp);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Property 'writeOnly' of <WonkHolder> is not readable.", ex.getMessage());
            assertEquals(Gaderian.getLocation(parameters.get(0)), ex.getLocation());
        }

        verifyAllRegisteredMocks();
    }

    public void testPropertyTypeMismatch()
    {
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        ServicePropertyFactory f = newFactory();

        List parameters = newParameters(new WonkHolder(null), "class");

       expect(fp.getParameters()).andReturn(parameters);

       expect(fp.getServiceInterface()).andReturn(Wonk.class);

        replayAllRegisteredMocks();

        try
        {
            f.createCoreServiceImplementation(fp);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Property 'class' of <WonkHolder> is type java.lang.Class, which does not match the expected interface org.ops4j.gaderian.impl.Wonk.",
                    ex.getMessage());
            assertEquals(Gaderian.getLocation(parameters.get(0)), ex.getLocation());
        }

        verifyAllRegisteredMocks();
    }

    public void testIntegrated() throws Exception
    {
        Registry r = buildFrameworkRegistry("ServicePropertyFactory.xml", false );

        WonkSource source = r.getService(WonkSource.class);
        Wonk wonkService = r.getService(Wonk.class);

        Wonk wonk = createMock(Wonk.class);

        source.setWonk(wonk);

        wonk.wonkVoid();

        replayAllRegisteredMocks();

        // Invoking this (on the proxy) will cause the corresponding
        // method (on the mock) to be invoked.

        wonkService.wonkVoid();

        verifyAllRegisteredMocks();
    }

    /**
     * HIVEMIND-48: ServicePropertyFactory fails when the holding service and the property are in
     * different modules. Class loader issue involving Javasssist.
     */
    public void testIntegratedTwoModules() throws Exception
    {
        Registry r = buildFrameworkRegistry("ServicePropertyFactoryMaster.xml", false );

        WonkSource source = r.getService(WonkSource.class);
        Wonk wonkService = r.getService(Wonk.class);

        Wonk wonk = createMock(Wonk.class);

        source.setWonk(wonk);

        wonk.wonkVoid();

        replayAllRegisteredMocks();

        // Invoking this (on the proxy) will cause the corresponding
        // method (on the mock) to be invoked.

        wonkService.wonkVoid();

        verifyAllRegisteredMocks();
    }
}