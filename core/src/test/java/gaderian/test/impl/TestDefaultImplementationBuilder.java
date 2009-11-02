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

package gaderian.test.impl;

import junit.framework.Assert;
import static org.easymock.EasyMock.expect;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.DefaultImplementationBuilder;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.impl.DefaultImplementationBuilderImpl;
import org.ops4j.gaderian.impl.PlaceholderFactory;
import org.ops4j.gaderian.service.impl.ClassFactoryImpl;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.impl.DefaultImplementationBuilderImpl} and
 * {@link org.ops4j.gaderian.impl.PlaceholderFactory}.
 *
 * @author Howard Lewis Ship
 */
public class TestDefaultImplementationBuilder extends GaderianCoreTestCase
{
    private DefaultImplementationBuilder _builder;

    protected void setUp() throws Exception
    {
        super.setUp();

        DefaultImplementationBuilderImpl bi = new DefaultImplementationBuilderImpl();

        bi.setClassFactory(new ClassFactoryImpl());

        _builder = bi;
    }

    private Object create(Class interfaceType)
    {
        replayAllRegisteredMocks();

        return _builder.buildDefaultImplementation(interfaceType);
    }

    public void testSimple()
    {
        Runnable r = (Runnable) create(Runnable.class);

        r.run();

        assertEquals("<Default implementation of interface java.lang.Runnable>", r.toString());

        verifyAllRegisteredMocks();
    }

    public void testComplex()
    {
        ValueHolder vh = (ValueHolder) create(ValueHolder.class);

        assertNull(vh.getStringValue());
        Assert.assertEquals(false, vh.getBooleanValue());
        Assert.assertEquals(0, vh.getIntValue());

        verifyAllRegisteredMocks();
    }

    public void testToStringInInterface()
    {
        ToString ts = (ToString) create(ToString.class);

        assertNull(ts.toString());

        verifyAllRegisteredMocks();
    }

    public void testCache()
    {
        Runnable r1 = (Runnable) create(Runnable.class);
        Runnable r2 = (Runnable) _builder.buildDefaultImplementation(Runnable.class);

        assertSame(r1, r2);

        verifyAllRegisteredMocks();
    }

    public void testNotInterface()
    {
        try
        {
            create(String.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Class java.lang.String is not an interface.");
        }
    }

    public void testModuleDescriptor() throws Exception
    {
        Registry r = buildFrameworkRegistry("DefaultImplementationBuilder.xml", false );

        DefaultImplementationBuilder dib = (DefaultImplementationBuilder) r.getService(
                "gaderian.DefaultImplementationBuilder",
                DefaultImplementationBuilder.class);

        replayAllRegisteredMocks();

        Runnable o = (Runnable) dib.buildDefaultImplementation(Runnable.class);

        o.run();

        verifyAllRegisteredMocks();
    }

    public void testPlaceholderBuilderSimulated() throws Exception
    {
        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        PlaceholderFactory db = new PlaceholderFactory();

        db.setBuilder(_builder);

        expect(fp.getServiceInterface()).andReturn(Runnable.class);

        replayAllRegisteredMocks();

        Runnable r = (Runnable) db.createCoreServiceImplementation(fp);

        r.run();

        verifyAllRegisteredMocks();
    }

    public void testPlaceholderFactory() throws Exception
    {
        Registry r = buildFrameworkRegistry("DefaultImplementationBuilder.xml", false );

        Runnable o = (Runnable) r.getService("gaderian.test.Runnable", Runnable.class);

        o.run();
    }
}