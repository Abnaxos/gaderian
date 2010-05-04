package org.ops4j.gaderian.impl.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.internal.Module;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class StringConstructorHandler implements TypeHandler
{

    private final Constructor m_constructor;

    private StringConstructorHandler( Constructor constructor )
    {
        m_constructor = constructor;
    }

    public Object stringToObject( Module module, String input, Location location ) throws Exception
    {
        return m_constructor.newInstance( input );
    }

    public static StringConstructorHandler tryCreate( Class<?> clazz )
    {
        if( clazz.isInterface() || clazz.isPrimitive() )
        {
            return null;
        }
        if( (clazz.getModifiers() & Modifier.ABSTRACT) != 0 )
        {
            return null;
        }
        Constructor constructor;
        try
        {
            constructor = clazz.getConstructor( String.class );
        }
        catch( NoSuchMethodException e )
        {
            return null;
        }
        if( (constructor.getModifiers() & Modifier.PUBLIC) == 0 )
        {
            return null;
        }
        return new StringConstructorHandler( constructor );
    }

}
