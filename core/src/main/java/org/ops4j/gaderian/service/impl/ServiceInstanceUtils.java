package org.ops4j.gaderian.service.impl;

import java.lang.reflect.Field;

import org.ops4j.gaderian.annotations.validation.Required;
import org.ops4j.gaderian.ApplicationRuntimeException;

/**
 * @author Johan Lindquist
 */
public class ServiceInstanceUtils
{
    public static void validate( final Class serviceClass, final Object serviceInstance )
    {
        final Field[] fields = serviceInstance .getClass().getDeclaredFields();

        for ( Field field : fields )
        {
            validateField(field, serviceClass, serviceInstance);
        }

    }

    private static void validateField( final Field field, final Class serviceClass, final Object serviceInstance )
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
                if (field.get( serviceInstance ) == null)
                {
                    throw new ApplicationRuntimeException( ServiceMessages.requiredDependencyNotFulfilled(field.getName(), serviceClass ));
                }
            }
            catch ( IllegalAccessException e )
            {
                e.printStackTrace();
            }
        }
    }
}
