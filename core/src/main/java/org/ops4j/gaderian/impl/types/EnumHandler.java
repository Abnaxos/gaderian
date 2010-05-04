package org.ops4j.gaderian.impl.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.internal.Module;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EnumHandler implements TypeHandler
{

    private final Class<? extends Enum> enumClass;
    private final Map<String, Enum> enumConstants;

    public EnumHandler( Class<? extends Enum> enumClass )
    {
        this.enumClass = enumClass;
        Enum[] enumConstants = enumClass.getEnumConstants();
        Map<String, Enum> map = new HashMap<String, Enum>( enumConstants.length );
        for( Enum val : enumConstants )
        {
            map.put( val.name(), val );
        }
        this.enumConstants = Collections.unmodifiableMap( map );
    }

    public Object stringToObject( Module module, String input, Location location ) throws Exception
    {
        String name = input.trim().replace( '-', '_' );
        Enum val = enumConstants.get( name );
        if( val == null )
        {
            val = enumConstants.get( name.toUpperCase() );
            if( val == null )
            {
                // TODO: i18n
                throw new ApplicationRuntimeException( "Cannot convert '" + input + "' to enum type " + enumClass, location, null );
            }
        }
        return val;
    }
}
