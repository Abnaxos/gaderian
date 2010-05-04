package org.ops4j.gaderian.impl.types;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.impl.JavaTypeUtils;
import org.ops4j.gaderian.internal.Module;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ClassHandler implements TypeHandler
{

    public Object stringToObject( Module module, String input, Location location ) throws Exception
    {
        ClassLoader loader;
        if( module != null )
        {
            return module.resolveType( input );
        }
        else
        {
            Class<?> result = JavaTypeUtils.getPrimtiveClass( input );
            if( result != null )
            {
                return result;
            }
            String jvmName = JavaTypeUtils.getJVMClassName( input );
            loader = Thread.currentThread().getContextClassLoader();
            if( loader == null )
            {
                loader = getClass().getClassLoader();
            }
            return Class.forName( jvmName, true, loader );
        }
    }
}
