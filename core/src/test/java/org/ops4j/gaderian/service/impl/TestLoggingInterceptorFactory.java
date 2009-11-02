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

package org.ops4j.gaderian.service.impl;

import java.util.Collections;

import org.apache.commons.logging.Log;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.impl.InterceptorStackImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.service.ClassFactory;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.service.impl.LoggingInterceptorFactory}.
 *
 * @author Howard Lewis Ship
 * @author James Carman
 */
public class TestLoggingInterceptorFactory extends GaderianCoreTestCase
{
    /**
     * A test for HIVEMIND-55 ... ensure that the LoggingInterceptor can work on
     * top of a JDK proxy.
     */
    public void testLoggingOverProxy()
    {
        ClassFactory cf = new ClassFactoryImpl();

        Runnable r = (Runnable) createMock(Runnable.class);
        Log log = createMock(Log.class);

        LoggingInterceptorFactory f = new LoggingInterceptorFactory();
        f.setFactory(cf);

        ServicePoint sp = createMock(ServicePoint.class);

        Module module = createMock(Module.class);

        // Training

        expect(sp.getServiceInterface()).andReturn(Runnable.class);

        expect(sp.getExtensionPointId()).andReturn("foo.bar");

        replayAllRegisteredMocks();

        InterceptorStackImpl is = new InterceptorStackImpl(log, sp, r);

        f.createInterceptor(is, module, Collections.EMPTY_LIST);

        Runnable ri = (Runnable) is.peek();

        verifyAllRegisteredMocks();

        // Training

        expect(log.isDebugEnabled()).andReturn(true);

        log.debug("BEGIN run()");
        log.debug("END run()");

        r.run();

        replayAllRegisteredMocks();

        ri.run();

        verifyAllRegisteredMocks();
    }

    public void testJavassistProxies() throws Exception {
		final Registry reg = buildFrameworkRegistry("generated.xml", false );
		final BeanInterface bean = ( BeanInterface )reg.getService( "generated.JavassistBeanInterface", BeanInterface.class );
		bean.interfaceMethod();
	}

	public void testCglibProxies() throws Exception {
		final Registry reg = buildFrameworkRegistry("generated.xml", false );
		final BeanInterface bean = ( BeanInterface )reg.getService( "generated.CglibBeanInterface", BeanInterface.class );
		bean.interfaceMethod();
	}

	public void testJdkProxies() throws Exception {
		final Registry reg = buildFrameworkRegistry("generated.xml", false );
		final BeanInterface bean = ( BeanInterface )reg.getService( "generated.JdkBeanInterface", BeanInterface.class );
		bean.interfaceMethod();
	}

}