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

package org.ops4j.gaderian.utilities.factory;

import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.service.ObjectProvider;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.utilities.BeanFactory;

/**
 * Tests for {@link org.ops4j.gaderian.utilities.factory.BeanFactoryObjectProvider}.
 *
 * @author Howard Lewis Ship
 */
public class TestBeanFactoryObjectProvider extends GaderianCoreTestCase
{
    public void testNullInput()
    {
        ObjectProvider op = new BeanFactoryObjectProvider();

        assertNull(op.provideObject(null, null, null, null));
    }

    public void testNoServiceId()
    {
        ObjectProvider op = new BeanFactoryObjectProvider();
        Location l = newLocation();

        try
        {
            op.provideObject(null, null, "foo", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "'foo' is not formatted correctly, it should be 'service-id:name[,initializer]'.",
                ex.getMessage());

            assertSame(l, ex.getLocation());
        }
    }

    public void testNoLocator()
    {
        ObjectProvider op = new BeanFactoryObjectProvider();
        Location l = newLocation();

        try
        {
            op.provideObject(null, null, "foo:", l);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                "'foo:' is not formatted correctly, it should be 'service-id:name[,initializer]'.",
                ex.getMessage());
            assertSame(l, ex.getLocation());
        }
    }

    public void testSuccess()
    {
        String result = "Obtained via BeanFactory.";

        BeanFactory factory = createMock(BeanFactory.class);

        Module module = createMock(Module.class);

        expect(module.getService("factory", BeanFactory.class)).andReturn(factory);

        expect(factory.get("my-bean,initialized")).andReturn(result);

        replayAllRegisteredMocks();

        ObjectProvider op = new BeanFactoryObjectProvider();

        assertSame(result, op.provideObject(module, null, "factory:my-bean,initialized", null));

        verifyAllRegisteredMocks();
    }
}
