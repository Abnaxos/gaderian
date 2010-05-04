package org.ops4j.gaderian.impl.types;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.internal.Module;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DoubleHandler implements TypeHandler
{

    public Object stringToObject( Module module, String input, Location location ) throws Exception
    {
        return Double.valueOf(input.trim());
    }
}