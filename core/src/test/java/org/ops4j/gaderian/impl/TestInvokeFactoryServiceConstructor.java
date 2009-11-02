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

import java.util.Collections;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Occurances;
import org.ops4j.gaderian.ServiceImplementationFactory;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests some error conditions related to invoking a service factory.
 *
 * @author Howard Lewis Ship
 */
public class TestInvokeFactoryServiceConstructor extends GaderianCoreTestCase
{
    public void testWrongNumberOfParameters()
    {
        Module module = createMock( Module.class );

        ServicePoint factoryPoint = createMock( ServicePoint.class );

        ServiceImplementationFactory factory = createMock( ServiceImplementationFactory.class );
        ServicePoint point = createMock( ServicePoint.class );

        InvokeFactoryServiceConstructor c = new InvokeFactoryServiceConstructor();

        ErrorLog log = createMock( ErrorLog.class );

        // Training !

        expect(point.getErrorLog()).andReturn( log );

        expect(module.getServicePoint( "foo.bar.Baz" )).andReturn( factoryPoint );

        expect(factoryPoint.getParametersCount()).andReturn( Occurances.REQUIRED );

        expect(factoryPoint.getService( ServiceImplementationFactory.class )).andReturn( factory );

        expect(factoryPoint.getParametersSchema()).andReturn( null );

        String message = ImplMessages
                .wrongNumberOfParameters( "foo.bar.Baz", 0, Occurances.REQUIRED );

        log.error( message, null, null );

        expect(factory.createCoreServiceImplementation( new ServiceImplementationFactoryParametersImpl(
                point, module, Collections.EMPTY_LIST ) )).andReturn( "THE SERVICE" );

        replayAllRegisteredMocks();

        c.setContributingModule( module );
        c.setFactoryServiceId( "foo.bar.Baz" );
        c.setParameters( Collections.EMPTY_LIST );
        c.setServiceExtensionPoint( point );

        assertEquals( "THE SERVICE", c.constructCoreServiceImplementation() );

        verifyAllRegisteredMocks();
    }
}