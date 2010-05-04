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

import java.beans.PropertyEditorManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.impl.TypeConverter;
import org.ops4j.gaderian.impl.TypeConverterImpl;
import org.ops4j.gaderian.impl.types.*;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.schema.rules.SmartTranslator;
import org.ops4j.gaderian.test.GaderianCoreTestCase;

/**
 * Tests for {@link org.ops4j.gaderian.schema.rules.SmartTranslator}.
 *
 * @author Howard Lewis Ship
 */
public class TestTypeConverter extends GaderianCoreTestCase
{

    // TODO: re-enable tests

    /**
     * Test a primitive type (int).
     */
    public void testInt()
    {
        Translator t = new SmartTranslator( createTestConverter());

        int result = t.translate(null, int.class, "-37", null);

        assertEquals(-37, result);
    }

    public void testNullInput()
    {
        Translator t = new SmartTranslator( createTestConverter());

        assertNull(t.translate(null, int.class, null, null));
    }

    public void testBlankInput()
    {
        Translator t = new SmartTranslator( createTestConverter());

        assertEquals("", t.translate(null, String.class, "", null));
    }

    /**
     * Test a wrapper type (Double).
     */
    public void testDouble()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Object result = t.translate(null, Double.class, "3.14", null);

        assertEquals(new Double("3.14"), result);
    }

    /**
     * Test a wrapper type (Float).
     */
    public void testFloat()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Float result = t.translate(null, Float.class, "3.14", null);

        assertEquals(new Float("3.14"), result);
    }

    /**
     * Test with a String value (apparently, this doesn't always work, see bug
     * HIVEMIND-15).
     */
    public void testString()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Object result = t.translate(null, String.class, "Fluffy Puppies", null);

        assertEquals("Fluffy Puppies", result);
    }

    /**
     * The input value should be returned as is (i.e. as a String) when the
     * property type is Object (see HIVEMIND-15).
     */
    public void testObjectAsString()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Object result = t.translate(null, Object.class, "Fluffy Puppies", null);

        assertEquals("Fluffy Puppies", result);
    }

    public void testStringWithNoEditor()
    {
        String[] paths = PropertyEditorManager.getEditorSearchPath();

        try
        {
            PropertyEditorManager.setEditorSearchPath(new String[] { "bogus.package" });
            Translator t = new SmartTranslator( createTestConverter());

            Object result = t.translate(null, String.class, "Fluffy Puppies", null);

            assertEquals("Fluffy Puppies", result);
        }
        finally
        {
            PropertyEditorManager.setEditorSearchPath(paths);
        }

    }

    public void testNoEditor()
    {
        Translator t = new SmartTranslator( createTestConverter());
        Location l = newLocation();

        try
        {
            t.translate(null, Registry.class, "fred", l);

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Could not convert string 'fred' to interface org.ops4j.gaderian.Registry: " +
                    "No type handler for interface org.ops4j.gaderian.Registry", ex.getMessage());

            assertSame(l, ex.getLocation());
        }
    }


    public void testByte()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Byte result = t.translate(null,  Byte.class, "100", null);

        assertEquals(new Byte((byte) 100), result);
    }

    public void testShort()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Short result = t.translate(null, Short.class, "-37", null);

        assertEquals(new Short((short) -37), result);
    }

    public void testInteger()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Integer result = t.translate(null, Integer.class, "-37", null);

        assertEquals(new Integer(-37), result);
    }

    public void testLong()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Long result = t.translate(null, Long.class, "-37", null);

        assertEquals(new Long(-37), result);
    }

    public void testPrimitiveLong()
    {
        Translator t = new SmartTranslator( createTestConverter());

        long result = t.translate(null, long.class, "-37", null);

        assertEquals(-37L, result);
    }

    public void testBoolean()
    {
        Translator t = new SmartTranslator( createTestConverter());

        Boolean result = t.translate(null, Boolean.class, "true", null);

        assertEquals(Boolean.TRUE, result);
    }

    public void testEnum()
    {
        TypeConverter c = createTestConverter();
        assertEquals( TestEnum.FOO, c.stringToObject(null, "FOO", TestEnum.class, null ));
        assertEquals( TestEnum.FOO, c.stringToObject(null, "foo", TestEnum.class, null ));
        assertEquals( TestEnum.FOO_BAR, c.stringToObject(null, "foo-Bar", TestEnum.class, null ));
        assertEquals( TestEnum.fooBar, c.stringToObject(null, "fooBar", TestEnum.class, null ));
        assertEquals( TestEnum.foo_bar, c.stringToObject(null, "foo-bar", TestEnum.class, null ));
    }

    public static TypeConverter createTestConverter()
    {
        RegistryInfrastructure registry = EasyMock.createMock( RegistryInfrastructure.class );
        registry.getConfigurationAsMap( TypeConverterImpl.TYPE_HANDLERS, null );
        Map<Class<?>, TypeHandler> config = new HashMap<Class<?>, TypeHandler>();
        config.put( int.class, new IntegerHandler() );
        config.put( short.class, new ShortHandler() );
        config.put( byte.class, new ByteHandler() );
        config.put( long.class, new LongHandler() );
        config.put( double.class, new DoubleHandler() );
        config.put( float.class, new FloatHandler() );
        config.put( boolean.class, new BooleanHandler() );
        config.put( Integer.class, new IntegerHandler() );
        config.put( Short.class, new ShortHandler() );
        config.put( Byte.class, new ByteHandler() );
        config.put( Long.class, new LongHandler() );
        config.put( Double.class, new DoubleHandler() );
        config.put( Float.class, new FloatHandler() );
        config.put( Boolean.class, new BooleanHandler() );
        config.put( String.class, new NullHandler() );
        config.put( Object.class, new NullHandler() );
        config.put( Date.class, new DateHandler() );
        EasyMock.expectLastCall().andReturn( config );
        EasyMock.replay( registry );
        return new TypeConverterImpl( registry );
    }

    private static enum TestEnum {
        FOO, FOO_BAR, fooBar, foo_bar
    }

}
