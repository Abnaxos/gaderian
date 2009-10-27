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

package gaderian.test.rules;

import gaderian.test.FrameworkTestCase;

import java.util.List;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.impl.ElementImpl;
import org.ops4j.gaderian.impl.LocationImpl;
import org.ops4j.gaderian.impl.ModuleImpl;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.schema.rules.ClassTranslator;
import org.ops4j.gaderian.schema.rules.InstanceTranslator;

/**
 * Fill in some gaps in {@link org.ops4j.gaderian.schema.rules.InstanceTranslator} and
 * {@link org.ops4j.gaderian.schema.rules.ClassTranslator}.
 * 
 * @author Howard Lewis Ship
 */
public class TestInstanceTranslator extends FrameworkTestCase
{

    public void testNull()
    {
        InstanceTranslator t = new InstanceTranslator();

        assertNull(t.translate(null, null, null, null));
    }

    protected Module newModule()
    {
        return (Module) newMock(Module.class);
    }

    public void testBadClass() throws Exception
    {
        InstanceTranslator t = new InstanceTranslator();
        ElementImpl e = new ElementImpl();
        Location l = new LocationImpl(getResource("TestInstanceTranslator.class"), 50);
        e.setLocation(l);

        Module m = newModule();

        m.resolveType("bad.class.Name");
        ApplicationRuntimeException cause = new ApplicationRuntimeException("failure");
        setThrowable(m, cause);

        replayControls();

        try
        {
            t.translate(m, null, "bad.class.Name", null);
        }
        catch (ApplicationRuntimeException ex)
        {
            assertSame(cause, ex);
        }

        verifyControls();
    }

    public void testInitializer() throws Exception
    {
        InstanceTranslator t = new InstanceTranslator();

        Module m = newModule();

        m.resolveType("Bean");
        setReturnValue(m, IntHolder.class);

        replayControls();

        IntHolder ih = (IntHolder) t.translate(m, Object.class, "Bean,value=37", null);

        assertEquals(37, ih.getValue());

        verifyControls();
    }

    public void testPrivateObject() throws Exception
    {
        InstanceTranslator t = new InstanceTranslator();
        ElementImpl e = new ElementImpl();
        Location l = new LocationImpl(getResource("TestInstanceTranslator.class"), 50);
        e.setLocation(l);

        ModuleImpl m = new ModuleImpl();
        m.setClassResolver(getClassResolver());

        replayControls();

        try
        {
            t.translate(m, null, PrivateObject.class.getName(), null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to instantiate instance of class gaderian.test.rules.PrivateObject");
        }

        verifyControls();
    }

    public void testWrongType() throws Exception
    {
        Registry r = buildFrameworkRegistry("WrongType.xml", false );

        interceptLogging();

        List l = r.getConfiguration("gaderian.test.rules.WrongType");

        // Convert the proxy into a real list; this will trigger the
        // expected errors.

        l.size();

        assertLoggedMessagePattern("Unable to update property value of object gaderian\\.test\\.config\\.impl\\.Datum@");
    }

    public void testClassTranslator() throws Exception
    {
        ModuleImpl m = new ModuleImpl();
        m.setClassResolver(getClassResolver());

        replayControls();

        ClassTranslator t = new ClassTranslator();

        Class c = (Class) t.translate(m, null, getClass().getName(), null);

        assertEquals(getClass(), c);

        verifyControls();
    }
}