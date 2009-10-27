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

import org.apache.commons.logging.Log;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.test.AggregateArgumentsMatcher;
import org.ops4j.gaderian.test.ArgumentMatcher;
import org.ops4j.gaderian.test.GaderianTestCase;
import org.ops4j.gaderian.test.TypeMatcher;
import org.easymock.MockControl;

/**
 * Additional tests for {@link org.ops4j.gaderian.service.impl.BuilderFactoryLogic}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestBuilderFactory extends GaderianTestCase
{
    /**
     * Tests for errors when actually invoking the initializer method (as opposed to an error
     * finding the method).
     *
     * @since 1.1
     */
    public void testErrorInInitializer() throws Exception
    {
        Location l = newLocation();

        MockControl fpc = MockControl
                .createNiceControl(ServiceImplementationFactoryParameters.class);
        addControl(fpc);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        Log log = (Log) newMock(Log.class);

        MockControl mc = newControl(Module.class);
        Module module = (Module) mc.getMock();

        module.getClassResolver();
        mc.setReturnValue( getClassResolver() );

        MockControl errorLogc = newControl(ErrorLog.class);
        ErrorLog errorLog = (ErrorLog) errorLogc.getMock();

        fp.getServiceId();
        fpc.setDefaultReturnValue("foo.Bar");

        fp.getInvokingModule();
        fpc.setDefaultReturnValue(module);

        module.resolveType("org.ops4j.gaderian.service.impl.InitializerErrorRunnable");
        mc.setReturnValue(InitializerErrorRunnable.class);

        fp.getLog();
        fpc.setDefaultReturnValue(log);

        fp.getErrorLog();
        fpc.setDefaultReturnValue(errorLog);

        Throwable cause = new ApplicationRuntimeException("Failure in initializeService().");

        String message = ServiceMessages.unableToInitializeService(
                "foo.Bar",
                "initializeService",
                InitializerErrorRunnable.class,
                cause);

        errorLog.error(message, l, new ApplicationRuntimeException(""));
        errorLogc.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, null, new TypeMatcher() }));

        BuilderParameter p = new BuilderParameter();
        p.setClassName(InitializerErrorRunnable.class.getName());
        p.setLocation(l);

        replayControls();

        BuilderFactoryLogic logic = new BuilderFactoryLogic(fp, p);

        assertNotNull(logic.createService());

        verifyControls();
    }

    public void testListPropertyAutowire() throws Exception
    {
        final Registry reg = buildFrameworkRegistry("ListProperty.xml", false );
        ListPropertyBean bean = (ListPropertyBean) reg.getService(ListPropertyBean.class);
        assertNull(bean.getList());

    }
}