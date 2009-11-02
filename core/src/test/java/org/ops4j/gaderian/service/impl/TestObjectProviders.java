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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaderian.test.services.StringHolder;
import gaderian.test.services.impl.StringHolderImpl;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.impl.ModuleImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for several implementations of {@link org.ops4j.gaderian.service.ObjectProvider}.
 *
 * @author Howard Lewis Ship
 */
public class TestObjectProviders extends GaderianCoreTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();
        result.setClassResolver(getClassResolver());

        return result;
    }

    public void testServiceObjectProvider()
    {
        ServiceObjectProvider p = new ServiceObjectProvider();

        String expected = "EXPECTED RESULT";

        Module m = createMock(Module.class);

        expect(m.getService("fred", Object.class)).andReturn(expected);

        replayAllRegisteredMocks();

        Object actual = p.provideObject(m, Location.class, "fred", null);

        assertSame(expected, actual);

        assertNull(p.provideObject(m, Location.class, "", null));

        verifyAllRegisteredMocks();
    }

    public void testConfigurationObjectProvider()
    {
        ConfigurationObjectProvider p = new ConfigurationObjectProvider();

        List expectedList = new ArrayList();
        Map expectedMap = new HashMap();

        Module m = createMock(Module.class);

        expect(m.getConfiguration("barney")).andReturn(expectedList);

        expect(m.isConfigurationMappable("barney")).andReturn( true );

        expect(m.getConfigurationAsMap("barney")).andReturn(expectedMap);

        replayAllRegisteredMocks();

        Object actual = p.provideObject(m, List.class, "barney", null);

        assertSame(expectedList, actual);

        actual = p.provideObject(m, Map.class, "barney", null);

        assertSame(expectedMap, actual);

        verifyAllRegisteredMocks();
    }

    public void testInstanceProvider()
    {
        ObjectInstanceObjectProvider p = new ObjectInstanceObjectProvider();

        Module m = newModule();

        Object actual = p.provideObject(m, List.class, "java.util.ArrayList", null);

        assertTrue(actual.getClass().equals(ArrayList.class));

    }

    public void testInstanceProviderFailure()
    {
        ObjectInstanceObjectProvider p = new ObjectInstanceObjectProvider();
        Module m = newModule();

        try
        {
            p.provideObject(m, List.class, "java.util.List", null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to instantiate instance of class java.util.List");
        }
    }

    public void testServicePropertyObjectProvider()
    {
        Module m = createMock(Module.class);

        StringHolder h = new StringHolderImpl();

        h.setValue("abracadabra");

        expect(m.getService("MyService", Object.class)).andReturn( h );

        replayAllRegisteredMocks();

        ServicePropertyObjectProvider p = new ServicePropertyObjectProvider();

        Object result = p.provideObject(m, String.class, "MyService:value", null);

        assertEquals(h.getValue(), result);

        verifyAllRegisteredMocks();
    }

    public void testServicePropertyObjectProviderWithInvalidLocator()
    {
        ServicePropertyObjectProvider p = new ServicePropertyObjectProvider();
        Location l = newLocation();

        try
        {
            p.provideObject(null, null, "MyService", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, ServiceMessages.invalidServicePropertyLocator("MyService"));
            assertSame(l, ex.getLocation());
        }
    }

    public void testClassProvider()
    {
        Module module = createMock(Module.class);

        expect(module.resolveType("List")).andReturn(List.class);

        replayAllRegisteredMocks();

        assertSame(List.class, new ClassObjectProvider().provideObject(
                module,
                Class.class,
                "List",
                null));

        verifyAllRegisteredMocks();
    }

    // TODO: Integration test that proves the XML is valid.
}