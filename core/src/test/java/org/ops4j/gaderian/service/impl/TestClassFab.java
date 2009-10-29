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

import gaderian.test.services.AbstractIntWrapper;
import gaderian.test.services.FailService;
import gaderian.test.services.SimpleService;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import javassist.CtClass;
import junit.framework.AssertionFailedError;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.service.ClassFab;
import org.ops4j.gaderian.service.MethodFab;
import org.ops4j.gaderian.service.MethodSignature;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.util.PropertyUtils;

/**
 * Tests related to {@link org.ops4j.gaderian.service.impl.ClassFabImpl},
 * {@link org.ops4j.gaderian.service.impl.CtClassSource}, etc.
 *
 * @author Howard Lewis Ship
 */
public class TestClassFab extends GaderianCoreTestCase
{
    private CtClassSource _source;

    protected void setUp() throws Exception
    {
        super.setUp();

        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();

        GaderianClassPool pool = new GaderianClassPool();

        pool.appendClassLoader(threadLoader);

        _source = new CtClassSource(pool);
    }

    private ClassFab newClassFab(String className, Class superClass)
    {
        CtClass ctClass = _source.newClass(className, superClass);

        return new ClassFabImpl(_source, ctClass);
    }

    public void testCreateBean() throws Exception
    {
        ClassFab cf = newClassFab("TargetBean", Object.class);

        cf.addField("_stringValue", String.class);

        MethodSignature setStringValue = new MethodSignature(void.class, "setStringValue",
                new Class[]
                        {String.class}, null);

        cf.addMethod(Modifier.PUBLIC, setStringValue, "_stringValue = $1;");

        MethodSignature getStringValue = new MethodSignature(String.class, "getStringValue", null,
                null);

        cf.addMethod(Modifier.PUBLIC, getStringValue, "return _stringValue;");

        Class targetClass = cf.createClass();

        Object targetBean = targetClass.newInstance();

        PropertyUtils.write(targetBean, "stringValue", "Fred");

        String actual = (String) PropertyUtils.read(targetBean, "stringValue");

        assertEquals("Fred", actual);
    }

    public void testConstructor() throws Exception
    {
        ClassFab cf = newClassFab("ConstructableBean", Object.class);

        cf.addField("_stringValue", String.class);
        cf.addConstructor(new Class[]
                {String.class}, null, "{ _stringValue = $1; }");

        MethodSignature getStringValue = new MethodSignature(String.class, "getStringValue", null,
                null);

        cf.addMethod(Modifier.PUBLIC, getStringValue, "return _stringValue;");

        Class targetClass = cf.createClass();

        try
        {
            targetClass.newInstance();
            unreachable();
        }
        catch (InstantiationException ex)
        {
        }

        Constructor c = targetClass.getConstructors()[0];

        Object targetBean = c.newInstance(new Object[]
                {"Buffy"});

        String actual = (String) PropertyUtils.read(targetBean, "stringValue");

        assertEquals("Buffy", actual);
    }

    public void testConstructorFromBaseClass() throws Exception
    {
        ClassFab cf = newClassFab("MyIntHolder", AbstractIntWrapper.class);

        cf.addField("_intValue", int.class);
        cf.addConstructor(new Class[]
                {int.class}, null, "{ _intValue = $1; }");

        cf.addMethod(
                Modifier.PUBLIC,
                new MethodSignature(int.class, "getIntValue", null, null),
                "return _intValue;");

        Class targetClass = cf.createClass();
        Constructor c = targetClass.getConstructors()[0];

        AbstractIntWrapper targetBean = (AbstractIntWrapper) c.newInstance(new Object[]
                {new Integer(137)});

        assertEquals(137, targetBean.getIntValue());
    }

    public void testInvalidSuperClass() throws Exception
    {
        ClassFab cf = newClassFab("InvalidSuperClass", List.class);

        try
        {
            cf.createClass();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to create class InvalidSuperClass");
        }
    }

    public void testAddInterface() throws Exception
    {
        ClassFab cf = newClassFab("SimpleService", Object.class);

        cf.addInterface(SimpleService.class);

        cf.addMethod(Modifier.PUBLIC, new MethodSignature(int.class, "add", new Class[]
                {int.class, int.class}, null), "return $1 + $2;");

        Class targetClass = cf.createClass();

        SimpleService s = (SimpleService) targetClass.newInstance();

        assertEquals(207, s.add(99, 108));
    }

    public void testSubclassFromFinal() throws Exception
    {
        ClassFab cf = newClassFab("StringSubclass", String.class);

        try
        {
            cf.createClass();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionRegexp(
                    ex,
                    "Unable to create class StringSubclass\\:.*Cannot inherit from final class");
        }
    }

    public void testInPackage() throws Exception
    {
        ClassFab cf = newClassFab("org.ops4j.gaderian.InPackage", Object.class);

        Class c = cf.createClass();

        Object o = c.newInstance();

        assertEquals("org.ops4j.gaderian.InPackage", o.getClass().getName());
    }

    public void testBadMethodBody() throws Exception
    {
        ClassFab cf = newClassFab("BadMethodBody", Object.class);

        cf.addInterface(Runnable.class);

        try
        {
            cf.addMethod(
                    Modifier.PUBLIC,
                    new MethodSignature(void.class, "run", null, null),
                    "fail;");
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to add method void run() to class BadMethodBody:");
        }
    }

    public void testGetMethodFab() throws Exception
    {
        ClassFab cf = newClassFab("GetMethodFab", Object.class);

        MethodSignature s = new MethodSignature(void.class, "run", null, null);
        MethodFab mf = cf.addMethod(Modifier.PUBLIC, s, null);

        assertSame(mf, cf.getMethodFab(s));

        assertNull(cf.getMethodFab(new MethodSignature(void.class, "skip", null, null)));
    }

    public void testExtendMethod() throws Exception
    {
        ClassFab cf = newClassFab("ExtendMethod", Object.class);

        MethodFab mf = cf.addMethod(Modifier.PUBLIC, new MethodSignature(int.class, "getValue",
                null, null), "return 1;");

        mf.extend("return 2 * $_;", false);

        Object bean = cf.createClass().newInstance();

        assertEquals(new Integer(2), PropertyUtils.read(bean, "value"));
    }

    public void testExtendMethodAlterReturn() throws Exception
    {
        ClassFab cf = newClassFab("ExtendMethodAlterReturn", Object.class);

        MethodFab mf = cf.addMethod(Modifier.PUBLIC, new MethodSignature(int.class, "getValue",
                null, null), "return 2;");

        mf.extend("$_ = 3 * $_;", false);

        Object bean = cf.createClass().newInstance();

        assertEquals(new Integer(6), PropertyUtils.read(bean, "value"));
    }

    public void testExtendMethodFailure() throws Exception
    {
        ClassFab cf = newClassFab("ExtendMethodFailure", Object.class);

        MethodFab mf = cf.addMethod(Modifier.PUBLIC, new MethodSignature(int.class, "getValue",
                null, null), "return 1;");

        try
        {
            mf.extend("$_ =", true);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to extend method int getValue() of class ExtendMethodFailure:");
        }
    }

    public void testDupeMethodAdd() throws Exception
    {
        ClassFab cf = newClassFab("DupeMethodAdd", Object.class);

        cf.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "foo", null, null), "{}");

        try
        {
            cf.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "foo", null, null), "{}");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Attempt to redefine method void foo() of class DupeMethodAdd.", ex
                    .getMessage());
        }
    }

    public void testBadConstructor() throws Exception
    {
        ClassFab cf = newClassFab("BadConstructor", Object.class);

        try
        {
            cf.addConstructor(null, null, " woops!");
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to add constructor to class BadConstructor");
        }

    }

    public void testCatchException() throws Exception
    {
        ClassFab cf = newClassFab("Fail", Object.class);

        cf.addInterface(FailService.class);

        MethodFab mf = cf.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "fail", null,
                null), "throw new java.lang.RuntimeException(\"Ouch!\");");

        mf.addCatch(RuntimeException.class, "throw new java.io.IOException($e.getMessage());");

        Class targetClass = cf.createClass();

        FailService fs = (FailService) targetClass.newInstance();

        try
        {
            fs.fail();
            unreachable();
        }
        catch (IOException ex)
        {
            assertEquals("Ouch!", ex.getMessage());
        }
    }

    public void testBadCatch() throws Exception
    {
        ClassFab cf = newClassFab("BadCatch", Object.class);

        cf.addInterface(Runnable.class);

        MethodFab mf = cf.addMethod(Modifier.PUBLIC, new MethodSignature(void.class, "run", null,
                null), "return;");

        try
        {
            mf.addCatch(RuntimeException.class, "woops!");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to add catch block for exception java.lang.RuntimeException to class BadCatch");
        }
    }

    public void testInvalidFields() throws Exception
    {
        // Javassist lets us down here; I can't think of a way to get addField() to actually
        // fail.

        // JLI: Upgrading to version 3.7-GA caused this test to surprisingly fail
        // JLI: Are there no 'invalid' field names if you create your 'own' bytecode?
        // JLI: Anyway, made test check for the presence of the fields in question

        ClassFab cf = newClassFab("InvalidField", Object.class);

        cf.addField(".", String.class);
        cf.addField("buffy", int.class);
        cf.addField("", int.class);

        try
        {
            cf.createClass();

            final String toString = cf.toString();

            contains(toString, "private java.lang.String .");
            contains(toString, "private int buffy");
            contains(toString, "private int ");

        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Unable to create class InvalidField");
        }

    }

    /**
     * @since 1.1
     */
    public void testToString() throws Exception
    {
        ClassFab cf = newClassFab("FredRunnable", BaseLocatable.class);

        cf.addInterface(Runnable.class);
        cf.addInterface(Serializable.class);

        cf.addField("_map", Map.class);

        cf.addConstructor(new Class[]
                {Map.class, Runnable.class}, new Class[]
                {IllegalArgumentException.class, DataFormatException.class}, "{ _map = $1; }");

        MethodSignature sig = new MethodSignature(Map.class, "doTheNasty", new Class[]
                {int.class, String.class}, new Class[]
                {InstantiationException.class, IllegalAccessException.class});

        MethodFab mf = cf.addMethod(
                Modifier.PUBLIC + Modifier.FINAL + Modifier.SYNCHRONIZED,
                sig,
                "{ return _map; }");

        mf.addCatch(NullPointerException.class, "return null;");
        mf.extend("_map.clear();", true);

        String toString = cf.toString();

        contains(
                toString,
                "public class FredRunnable extends org.ops4j.gaderian.impl.BaseLocatable\n"
                        + "  implements java.lang.Runnable, java.io.Serializable");

        contains(toString, "private java.util.Map _map;");

        contains(
                toString,
                "public FredRunnable(java.util.Map $1, java.lang.Runnable $2)\n"
                        + "  throws java.lang.IllegalArgumentException, java.util.zip.DataFormatException\n"
                        + "{ _map = $1; }");

        contains(
                toString,
                "public final synchronized java.util.Map doTheNasty(int $1, java.lang.String $2)\n"
                        + "  throws java.lang.InstantiationException, java.lang.IllegalAccessException\n"
                        + "{ return _map; }\n" + "catch(java.lang.NullPointerException $e)\n"
                        + "return null;\n" + "finally\n" + "_map.clear();");

    }

    public void testCanConvert()
    {
        final ClassFab cf = newClassFab("BamBam", Object.class);
        assertTrue(cf.canConvert(String.class));
        assertFalse(cf.canConvert(CglibBeanInterfaceFactory.createCglibBean().getClass()));
        assertFalse(cf.canConvert(JavassistBeanInterfaceFactory.createJavassistBean().getClass()));
        assertFalse(cf.canConvert(JdkBeanInterfaceFactory.createJdkBean().getClass()));
    }

    /**
     * @since 1.1
     */
    private void contains(String actual, String expected)
    {
        if (actual.indexOf(expected) < 0)
        {
            throw new AssertionFailedError("Missing substring: " + expected + " Actual: " + actual);
        }
    }


}