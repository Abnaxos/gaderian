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

package org.ops4j.gaderian.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.easymock.classextension.EasyMock.expect;
import org.ops4j.gaderian.*;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.util.TestPropertyUtils;

/**
 * Tests for {@link org.ops4j.gaderian.factory.BeanFactoryImpl} and
 * {@link org.ops4j.gaderian.factory.BeanFactoryBuilder}.
 *
 * @author Howard Lewis Ship
 */
public class TestBeanFactoryImpl extends GaderianCoreTestCase
{
    private BeanFactoryContribution build(String name, Class objectClass)
    {
        return build(name, objectClass, null);
    }

    private BeanFactoryContribution build(String name, Class objectClass, Boolean cacheable)
    {
        BeanFactoryContribution result = new BeanFactoryContribution();
        result.setName(name);
        result.setBeanClass(objectClass);
        result.setCacheable(cacheable);
        result.setModule( TestPropertyUtils.createModuleForConversion() );

        return result;
    }

    private void executeNonClassContribution(String name, Class objectClass, String message)
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build(name, objectClass));

        ErrorLog el = (ErrorLog) createMock(ErrorLog.class);

        el.error(message, null, null);

        replayAllRegisteredMocks();

        BeanFactoryImpl f = new BeanFactoryImpl(el, Object.class, l, true);

        try
        {
            f.get(name);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals( FactoryMessages.unknownContribution(name), ex.getMessage());
        }

        verifyAllRegisteredMocks();
    }

    public void testInterfaceContribution()
    {
        executeNonClassContribution(
                "serializable",
                Serializable.class,
                "Contribution 'serializable' is for java.io.Serializable which is inappropriate for an object factory. The contribution has been ignored.");
    }

    public void testArrayContribution()
    {
        executeNonClassContribution(
                "array",
                String[].class,
                "Contribution 'array' is for java.lang.String[] which is inappropriate for an object factory. The contribution has been ignored.");
    }

    public void testPrimitiveContribution()
    {
        executeNonClassContribution(
                "primitive",
                double.class,
                "Contribution 'primitive' is for double which is inappropriate for an object factory. The contribution has been ignored.");
    }

    public void testIncorrectType()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("array-list", ArrayList.class));

        ErrorLog el = (ErrorLog) createMock(ErrorLog.class);

        el
                .error(
                        "Contribution 'array-list' (class java.util.ArrayList) is not assignable to interface java.util.Map and has been ignored.",
                        null,
                        null);

        replayAllRegisteredMocks();

        BeanFactoryImpl f = new BeanFactoryImpl(el, Map.class, l, true);

        try
        {
            f.get("array-list");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals( FactoryMessages.unknownContribution("array-list"), ex.getMessage());
        }

        verifyAllRegisteredMocks();
    }

    public void testTranslator()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("string", String.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        String s = (String) f.get("string,locator");

        assertEquals("locator", s);
    }

    public void testPlain()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("string", String.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        String s1 = (String) f.get("string");
        String s2 = (String) f.get("string");

        assertSame(s1, s2);
    }

    public void testNonCache()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("buffer", StringBuffer.class, Boolean.FALSE));

        BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        StringBuffer s1 = (StringBuffer) f.get("buffer");
        StringBuffer s2 = (StringBuffer) f.get("buffer");

        assertNotSame(s1, s2);
    }

    public void testConstructFailure()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, Number.class, l, true);

        try
        {
            f.get("integer");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Unable to instantiate instance of class java.lang.Integer: java.lang.Integer",
                    ex.getMessage());
        }

    }

    public void testBuilder()
    {
        BeanFactoryParameter p = new BeanFactoryParameter();

        List<BeanFactoryContribution> l = Collections.singletonList(build("integer", Integer.class));

        p.setContributions(l);

        ServiceImplementationFactoryParameters fp = createMock(ServiceImplementationFactoryParameters.class);

        expect(fp.getParameters()).andReturn(Collections.singletonList(p));
        expect(fp.getErrorLog()).andReturn(createMock(ErrorLog.class));

        replayAllRegisteredMocks();

        BeanFactoryBuilder b = new BeanFactoryBuilder();

        BeanFactory f = (BeanFactory) b.createCoreServiceImplementation(fp);

        Integer i = (Integer) f.get("integer,5");

        assertEquals(new Integer(5), i);

        verifyAllRegisteredMocks();
    }

    /**
     * Test integration; i.e., a service and configuration in a descriptor.
     */
    public void testIntegration() throws Exception
    {
        Registry r = buildFrameworkRegistry("NumberFactory.xml", false );

        BeanFactory f = (BeanFactory) r.getService(
                "gaderian.test.NumberFactory",
                BeanFactory.class);

        assertEquals(27, f.get("int,27"));
        assertEquals(-22.5, f.get("double,-22.5"));
    }

    public void testContains()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, Integer.class, l, true);

        boolean contains = f.contains("integer");

        assertTrue(contains);
    }

    public void testContainsFailure()
    {
        List<BeanFactoryContribution> l = Collections.singletonList(build("integer", Integer.class));

        BeanFactoryImpl f = new BeanFactoryImpl(null, Integer.class, l, true);

        boolean contains = f.contains("not_found");

        assertTrue(!contains);
    }


    /** GAD-20 */
    public void testNoArgConstructorHandling()
    {

        final List<BeanFactoryContribution> l = Collections.singletonList(build("simple", SimpleNoArgConstructorBean.class));

        final BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        final SimpleNoArgConstructorBean simpleNoArgConstructorBean1 = (SimpleNoArgConstructorBean) f.get("simple,stringValue=fred");

        final SimpleNoArgConstructorBean simpleNoArgConstructorBean2 = (SimpleNoArgConstructorBean) f.get("simple,stringValue=bob");

        assertNotSame(simpleNoArgConstructorBean1,simpleNoArgConstructorBean2);

        assertEquals("fred",simpleNoArgConstructorBean1.getStringValue());
        assertEquals("bob", simpleNoArgConstructorBean2.getStringValue());

    }

    /** GAD-20 */
    public void testNoArgConstructorHandlingWithPrimitiveProperty()
    {

        final List<BeanFactoryContribution> l = Collections.singletonList(build("simple", SimpleNoArgConstructorBean.class));

        final BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        final SimpleNoArgConstructorBean simpleNoArgConstructorBean1 = (SimpleNoArgConstructorBean) f.get("simple,intValue=1");

        final SimpleNoArgConstructorBean simpleNoArgConstructorBean2 = (SimpleNoArgConstructorBean) f.get("simple,intValue=2");

        assertNotSame(simpleNoArgConstructorBean1, simpleNoArgConstructorBean2);

        assertEquals(1,simpleNoArgConstructorBean1.getIntValue());
        assertEquals(2, simpleNoArgConstructorBean2.getIntValue());
    }

    /** GAD-20 */
    public void testNoArgConstructorHandlingInvalidInitializerPropertyName()
    {
        final List<BeanFactoryContribution> l = Collections.singletonList(build("simple", SimpleNoArgConstructorBean.class));

        final BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        try
        {
            f.get("simple,badpropertyname=fred");
            fail("could create with bad property name");
        }
        catch (ApplicationRuntimeException e)
        {
            // Should happen
            assertEquals("Class 'org.ops4j.gaderian.factory.SimpleNoArgConstructorBean' does not define a property named 'badpropertyname' as indicated by the initializer 'badpropertyname=fred'.",e.getMessage());
        }

    }

    /** GAD-20 */
    public void testNoArgConstructorHandlingInvalidInitializer()
    {
        final List<BeanFactoryContribution> l = Collections.singletonList(build("simple", SimpleNoArgConstructorBean.class));

        final BeanFactoryImpl f = new BeanFactoryImpl(null, Object.class, l, true);

        try
        {
            f.get("simple,=badinitializer");
            fail("could create with bad property name");
        }
        catch (ApplicationRuntimeException e)
        {
            // Should happen
            assertEquals("Initializer '=badinitializer' is not formatted correctly for use as a property based intitializer, it should be 'propertyName=value' when no String constructor is available.", e.getMessage());
        }

        try
        {
            f.get("simple,badinitializer");
            fail("could create with bad property name");
        }
        catch (ApplicationRuntimeException e)
        {
            // Should happen
            assertEquals("Initializer 'badinitializer' is not formatted correctly for use as a property based intitializer, it should be 'propertyName=value' when no String constructor is available.", e.getMessage());
        }

    }

}