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

package gaderian.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.SymbolSource;
import org.ops4j.gaderian.impl.*;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.ServicePoint;

/**
 * Additional tests to fill in minor code coverage gaps.
 *
 * @author Howard Lewis Ship
 */
public class TestMisc extends FrameworkTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();
        result.setClassResolver(getClassResolver());

        return result;
    }

    public void testApplicationRuntimeExceptionGetComponent()
    {
        ApplicationRuntimeException ex = new ApplicationRuntimeException("My Message", this, null,
                null);

        assertSame(this, ex.getComponent());
    }

    public void testApplicationRuntimeExceptionThrowableConstructor()
    {
        RuntimeException re = new RuntimeException("Now it can be told.");
        ApplicationRuntimeException ex = new ApplicationRuntimeException(re);

        assertEquals("Now it can be told.", ex.getMessage());
        assertSame(re, ex.getRootCause());
    }

    public void testExtensionImplGetElementsEmpty()
    {
        ContributionImpl e = new ContributionImpl();

        assertSame(Collections.EMPTY_LIST, e.getElements());
    }

    public void testInvokeFactoryServiceConstructorAccessors()
    {
        Module m = new ModuleImpl();
        ServicePoint sep = new ServicePointImpl();
        List p = new ArrayList();
        InvokeFactoryServiceConstructor c = new InvokeFactoryServiceConstructor();

        c.setContributingModule(m);
        c.setServiceExtensionPoint(sep);
        c.setParameters(p);

        assertSame(m, c.getContributingModule());
        assertSame(sep, c.getServiceExtensionPoint());
        assertSame(p, c.getParameters());
    }

    public void testCreateClassServiceConstructorAccessors()
    {
        Module m = (Module) createMock(Module.class);

        replayAllRegisteredMocks();

        CreateClassServiceConstructor c = new CreateClassServiceConstructor();

        c.setContributingModule(m);

        assertSame(m, c.getContributingModule());

        c.setInstanceClassName("java.util.HashMap");

        assertEquals("java.util.HashMap", c.getInstanceClassName());

        verifyAllRegisteredMocks();
    }

    public void testCreateClassServiceConstructorTwice()
    {
        Module m = newModule();

        replayAllRegisteredMocks();

        CreateClassServiceConstructor c = new CreateClassServiceConstructor();

        c.setContributingModule(m);

        c.setInstanceClassName("java.util.HashMap");

        Object o1 = c.constructCoreServiceImplementation();
        Object o2 = c.constructCoreServiceImplementation();

        assertNotSame(o1, o2);

        assertTrue(o1 instanceof HashMap);
        assertTrue(o2 instanceof HashMap);
    }

    /** @since 1.1 */

    public void testCreateInstanceWithInitializer()
    {
        Module m = newModule();

        CreateClassServiceConstructor c = new CreateClassServiceConstructor();

        c.setContributingModule(m);

        c.setInstanceClassName(SimpleBean.class.getName() + ",value=Gaderian");

        SimpleBean b = (SimpleBean) c.constructCoreServiceImplementation();

        assertEquals("Gaderian", b.getValue());
    }

    public void testCreateClassServiceConstructorFailure()
    {
        Module m = newModule();

        CreateClassServiceConstructor c = new CreateClassServiceConstructor();

        c.setContributingModule(m);

        c.setInstanceClassName(PrivateBean.class.getName());

        try
        {
            c.constructCoreServiceImplementation();
            unreachable();
        }
        catch (Exception ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to instantiate instance of class gaderian.test.PrivateBean");
        }

    }

    public void testSystemPropertiesSymbolSource()
    {
        SymbolSource s = new SystemPropertiesSymbolSource();

        assertEquals(System.getProperty("user.home"), s.valueForSymbol("user.home"));
    }
}