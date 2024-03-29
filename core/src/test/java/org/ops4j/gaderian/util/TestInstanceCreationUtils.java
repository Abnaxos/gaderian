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

package org.ops4j.gaderian.util;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.util.InstanceCreationUtils}.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestInstanceCreationUtils extends GaderianCoreTestCase
{
    public static class Bean
    {
        private int _value;

        public int getValue()
        {
            return _value;
        }

        public void setValue(int value)
        {
            _value = value;
        }
    }

    private Module newModule(String name, Class returnValue)
    {
        Module module = createMock(Module.class);

        expect(module.resolveType(name)).andReturn(returnValue);
        TestPropertyUtils.addConversionBehaviour( module );

        return module;
    }

    public void testSimple()
    {
        Module module = newModule("Bean", Bean.class);

        replayAllRegisteredMocks();

        Bean bean = (Bean) InstanceCreationUtils.createInstance(module, "Bean", null);

        assertNotNull(bean);

        verifyAllRegisteredMocks();
    }

    public void testComplex()
    {
        Module module = newModule("Bean", Bean.class);

        replayAllRegisteredMocks();

        Bean bean = (Bean) InstanceCreationUtils.createInstance(module, "Bean,value=42", null);

        assertEquals(42, bean.getValue());

        verifyAllRegisteredMocks();
    }

    public void testSetLocation()
    {
        Location l = newLocation();
        Module module = newModule("Holder", BaseLocatable.class);

        replayAllRegisteredMocks();

        BaseLocatable holder = (BaseLocatable) InstanceCreationUtils.createInstance(
                module,
                "Holder",
                l);

        assertSame(l, holder.getLocation());

        verifyAllRegisteredMocks();
    }

    public void testFailure()
    {
        Location l = newLocation();
        Module module = newModule("Bean", Bean.class);

        replayAllRegisteredMocks();

        try
        {
            InstanceCreationUtils.createInstance(module, "Bean,value=fred", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to instantiate instance of class org.ops4j.gaderian.util.TestInstanceCreationUtils$Bean");
            assertSame(l, ex.getLocation());
        }

        verifyAllRegisteredMocks();
    }
}