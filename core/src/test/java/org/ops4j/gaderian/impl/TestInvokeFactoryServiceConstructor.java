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

import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Occurances;
import org.ops4j.gaderian.ServiceImplementationFactory;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.easymock.MockControl;

/**
 * Tests some error conditions related to invoking a service factory.
 *
 * @author Howard Lewis Ship
 */
public class TestInvokeFactoryServiceConstructor extends GaderianCoreTestCase
{
    public void testWrongNumberOfParameters()
    {
        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        MockControl factoryPointControl = newControl(ServicePoint.class);
        ServicePoint factoryPoint = (ServicePoint) factoryPointControl.getMock();

        MockControl factoryControl = newControl(ServiceImplementationFactory.class);
        ServiceImplementationFactory factory = (ServiceImplementationFactory) factoryControl
                .getMock();

        MockControl pointControl = newControl(ServicePoint.class);
        ServicePoint point = (ServicePoint) pointControl.getMock();

        InvokeFactoryServiceConstructor c = new InvokeFactoryServiceConstructor();

        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        // Training !

        point.getErrorLog();
        pointControl.setReturnValue(log);

        module.getServicePoint("foo.bar.Baz");
        moduleControl.setReturnValue(factoryPoint);

        factoryPoint.getParametersCount();
        factoryPointControl.setReturnValue(Occurances.REQUIRED);

        factoryPoint.getService(ServiceImplementationFactory.class);
        factoryPointControl.setReturnValue(factory);

        factoryPoint.getParametersSchema();
        factoryPointControl.setReturnValue(null);

        String message = ImplMessages
                .wrongNumberOfParameters("foo.bar.Baz", 0, Occurances.REQUIRED);

        log.error(message, null, null);

        factory.createCoreServiceImplementation(new ServiceImplementationFactoryParametersImpl(
                point, module, Collections.EMPTY_LIST));
        factoryControl.setReturnValue("THE SERVICE");

        replayControls();

        c.setContributingModule(module);
        c.setFactoryServiceId("foo.bar.Baz");
        c.setParameters(Collections.EMPTY_LIST);
        c.setServiceExtensionPoint(point);

        assertEquals("THE SERVICE", c.constructCoreServiceImplementation());

        verifyControls();
    }
}