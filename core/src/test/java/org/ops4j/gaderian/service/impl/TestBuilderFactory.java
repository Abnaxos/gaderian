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
import static org.easymock.classextension.EasyMock.*;
import org.ops4j.gaderian.*;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Additional tests for {@link org.ops4j.gaderian.service.impl.BuilderFactoryLogic}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestBuilderFactory extends GaderianCoreTestCase
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
        
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);
        
        Log log = createMock(Log.class);

        Module module = createMock(Module.class);

        expect(module.getClassResolver()).andReturn(getClassResolver());

        ErrorLog errorLog = createMock(ErrorLog.class);

        expect(fp.getServiceId()).andReturn("foo.Bar").times(2);
        expect(fp.getInvokingModule()).andReturn(module);
        expect(module.resolveType("org.ops4j.gaderian.service.impl.InitializerErrorRunnable")).andReturn(InitializerErrorRunnable.class);
        // expect(fp.getLog()).andReturn(log);
        expect(fp.getErrorLog()).andReturn(errorLog);

        final Throwable cause = new ApplicationRuntimeException("Failure in initializeService().");

        final String message = ServiceMessages.unableToInitializeService(
                "foo.Bar",
                "initializeService",
                InitializerErrorRunnable.class,
                cause);

        errorLog.error(eq(message), eq(l), isA(ApplicationRuntimeException.class));
        
        BuilderParameter p = new BuilderParameter();
        p.setClassName(InitializerErrorRunnable.class.getName());
        p.setLocation(l);

        replayAllRegisteredMocks();

        BuilderFactoryLogic logic = new BuilderFactoryLogic(fp, p);

        assertNotNull(logic.createService());

        verifyAllRegisteredMocks();
    }

    public void testListPropertyAutowire() throws Exception
    {
        final Registry reg = buildFrameworkRegistry("ListProperty.xml", false );
        ListPropertyBean bean = (ListPropertyBean) reg.getService(ListPropertyBean.class);
        assertNull(bean.getList());

    }
}