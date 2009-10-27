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

package gaderian.test.utilities;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.utilities.DefaultImplementationBuilder;
import org.ops4j.gaderian.utilities.impl.DefaultImplementationBuilderImpl;
import org.ops4j.gaderian.utilities.impl.PlaceholderFactory;
import org.ops4j.gaderian.service.impl.ClassFactoryImpl;
import org.ops4j.gaderian.test.GaderianTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.ops4j.gaderian.utilities.impl.DefaultImplementationBuilderImpl} and
 * {@link org.ops4j.gaderian.utilities.impl.PlaceholderFactory}.
 *
 * @author Howard Lewis Ship
 */
public class TestDefaultImplementationBuilder extends GaderianTestCase
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
        replayControls();

        return _builder.buildDefaultImplementation(interfaceType);
    }

    public void testSimple()
    {
        Runnable r = (Runnable) create(Runnable.class);

        r.run();

        assertEquals("<Default implementation of interface java.lang.Runnable>", r.toString());

        verifyControls();
    }

    public void testComplex()
    {
        ValueHolder vh = (ValueHolder) create(ValueHolder.class);

        assertNull(vh.getStringValue());
        assertEquals(false, vh.getBooleanValue());
        assertEquals(0, vh.getIntValue());

        verifyControls();
    }

    public void testToStringInInterface()
    {
        ToString ts = (ToString) create(ToString.class);

        assertNull(ts.toString());

        verifyControls();
    }

    public void testCache()
    {
        Runnable r1 = (Runnable) create(Runnable.class);
        Runnable r2 = (Runnable) _builder.buildDefaultImplementation(Runnable.class);

        assertSame(r1, r2);

        verifyControls();
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
                "gaderian.utilities.DefaultImplementationBuilder",
                DefaultImplementationBuilder.class);

        replayControls();

        Runnable o = (Runnable) dib.buildDefaultImplementation(Runnable.class);

        o.run();

        verifyControls();
    }

    public void testPlaceholderBuilderSimulated() throws Exception
    {
        MockControl fpc = newControl(ServiceImplementationFactoryParameters.class);
        ServiceImplementationFactoryParameters fp = (ServiceImplementationFactoryParameters) fpc
                .getMock();

        PlaceholderFactory db = new PlaceholderFactory();

        db.setBuilder(_builder);

        fp.getServiceInterface();
        fpc.setReturnValue(Runnable.class);

        replayControls();

        Runnable r = (Runnable) db.createCoreServiceImplementation(fp);

        r.run();

        verifyControls();
    }

    public void testPlaceholderFactory() throws Exception
    {
        Registry r = buildFrameworkRegistry("DefaultImplementationBuilder.xml", false );

        Runnable o = (Runnable) r.getService("gaderian.test.utilities.Runnable", Runnable.class);

        o.run();
    }
}