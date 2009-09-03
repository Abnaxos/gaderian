package org.ops4j.gaderian.service.impl;

import java.lang.reflect.Field;

import org.ops4j.gaderian.annotations.validation.Required;
import org.ops4j.gaderian.ApplicationRuntimeException;

/**
 * @author Johan Lindquist
 */
public class ServiceInstanceUtils
{
    public static void validate( final Object coreServiceInstance)
    {
        final Field[] fields = coreServiceInstance.getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            validateField(field, coreServiceInstance);
        }

    }

    private static void validateField( final Field field, final Object coreServiceInstance )
    {
        if (field.isAnnotationPresent( Required.class ))
        {
            try
            {
                if (!field.isAccessible())
                {
                    // Attempt to access the value
                    field.setAccessible( true );
                }
                if (field.get( coreServiceInstance) == null)
                {
                    throw new ApplicationRuntimeException( "Required field '" + field.getName() + "' is null" );
                }
            }
            catch ( IllegalAccessException e )
            {
                e.printStackTrace();
            }
        }
    }
}
