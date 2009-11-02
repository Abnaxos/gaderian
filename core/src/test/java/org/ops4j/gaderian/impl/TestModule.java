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

import gaderian.test.services.StringHolder;
import gaderian.test.services.impl.StringHolderImpl;
import org.easymock.classextension.EasyMock;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ClassResolver;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Locatable;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.util.URLResource;

/**
 * Some additional tests for {@link org.ops4j.gaderian.impl.ModuleImpl}.
 *
 * @author Howard Lewis Ship
 */
public class TestModule extends GaderianCoreTestCase
{

    public void testGetServiceByInterface()
    {
        RegistryInfrastructure r = createMock(RegistryInfrastructure.class);

        ModuleImpl m = new ModuleImpl();
        m.setRegistry(r);

        StringHolder h = new StringHolderImpl();

        EasyMock.expect(r.getService(StringHolder.class, m)).andReturn( h );

        replayAllRegisteredMocks();

        Object result = m.getService(StringHolder.class);

        assertEquals(h, result);

        verifyAllRegisteredMocks();
    }

    public void testResolveType()
    {
        ModuleImpl module = new ModuleImpl();
        module.setPackageName("org.ops4j.gaderian");
        module.setClassResolver(getClassResolver());

        assertSame(Locatable.class, module.resolveType("org.ops4j.gaderian.Locatable"));
        assertSame(ErrorLog.class, module.resolveType("ErrorLog"));
        assertSame(BaseLocatable.class, module.resolveType("impl.BaseLocatable"));
    }

    /**
     * @since 1.1.1
     */
    public void testResolveTypeCache()
    {
        Class expected = getClass();

        ClassResolver resolver = newClassResolver();

        trainCheckForClass(resolver, "FooBar", expected);

        replayAllRegisteredMocks();

        ModuleImpl module = new ModuleImpl();
        module.setPackageName("org.ops4j.gaderian");
        module.setClassResolver(resolver);

        assertSame(expected, module.resolveType("FooBar"));

        // And this time it comes out of the cache

        assertSame(expected, module.resolveType("FooBar"));

        verifyAllRegisteredMocks();
    }

    private void trainCheckForClass(ClassResolver resolver, String type, Class returnClass)
    {
        EasyMock.expect( resolver.checkForClass(type) ).andReturn( returnClass );
    }

    private ClassResolver newClassResolver()
    {
        return createMock(ClassResolver.class);
    }

    public void testResolveTypeFailure()
    {
        ModuleImpl module = new ModuleImpl();
        module.setPackageName("org.ops4j.gaderian.order");
        module.setClassResolver(getClassResolver());

        LocationImpl location = new LocationImpl(new URLResource("http://www.ops4j.org"),1,2);
        module.setLocation(location);

        try
        {
            module.resolveType("Qbert");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Unable to convert type 'Qbert' to a Java class, either as is, or in package org.ops4j.gaderian.order.",
                    ex.getMessage());
            assertEquals("bad location defined", location, ex.getLocation());
        }
    }
}